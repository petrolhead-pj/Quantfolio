# Quantfolio — Stock Portfolio Analytics & Investment Intelligence Platform

[![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=java&logoColor=white)]()
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)]()
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)]()
[![Tomcat](https://img.shields.io/badge/Jetty-Server-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=black)]()
[![Chart.js](https://img.shields.io/badge/Chart.js-Visualization-FF6384?style=for-the-badge&logo=chartdotjs&logoColor=white)]()

**A full-stack, database-driven financial analytics platform for tracking stock investments, portfolio performance, and sector diversification — built with Java Servlets, JSP/JSTL, MySQL, and Chart.js.**

> Developed by **Paarth Jain** | Academic Project — Database Systems & Java Programming

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 17, Servlets 4.0, JSP 2.3, JSTL 1.2 |
| Database | MySQL 8.0, JDBC, Views, Triggers, Window Functions |
| Frontend | HTML5, CSS3, JavaScript ES6, Chart.js |
| Desktop | Java Swing (Admin Dashboard) |
| Build | Apache Maven 3.9, Jetty 10 |
| Security | BCrypt password hashing |

---

## Quick Start

### Prerequisites

| Requirement | Version |
|-------------|---------|
| Java JDK | 17+ |
| Apache Maven | 3.6+ |
| MySQL Server | 8.0+ |

### Setup

**1. Clone the repository**
```bash
git clone https://github.com/petrolhead-pj/Quantfolio.git
cd Quantfolio
```

**2. Configure database credentials**

Edit `src/main/resources/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/quantfolio?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=your_mysql_password
```

**3. Set up the database**
```sql
mysql -u root -p
CREATE DATABASE quantfolio;
USE quantfolio;
SOURCE db/schema.sql;
SOURCE db/seed_data.sql;
SOURCE db/triggers.sql;
SOURCE db/views.sql;
```

**4. Build and run**
```bash
mvn clean package
mvn jetty:run
```

**5. Open in browser**
```
http://localhost:8080/quantfolio
```

---

## Demo Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@quantfolio.com | admin123 |
| Investor | alice@example.com | user123 |

---

## Features

- Secure login with BCrypt password hashing
- Real-time portfolio value calculation via JDBC
- Sector diversification charts (Chart.js)
- Buy/Sell transaction recording with trigger-based auto-update
- Java Swing desktop admin dashboard
- Advanced SQL: Views, Triggers, Window Functions, Analytical Queries

---

## Database Design

10 tables: `users`, `stocks`, `sectors`, `transactions`, `portfolio_holdings`, `price_history`, `watchlist`, `alerts`, `analytics_cache`, `audit_log`

4 triggers:
- `after_buy_transaction` — auto-updates portfolio holdings on BUY
- `before_sell_transaction` — validates sufficient shares before SELL
- `after_sell_transaction` — reduces holding quantity on SELL
- `audit_new_transaction` — logs every transaction automatically

5 analytical views for dashboard queries.

---

## Desktop Application

Run the Java Swing admin app:
```bash
java -cp "target/classes;target/quantfolio/WEB-INF/lib/*" com.quantfolio.desktop.DesktopApp
```

Features: Analytics Dashboard, Stock Management, Transaction Monitoring, User Management.

---

## License

Academic project — Database Systems major project.

---

Built with Java · MySQL · Chart.js · Maven · Jetty
