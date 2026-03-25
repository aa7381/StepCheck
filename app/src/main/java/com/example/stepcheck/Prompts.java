package com.example.stepcheck;

public class Prompts {
    public static final String SHOE_SCHEMA = "{\n" +
            "  \"type\": \"object\",\n" +
            "  \"properties\": {\n" +
            "    \"id\": {\n" +
            "      \"type\": \"string\",\n" +
            "      \"description\": \"The EXACT raw text decoded from the QR code. Look for a square pattern and read it.\"\n" +
            "    },\n" +
            "    \"shoe_name\": {\n" +
            "      \"type\": \"string\"\n" +
            "    },\n" +
            "    \"color\": {\n" +
            "      \"type\": \"string\"\n" +
            "    },\n" +
            "    \"type\": {\n" +
            "      \"type\": \"string\",\n" +
            "      \"description\": \"The type of shoe (e.g., Running, Casual, Sport, etc.)\"\n" +
            "    },\n" +
            "    \"price\": {\n" +
            "      \"type\": \"number\"\n" +
            "    },\n" +
            "    \"manufacturing_company\": {\n" +
            "      \"type\": \"string\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"required\": [\"id\", \"shoe_name\", \"color\", \"type\", \"price\", \"manufacturing_company\"]\n" +
            "}";

    public static final String SHOE_PROMPT =
            "You are a professional QR scanner and shoe expert.\n\n" +
            "IMAGE ANALYSIS RULES:\n" +
            "1. Scan the image for a QR code. You MUST decode the QR code and return its literal string content in the 'id' field. This is your primary goal.\n" +
            "2. Identify the shoe: name, brand, color, and specific shoe type.\n\n" +
            "OUTPUT RULES:\n" +
            "- Return ONLY raw JSON.\n" +
            "- NO placeholder data like '12345'.\n" +
            "- If you cannot find a QR code, return ONLY the word ERROR.\n\n" +
            "JSON Schema: " + SHOE_SCHEMA;
}
