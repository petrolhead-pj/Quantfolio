# QUANTFOLIO - SETUP & RUN GUIDE

## Quickest Way to Get Running

### Step 1: Install 3 Tools (one-time)

**Java 17:**
1. Go to: https://adoptium.net
2. Download: Windows x64 JDK 17 LTS installer
3. Run installer, keep all defaults
4. Set JAVA_HOME:
   - Search "Environment Variables" in Start Menu
   - New System Variable: JAVA_HOME = C:\Program Files\Eclipse Adoptium\jdk-17.x.x
5. Restart Command Prompt

**Maven:**
1. Go to: https://maven.apache.org/download.cgi
2. Download: apache-maven-3.9.x-bin.zip
3. Extract to: C:\Maven
4. Add to PATH: C:\Maven\apache-maven-3.9.x\bin
5. Restart Command Prompt, verify: mvn -version

**MySQL:**
1. Go to: https://dev.mysql.com/downloads/installer/
2. Download: MySQL Installer for Windows (~450MB)
3. Setup Type: Developer Default
4. Set root password, remember it
5. Finish installation

---

### Step 2: Set Up Database

Open MySQL Workbench, connect, and run:

```sql
CREATE DATABASE quantfolio;
USE quantfolio;
```

Then open and execute each file via File > Open SQL Script:
- `db/schema.sql`
- `db/seed_data.sql`
- `db/triggers.sql`
- `db/views.sql`

---

### Step 3: Configure Credentials

Edit `src/main/resources/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/quantfolio?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=your_mysql_root_password
```

---

### Step 4: Build and Run

```bash
cd C:\Users\paarth jain\Documents\Quant\Quantfolio
mvn clean package
mvn jetty:run
```

Open browser: http://localhost:8080/quantfolio

---

### Step 5: Run Swing Desktop App

Open a second Command Prompt:
```bash
cd C:\Users\paarth jain\Documents\Quant\Quantfolio
java -cp "target\classes;target\quantfolio\WEB-INF\lib\*" com.quantfolio.desktop.DesktopApp
```

---

## Demo Login Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@quantfolio.com | admin123 |
| Investor | alice@example.com | user123 |

---

## What the App Demonstrates

### Live Database Connection
- All data fetched in real time from MySQL via JDBC
- Portfolio values calculated live from `portfolio_holdings` table
- Sector charts built from live `sectors` and `stocks` data

### Triggers in Action
Run a BUY transaction in the app, then check in MySQL Workbench:
```sql
SELECT * FROM portfolio_holdings;
SELECT * FROM audit_log ORDER BY log_id DESC LIMIT 5;
```
Both tables update automatically — that is the trigger firing.

### Advanced SQL Features
- 5 analytical views powering the dashboard
- Window functions for stock performance ranking
- Sector allocation with percentage calculations

---

## Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Java 17 |
| Web Layer | Servlets 4.0, JSP 2.3, JSTL 1.2 |
| Database | MySQL 8.0 |
| DB Access | JDBC (mysql-connector-j 8.0.33) |
| Desktop | Java Swing |
| Charts | Chart.js |
| Build | Apache Maven 3.9 |
| Server | Jetty 10 (embedded) |
| Security | BCrypt password hashing |

---

## Troubleshooting

| Issue | Fix |
|-------|-----|
| mvn not recognised | Open fresh Command Prompt after setting PATH |
| MySQL connection failed | Check password in db.properties matches MySQL root password |
| Build failed | Run mvn clean package again, check Java 17 installed |
| Swing app blank | Make sure mvn clean package ran successfully first |
| Port 8080 in use | Close other apps using port 8080 or change Jetty port in pom.xml |

---

## Project Structure

```
Quantfolio/
├── pom.xml                          Maven build config
├── src/main/
│   ├── java/com/quantfolio/
│   │   ├── dao/                     Database Access Objects (JDBC)
│   │   ├── model/                   POJOs
│   │   ├── servlet/                 MVC Controllers
│   │   ├── desktop/                 Java Swing Admin App
│   │   └── util/                    DBConnection, PasswordUtil
│   ├── resources/
│   │   └── db.properties            Database credentials (not in git)
│   └── webapp/
│       ├── WEB-INF/views/           JSP pages
│       ├── css/style.css
│       └── js/charts.js
└── db/
    ├── schema.sql                   10 table definitions
    ├── seed_data.sql                Sample stocks, users, transactions
    ├── triggers.sql                 4 auto-update triggers
    └── views.sql                    5 analytical views
```

---

Developed by Paarth Jain | Academic Project
