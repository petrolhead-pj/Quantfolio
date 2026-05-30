# Quick Setup Guide

## 1. Clone the Repository

```bash
git clone https://github.com/petrolhead-pj/Quantfolio.git
cd Quantfolio
```

## 2. Configure Database Credentials

Edit `src/main/resources/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/quantfolio?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=your_mysql_password
```

## 3. Database Setup

Open MySQL Workbench and run each file via File > Open SQL Script:
```
db/schema.sql
db/seed_data.sql
db/views.sql
db/triggers.sql
```

## 4. Build and Run

```bash
mvn clean package
mvn jetty:run
```

Open: http://localhost:8080/quantfolio

## 5. Demo Login

| Role     | Email                | Password |
|----------|----------------------|----------|
| Admin    | admin@quantfolio.com | admin123 |
| Investor | alice@example.com    | user123  |

## 6. Swing Desktop App

```bash
java -cp "target\classes;target\quantfolio\WEB-INF\lib\*" com.quantfolio.desktop.DesktopApp
```
