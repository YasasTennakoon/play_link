# Play Link Application

A Spring Boot REST API application for managing a play link inventory.

## Prerequisites

Before running this application, ensure you have the following installed:

- **Java 22** (or higher)
- **MongoDB 7.0.11** (or compatible version)
- **Maven** (included via Maven Wrapper)

## Local Setup and Running

### 1. Start MongoDB

First, start the MongoDB server:

```bash
# Create data directory if it doesn't exist
mkdir -p ~/data/db

# Start MongoDB
/Users/yasastennakoon/mongodb-macos-aarch64-7.0.11/bin/mongod --dbpath ~/data/db
```

MongoDB will run on the default port `27017`.

### 2. Run the Application

In a new terminal, navigate to the project directory and run:

```bash
# Using Maven Wrapper (recommended)
./mvnw spring-boot:run
```

The application will start on **http://localhost:8080**

## Project Structure

```
play_link/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/play_link/
│   │   │       ├── api/              # REST Controllers
│   │   │       ├── application/      # Service Layer
│   │   │       ├── domain/           # Domain Models & Repositories
│   │   │       └── infrastructure/   # Infrastructure Components
│   │   └── resources/
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## Technology Stack

- **Spring Boot 3.3.0**
- **Spring Data MongoDB**
- **Spring Web**
- **Lombok 1.18.30**
- **Java 22**
- **MongoDB 7.0.11**

## Troubleshooting

### Port 8080 already in use
```bash
# Kill the process using port 8080
lsof -ti:8080 | xargs kill -9
```

### MongoDB connection issues
- Ensure MongoDB is running before starting the application
- Check that MongoDB is listening on `localhost:27017`
- Verify the data directory has proper permissions

### Java version mismatch
```bash
# Check your Java version
java -version

# Should be Java 22 or higher
```

## API Endpoints

The application provides REST endpoints for managing shoes. Check the `ShoeController` for available endpoints.

## Development

To build the project:
```bash
./mvnw clean install
```

To run tests:
```bash
./mvnw test
```
