# PlayLink Authentication & Authorization Architecture

## 📋 Table of Contents
1. [Overview](#overview)
2. [Architecture Design](#architecture-design)
3. [Technology Stack](#technology-stack)
4. [Component Details](#component-details)
5. [Security Flow](#security-flow)
6. [API Endpoints](#api-endpoints)
7. [Database Schema](#database-schema)
8. [Testing Guide](#testing-guide)

---

## 🎯 Overview

PlayLink is a venue booking platform with a robust JWT-based authentication system. The application follows **Clean Architecture** principles with clear separation of concerns.

### Key Features
- ✅ JWT-based stateless authentication
- ✅ Role-based access control (RBAC)
- ✅ Password encryption with BCrypt
- ✅ Input validation
- ✅ MongoDB for data persistence
- ✅ Spring Security integration

---

## 🏗️ Architecture Design

### Layered Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      Presentation Layer                      │
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────────┐   │
│  │ AuthController│  │VenueController│  │  Other APIs     │   │
│  └──────────────┘  └──────────────┘  └─────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────┐
│                     Application Layer                        │
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────────┐   │
│  │  AuthService  │  │ VenueService │  │  Other Services │   │
│  └──────────────┘  └──────────────┘  └─────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────┐
│                       Domain Layer                           │
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────────┐   │
│  │     User      │  │    Venue     │  │  Other Entities │   │
│  └──────────────┘  └──────────────┘  └─────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────┐
│                   Infrastructure Layer                       │
│  ┌────────────────┐  ┌────────────────┐  ┌──────────────┐  │
│  │ UserRepository  │  │VenueRepository │  │ Security     │  │
│  │   (MongoDB)     │  │   (MongoDB)    │  │ Config       │  │
│  └────────────────┘  └────────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Technology Stack

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **Backend Framework** | Spring Boot 3.3.0 | Application framework |
| **Security** | Spring Security 6.x | Authentication & Authorization |
| **JWT** | JJWT 0.12.3 | Token generation/validation |
| **Database** | MongoDB 7.0.11 | NoSQL data storage |
| **ORM** | Spring Data MongoDB | Database access |
| **Validation** | Jakarta Validation | Input validation |
| **Build Tool** | Maven 3.9.7 | Dependency management |
| **Java Version** | Java 22 | Runtime environment |

---

## 📦 Component Details

### 1. Domain Layer (`com.example.play_link.domain`)

#### User Entity
```java
@Document(collection = "users")
public class User {
    @MongoId
    private String id;
    
    @Indexed private String userName;  // Unique username
    @Email private String email;        // Email address
    private String password;             // BCrypt hashed password
    private List<UserRoles> userRoles;  // User roles (ADMIN, VENUE_OWNER, CUSTOMER)
    private boolean enabled;             // Account status
}
```

**User Roles:**
- `ADMIN`: Full system access
- `VENUE_OWNER`: Can manage venues
- `CUSTOMER`: Can browse and book venues

### 2. Application Layer (`com.example.play_link.application`)

#### AuthService
**Responsibilities:**
- User registration
- User login
- JWT token generation
- Password encryption
- UserDetailsService implementation for Spring Security

**Key Methods:**
- `register(RegisterRequest)`: Creates new user account
- `login(LoginRequest)`: Authenticates user and returns JWT
- `loadUserByUsername(String)`: Loads user for Spring Security

### 3. Presentation Layer (`com.example.play_link.api`)

#### AuthController
**Endpoints:**
- `POST /auth/register`: User registration
- `POST /auth/login`: User login
- `GET /auth/health`: Health check

**DTOs:**
- `RegisterRequest`: Registration input data
- `LoginRequest`: Login credentials
- `AuthResponse`: Contains JWT token and user data

### 4. Infrastructure Layer (`com.example.play_link.infrastructure`)

#### Security Components

**JwtUtil:**
- Generates JWT tokens
- Validates JWT tokens
- Extracts claims from tokens

**JwtAuthenticationFilter:**
- Intercepts HTTP requests
- Validates Authorization header
- Extracts and validates JWT
- Sets SecurityContext

**SecurityConfig:**
- Configures Spring Security
- Defines authentication rules
- Configures password encoder (BCrypt)
- Sets up authentication manager

**UserRepository:**
- MongoDB data access
- Query methods for user lookup

---

## 🔐 Security Flow

### Registration Flow
```
1. Client sends POST /auth/register with user data
   ↓
2. AuthController validates input (@Valid)
   ↓
3. AuthService checks if username/email exists
   ↓
4. Password is hashed with BCrypt
   ↓
5. User saved to MongoDB
   ↓
6. JWT token generated
   ↓
7. Response with token + user data
```

### Login Flow
```
1. Client sends POST /auth/login with credentials
   ↓
2. AuthController validates input
   ↓
3. AuthenticationManager authenticates user
   ↓
4. If valid, AuthService loads user
   ↓
5. JWT token generated
   ↓
6. Response with token + user data
```

### Protected Request Flow
```
1. Client sends request with Authorization: Bearer <JWT>
   ↓
2. JwtAuthenticationFilter intercepts request
   ↓
3. Extract and validate JWT token
   ↓
4. Load user from database
   ↓
5. Set authentication in SecurityContext
   ↓
6. Continue to controller
   ↓
7. Controller processes request
   ↓
8. Response sent to client
```

---

## 🌐 API Endpoints

### Authentication Endpoints

#### 1. Register User
```bash
POST /auth/register
Content-Type: application/json

{
  "userName": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "userRoles": ["CUSTOMER"],
  "enabled": true
}
```

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": "65a1b2c3d4e5f6g7h8i9",
    "userName": "john_doe",
    "email": "john@example.com",
    "userRoles": ["CUSTOMER"],
    "enabled": true
  }
}
```

**Error Responses:**
- `409 CONFLICT`: Username or email already exists
- `400 BAD REQUEST`: Validation errors

#### 2. Login
```bash
POST /auth/login
Content-Type: application/json

{
  "userName": "john_doe",
  "password": "password123"
}
```

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": "65a1b2c3d4e5f6g7h8i9",
    "userName": "john_doe",
    "email": "john@example.com",
    "userRoles": ["CUSTOMER"],
    "enabled": true
  }
}
```

#### 3. Accessing Protected Endpoints
```bash
GET /venues
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## 💾 Database Schema

### Users Collection
```json
{
  "_id": "ObjectId",
  "userName": "string (unique, indexed)",
  "email": "string (unique, validated)",
  "password": "string (BCrypt hashed)",
  "userRoles": ["enum: ADMIN | VENUE_OWNER | CUSTOMER"],
  "enabled": "boolean",
  "_class": "com.example.play_link.domain.user.User"
}
```

### Venues Collection
```json
{
  "_id": "ObjectId",
  "ownerId": "string (references User)",
  "venueName": "string",
  "description": "string",
  "location": {
    "address": "string",
    "city": "string",
    "district": "string",
    "country": "string"
  },
  "geoLocation": {
    "latitude": "number",
    "longitude": "number"
  },
  "contactInfo": {
    "phone": "string",
    "email": "string",
    "whatsapp": "string"
  },
  "images": ["string"],
  "facilities": ["string"],
  "operatingHours": {
    "monday": {"open": "string", "close": "string", "isOpen": "boolean"},
    ...
  },
  "rating": {
    "average": "number",
    "totalReviews": "number"
  },
  "venueApprovalStatus": "enum: PENDING | APPROVED | REJECTED | SUSPENDED",
  "isActive": "boolean",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

---

## 🧪 Testing Guide

### 1. Test Registration

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "alice_customer",
    "email": "alice@example.com",
    "password": "securepass123",
    "userRoles": ["CUSTOMER"],
    "enabled": true
  }'
```

### 2. Test Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "alice_customer",
    "password": "securepass123"
  }'
```

### 3. Test Protected Endpoint
```bash
# Save the token from login response
TOKEN="your_jwt_token_here"

curl -X GET http://localhost:8080/venues \
  -H "Authorization: Bearer $TOKEN"
```

### 4. Test Different Roles

**Create VENUE_OWNER:**
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "bob_owner",
    "email": "bob@example.com",
    "password": "ownerpass123",
    "userRoles": ["VENUE_OWNER"],
    "enabled": true
  }'
```

**Create ADMIN:**
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "admin_user",
    "email": "admin@playlink.com",
    "password": "adminpass123",
    "userRoles": ["ADMIN"],
    "enabled": true
  }'
```

---

## 🔑 JWT Configuration

Located in `application.properties`:

```properties
# JWT Secret Key (256-bit)
jwt.secret=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437

# JWT Expiration (24 hours in milliseconds)
jwt.expiration=86400000

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/play_link
spring.data.mongodb.auto-index-creation=true
```

**Security Notes:**
- JWT secret should be at least 256 bits
- In production, use environment variables
- Never commit secrets to version control

---

## 📁 Project Structure

```
play_link/
├── src/
│   ├── main/
│   │   ├── java/com/example/play_link/
│   │   │   ├── api/                      # Controllers & DTOs
│   │   │   │   ├── auth/
│   │   │   │   │   ├── AuthController.java
│   │   │   │   │   └── dto/
│   │   │   │   │       ├── RegisterRequest.java
│   │   │   │   │       ├── LoginRequest.java
│   │   │   │   │       └── AuthResponse.java
│   │   │   │   └── venue/
│   │   │   │       └── VenueController.java
│   │   │   ├── application/              # Services
│   │   │   │   ├── auth/
│   │   │   │   │   └── AuthService.java
│   │   │   │   └── venue/
│   │   │   │       └── VenueService.java
│   │   │   ├── domain/                   # Entities & Business Logic
│   │   │   │   ├── user/
│   │   │   │   │   ├── User.java
│   │   │   │   │   └── enums/
│   │   │   │   │       └── UserRoles.java
│   │   │   │   └── venue/
│   │   │   │       ├── Venue.java
│   │   │   │       └── enums/
│   │   │   │           └── VenueApprovalStatus.java
│   │   │   └── infrastructure/           # DB, Security, Config
│   │   │       ├── config/
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   └── MongoConfig.java
│   │   │       ├── security/
│   │   │       │   ├── JwtUtil.java
│   │   │       │   └── JwtAuthenticationFilter.java
│   │   │       ├── user/
│   │   │       │   └── UserRepository.java
│   │   │       └── venue/
│   │   │           └── VenueRepository.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
├── start.sh                              # Startup script
└── README.md
```

---

## 🚀 Running the Application

### Prerequisites
- Java 22
- MongoDB 7.0.11
- Maven 3.9+

### Startup

```bash
# Start MongoDB
/path/to/mongodb/bin/mongod --dbpath ~/data/db

# Start Application (automated script)
./start.sh
```

The script will:
1. ✅ Check if MongoDB is running
2. ✅ Set JAVA_HOME to Java 22
3. ✅ Build and run the application

Application will be available at: **http://localhost:8080**

---

## 🔒 Security Best Practices Implemented

1. **Password Security**
   - BCrypt hashing with salt
   - Minimum password length validation
   - Never store plain text passwords

2. **JWT Security**
   - Stateless authentication
   - Token expiration (24 hours)
   - HMAC-SHA256 signing

3. **Input Validation**
   - Jakarta Validation annotations
   - Email format validation
   - Required field checks

4. **CORS & CSRF**
   - CSRF disabled (stateless JWT)
   - Configurable CORS settings

5. **Role-Based Access Control**
   - Multiple roles support
   - Authority-based endpoint protection

---

## 📈 Future Enhancements

- [ ] Refresh token mechanism
- [ ] Email verification
- [ ] Password reset flow
- [ ] Rate limiting
- [ ] OAuth2 integration (Google, Facebook)
- [ ] Audit logging
- [ ] Two-factor authentication (2FA)
- [ ] Redis for token blacklisting

---

## 📞 Contact & Support

For issues or questions, refer to the main README.md or create an issue in the repository.

---

**Last Updated:** January 16, 2026  
**Version:** 1.0.0
