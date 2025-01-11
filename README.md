# API Documentation

## Location of Documentation
The complete API documentation is available in the `swagger.yaml` file, located in:
\TransPerfect task\springboot-test-task-polishing\src\main\resources

## Service Setup
The `TextPolishingService` is hosted on port 8082, as defined in the `application.properties` file:

`server.port = 8082`

For testing communication between this service and the Proofreading Service, an improvised Proofreading Service is hosted on port 8081. The base URL for this service is defined in application.properties as:
`proofread.baseurl = http://localhost:8081`

# API Details
## Endpoint: POST /polish
**Workflow**
1. PolishTextController:

- Receives a request at the POST /polish endpoint with a PolishingRequestDto containing the fields:
language,
domain,
content
Invokes the `polishText` method of the `PolishTextService`.

2. PolishTextService:

- Validates the request (details below).
- If validation passes:
  - The request is forwarded to the Proofreading Service (POST /proofread endpoint).
  - The content returned by the Proofreading Service is processed and wrapped into a PolishingResponseDto, which includes:
    - Polished Content: The revised text.
    - Similarity Score: A measure of similarity between the original and polished content, calculated using the Jaro-Winkler Similarity algorithm.
  - The PolishingResponseDto is then returned to the controller.
- If validation fails:
  - An exception is thrown, returning a "Bad Request" error to the controller.
# Similarity Calculation
- The similarity score is calculated using the JaroWinklerSimilarity algorithm from the Apache Commons Text library, which returns a value between 0 and 1.
- Before calculating similarity:
  - Tags are removed from the original content using the removeTags method (uses regex).
- The similarity score is rounded to two decimal places before being included in the response.
# Validation Details
### Validation Components
**Language and Domain Validation:**
- Utilizes the MockCacheService, a singleton class containing:
  - A list of valid languages.
  - A list of valid domains.
- Methods provided by MockCacheService:
  - Check if a given language is valid.
  - Check if a given domain is valid.
  - Update the valid languages and domains.
**Fetching Valid Languages and Domains:**
- The valid languages and domains are fetched daily from the Proofreading Service by the `CronJobService`.
- Schedule: Runs every day at 1:00 AM.
- Process:
  - Calls Proofreading Service APIs to fetch valid languages and domains.
  - Updates the MockCacheService arrays using its setter methods.
    Logs each fetch using log.info.
**Word Count Validation:**
Words in the `content` field are counted using:
`StringTokenizer(content).countTokens()`

**Note:** This method only counts words separated by white spaces.

- Room for Improvement: Implement a more sophisticated method to handle additional separators (e.g., punctuation).
- Excluding Tags: Tags could also be excluded from the content before word counting if required.

**Additional note:** for excluding tags, it expects the backslash sign before every quotation mark (e.g., <mark type=\”bold\” size=\”13\”/> instead of <mark type="bold" size="13"/>).

# Redis-based Validation (Optional Solution)
The Redis folder in the project contains configuration and service files that can also be used for validation purposes. This implementation is an additional way to solve the validation problem and could be considered an improvement in some use cases.

## RedisService
RedisService provides methods to store and retrieve data from Redis:

- setCache: Stores an array of strings (such as valid languages or domains) in Redis.
- findValueInRedis: Checks if a given value exists in the list stored under a specific key in Redis.
## Redis Validation Workflow
- The same validation process (for language and domain) that currently uses the MockCacheService can be implemented using Redis.
  - Instead of using an in-memory list, Redis can be used to store valid languages and domains.
  - RedisService can store and retrieve these values quickly, making the validation more efficient.
  - Redis also allows for persistence and faster lookups, especially beneficial for large datasets or high-frequency requests.
# Areas for Improvement
## 1. Word Count Accuracy:
- Enhance the word-counting logic to consider separators other than white spaces.
- Exclude tags from the content before counting words, if necessary.
## 2. Error Handling:
- Add more descriptive error messages for validation failures.
## 3. Redis as a Primary Cache:
You may want to consider switching to Redis as the primary validation store for improved performance and scalability in the long term.
## 4. Production-Readiness (Security Aspects):
- **JWT Authentication:** Implement JWT token-based authentication to ensure secure access to the API. Each request should include a valid JWT token in the authorization header.
- **Authorization:** Implement role-based access control (RBAC) to ensure that only authorized users can access sensitive endpoints, like the /polish endpoint.
- **Rate Limiting:** Implement rate limiting to prevent abuse and ensure that the service can handle a high number of requests without performance degradation.


