# QUANTFOLIO - AUTOMATED SETUP & RUN GUIDE

## ⚡ QUICKEST WAY TO GET RUNNING

### 3 Easy Steps:

#### Step 1: Install 3 Tools (One-time, ~15 minutes each)

**Java 21:**
1. Go to: https://www.oracle.com/java/technologies/downloads/#java21
2. Download: "Windows x64 Installer"
3. Run installer → Next → Next → Finish
4. Set environment variable:
   - Win+X → System
   - Advanced system settings → Environment Variables
   - New → JAVA_HOME = C:\Program Files\Java\jdk-21.x.x
5. Restart Command Prompt

**Maven:**
1. Go to: https://maven.apache.org/download.cgi
2. Download: apache-maven-3.9.x-bin.zip
3. Extract to: C:\Maven
4. Set environment variables:
   - New → MAVEN_HOME = C:\Maven\apache-maven-3.9.x
   - Edit PATH → Add C:\Maven\apache-maven-3.9.x\bin
5. Restart Command Prompt

**MySQL:**
1. Go to: https://dev.mysql.com/downloads/mysql/
2. Download: MySQL Community Server (Windows x64)
3. Run installer → Next → Server only → Next → 3306 → Next
4. Set root password (e.g., "password123") → Remember it!
5. Finish → Start service (Win+R: services.msc → MySQL80 → Start)

#### Step 2: Run ONE Script

```bash
cd C:\Users\[YourUsername]\OneDrive\Desktop\Quant\Quantfolio-main
SETUP_AND_RUN.bat
```

When prompted: Enter your MySQL root password

#### Step 3: Done! ✓

The script will:
- ✓ Verify Java, Maven, MySQL installed
- ✓ Create database (quantfolio)
- ✓ Load schema & sample data
- ✓ Create triggers
- ✓ Build project with Maven
- ✓ Launch Swing desktop app

---

## 📋 WHAT HAPPENS WHEN YOU RUN THE SCRIPT

### [PHASE 1] Verification
```
✓ Java 21 found
✓ Maven found  
✓ MySQL found
✓ MySQL Service running
```

### [PHASE 2] Configuration
```
Enter MySQL root password: ________
✓ Connection successful
```

### [PHASE 3] Update Credentials
```
✓ DBConnection.java updated with your password
```

### [PHASE 4] Database Setup
```
✓ Schema created (01_schema.sql)
✓ Sample data loaded (02_seed_data.sql)
✓ Views created (03_views.sql)
✓ Triggers created (04_triggers.sql)
```

Database created with:
- 3 users: admin, alice, bob
- 20 stocks: AAPL, MSFT, GOOGL, etc.
- Sample transactions
- 4 automatic triggers

### [PHASE 5] Verification
```
✓ Database tables verified (8 tables)
```

### [PHASE 6] Build
```
✓ Maven clean install successful
✓ Project compiled (target/quantfolio-1.0.war)
```

### [PHASE 7] Launch
```
Swing Application Opens!
```

---

## 🎯 WHAT YOU GET

### Swing Desktop Window:

```
┌─────────────────────────────────────────────┐
│ Quantfolio — Admin Desktop                  │
├─────────────────────────────────────────────┤
│ 📈 Quantfolio│ 📊Analytics │ 📋Stocks │     │
│ Admin        │ 📋Stocks    │ 🔄Txns   │     │
│              │ 🔄Txns      │ 👤Users  │     │
│              │ 👤Users     │          │     │
│              │             │          │     │
│              │ Total Users: 3         │     │
│              │ Total Txns: 22         │     │
│              │ Active Portfolios: 2   │     │
│              │ Stocks: 20             │     │
└─────────────────────────────────────────────┘
```

### Live Database Connection:

```
✓ Connected to MySQL (localhost:3306)
✓ Database: quantfolio
✓ Tables: users, stocks, transactions, portfolio_holdings, audit_log, etc.
✓ Triggers: 4 active (auto-update portfolio on transactions)
```

### What You Can Do:

1. **View data** - See all users, stocks, transactions in tables
2. **Verify database** - Check that data is live from MySQL
3. **Test transactions** - Insert into database, watch triggers fire
4. **Understand flow** - See how Swing GUI connects to database

---

## 🔧 TROUBLESHOOTING

### Issue: "Java not found"
```
Solution: Install Java 21, set JAVA_HOME, restart Command Prompt
Link: https://www.oracle.com/java/technologies/downloads/#java21
```

### Issue: "Maven not found"
```
Solution: Install Maven, add to PATH, restart Command Prompt
Link: https://maven.apache.org/download.cgi
```

### Issue: "MySQL not found"
```
Solution: Install MySQL Server, start service
Link: https://dev.mysql.com/downloads/mysql/
```

