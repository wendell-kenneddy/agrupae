# Agrupaê API (Backend)

This is the backend application for Agrupaê, a system to manage academic group assignments. It is built with Java 21, Spring Boot, and PostgreSQL.

## Prerequisites

Before you begin, ensure you have the following installed on your machine:
- **Java 21**
- **PostgreSQL**
- **Maven** (optional, you can use the provided `./mvnw` wrapper)

## Database Setup

1. Make sure you have a running instance of PostgreSQL.
2. By default, the application is configured to connect to:
   - **Host:** `localhost`
   - **Port:** `6543`
   - **Database Name:** `agrupae`
   - **Username:** `admin`
   - **Password:** `admin`

If your local database configuration differs, you can override these defaults by setting the following environment variables:
- `DATABASE_URL` (e.g., `jdbc:postgresql://localhost:5432/agrupae`)
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`

## Flyway Setup & Migrations

The application uses Flyway for database migrations. You can run migrations either via the Maven plugin or automatically on application startup.

### Setting up `flyway.conf` (for Maven Plugin)

1. Copy the example configuration file:
   ```bash
   cp flyway.conf.example flyway.conf
   ```
2. Edit `flyway.conf` to match your local database credentials:
   ```properties
   flyway.url=jdbc:postgresql://localhost:6543/agrupae
   flyway.user=admin
   flyway.password=admin
   flyway.validateMigrationNaming=true
   ```

### Running Migrations Manually
To execute pending migrations using the Maven wrapper, run:
```bash
./mvnw flyway:migrate
```

*(Note: Spring Boot will also attempt to run pending Flyway migrations automatically when you start the application, using the database configuration from `application.properties` or your environment variables).*

## Security and JWT Setup

This project uses an OAuth2 Resource Server configuration with RSA key pairs for signing and verifying JWTs.

For security reasons, RSA keys are not provided by default in the repository. You must generate your own private and public keys before running the application.

### Generating RSA Keys

1. Navigate to the `certs` directory (create it if it doesn't exist):
   ```bash
   mkdir -p src/main/resources/certs
   cd src/main/resources/certs
   ```
2. Generate the private key (`private.pem`):
   ```bash
   openssl genpkey -algorithm RSA -out private.pem -pkeyopt rsa_keygen_bits:2048
   ```
3. Generate the corresponding public key (`public.pem`):
   ```bash
   openssl rsa -pubout -in private.pem -out public.pem
   ```

By default, the application looks for `private.pem` and `public.pem` in `src/main/resources/certs/`. You can override these locations by setting the `PRIVATE_KEY` and `PUBLIC_KEY` environment variables.

## Running the Application Locally

1. Open a terminal and navigate to the `backend` directory.
2. Run the application using the Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start, connect to the database, apply any pending Flyway migrations (if not already applied), and be ready to accept requests.

## Testing

To run the automated tests, execute:
```bash
./mvnw test
```
