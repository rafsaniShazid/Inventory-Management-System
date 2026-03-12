# Inventory Management System

A comprehensive REST API for managing inventory with categories and items, built with Spring Boot and PostgreSQL.

## 🚀 Technologies Used

- **Java 23**
- **Spring Boot 3.5.11**
- **Spring Data JPA**
- **PostgreSQL 18.3**
- **Docker & Docker Compose**
- **Maven**
- **Lombok**

## 📋 Features

### Category Management

- Create, read, update, and delete categories
- Prevent deletion of categories with associated items
- Unique category names validation

### Item Management

- Complete CRUD operations for inventory items
- Stock quantity tracking and updates
- Item search by name
- Low stock monitoring
- Category-based filtering
- Inventory statistics

## 🛠️ Setup & Installation

### Prerequisites

- Java 23 or higher
- Docker Desktop
- Maven (included via wrapper)

### 1. Start PostgreSQL Database

```powershell
docker compose up -d
```

### 2. Run the Application

```powershell
.\mvnw.cmd spring-boot:run
```

The application will start on `http://localhost:8081`

## 📡 API Endpoints

### Category Endpoints

| Method | Endpoint               | Description         |
| ------ | ---------------------- | ------------------- |
| POST   | `/api/categories`      | Create new category |
| GET    | `/api/categories`      | Get all categories  |
| GET    | `/api/categories/{id}` | Get category by ID  |
| PUT    | `/api/categories/{id}` | Update category     |
| DELETE | `/api/categories/{id}` | Delete category     |

### Item Endpoints

| Method | Endpoint                             | Description              |
| ------ | ------------------------------------ | ------------------------ |
| POST   | `/api/items`                         | Create new item          |
| GET    | `/api/items`                         | Get all items            |
| GET    | `/api/items/{id}`                    | Get item by ID           |
| GET    | `/api/items/category/{categoryId}`   | Get items by category    |
| GET    | `/api/items/search?name={name}`      | Search items by name     |
| GET    | `/api/items/low-stock?threshold={n}` | Get low stock items      |
| GET    | `/api/items/stats`                   | Get inventory statistics |
| PUT    | `/api/items/{id}`                    | Update item              |
| PUT    | `/api/items/{id}/stock`              | Update stock quantity    |
| DELETE | `/api/items/{id}`                    | Delete item              |

## 📝 Example API Usage

### Create Category

```powershell
Invoke-RestMethod -Uri "http://localhost:8081/api/categories" -Method POST -ContentType "application/json" -Body '{"categoryName":"Electronics","description":"Electronic items"}'
```

### Create Item

```powershell
Invoke-RestMethod -Uri "http://localhost:8081/api/items" -Method POST -ContentType "application/json" -Body '{"itemName":"Laptop","description":"Dell Laptop","stockQuantity":50,"categoryId":1}'
```

### Get All Items

```powershell
Invoke-RestMethod -Uri "http://localhost:8081/api/items" -Method GET
```

### Search Items

```powershell
Invoke-RestMethod -Uri "http://localhost:8081/api/items/search?name=Laptop" -Method GET
```

### Get Low Stock Items

```powershell
Invoke-RestMethod -Uri "http://localhost:8081/api/items/low-stock?threshold=10" -Method GET
```

### Update Stock

```powershell
Invoke-RestMethod -Uri "http://localhost:8081/api/items/1/stock" -Method PUT -ContentType "application/json" -Body '{"stockQuantity":75}'
```

## 🗄️ Database Configuration

**PostgreSQL Details:**

- Host: `localhost`
- Port: `5433` (mapped from container port 5432)
- Database: `mydatabase`
- Username: `myuser`
- Password: `secret`

**Connection String:**

```
jdbc:postgresql://127.0.0.1:5433/mydatabase
```

## 🏗️ Project Structure

```
src/main/java/com/inventory/inventory_management/
├── controller/          # REST API endpoints
├── service/            # Business logic layer
├── repository/         # Data access layer
├── entity/             # JPA entities
├── dto/                # Data transfer objects
├── exception/          # Exception handling
└── config/             # Application configuration
```

## 🔒 Security

**Note:** Security is currently disabled for development. All endpoints permit unrestricted access.

**Future Implementation:**

- JWT-based authentication
- Role-based access control (ADMIN, MANAGER, USER)

## 🧪 Testing

Access the database directly:

```powershell
docker exec -it inventory_management-postgres-1 psql -U myuser -d mydatabase
```

View tables:

```sql
\dt
SELECT * FROM categories;
SELECT * FROM items;
```

## 📦 Docker Commands

**Start Database:**

```powershell
docker compose up -d
```

**Stop Database:**

```powershell
docker compose down
```

**View Logs:**

```powershell
docker logs inventory_management-postgres-1
```

**Check Running Containers:**

```powershell
docker ps
```

## 👥 Team

**SEPM Project - Phase 2: Inventory Module**

- Categories & Items Management
- REST API Development
- Database Integration

## 📄 License

This project is developed as part of the SEPM course.

---

**Last Updated:** March 12, 2026