### Issue: "MySQL connection failed"
```
Solution: 
1. Check MySQL service is running (services.msc)
2. Check root password is correct
3. Restart Command Prompt
```

### Issue: "Build failed"
```
Solution:
1. Delete target folder
2. Run SETUP_AND_RUN.bat again
3. Check Java 21 installed (java -version)
```

### Issue: "Swing window won't open"
```
Solution:
1. Check database connection (see MySQL section above)
2. Verify DBConnection.java password matches MySQL
3. Check build succeeded (target folder exists)
```

---

## ✅ VERIFICATION STEPS

After script completes, verify everything works:

### 1. Swing App Running?
- [ ] Window titled "Quantfolio — Admin Desktop" visible
- [ ] Tabs visible: Analytics, Stocks, Transactions, Users
- [ ] Tables showing data

### 2. Database Connected?

Open MySQL console:
```bash
mysql -u root -p
USE quantfolio;
SHOW TABLES;
SELECT COUNT(*) FROM users;    -- Should show: 3
SELECT COUNT(*) FROM stocks;   -- Should show: 20
SHOW TRIGGERS;                 -- Should show: 4
exit
```

### 3. Data Flowing?

In MySQL:
```sql
-- Add a test transaction
INSERT INTO transactions (user_id, stock_id, type, quantity, price, notes)
VALUES (1, 1, 'BUY', 10, 150.00, 'Test');

-- Check portfolio was auto-updated by trigger
SELECT * FROM portfolio_holdings WHERE user_id=1 AND stock_id=1;
-- Should show: quantity=10, avg_buy_price=150.00

-- Check audit log was created by trigger
SELECT * FROM audit_log WHERE user_id=1 ORDER BY log_id DESC LIMIT 1;
-- Should show: action=BUY_STOCK with timestamp
```

---

## 📁 IMPORTANT LOCATIONS

| What | Path |
|------|------|
| Setup script | `Quantfolio-main\SETUP_AND_RUN.bat` |
| Source code | `Quantfolio-main\src\main\java\com\quantfolio\` |
| Database scripts | `Quantfolio-main\db\` |
| Built app | `Quantfolio-main\target\quantfolio-1.0.war` |
| Java | `C:\Program Files\Java\jdk-21.x.x` |
| Maven | `C:\Maven\apache-maven-3.9.x` |
| MySQL data | `C:\ProgramData\MySQL\MySQL Server 8.0\data\` |

---

## 🚀 RUNNING AGAIN

Once everything is set up, to run the app again:

**Option A: Run the batch file again**
```bash
cd Quantfolio-main
SETUP_AND_RUN.bat
# Skip setup, just launches app
```

**Option B: Run from IDE (IntelliJ IDEA)**
```
1. Open Quantfolio-main project
2. Navigate to: src/main/java/com/quantfolio/desktop/DesktopApp.java
3. Right-click → Run DesktopApp.main()
```

**Option C: Run from command line**
```bash
cd Quantfolio-main
java -cp "target/quantfolio-1.0.war;target/quantfolio-1.0/WEB-INF/lib/*" com.quantfolio.desktop.DesktopApp
```

---

## 📊 EXPECTED TIMELINE

| Phase | Duration | What Happens |
|-------|----------|--------------|
| Java install | 5-10 min | Download + Install + Set JAVA_HOME |
| Maven install | 5-10 min | Download + Extract + Set PATH |
| MySQL install | 5-10 min | Download + Install + Set password |
| SETUP_AND_RUN.bat | 3-5 min | Auto setup, build, and run |
| **Total** | **20-35 min** | **Full working application** |

---

## 🎉 CONGRATULATIONS!

Once the script completes, you have:

✅ **Swing Desktop Application** running
✅ **MySQL Database** with live connection
✅ **8 Database Tables** with sample data
✅ **4 Triggers** auto-updating portfolio
✅ **Complete Integration** Swing ↔ JDBC ↔ MySQL

You can now:
- See transactions in real-time
- Test database triggers
- Verify portfolio updates
- Check audit logs
- Understand Swing + SQL integration

---

## 📞 NEED HELP?

1. **Stuck on setup?** → Check TROUBLESHOOTING section above
2. **Want to understand the code?** → Read BACKEND_ARCHITECTURE.md
3. **Want to see how it works?** → Read SWING_SQL_INTEGRATION_DEMO.md
4. **Need quick answers?** → Check README_QUICK_REF.md

---

**Ready? Run this command:**

```batch
cd C:\Users\[YourUsername]\OneDrive\Desktop\Quant\Quantfolio-main
SETUP_AND_RUN.bat
```

Everything else is automated! ✓
