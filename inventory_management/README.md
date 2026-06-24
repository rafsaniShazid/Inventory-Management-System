# Inventory Management System

A full-stack web application for managing inventory with role-based access control, built with Spring Boot, PostgreSQL, Docker, and CI/CD pipelines.

## 📋 Project Overview

This is a **Software Engineering Lab Project** implementing a complete professional development workflow with:
- Authentication & Authorization with JWT and Spring Security
- REST API following RESTful principles
- Role-based access control (ADMIN, MANAGER, USER)
- Comprehensive testing (68+ unit & integration tests)
- Docker containerization with docker-compose
- GitHub Actions CI/CD pipeline
- Deployment-ready architecture

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Client Layer                            │
│            (Web Browser / REST API Consumer)                   │
└────────────────────────────┬────────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────────┐
│                   Spring Boot Application                       │
├─────────────────────────────────────────────────────────────────┤
│  Controllers (REST Endpoints)                                  │
│  ├── AuthController      (Authentication & Registration)       │
│  ├── CategoryController  (Category Management)                 │
│  ├── ItemController      (Item Management)                     │
│  └── RequestController   (Request Management)                  │
├─────────────────────────────────────────────────────────────────┤
│  Services (Business Logic)                                     │
│  ├── UserService         ├── ItemService                       │
│  ├── CategoryService     └── RequestService                    │
├─────────────────────────────────────────────────────────────────┤
│  Repositories (Data Access - JPA)                              │
│  ├── UserRepository      ├── ItemRepository                    │
│  ├── CategoryRepository  └── RequestRepository                 │
├─────────────────────────────────────────────────────────────────┤
│  Security Layer                                                │
│  ├── JWT Authentication  ├── Role-Based Access Control         │
│  └── Password Encryption (BCrypt)                              │
└────────────────────────────┬────────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────────┐
│              PostgreSQL Database (Docker Container)             │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📊 Entity-Relationship Diagram (ER Diagram)

```
┌──────────────────────────────────────────────────────────────────┐
│            DATABASE ENTITIES (Many-to-Many Implemented)          │
└──────────────────────────────────────────────────────────────────┘

┌──────────────┐
│    User      │◄──────────┐
├──────────────┤           │
│ userId (PK)  │           │
│ username     │           │
│ email        │      ┌────┴──────┐
│ password     │      │   1:M     │
│ roleId (FK)  ├─────►│           │
└──────────────┘      │   Role    │
       │              ├───────────┤
       │              │ roleId    │
    1:M│              │ roleName  │
       │              │ description
       ▼              └───────────┘
       │
       │
       │         ┌────────────────────┐
       └────────►│   RequestStatus    │
       (statusId)├────────────────────┤
                 │ statusId (PK)      │
                 │ statusName         │
                 └────────────────────┘


┌──────────────┐      ┌────────────────────┐      ┌──────────────┐
│   Request    │◄─────│   RequestItem      │─────►│    Item      │
├──────────────┤  1:M ├────────────────────┤ M:1  ├──────────────┤
│ requestId    │      │ requestItemId (PK) │      │ itemId (PK)  │
│ requesterName│      │ requestId (FK)     │      │ categoryId FK│
│ requesterEmail      │ itemId (FK)        │      │ itemName     │
│ status (FK)  │      │ quantity ★         │      │ description  │
│ requestedAt  │      └────────────────────┘      │ stockQuantity
│ reviewedAt   │           ▲                       └──────────────┘
│ reviewRemarks│           │                              ▲
└──────────────┘    M:N Relationship              1:M│
                  (Many Items per                   │
                   Many Requests)          ┌──────────────┐
                                           │   Category   │
            ★ quantity field specifies     ├──────────────┤
              how many of each item       │ categoryId   │
              per request                 │ categoryName │
                                          └──────────────┘
```

### Database Schema

| Entity | Primary Key | Fields | Relationships |
|--------|---|---|---|
| **User** | userId (SERIAL) | username, email, password, roleId | M:1 Role |
| **Role** | roleId (SERIAL) | roleName, description | 1:M User |
| **Category** | categoryId (SERIAL) | categoryName, description | 1:M Item |
| **Item** | itemId (SERIAL) | categoryId (FK), itemName, description, stockQuantity, createdDate | M:1 Category, 1:M RequestItem |
| **Request** | requestId (SERIAL) | requesterName, requesterEmail, statusId (FK), requestedAt, reviewedAt, reviewRemarks | M:1 RequestStatus, 1:M RequestItem |
| **RequestItem** | requestItemId (SERIAL) | requestId (FK), itemId (FK), quantity | M:1 Request, M:1 Item |
| **RequestStatus** | statusId (SERIAL) | statusName | 1:M Request |

