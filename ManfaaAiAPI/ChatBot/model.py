import json

from langchain_core.runnables import RunnablePassthrough

import ChatBot.ingest as ingest
import ChatBot.constants as constants
from langchain_groq import ChatGroq
from langchain_core.output_parsers import StrOutputParser

pdf_exists, pdf_db = ingest.get_vectorstore()

pdf_retriever = pdf_db.as_retriever(search_kwargs={"k": 4}) if pdf_exists else None

llm = ChatGroq(
    model="llama-3.1-8b-instant",
    groq_api_key=constants.api_key
)


rag_chain = (
    {
        "question": RunnablePassthrough(),
        "documents": lambda x: "\n".join([doc.page_content for doc in pdf_retriever.invoke(x)])
    }
    | constants.prompt | llm | StrOutputParser())

hours_chain = (constants.TASK_HOUR_ESTIMATION_PROMPT | llm | StrOutputParser())


class RAGApplication:
    def __init__(self, rag_chain):
        self.rag_chain = rag_chain

    def run(self, question):
        return self.rag_chain.invoke(question)

