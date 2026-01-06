# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
Yes, I would refactor certain aspects of the database access layer to improve consistency and maintainability:

1. **Consistency in Data Access Patterns**: The codebase uses different patterns for database access. Some areas use JPA entities with Panache (like Store), while others use custom repository implementations. I would standardize on a consistent approach, likely using Panache entities or repositories throughout for better maintainability.

2. **Domain Model Separation**: Currently, there's inconsistency in how domain models are handled. Some services directly manipulate JPA entities, while others have separate domain models. I would implement a clear separation between persistence models and domain models to enforce better encapsulation and maintainability.

3. **Exception Handling**: The exception handling is inconsistent. Some parts throw generic RuntimeExceptions while others have custom exceptions. I would implement a consistent exception hierarchy throughout the application.

4. **Repository Pattern**: I would ensure all data access follows the repository pattern consistently, with clear contracts defined in interfaces, making the code more testable and maintainable.

5. **Transaction Management**: The transaction management could be improved by using more declarative approaches and ensuring proper isolation levels are used consistently.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
Both approaches have their merits, and the choice depends on the specific context and requirements:

**OpenAPI-first approach (Warehouse API)**:
Pros:
- Ensures contract-first development, leading to better API design
- Automatic client SDK generation for different languages
- Clear documentation that stays in sync with implementation
- Better collaboration between frontend and backend teams
- Early validation of API design decisions

Cons:
- More overhead in initial setup and maintenance
- Less flexibility for rapid prototyping
- Learning curve for team members unfamiliar with OpenAPI
- Potential disconnect between specification and actual implementation if not carefully managed

**Code-first approach (Product and Store APIs)**:
Pros:
- Faster development for simple APIs
- More flexibility during development
- Familiar to most Java developers
- Less ceremony and overhead

Cons:
- Documentation can become outdated
- API design might be less consistent
- Client SDK generation is more difficult
- Potential for breaking changes without proper versioning

My preference would be to use the OpenAPI-first approach for public APIs or when working in a microservices environment where multiple teams depend on the API. For internal APIs or rapid prototyping, the code-first approach might be acceptable. In a mature codebase like this, having consistent API development practices across all endpoints would be beneficial for maintainability.
```