### Many-to-Many Relationship

**Request ↔ RequestItem ↔ Item (Join Table Pattern)**

**Benefits:**
- ✅ One request can contain **multiple items**
- ✅ One item can be in **multiple requests**  
- ✅ Each item-request pair tracks its own **quantity**
- ✅ Shopping cart-like functionality enabled
- ✅ More flexible inventory request management

**Example:**
```
Request #101 (from John Doe):
  ├─ RequestItem #1: Blue Pen × 50 units
  ├─ RequestItem #2: Notebook × 20 units
  └─ RequestItem #3: Pencil × 100 units

Request #102 (from Jane Smith):
  ├─ RequestItem #4: Blue Pen × 30 units (same item, different request)
  └─ RequestItem #5: Eraser × 15 units
```

---

## 🚀 Technologies Used

| Component | Technology |
|-----------|-----------|
| **Backend Framework** | Spring Boot 3.5.11 |
| **Language** | Java 17 |
| **Database** | PostgreSQL 17 |
| **ORM** | Spring Data JPA with Hibernate |
| **Security** | Spring Security 6 + JWT |
| **Password Encoding** | BCrypt |
| **API Format** | REST JSON |
| **Testing** | JUnit 5, Mockito, Spring Boot Test, MockMvc |
| **Build Tool** | Maven 3.9.9 |
| **Containerization** | Docker & Docker Compose |
| **CI/CD** | GitHub Actions |
| **Utilities** | Lombok, Spring Validation |

---

## ✨ Key Features

### 🔐 Authentication & Authorization
- User registration with email validation
- JWT-based login with token generation
- BCrypt password encryption
- Role-based access control (ADMIN, MANAGER, USER)
- Secure logout with token revocation
- Method-level security annotations

### 📁 Category Management
- Create, read, update, delete categories
- Unique category name validation
- Prevent deletion of categories with items
- List all categories with pagination

### 📦 Item Management
- Complete CRUD operations
- Stock quantity tracking
- Search items by name
- Low stock alerts/filtering
- Category-based filtering
- Inventory statistics

### 📋 Request Management
- Create and track requests
- Status management (PENDING, APPROVED, REJECTED)
- Request history tracking
- Audit trail for status changes

---

## 🛠️ Setup & Installation

### Prerequisites

- Java 17 or higher
- Maven 3.9+ (included via wrapper)
- Docker Desktop installed
- Git

### Step-by-Step Setup

#### 1. Clone Repository
```bash
git clone <repository-url>
cd Inventory-Management-System
```

#### 2. Start PostgreSQL Database
```bash
cd inventory_management
docker compose up -d
```

#### 3. Build Application
```bash
./mvnw.cmd clean install
```

#### 4. Run Application
```bash
./mvnw.cmd spring-boot:run
```

#### 5. Access Application
- API URL: `http://localhost:8081`
- Database: `postgresql://localhost:5433/mydatabase`

---

## 🔐 Authentication & Authorization

### Available Roles

| Role | Permissions |
|------|---|
| **ADMIN** | Full access - Create, Read, Update, Delete all resources |
| **MANAGER** | Manage inventory - Create/Update items and requests |
| **USER** | Read-only access - View categories and items |

### Authentication Flow

```
1. User registers/logs in
2. System validates credentials
3. JWT token generated
4. Client includes token in Authorization header
5. System validates token on each request
6. Role-based access control enforced
```

---

## 📡 API Endpoints

### Authentication Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register new user | ❌ |
| POST | `/api/auth/login` | Login & get JWT token | ❌ |
| POST | `/api/auth/logout` | Logout (revoke token) | ✅ |

### Category Endpoints

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| POST | `/api/categories` | Create category | ADMIN |
| GET | `/api/categories` | Get all categories | USER |
| GET | `/api/categories/{id}` | Get category by ID | USER |
| PUT | `/api/categories/{id}` | Update category | ADMIN |
| DELETE | `/api/categories/{id}` | Delete category | ADMIN |

