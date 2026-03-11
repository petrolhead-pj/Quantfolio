# 📈 Quantfolio — Stock Portfolio Analytics & Investment Intelligence Platform

<div align="center">

![Java](https://img.shields.io/badge/Java-11-007396?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Tomcat](https://img.shields.io/badge/Tomcat-Server-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=black)
![Chart.js](https://img.shields.io/badge/Chart.js-Visualization-FF6384?style=for-the-badge&logo=chartdotjs&logoColor=white)
![License](https://img.shields.io/badge/License-Academic-green?style=for-the-badge)

**A full-stack, database-driven financial analytics platform for tracking stock investments, portfolio performance, and sector diversification — built with Java Servlets, JSP/JSTL, MySQL, and Chart.js.**

[Features](#-feature-overview) • [Architecture](#-system-architecture) • [Database Design](#-database-design) • [Setup](#-installation--setup) • [Analytics Engine](#-portfolio-analytics-engine) • [Screenshots](#-user-interface)

</div>

---

## 📌 Table of Contents

1. [Purpose & Problem Statement](#-purpose--problem-statement)
2. [Similar Existing Platforms](#-similar-existing-platforms)
3. [Feature Overview](#-feature-overview)
4. [System Architecture](#-system-architecture)
5. [Database Design](#-database-design)
6. [Technology Stack](#-technology-stack)
7. [Module-wise Breakdown](#-module-wise-breakdown)
8. [Security Implementation](#-security-implementation)
9. [Portfolio Analytics Engine](#-portfolio-analytics-engine)
10. [User Interface](#-user-interface)
11. [Project Structure](#-project-structure)
12. [Installation & Setup](#-installation--setup)
13. [Demo Credentials](#-demo-credentials)
14. [Advanced SQL Features](#-advanced-sql-features)
15. [Desktop Application](#-desktop-application)
16. [Future Scope](#-future-scope)

---

## 🎯 Purpose & Problem Statement

### The Problem

Retail investors typically rely on **multiple disconnected tools** to track their investments:

| Tool | Limitation |
|------|-----------|
| Trading platforms | Show only executed trades |
| Spreadsheet tools | Require manual calculations |
| Financial apps | Offer limited analytics and no customization |

As portfolios grow, these fragmented workflows lead to:
- ❌ Error-prone manual portfolio calculations
- ❌ Poor visibility into investment performance over time
- ❌ Difficulty tracking sector diversification
- ❌ No single centralized analytics platform

### The Solution — Quantfolio

Quantfolio is a **database-driven portfolio analytics system** that centralizes investment tracking and generates deep insights using SQL-based analytics.

```
User Records Transactions
         │
         ▼
Transaction Data Stored in MySQL Database
         │
         ▼
SQL Analytics Engine Processes Data
         │
         ▼
Interactive Portfolio Dashboard Rendered
```

Quantfolio enables investors to:
- ✅ Track all stock transactions in one place
- ✅ Analyze portfolio performance with real-time calculations
- ✅ Monitor diversification across market sectors
- ✅ Identify and rank top-performing investments

---

## 🌐 Similar Existing Platforms

Quantfolio draws inspiration from leading financial portfolio platforms, replicating their core analytics concepts in a clean, database-centric architecture.

| Platform | Organization | Description | How Quantfolio Compares |
|----------|-------------|-------------|------------------------|
| **Zerodha Console** | Zerodha | Portfolio analytics & tax reports | Quantfolio provides simplified, focused analytics |
| **Yahoo Finance** | Yahoo | Market tracking & portfolio monitoring | Quantfolio emphasizes custom investment analytics |
| **Google Finance** | Google | Financial news & portfolio insights | Quantfolio is fully database-driven with no external feeds |
| **Personal Capital** | Empower | Wealth management dashboards | Quantfolio is an academic implementation of similar dashboards |

---

## ✨ Feature Overview

### 🧑‍💼 Investor Features
- Secure registration and authentication
- Add stocks to personal portfolio
- Record and manage buy/sell transactions
- Track current holdings and quantities
- Monitor total portfolio value in real time
- View complete transaction history
- Analyze portfolio diversification by sector

### 📊 Portfolio Tracking
The system automatically calculates:
- **Total Portfolio Value** — live valuation of all holdings
- **Total Investment Amount** — cumulative capital deployed
- **Unrealized Profit / Loss** — current gain or loss
- **Average Purchase Price** — per-stock cost basis

### 📈 Analytics Dashboard
Interactive charts rendered with **Chart.js**:
- Portfolio allocation by sector (Pie/Donut chart)
- Investment growth over time (Line chart)
- Top performing stocks (Bar chart)
- Monthly investment activity (Bar chart)

---

## 🏗️ System Architecture

Quantfolio is built on the **MVC (Model-View-Controller)** design pattern, ensuring a clean separation of concerns and a scalable codebase.

```
┌─────────────────────────────────────┐
│          CLIENT LAYER               │
│   Browser — JSP + JS + Chart.js     │
└──────────────┬──────────────────────┘
               │ HTTP Requests
               ▼
┌─────────────────────────────────────┐
│         CONTROLLER LAYER            │
│         Java Servlets               │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│       BUSINESS LOGIC LAYER          │
│     Portfolio Analytics Engine      │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│        DATA ACCESS LAYER            │
│         DAO Classes (JDBC)          │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│          DATABASE LAYER             │
│   MySQL — Tables, Views, Triggers   │
└─────────────────────────────────────┘
```

**Benefits of this architecture:**
- 🔹 Clear separation of concerns
- 🔹 Highly maintainable and testable codebase
- 🔹 Scalable and extensible application structure

---

## 🗄️ Database Design

The database schema is engineered to efficiently store financial transactions and power analytical queries.

### Core Tables

| Table | Description |
|-------|-------------|
| `users` | Investor accounts and authentication |
| `stocks` | Stock symbols and company metadata |
| `sectors` | Market sectors for diversification tracking |
| `transactions` | All buy/sell transaction records |
| `portfolio_holdings` | Aggregated holdings per user per stock |
| `price_history` | Historical price data per stock |
| `watchlist` | User-defined stock watchlists |
| `alerts` | Price alert configurations |
| `analytics_cache` | Cached analytics results for performance |
| `audit_log` | System audit trail |

### Entity Relationships

```
Users   (1) ──────────── (N)  Transactions
Stocks  (1) ──────────── (N)  Transactions
Sectors (1) ──────────── (N)  Stocks
Users   (1) ──────────── (N)  Portfolio Holdings
```

### Key Table Schemas

#### `users`
| Column | Type | Description |
|--------|------|-------------|
| `user_id` | INT PK | Unique identifier |
| `name` | VARCHAR(100) | Full name |
| `email` | VARCHAR(255) | Email address (unique) |
| `password_hash` | VARCHAR(255) | BCrypt-hashed password |

#### `stocks`
| Column | Type | Description |
|--------|------|-------------|
| `stock_id` | INT PK | Unique identifier |
| `symbol` | VARCHAR(10) | Stock ticker (e.g., AAPL) |
| `company_name` | VARCHAR(255) | Full company name |
| `sector_id` | INT FK | Reference to sectors table |

#### `transactions`
| Column | Type | Description |
|--------|------|-------------|
| `transaction_id` | INT PK | Unique transaction ID |
| `user_id` | INT FK | Investor reference |
| `stock_id` | INT FK | Stock reference |
| `type` | ENUM | `BUY` / `SELL` |
| `quantity` | INT | Number of shares |
| `price` | DECIMAL(10,2) | Price per share at transaction time |

---

## 🛠️ Technology Stack

### Backend
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 11 | Core programming language |
| **Java Servlets** | 4.0 | HTTP request handling and routing |
| **JSP** | 2.3 | Dynamic server-side page rendering |
| **JSTL** | 1.2 | JSP standard tag library |

### Frontend
| Technology | Purpose |
|-----------|---------|
| **HTML5 / CSS3** | Responsive user interface |
| **JavaScript (ES6)** | Client-side interactivity |
| **Chart.js** | Interactive data visualization |

### Database
| Technology | Version | Purpose |
|-----------|---------|---------|
| **MySQL** | 8.0 | Relational database engine |

### Build & Deployment
| Technology | Purpose |
|-----------|---------|
| **Apache Maven** | Dependency management and build automation |
| **Apache Tomcat** | Java web application server |

---

## 🧩 Module-wise Breakdown

### 🔐 Authentication Module
Handles the full user identity lifecycle:
- User registration with input validation
- Login authentication with BCrypt password comparison
- Session creation and management with secure cookies
- Session invalidation on logout

### 💼 Portfolio Management Module
The core investment tracking engine:
- Recording new buy/sell stock transactions
- Automatic update of `portfolio_holdings` after each transaction
- Calculation of average purchase price and total quantity
- Guard against selling more shares than currently held

### 📊 Analytics Module
Responsible for all performance insights:
- Total portfolio value calculation
- Realized and unrealized profit/loss tracking
- Sector diversification analysis
- Stock performance ranking by return percentage

### 🖥️ Dashboard Module
Provides the investor-facing data interface:
- Interactive Chart.js charts (sector allocation, growth, rankings)
- Summary cards (total value, P&L, top performers)
- Responsive portfolio and transaction tables
- Clean financial data visualization optimized for clarity

---

## 🔒 Security Implementation

### Password Encryption
All passwords are secured with **BCrypt hashing** — passwords are never stored in plain text.

```
User Password Input
        │
        ▼
  BCrypt Hash (Cost Factor: 12)
        │
        ▼
  Stored in `password_hash` Column
```

### Session Security
| Feature | Implementation |
|---------|---------------|
| **Session Timeout** | Auto-invalidation after 30 minutes of inactivity |
| **Secure Cookies** | `HttpOnly` and `Secure` flags set on session cookies |
| **Access Control** | Role-based access (Admin / Investor) enforced at servlet level |
| **Input Validation** | Server-side validation on all transaction and user inputs |

---

## 🧮 Portfolio Analytics Engine

The analytics engine processes investment data using optimized SQL queries, with results served to the UI through the DAO layer.

### Portfolio Value Calculation
```sql
portfolio_value = SUM(quantity × current_price)
```

### Profit / Loss Calculation
```sql
profit_loss = current_portfolio_value − total_investment_cost
```

### Sector Allocation
Distribution of investment capital across sectors, expressed as a percentage of total portfolio value — used to generate the sector allocation pie chart.

### Stock Performance Ranking
Stocks are ranked by return percentage:
```sql
return_pct = ((current_price − avg_buy_price) / avg_buy_price) × 100
```
Results power the **Top Performers** bar chart on the dashboard.

---

## 🖥️ User Interface

The frontend is built with a focus on **clear, actionable financial data visualization**.

### Dashboard Views
| View | Description |
|------|-------------|
| **Portfolio Summary** | Cards showing total value, invested capital, and P&L |
| **Sector Allocation Chart** | Donut chart of investment distribution by sector |
| **Growth Over Time** | Line chart of portfolio value history |
| **Top Performers** | Bar chart ranking stocks by return percentage |
| **Transaction Table** | Paginated history of all buy/sell transactions |
| **Holdings Breakdown** | Per-stock table with quantity, avg price, current value |

---

## 📁 Project Structure

```
quantfolio/
│
├── README.md
├── pom.xml                          # Maven build configuration
│
├── db/
│   ├── schema.sql                   # Full database schema
│   ├── seed_data.sql                # Sample stocks and sector data
│   ├── triggers.sql                 # Auto-update and guard triggers
│   ├── views.sql                    # Analytical views
│   └── advanced_queries.sql         # Complex analytics queries
│
└── src/main/
    ├── java/com/quantfolio/
    │   ├── model/                   # POJO entity classes
    │   ├── dao/                     # Data Access Objects (JDBC)
    │   ├── servlet/                 # Controller servlets
    │   └── util/                    # Helpers (DB connection, BCrypt)
    │
    └── webapp/
        ├── dashboard.jsp            # Main analytics dashboard
        ├── portfolio.jsp            # Holdings view
        ├── transactions.jsp         # Transaction history
        ├── css/                     # Stylesheets
        └── js/                      # Chart.js scripts
```

---

## ⚙️ Installation & Setup

### Prerequisites

| Requirement | Version |
|------------|---------|
| Java JDK | 11+ |
| Apache Maven | 3.6+ |
| MySQL Server | 8.0+ |
| Apache Tomcat | 9.0+ |

### Step-by-Step Setup

**1. Clone the repository**
```bash
git clone https://github.com/yourusername/quantfolio.git
cd quantfolio
```

**2. Set up the database**
```bash
# Log in to MySQL
mysql -u root -p

# Create database and run scripts
CREATE DATABASE quantfolio;
USE quantfolio;

SOURCE db/schema.sql;
SOURCE db/seed_data.sql;
SOURCE db/triggers.sql;
SOURCE db/views.sql;
```

**3. Configure database connection**

Update the DB credentials in `src/main/java/com/quantfolio/util/DBConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/quantfolio";
private static final String USER = "your_mysql_user";
private static final String PASSWORD = "your_mysql_password";
```

**4. Build the project**
```bash
mvn clean package
```

**5. Deploy to Tomcat**

Copy the generated WAR file to your Tomcat `webapps/` directory:
```bash
cp target/quantfolio.war $TOMCAT_HOME/webapps/
```

**6. Start Tomcat and access the app**
```
http://localhost:8080/quantfolio
```

---

## 🔑 Demo Credentials

| Role | Username | Password | Access |
|------|---------|---------|--------|
| **Admin** | `admin` | `admin123` | Full system access + Swing desktop app |
| **Investor** | `user1` | `user123` | Portfolio management + analytics dashboard |

> ⚠️ Change these credentials before any non-local deployment.

---

## 🔬 Advanced SQL Features

This project demonstrates several advanced relational database concepts:

### Views
Simplify complex analytical queries by abstracting them into reusable virtual tables.

```sql
-- Example view: portfolio summary per user
CREATE VIEW portfolio_summary_view AS
SELECT u.user_id, u.name,
       SUM(ph.quantity * s.current_price) AS portfolio_value,
       SUM(ph.quantity * ph.avg_buy_price) AS total_invested,
       SUM(ph.quantity * (s.current_price - ph.avg_buy_price)) AS unrealized_pnl
FROM portfolio_holdings ph
JOIN users u ON ph.user_id = u.user_id
JOIN stocks s ON ph.stock_id = s.stock_id
GROUP BY u.user_id, u.name;
```

### Triggers
Automate database state management on transaction events:
- **After BUY** → Update `portfolio_holdings` quantity and recalculate average buy price
- **Before SELL** → Validate sufficient holdings; raise error if quantity exceeds held shares

### Analytical Queries
Advanced SQL used throughout the analytics engine:
- **Portfolio Diversification** — `GROUP BY` sector with percentage allocation
- **Top Performing Stocks** — Ranked by `return_pct` using window functions
- **Monthly Investment Trends** — Aggregated by `YEAR(transaction_date), MONTH(transaction_date)`

---

## 🖱️ Desktop Application

A **Java Swing** desktop client is included for administrative-level analytics, providing a native GUI without the need for a browser.

### Features
| Feature | Description |
|---------|-------------|
| **Analytics Dashboard** | Visual portfolio performance charts in Swing |
| **Stock Database Management** | Add, edit, and manage the stocks master list |
| **Transaction Monitoring** | View and audit all user transactions system-wide |

---

## 🚀 Future Scope

| Enhancement | Description |
|-------------|-------------|
| **Live Stock Price API** | Integrate with Alpha Vantage / Yahoo Finance API for real-time prices |
| **AI Investment Recommendations** | ML-based portfolio suggestions using historical performance |
| **Mobile Application** | Android/iOS app for portfolio tracking on the go |
| **Cloud Deployment** | Containerize with Docker and deploy to AWS / GCP |
| **Real-time Analytics** | WebSocket-based live portfolio value updates |
| **Tax Reports** | Automated capital gains reports for filing |

---

## 📄 License

This project was developed for **academic and educational purposes** as part of a Database Systems major project.

---

<div align="center">

Built with ☕ Java · 🗄️ MySQL · 📊 Chart.js

**[⬆ Back to Top](#-quantfolio--stock-portfolio-analytics--investment-intelligence-platform)**

</div>
