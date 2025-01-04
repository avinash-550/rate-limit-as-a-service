# Rate-limit-as-a-service AKA Raas
A dedicated microservice for rate limiting to check if users are within limits.

---

## Features

- **User-Specific Rate Limiting**: Limits requests per user per API endpoint.
- **Redis Integration**: Uses Redis as a fast and scalable key-value store.
- **Time Window Control**: Enforces rate limits within a defined time window.
- **HTTP 429 Response**: Returns a "Too Many Requests" response when limits are exceeded.
- **Configurable Limits**: Easily adjust request limits and time windows.

---

## Requirements

- **Java 17**
- **Spring Boot 3.4.1**
- **Redis** (Installed locally or accessible via Docker)

---

## Setup and Installation

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/api-rate-limiter.git
cd api-rate-limiter
```
### 2. Build and Run the Docker-Compose file

```bash
docker-compose up --build
```
## API Design

### **Signup**
- **Method**: `GET`
- **URL**: `/api/{urlPath}`
- **Header**: User-Id (Required)
- **Description**: verifies rate limit for the user
- **Response(s)**:
  ```
    200 OK: Request successful.
  ```
  ```
    429 Too Many Requests: Request limit exceeded.
  ```
## How It Works

1. **Key Generation**:  
   Each user + `urlPath` combination generates a unique Redis key (e.g., `user123:test-endpoint`).

2. **Request Tracking**:
   - If the key does not exist, it is created with a count of `1` and an expiration time.
   - If the key exists, the counter is incremented.

3. **Rate Limiting**:
   - If the counter exceeds the maximum allowed requests, the API returns an HTTP 429 response.

4. **Reset**:
   - After the expiration time (e.g., 60 seconds), the Redis key is automatically removed, resetting the counter.

---

## To Do

- [ ] Add support for dynamic rate limits per user or endpoint.
- [ ] Implement a sliding window algorithm for finer-grained limits.
- [ ] Add unit tests for edge cases.
- [ ] Dockerize the application for easier deployment.

