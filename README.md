# Bank Application

This is a bank management API project built with Spring Boot. It includes endpoints to create accounts, check balances, make deposits, and withdrawals. The application uses an H2 database (file mode) for data persistence and Flyway for database migrations.

## Technologies Used

- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **Spring Security** (for basic authentication)
- **H2 Database** (in file mode for persistence)
- **Flyway** (for database migrations)
- **Docker and Docker Compose**

## Setup and Execution

### Prerequisites

- **Docker** and **Docker Compose** installed
- (Optional) **Maven** to run locally without Docker

### Steps to Run with Docker

1. **Clone the repository**:

    ```bash
    git clone git@github.com:laisbento/bank.git
    cd bank
    ```

2. **Build and start the Docker container** (using multi-stage build in Dockerfile):

    ```bash
    docker-compose up --build
    ```

3. **Access the application**:

   The application will be available at `http://localhost:8080`.

4. **Access the H2 Console**:

   The H2 console can be accessed at `http://localhost:8080/h2-console`. Use the following credentials:

    - **JDBC URL**: `jdbc:h2:file:/data/h2/testdb`
    - **Username**: `admin`
    - **Password**: `password`

### Running Locally (without Docker)

1. Ensure **Maven** is installed on your machine.
2. **Build the project**:

    ```bash
    mvn clean package
    ```

3. **Run the application**:

    ```bash
    mvn spring-boot:run
    ```

4. **Access the application** at `http://localhost:8080` and the H2 console at `http://localhost:8080/h2-console`.

## API Endpoints

### Authentication

Basic authentication is enabled. Use the following default credentials:

- **Username**: `admin`
- **Password**: `password`

### Available Endpoints

#### Account Management

- **POST /account** - Create a new account
- **GET /account/{iban}/balance** - Retrieve account balance
- **POST /account/{iban}/deposit** - Deposit an amount into an account
- **POST /account/{iban}/withdraw** - Withdraw an amount from an account

### Swagger Documentation

The API documentation is available at `http://localhost:8080/swagger-ui/index.html`.

## Database Migration

Flyway is configured to handle database migrations. The migration scripts are located in `src/main/resources/db/migration`.

## Development Notes

- **Multi-Stage Docker Build**: The Dockerfile uses a multi-stage build to compile the application and create a final image with only the JAR file, reducing the final image size.
- **Persistence**: The H2 database is used in file mode to ensure data is persistent between container restarts.