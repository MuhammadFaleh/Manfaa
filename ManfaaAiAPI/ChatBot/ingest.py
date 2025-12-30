import os

from sklearn.neighbors import NearestNeighbors

import ChatBot.constants as constants
from dotenv import load_dotenv

import fitz  # PyMuPDF
from sentence_transformers import SentenceTransformer
from langchain_core.documents import Document
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_chroma import Chroma
from sqlalchemy import create_engine
from sqlalchemy.sql import text as text_sql
from sqlalchemy.orm import sessionmaker

if not load_dotenv():
    print("please check that env file exist and readable")
    exit(0)

from ChatBot.constants import CHROMA_SETTINGS
import chromadb

# environment variables
load_dotenv()
persist_directory = constants.persist_directory
persist_directory_pdf = os.path.join(persist_directory, "pdf_db")
persist_directory_sql = os.path.join(persist_directory, "sql_db")

embeddings_id = constants.embeddings_id
embeddings = constants.embeddings
MODEL = constants.MODEL
pdf_file_paths = os.getenv("pdf_PATH")


mysql_url = os.getenv('SQL_db')
engine = create_engine(
    mysql_url,
    pool_pre_ping=True,
    pool_recycle=3600
)
Session = sessionmaker(bind=engine)


class BidMatcher:
    def __init__(self, model_name='all-MiniLM-L6-v2'):
        self.model = SentenceTransformer(model_name)

    def prepare_text(self, data):
        parts = [
            f"Category: {data.get('category', '')}",
            f"Description: {data.get('description', '')}",
            f"Deliverables: {data.get('deliverables', '')}",
            f"Skills: {data.get('skills', '')}"
        ]
        return " | ".join(parts)

    def rank_bids_for_request(self, request, bids, top_k=5):
        if not bids:
            return []

        request_text = self.prepare_text(request)
        request_emb = self.model.encode([request_text], normalize_embeddings=True)

        bid_texts = [self.prepare_text(bid) for bid in bids]
        bid_embs = self.model.encode(bid_texts, normalize_embeddings=True)

        knn = NearestNeighbors(
            n_neighbors=min(top_k, len(bids)),
            metric='cosine',
            algorithm='brute'
        )
        knn.fit(bid_embs)

        distances, indices = knn.kneighbors(request_emb)

        results = []
        for idx, distance in zip(indices[0], distances[0]):
            similarity = 1 - distance
            results.append((bids[idx], float(similarity)))

        return results


def get_request_data(request_id):
    session = Session()

    query = text_sql("""
    SELECT 
        sr.id,
        sr.description,
        sr.deliverables,
        cat.name as category,
        GROUP_CONCAT(DISTINCT s.name SEPARATOR ', ') as skills
    FROM service_request sr
    JOIN company_profile cp ON sr.company_profile_id = cp.id
    LEFT JOIN category cat ON sr.category_id = cat.id
    LEFT JOIN company_skills cs ON cp.id = cs.company_id
    LEFT JOIN skills s ON cs.skill_id = s.id
    WHERE sr.id = :request_id
    GROUP BY sr.id, cat.name
    """)

    result = session.execute(query, {"request_id": request_id}).first()
    session.close()

    if not result:
        return None

    return {
        'category': result.category or '',
        'description': result.description or '',
        'deliverables': result.deliverables or '',
        'skills': result.skills or ''
    }


def get_bids_for_request(request_id):
    session = Session()

    query = text_sql("""
    SELECT 
        sb.id as bid_id,
        sb.description,
        sb.deliverables,
        sb.notes,
        sb.estimated_hours,
        sb.token_amount,
        sb.payment_method,
        bc.name as company_name,
        bc.industry,
        GROUP_CONCAT(DISTINCT s.name SEPARATOR ', ') as skills
    FROM service_bid sb
    JOIN company_profile bc ON sb.company_profile_id = bc.id
    LEFT JOIN company_skills cs ON bc.id = cs.company_id
    LEFT JOIN skills s ON cs.skill_id = s.id
    WHERE sb.service_request_id = :request_id
    AND sb.status = 'PENDING'
    GROUP BY sb.id
    """)

    result = session.execute(query, {"request_id": request_id}).fetchall()
    session.close()

    bids = []
    for row in result:
        bids.append({
            'bid_id': row.bid_id,
            'category': '',
            'description': row.description or '',
            'deliverables': row.deliverables or '',
            'notes': row.notes or '',
            'estimated_hours': float(row.estimated_hours or 0),
            'token_amount': float(row.token_amount or 0),
            'payment_method': row.payment_method or '',
            'company_name': row.company_name or '',
            'industry': row.industry or '',
            'skills': row.skills or ''
        })

    return bids


def load_pdf_mupdf(file_path):
    doc = fitz.open(file_path)
    documents = []

    for page_num in range(len(doc)):
        page = doc.load_page(page_num)
        text = page.get_text()
        if text.strip():
            metadata = {"source": file_path, "page": page_num}
            documents.append(Document(page_content=text, metadata=metadata))

    doc.close()
    return documents


def split_documents(documents):
    splitter = RecursiveCharacterTextSplitter(chunk_size=constants.chunk_size, chunk_overlap=constants.chunk_overlap)
    return splitter.split_documents(documents)


def create_or_get_chroma_client(persist_dir):
    os.makedirs(persist_dir, exist_ok=True)
    client = chromadb.PersistentClient(path=persist_dir, settings=CHROMA_SETTINGS)
    try:
        collection = client.get_collection("documents")
    except Exception:
        collection = client.create_collection("documents")
    return client, collection


def does_vectorstore_exist(persist_dir, embeddings, chroma_client):
    try:
        db = Chroma(persist_directory=persist_dir, embedding_function=embeddings, client_settings=CHROMA_SETTINGS,
                    client=chroma_client)
    except Exception:
        return False
    if not db.get()['documents']:
        return False
    return True


def ingest_files_to_chroma(file_paths):
    all_documents = []

    loader = load_pdf_mupdf
    persist_dir = persist_directory_pdf

    for path in file_paths:
        docs = loader(path)
        all_documents.extend(docs)

    chunks = all_documents

    chroma_client, _ = create_or_get_chroma_client(persist_dir)

    if does_vectorstore_exist(persist_dir, embeddings, chroma_client):
        print(f"appending to existing vectorstore at {persist_dir}")
        db = Chroma(persist_directory=persist_dir, embedding_function=embeddings, client_settings=CHROMA_SETTINGS,
                    client=chroma_client)
        db.add_documents(chunks)
    else:
        print(f"creating new vectorstore at {persist_dir}")
        db = Chroma.from_documents(documents=chunks, embedding=embeddings, persist_directory=persist_dir,
                                   client_settings=CHROMA_SETTINGS, client=chroma_client)

    print(f"ingestion complete\ttotal chunks: {len(chunks)}")
    return db


def get_vectorstore():
    persist_dir = persist_directory_pdf

    chroma_client = chromadb.PersistentClient(settings=CHROMA_SETTINGS, path=persist_dir)
    if does_vectorstore_exist(persist_dir, embeddings, chroma_client):
        return True, Chroma(persist_directory=persist_dir, embedding_function=embeddings,
                            client_settings=CHROMA_SETTINGS,
                            client=chroma_client)
    return False, None

#
ingest_files_to_chroma([pdf_file_paths])
exists, db = get_vectorstore()
#
# ingest_files_to_chroma([], file_type='sql')
# exists2, db2 = get_vectorstore(db_type='sql')