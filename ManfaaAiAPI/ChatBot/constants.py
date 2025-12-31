import os
from pathlib import Path

from dotenv import load_dotenv
from chromadb.config import Settings
from langchain_core.prompts import PromptTemplate
import chromadb
from langchain_huggingface import HuggingFaceEmbeddings

load_dotenv()
PROJECT_ROOT = Path(__file__).resolve().parent.parent

persist_directory = os.path.join(PROJECT_ROOT, os.environ.get('PERSIST_DIRECTORY'))


source_directory = os.environ.get('SOURCE_DIRECTORY', 'source_documents')
embeddings_id = os.environ.get('EMBEDDINGS_ID')
api_key = os.environ.get("API_KEY")

chunk_size = 1000
chunk_overlap = 100
embeddings = HuggingFaceEmbeddings(model_name=embeddings_id)
MODEL = os.getenv('MODEL_ID')


if persist_directory is None:
    raise Exception("Please set the PERSIST_DIRECTORY environment variable")

CHROMA_SETTINGS = Settings(
    anonymized_telemetry=False
)

prompt = PromptTemplate(
    template="""You are an assistant for question-answering tasks.
    Use the following documents to answer the question.
    If you don't know the answer, just say that you don't know.
    keep the answer concise:
    Question: {question}
    Documents: {documents}
    Answer:
    """,
    input_variables=["question", "documents"],
)
skill_recommendation_prompt = PromptTemplate(
    template="""
You are a skill assignment advisor for projects.

Project and Skills Context:
{documents}

TASK:
- Identify 1 to 5 essential skills required for the project.
- Only select skills that exist in the company's skill database.

OUTPUT RULES:
- Do NOT use Markdown or code fences.
- Do NOT wrap JSON objects or arrays inside strings.
- All nested structures must be real JSON objects or arrays.
- Do NOT include explanations, comments, or extra text.
- Arrays must be JSON arrays, not stringified arrays.
- Objects must be JSON objects, not stringified objects.
- Output ONLY a JSON array.
- Return an empty array [] if no skills are found.
- Do not wrap the array in any key or string.

OUTPUT FORMAT:
[
  {{
    "skill_id": "<string>",
    "priority": "High | Medium | Low",
    "justification": "<short explanation referencing project requirements>"
  }}
]
""",
    input_variables=["documents"],
)

employee_matching_prompt = PromptTemplate(
    template="""You are a project staffing advisor matching employees to projects.

Context: {documents}


CRITICAL: Look at the employee's "Current Project" field. If it matches the project being staffed, DO NOT recommend that employee.

Analyze ONLY AVAILABLE employees or employees on OTHER projects.

Provide top 5 matches as a JSON array with objects containing the following keys exactly:
[
  {{
    "employee_name": "<Employee Name>",
    "employee_username": "<id>",
    "match_score": <integer between 0 and 100>,
    "current_assignment": "<Available or Assigned to: Other Project Name>",
    "matching_skills": ["Skill1", "Skill2", "Skill3"],
    "missing_skills": ["Skill1", "Skill2"] or null,
    "fit": "<One sentence why they're a good match>"
  }}
]

Rules:
- EXCLUDE any employee whose "Current Project" matches the project description
- Only recommend employees who are Available or on different projects
- Rank by skill match and availability
- Return ONLY valid JSON, no markdown or extra text
- Do NOT wrap the response in ``` or ```json.
- Limit to top 5 matches


Return a valid JSON array only with no explanations.
""",
    input_variables=["documents"],
)


employee_info_prompt = PromptTemplate(
    template="""
You are an HR assistant.
- Do NOT use Markdown or code fences.
- Do NOT wrap JSON objects or arrays inside strings.
- All nested structures must be real JSON objects or arrays.
- Do NOT include explanations, comments, or extra text.
- Arrays must be JSON arrays, not stringified arrays.
- Objects must be JSON objects, not stringified objects.
- Keys and string values must use double quotes (").
- Do NOT use single quotes (').
Given these employee documents:

{employee_docs_text}

Extract the following JSON object with keys exactly:
{{
  "employee_profile_name": "<full name>",
  "employee_username": "<id>",
  "current_skills": "<comma-separated skill_ids or names>"
}}

Return ONLY the JSON object with no explanations.
"""
,input_variables=["employee_docs_text"])

skill_emp_recommendation_prompt = PromptTemplate(

    template="""
You are a career advisor.

Given the employee info:

{employee_info_json}

and these active projects:

{project_docs_text}

and the available skills:

{skill_docs_text}

Recommend 1 to 5 new skills the employee does NOT currently have.  
- Do NOT use Markdown or code fences.
- Do NOT wrap JSON objects or arrays inside strings.
- All nested structures must be real JSON objects or arrays.
- Do NOT include explanations, comments, or extra text.
- Arrays must be JSON arrays, not stringified arrays.
- Objects must be JSON objects, not stringified objects.
- Output ONLY a JSON array.
- Keys and string values must use double quotes (").
Return JSON with:

{{
  [
    {{
      "skill_id": "<skill_id>",
      "priority": "High | Medium | Low",
      "justification": "<one sentence referencing active project names>"
    }}
  ]
}}

Return ONLY JSON, no extra text.
"""
,input_variables=["employee_info_json", "project_docs_text", "skill_docs_text"])
training_recommendation_prompt = PromptTemplate(

    template="""
You are a career advisor.

Given these recommended skills:

{recommended_skills_json}

and these training programs:

{training_docs_text}

Recommend 1 to 5 training programs that teach the recommended skills.
- Do NOT use Markdown or code fences.
- Do NOT wrap JSON objects or arrays inside strings.
- All nested structures must be real JSON objects or arrays.
- Do NOT include explanations, comments, or extra text.
- Arrays must be JSON arrays, not stringified arrays.
- Objects must be JSON objects, not stringified objects.
- Keys and string values must use double quotes (").
- Do NOT use single quotes (').
Return JSON with:

{{
  [
    {{
      "training_name": "<training name>",
      "teaches_skill_id": "<skill_id>",
      "benefit": "<one sentence referencing project names>"
    }}
  ]
}}

Return ONLY JSON.
"""
, input_variables=["recommended_skills_json", "training_docs_text"])


chroma_client = chromadb.PersistentClient(path=persist_directory, settings=CHROMA_SETTINGS)

TASK_HOUR_ESTIMATION_PROMPT = PromptTemplate(
    input_variables=[
        "category",
        "description",
        "deliverables",
    ],
    template="""
You are a senior operations consultant specializing in estimating
how many hours professional business services take.

You must follow these rules strictly:
- Output VALID JSON only
- Do NOT include markdown
- Do NOT include text outside JSON
- Be conservative and realistic
- Assume a competent professional (not junior, not elite)
- If details are missing, make reasonable assumptions and state them

Task Details:
Service Category: {category}
Task Description: {description}
Expected Deliverables: {deliverables}

Return the estimate using EXACTLY this JSON format:

{{
  "estimated_hours": number,
  "min_hours": number,
  "max_hours": number,
  "confidence": "LOW | MEDIUM | HIGH",
  "assumptions": string
}}
"""
)