### Item Endpoints

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| POST | `/api/items` | Create item | MANAGER |
| GET | `/api/items` | Get all items | USER |
| GET | `/api/items/{id}` | Get item by ID | USER |
| GET | `/api/items/category/{categoryId}` | Get items by category | USER |
| GET | `/api/items/search?name={name}` | Search items | USER |
| GET | `/api/items/low-stock?threshold={n}` | Get low stock items | MANAGER |
| GET | `/api/items/stats` | Get statistics | MANAGER |
| PUT | `/api/items/{id}` | Update item | MANAGER |
| PUT | `/api/items/{id}/stock` | Update stock | MANAGER |
| DELETE | `/api/items/{id}` | Delete item | ADMIN |

### Request Endpoints

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| POST | `/api/requests` | Create request | USER |
| GET | `/api/requests` | Get all requests | MANAGER |
| GET | `/api/requests/{id}` | Get request by ID | USER/MANAGER |
| PUT | `/api/requests/{id}` | Update request | MANAGER |
| DELETE | `/api/requests/{id}` | Delete request | ADMIN |

---

## 📝 API Request Examples

### Register New User
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "SecurePass123"
  }'
```

### Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "SecurePass123"
  }'
```

### Create Category (with JWT)
```bash
TOKEN="<jwt_token_from_login>"
curl -X POST http://localhost:8081/api/categories \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryName": "Electronics",
    "description": "Electronic items and gadgets"
  }'
```

### Create Item
```bash
curl -X POST http://localhost:8081/api/items \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "itemName": "Laptop",
    "description": "Dell XPS 15",
    "stockQuantity": 50,
    "categoryId": 1
  }'
```

### Search Items
```bash
curl -X GET "http://localhost:8081/api/items/search?name=Laptop" \
  -H "Authorization: Bearer $TOKEN"
```

### Get Low Stock Items
```bash
curl -X GET "http://localhost:8081/api/items/low-stock?threshold=10" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🧪 Testing & Quality Assurance

### Test Coverage

**Total: 68+ Tests**
- Unit Tests: 50+
- Integration Tests: 15+
- Repository Tests: 3+

### Test Breakdown

| Test Class | Count | Type |
|-----------|-------|------|
| CategoryServiceTest | 10 | Unit |
| ItemServiceTest | 11 | Unit |
| RequestServiceTest | 13 | Unit |
| UserServiceTest | 6 | Unit |
| GlobalExceptionHandlerTest | 2 | Unit |
| DtoMapperTest | 10 | Unit |
| ValidationTest | 10 | Unit |
| InventoryControllerIntegrationTest | 2 | Integration |
| RequestControllerIntegrationTest | 2 | Integration |
| InventoryRepositoryIntegrationTest | 1 | Repository |

### Running Tests

```bash
# Run all tests
./mvnw.cmd test

# Run specific test class
./mvnw.cmd test -Dtest=CategoryServiceTest

# Run specific test method
./mvnw.cmd test -Dtest=CategoryServiceTest#testCreateCategory_Success

# Generate test report
./mvnw.cmd surefire-report:report
```

### Testing Technologies

- **JUnit 5**: Modern testing framework
- **Mockito**: Mocking and stubbing
- **Spring Boot Test**: Integration testing
- **MockMvc**: HTTP layer testing
- **@DataJpaTest**: Repository testing

---

## 🐳 Docker Setup & Deployment

### Quick Start

```bash
# Start all services
docker compose up --build

# Stop all services
docker compose down

# View logs
docker compose logs -f

# Reset database
docker compose down -v
```

### Services

**Application Container**
- Image: inventory-management-app:latest
- Port: 8082 → 8081
- Depends: PostgreSQL

**PostgreSQL Container**
- Image: postgres:17-alpine
- Port: 5433 → 5432
- Database: mydatabase
- User: myuser
- Password: secret

### Environment Variables

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/mydatabase
SPRING_DATASOURCE_USERNAME=myuser
SPRING_DATASOURCE_PASSWORD=secret
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

### Database Access

```bash
# Connect to database
docker exec -it postgres psql -U myuser -d mydatabase

# List tables
\dt

# View users
SELECT * FROM users;

