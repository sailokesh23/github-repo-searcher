#  GitHub Repository Searcher (Spring Boot Backend)

A Spring Boot backend that:
- Searches GitHub repositories using the GitHub REST API.
- Saves results to a local PostgreSQL database.
- Provides REST endpoints to search and retrieve repositories with filters.
- Follows clean layered architecture: Controller → Service → Repository → Entity → DTO.

## How to Run

### Requirements
- Java 17+  
- Maven (`mvn -v`)  
- PostgreSQL installed and running  
  - Example DB: `github_repo_searcher`
  - In `src/main/resources/application.properties`:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/github_repo_searcher
    spring.datasource.username=YOUR_DB_USERNAME
    spring.datasource.password=YOUR_DB_PASSWORD

    github.api.token=YOUR_GITHUB_PERSONAL_ACCESS_TOKEN
    ```
- Make sure your `.gitignore` excludes `application.properties`.

---

### 2️⃣ Build and Run

Run from terminal:
```bash
./mvnw spring-boot:run
```

Or run the `GithubSearcherApplication` class from your IDE.

- Starts on port **8080**
- Connects to GitHub with your personal access token.
- Uses HikariCP for database connection pooling.

---

## ✅ REST API Endpoints

### 🔍 `POST /api/github/search`

**Description:**  
Searches GitHub repositories by `query`, `language`, and `sort`. Saves the results in PostgreSQL.

**Request Body:**
```json
{
  "query": "spring boot",
  "language": "Java",
  "sort": "stars"
}
```

**Example curl:**
```bash
curl -X POST http://localhost:8080/api/github/search \
  -H "Content-Type: application/json" \
  -d '{"query":"spring boot","language":"Java","sort":"stars"}'
```

---

###  `GET /api/github/repositories`

**Description:**  
Retrieves stored repositories with optional filters.

**Query Parameters (optional):**
- `language` → Filter by language
- `minStars` → Filter by minimum stars
- `sort` → Sort by `stars`, `forks` or `updated`

**Example curl:**
```bash
curl "http://localhost:8080/api/github/repositories?language=Java&minStars=50&sort=stars"
```

---

##  Testing the API

You can test the endpoints using:
- Postman
- curl
- Any HTTP client

Example:
1. Use `POST` to save repos to the DB.
2. Use `GET` to retrieve them with filters.

---

##  Tests

- Includes JUnit tests for:
  - Controller (`GitHubRepositoryControllerTest`)
  - Service (`GitHubRepositoryServiceTest`)
- Run tests:
  ```bash
  ./mvnw test
  ```


---

##  Security Reminder

**Never push your real `application.properties`!**  
Keep it local and add `src/main/resources/application.properties` to your `.gitignore`.

---

## ✅ Ready to go!

Run, test, and share this backend confidently.  
Built with Spring Boot + PostgreSQL + clean design for interview readiness 🚀
