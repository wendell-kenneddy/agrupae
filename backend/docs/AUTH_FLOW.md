# Authentication Flow

This document describes how authentication works in the Agrupaê API and how a frontend client should interact with it.

---

## Overview

The API uses a **dual-token strategy**:

| Token | Format | Lifetime | Storage |
|---|---|---|---|
| Access token | RSA-signed JWT (RS256) | 15 minutes | In-memory (JS variable) |
| Refresh token | Opaque UUID string | 7 days | `HttpOnly` cookie (automatic) |

The **access token** is returned in the response body and must be stored in memory by the client. It is sent by the client on every authenticated request in the `Authorization` header.

The **refresh token** is set by the server via `Set-Cookie` and is scoped to the path `/auth`. Because it is `HttpOnly`, JavaScript cannot read it — the browser sends it automatically whenever the client hits any `/auth/*` endpoint. The client never needs to handle it directly.

---

## Cookie attributes

The `refresh-token` cookie is always set with the following attributes:

```
Set-Cookie: refresh-token=<token>; HttpOnly; Secure; SameSite=Strict; Path=/auth; Max-Age=604800
```

- **HttpOnly** — not accessible from JavaScript
- **Secure** — only sent over HTTPS
- **SameSite=Strict** — not sent on cross-site requests (CSRF protection)
- **Path=/auth** — only sent to `/auth/*` endpoints

---

## JWT claims

The access token payload contains:

```json
{
  "iss": "agrupae",
  "sub": "<user-uuid>",
  "role": "USER" | "ADMIN",
  "iat": <unix-timestamp>,
  "exp": <unix-timestamp>
}
```

The client can decode the JWT (without verifying the signature) to read `sub` and `role` for UI purposes. Verification is done server-side.

---

## Endpoints

All request bodies are JSON (`Content-Type: application/json`). All error responses return a plain-text string body.

### `POST /auth/signup`

Register a new account. Automatically logs the user in.

**Request body:**
```json
{
  "name": "Alice",
  "email": "alice@example.com",
  "password": "secret"
}
```

**Success — `200 OK`:**
- Body: access token string (plain text)
- Header: `Set-Cookie: refresh-token=...`

**Errors:**
- `409 Conflict` — email already in use

---

### `POST /auth/login`

Authenticate with existing credentials.

**Request body:**
```json
{
  "email": "alice@example.com",
  "password": "secret"
}
```

**Success — `200 OK`:**
- Body: access token string (plain text)
- Header: `Set-Cookie: refresh-token=...`

**Errors:**
- `401 Unauthorized` — invalid email or password

---

### `POST /auth/refresh`

Exchange the current refresh token for a new access token + refresh token pair. No request body needed — the browser sends the cookie automatically.

**Success — `200 OK`:**
- Body: new access token string (plain text)
- Header: `Set-Cookie: refresh-token=...` (new token, old one is invalidated)

**Errors:**
- `401 Unauthorized` — token not found, already used, or expired
- `401 Unauthorized` — `refresh-token` cookie missing

> **Token rotation:** every successful refresh invalidates the previous refresh token and issues a new one. If a refresh token is used more than once (replay attack), the server detects the reuse and **invalidates the entire token family** (all tokens tied to that login session), forcing a full re-login.

---

### `POST /auth/logout`

Revoke the current session. No request body needed.

**Success — `200 OK`:**
- Header: `Set-Cookie: refresh-token=; Max-Age=0` (cookie cleared)
- All tokens in the same login family are revoked server-side

**Errors:**
- `401 Unauthorized` — token not found or cookie missing

---

### Authenticated requests (all other routes)

Every request to a protected route must include the access token:

```
Authorization: Bearer <access-token>
```

**Errors:**
- `401 Unauthorized` — token missing, expired, or invalid

---

## Recommended client flow

### On signup / login

```
1. POST /auth/signup  or  POST /auth/login
2. Store the access token in memory (e.g. a module-level variable or React state)
3. The browser stores the refresh-token cookie automatically
```

Do **not** store the access token in `localStorage` or `sessionStorage` — those are vulnerable to XSS. Keep it in memory only.

### On every API request

```
1. Attach Authorization: Bearer <access-token> to the request
2. If the response is 401:
   a. POST /auth/refresh (no body needed — cookie is sent automatically)
   b. If refresh succeeds: store the new access token, retry the original request
   c. If refresh fails (401): clear the in-memory token, redirect to login
```

### On logout

```
1. POST /auth/logout
2. Clear the in-memory access token
3. Redirect to login
```

### On app load / page refresh

Because the access token lives in memory, it is lost on page reload. On startup:

```
1. Attempt POST /auth/refresh immediately
2. If it succeeds: store the new access token and continue as authenticated
3. If it fails: treat the user as logged out and show the login screen
```

---

## Flow diagrams

### Signup / Login

```
Client                              Server
  |                                   |
  |-- POST /auth/signup or /login --> |
  |                                   | Validates credentials
  |                                   | Creates refresh token (stores hash in DB)
  |                                   | Issues JWT access token
  |<-- 200 OK                         |
  |    Body: <access-token>           |
  |    Set-Cookie: refresh-token=...  |
  |                                   |
  | [store access token in memory]    |
```

### Authenticated request with silent refresh

```
Client                              Server
  |                                   |
  |-- GET /some-resource              |
  |   Authorization: Bearer <at> --> |
  |<-- 401 Unauthorized               | (access token expired)
  |                                   |
  |-- POST /auth/refresh -----------> |
  |   [cookie sent automatically]     | Verifies refresh token
  |                                   | Revokes old, issues new refresh token
  |                                   | Issues new JWT
  |<-- 200 OK                         |
  |    Body: <new-access-token>       |
  |    Set-Cookie: refresh-token=...  |
  |                                   |
  |-- GET /some-resource (retry)      |
  |   Authorization: Bearer <new-at>  |
  |<-- 200 OK                         |
```

### Token reuse detection (replay attack)

```
Client                              Server
  |                                   |
  |-- POST /auth/refresh -----------> |
  |   [stolen/already-used cookie]    | Finds token is already revoked
  |                                   | Revokes ENTIRE token family
  |<-- 401 Unauthorized               |
  |                                   |
  | [clear access token, go to login] |
```
