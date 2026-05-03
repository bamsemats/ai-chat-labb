# AI-Integrated Spring Boot Middleware

Laboration 1: Middleware service acting as a bridge between clients and Large Language Models (LLMs).

## 🚀 Overview
This application is a Spring Boot-based middleware that allows users to interact with AI models (via OpenRouter) with added features like **personalities** and **conversation memory**. It is designed to be resilient, secure, and easy to test.

## ✨ Features
- **RESTful API:** Clean JSON interface for chat interactions.
- **Personalities:** Support for multiple AI personas:
  - `coder`: Concise technical expert.
  - `poet`: Creative and rhyming.
  - `pirate`: Salty sea dog with a focus on treasure.
  - `helper`: Patient and clear assistant.
- **Conversation Memory:** In-memory FIFO storage that remembers the last 10 messages per session.
- **Resilience:** Automatic retry logic with exponential backoff for transient AI provider errors.
- **Interactive Documentation:** Fully integrated Swagger UI for testing.

## 🛠️ Tech Stack
- **Java 21/26**
- **Spring Boot 3.4.1**
- **Spring Web** (RestClient for HTTP communication)
- **Spring Retry** (Resilience)
- **Springdoc-OpenAPI** (Swagger UI)
- **Mockito & MockMvc** (Testing)

## ⚙️ Setup & Configuration

### 1. Prerequisites
- JDK 21 or higher.
- An API Key from [OpenRouter](https://openrouter.ai/).

### 2. Environment Variables
The application requires an OpenRouter API key to function. Set the following environment variable in your IDE (IntelliJ) or Terminal:

```bash
OPENAI_API_KEY=sk-or-v1-...your-key-here...
```

### 3. Optional Properties
You can override default settings in `src/main/resources/application.properties`:
- `openai.api.url`: Change the LLM provider (Default: OpenRouter).
- `openai.model`: Change the model (Default: `google/gemini-2.0-flash-001`).

## 🚀 Running the Application

### Via IDE (Recommended)
1. Open the project in IntelliJ.
2. Edit your **Run Configuration** and add the `OPENAI_API_KEY` environment variable.
3. Click the **Run** button.

### Via Command Line
```bash
$env:OPENAI_API_KEY="your_key"
./mvnw spring-boot:run
```

## 🧪 Testing the API

### Swagger UI
Once the app is running, access the interactive test interface at:
👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

### API Endpoints

#### 1. Chat Interaction
**POST** `/api/v1/chat`
```json
{
  "personality": "pirate",
  "message": "What is the best way to store gold?",
  "sessionId": "user-unique-id"
}
```

#### 2. View History
**GET** `/api/v1/chat/history/{sessionId}`
Returns the current memory state for the specific session.

## 🧪 Automated Tests
Run the test suite using Maven:
```bash
./mvnw test
```

## ⚠️ Troubleshooting (Java 26 Notes)
If you encounter 500 errors on the `/v3/api-docs` endpoint while using Java 26, ensure the `GlobalExceptionHandler` is annotated with `@io.swagger.v3.oas.annotations.Hidden` to prevent introspection crashes during documentation generation.