# View items
SELECT * FROM items;
```

---

## 🔄 CI/CD Pipeline

### GitHub Actions Workflow

**Triggers:**
- Push to `main` or `feature-*` branches
- Pull requests to `main` branch

**Pipeline Stages:**
1. Checkout code
2. Setup JDK 17
3. Build with Maven
4. Run 68+ tests
5. Generate test reports
6. Package JAR
7. Upload artifacts

### Configuration

```yaml
name: Java CI with Maven
on:
  push:
    branches: [main, feature-*]
  pull_request:
    branches: [main]
```

### Branch Protection

- Main branch: Protected (requires PR reviews)
- Feature branches: `feature-*` naming convention
- CI checks: Must pass before merge
- Direct push to main: Disabled

---

## 🏗️ Project Structure

```
src/main/java/com/inventory/inventory_management/
│
├── controller/
│   ├── AuthController.java        # Login/Register endpoints
│   ├── CategoryController.java     # Category CRUD
│   ├── ItemController.java         # Item CRUD
│   ├── RequestController.java      # Request management
│   └── PageController.java         # UI pages
│
├── service/
│   ├── UserService.java            # User business logic
│   ├── CategoryService.java        # Category business logic
│   ├── ItemService.java            # Item business logic
│   └── RequestService.java         # Request business logic
│
├── repository/
│   ├── UserRepository.java         # User data access
│   ├── CategoryRepository.java      # Category data access
│   ├── ItemRepository.java          # Item data access
│   └── RequestRepository.java       # Request data access
│
├── entity/
│   ├── User.java                   # User entity
│   ├── Role.java                   # Role entity
│   ├── Category.java               # Category entity
│   ├── Item.java                   # Item entity
│   ├── Request.java                # Request entity
│   └── RequestStatus.java          # Request status enum
│
├── dto/
│   ├── LoginRequestDTO.java
│   ├── RegisterRequestDTO.java
│   ├── CategoryDTO.java
│   ├── ItemDTO.java
│   ├── RequestDTO.java
│   └── ... (other DTOs)
│
├── exception/
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
│
├── config/
│   ├── SecurityConfig.java         # Spring Security config
│   ├── JwtUtil.java                # JWT utilities
│   ├── JwtAuthenticationFilter.java # JWT filter
│   ├── AdminUserSeeder.java         # Seed admin user
│   └── InventoryDataSeeder.java    # Seed test data
│
└── InventoryManagementApplication.java  # Main class
```

---

## 🚀 Deployment

### Local Development

```bash
# Start database
docker compose up -d

# Build project
./mvnw.cmd clean package

# Run application
java -jar target/inventory_management-0.0.1-SNAPSHOT.jar
```

### Production Deployment

```bash
# Build Docker image
docker build -t inventory-management:latest .

# Run container
docker run -d \
  -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/mydatabase \
  -e SPRING_DATASOURCE_USERNAME=myuser \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  inventory-management:latest
```

---

## 📋 Git Workflow

### Branch Strategy

- **main**: Production-ready (protected)
- **develop**: Integration branch
- **feature-***: Feature development

### Commit Convention

```
[FEATURE]: Add new feature
[FIX]: Fix bug
[REFACTOR]: Refactor code
[TEST]: Add/update tests
[DOCS]: Update documentation
```

### PR Process

1. Create feature branch
2. Make changes
3. Write tests
4. Push commits
5. Create PR
6. Wait for CI
7. Request review
8. Merge after approval

---

## 📚 Key Learning Outcomes

✅ **Spring Boot Development** - Complete REST API development
✅ **RESTful Design** - Proper HTTP methods, status codes, documentation
✅ **Database Design** - Entity relationships, JPA/Hibernate
✅ **Security** - JWT tokens, role-based access, password encryption
✅ **Testing** - Unit, integration, and repository tests
✅ **Docker** - Containerization, Docker Compose orchestration
✅ **CI/CD** - GitHub Actions automated pipelines
✅ **Clean Architecture** - Layered architecture, separation of concerns
✅ **Professional Practices** - Git workflow, code review, documentation
✅ **Error Handling** - Global exception handling, meaningful error messages

---

## 📄 License

This project is developed as part of the SEPM (Software Engineering Lab) course.

---

**Last Updated:** April 5, 2026
**Status:** ✅ Complete & Production Ready
**Version:** 1.0.0
