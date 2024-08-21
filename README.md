# Shift booking

## API

### `/api/shifts/*` Endpoints

| **HTTP Method** | **Endpoint**          | **Description**                                 | **Request Body**                              | **Response**                                 | **Authorization**  |
|-----------------|-----------------------|-------------------------------------------------|----------------------------------------------|---------------------------------------------|-------------------|
| GET             | `/api/shifts`         | Retrieve a list of all shifts.                  | N/A                                          | List of Shift objects.                      | `USER` or `ADMIN`  |
| GET             | `/api/shifts/{id}`    | Retrieve a shift by its ID.                     | N/A                                          | Shift object.                               | `USER` or `ADMIN`  |
| POST            | `/api/shifts`         | Create a new shift.                             | JSON payload with shift details.             | Created Shift object.                      | `ADMIN`            |
| PUT             | `/api/shifts/{id}`    | Update an existing shift by its ID.             | JSON payload with updated shift details.     | Updated Shift object.                      | `ADMIN`            |
| DELETE          | `/api/shifts/{id}`    | Delete a shift by its ID.                       | N/A                                          | Success/Failure message.                   | `ADMIN`            |

### `/api/auth/*` Endpoints

| **HTTP Method** | **Endpoint**                | **Description**                                 | **Request Body**                              | **Response**                                 | **Authorization**  |
|-----------------|-----------------------------|-------------------------------------------------|----------------------------------------------|---------------------------------------------|-------------------|
| POST            | `/api/auth/register`        | Register a new regular user.                    | JSON payload with user details.              | JWT token in response body.                 | `ANONYMOUS`        |
| POST            | `/api/auth/login`           | Authenticate a user and generate a JWT token.   | JSON payload with username and password.     | JWT token in response body.                 | `ANONYMOUS`        |

### Additional Notes:

1. **Authentication & Authorization:**
    - Endpoints that require `USER` or `ADMIN` roles need proper JWT tokens for authentication.
    - **JWT Authentication:** Once a user logs in via `/api/auth/login`, a JWT token is generated. This token must be included in the `Authorization` header (e.g., `Authorization: Bearer {jwt_token}`) for protected endpoints.

2. **JWT Token Creation (`/api/auth/login`):**
    - The login endpoint accepts a username and password and returns a JWT token if the credentials are valid.
    - The token can then be used to authorize access to protected routes, such as `POST`, `PUT`, and `DELETE` requests to `/api/shifts/*`.
