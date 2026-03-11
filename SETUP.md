# Quick Setup Guide

## 1. Clone & Configure

```bash
git clone https://github.com/yourusername/quantfolio.git
cd quantfolio
```

Edit `src/main/java/com/quantfolio/util/DBConnection.java`:
```java
private static final String DB_URL      = "jdbc:mysql://localhost:3306/quantfolio?useSSL=false&serverTimezone=UTC";
private static final String DB_USER     = "root";
private static final String DB_PASSWORD = "your_password";
```

## 2. Database Setup

```sql
mysql -u root -p
SOURCE db/schema.sql;
SOURCE db/seed_data.sql;
SOURCE db/views.sql;
SOURCE db/triggers.sql;
```

## 3. Build & Deploy

```bash
mvn clean package
cp target/quantfolio.war $TOMCAT_HOME/webapps/
# Start Tomcat, open: http://localhost:8080/quantfolio
```

## 4. Demo Login

| Role     | Email                    | Password  |
|----------|--------------------------|-----------|
| Admin    | admin@quantfolio.com     | admin123  |
| Investor | user1@quantfolio.com     | user123   |

## 5. Desktop App (Optional)

```bash
cd desktop
mvn package
java -jar target/quantfolio-desktop-1.0.jar
```
