# QUANTFOLIO VIVA PREPARATION GUIDE

---

## PART 1: SQL CONCEPTS & TABLE DESIGN

### 1. CONSTRAINTS - WHY & HOW?

**Q: What are the main constraints you used in your schema?**

**A:**
```sql
PRIMARY KEY - unique identifier for each row
  Example: user_id in users table
  Why: Ensures no duplicate records, speeds up queries

FOREIGN KEY - links tables together
  Example: user_id in transactions → references users.user_id
  Why: Maintains data integrity, prevents orphaned records

UNIQUE - no duplicate values
  Example: email in users table
  Why: Each user has unique email, prevents duplicate registrations

NOT NULL - field must have value
  Example: name, email, password_hash NOT NULL
  Why: Ensures required data is always present

CHECK - validates data
  Example: quantity > 0 in transactions
  Why: Prevents invalid data (negative quantities, etc.)

DEFAULT - automatic value if not provided
  Example: created_at DEFAULT CURRENT_TIMESTAMP
  Why: Auto-fills timestamps without manual input
```

---

### 2. TABLE DESIGN DECISIONS

**Q: Why did you create these tables separately?**

**Schema Overview:**
```
users (id, name, email, password_hash, role)
    ↓ (1-to-many)
stocks (id, symbol, name, price)
    ↓ (many-to-many via portfolio_holdings)
transactions (id, user_id, stock_id, type, quantity, price)
    ↓
portfolio_holdings (user_id, stock_id, quantity, avg_buy_price)
    ↓
audit_log (log_id, user_id, action, timestamp)
```

**Normalization (3NF - Third Normal Form):**
- Each table has ONE purpose
- No duplicate data across tables
- All fields depend on PRIMARY KEY

**Example:**
```
❌ BAD (Denormalized):
users: (id, name, email, password, stock1, qty1, stock2, qty2, stock3, qty3)
Problem: Wasted space, hard to add more stocks, data anomalies

✓ GOOD (Normalized):
users: (id, name, email, password)
portfolio_holdings: (user_id, stock_id, quantity, avg_buy_price)
Problem: None, flexible and clean
```

---

### 3. EDGE CASES IN SQL

**Q: What edge cases did you handle?**

**A:**
```sql
1. ZERO or NEGATIVE VALUES
   Problem: User tries to buy -10 stocks or 0 stocks
   Solution: CHECK (quantity > 0) in transactions table
   
2. NULL VALUES
   Problem: User submits form without entering name
   Solution: NOT NULL constraint on critical fields
   
3. DUPLICATE ENTRIES
   Problem: User clicks "Buy" button twice → 2 transactions
   Solution: Application-level check in Swing
   
4. DECIMAL PRECISION
   Problem: Stock price 150.123456... loses precision
   Solution: DECIMAL(10, 2) → max 99999999.99
   
5. CONCURRENT TRANSACTIONS
   Problem: Two users buy same stock simultaneously
   Solution: Database locking, triggers update portfolio atomically
   
6. DELETED USERS
   Problem: Delete user but transactions still reference them
   Solution: ON DELETE CASCADE or ON DELETE RESTRICT
   
7. FLOATING POINT ERRORS
   Problem: 0.1 + 0.2 ≠ 0.3 in computers
   Solution: Use DECIMAL not FLOAT for money
   
8. DATE/TIME ISSUES
   Problem: Timezone differences, incorrect timestamps
   Solution: TIMESTAMP with UTC timezone
```

---

### 4. TRIGGERS - AUTOMATION

**Q: Why use triggers instead of handling in application?**

**A:**
```sql
TRIGGER 1: Auto-update portfolio when transaction inserted
  Purpose: Keep portfolio_holdings in sync with transactions
  Why trigger: Database enforces this rule always, 
              even if bypassed from app
  
TRIGGER 2: Create audit log entry for every transaction
  Purpose: Track who did what and when
  Why trigger: Happens automatically, can't be forgotten
```

**Example:**
```sql
CREATE TRIGGER update_portfolio_after_transaction
AFTER INSERT ON transactions
FOR EACH ROW
BEGIN
  IF NEW.type = 'BUY' THEN
    INSERT INTO portfolio_holdings (user_id, stock_id, quantity, avg_buy_price)
    VALUES (NEW.user_id, NEW.stock_id, NEW.quantity, NEW.price)
    ON DUPLICATE KEY UPDATE
      quantity = quantity + NEW.quantity,
      avg_buy_price = (avg_buy_price * (quantity - NEW.quantity) + NEW.price * NEW.quantity) / quantity;
  END IF;
END;
```

---

### 5. INDEXING

**Q: Did you add any indexes?**

**A:**
```sql
-- Without index: SELECT searches every row (slow)
-- With index: Database uses B-tree structure (fast)

CREATE INDEX idx_user_email ON users(email);
  -- Why: Email used in WHERE clause (login search)
  -- Speed: 1M rows: 1 sec → 1 ms

CREATE INDEX idx_transaction_user ON transactions(user_id);
  -- Why: Find all transactions for user_id
  
CREATE INDEX idx_portfolio_user_stock ON portfolio_holdings(user_id, stock_id);
  -- Why: Find specific user's specific stock holding
```

---

### 6. VIEWS

**Q: What are views and why use them?**

**A:**
```sql
VIEW = Saved SELECT query that looks like a table

Example:
CREATE VIEW user_portfolio_summary AS
SELECT 
  u.name,
  COUNT(DISTINCT ph.stock_id) as num_stocks,
  SUM(ph.quantity * s.price) as total_value
FROM users u
LEFT JOIN portfolio_holdings ph ON u.id = ph.user_id
LEFT JOIN stocks s ON ph.stock_id = s.id
GROUP BY u.id;

Why:
- Reusable: Use in multiple reports without rewriting query
- Security: Can grant access to view, not underlying tables
- Simplicity: Complex joins hidden, users see simple table
```

---

## PART 2: SWING GUI CONCEPTS

### 1. ARCHITECTURE - MVC PATTERN

**Q: How is your Swing app structured?**

**A:**
```
Model (Data Layer):
  - DBConnection.java: connects to MySQL
  - PasswordUtil.java: handles authentication
  - Data objects (User, Stock, Transaction)
  
View (UI Layer):
  - LoginFrame.java: login window
  - DesktopApp.java: main window with tabs
  - Tables display data (JTable)
  
Controller (Logic Layer):
  - Event listeners on buttons
  - Validates input (prevent nulls, negatives)
  - Calls model to update database
  - Updates view to show new data
```

**Flow:**
```
User clicks "Login" button
        ↓
ActionListener triggered
        ↓
Read email/password from text fields
        ↓
Call PasswordUtil.verify(password, hash)
        ↓
If match: Query database, show data
If no match: Show error dialog
```

---

### 2. COMPONENTS USED

**Q: What Swing components did you use and why?**

**A:**
```java
JFrame          - Main window container
JPanel          - Group related components
JTabbedPane     - Multiple tabs (Analytics, Stocks, etc.)
JTable          - Display data in rows/columns
JTextField      - Input field (username, search)
JPasswordField  - Masked password input
JButton         - Clickable actions
JLabel          - Static text
JScrollPane     - Scrolling for large tables
JDialog         - Pop-up windows (alerts, confirm)
DefaultTableModel - Manage table data dynamically
```

---

### 3. DATABASE CONNECTION

**Q: How does Swing connect to MySQL?**

**A:**
```java
JDBC (Java Database Connectivity):
  - Driver: MySQL connector/J (mysql-connector-java-8.x.jar)
  - Connection string: jdbc:mysql://localhost:3307/quantfolio
  - Username: root
  - Password: (hashed in database)

Process:
1. Load driver: Class.forName("com.mysql.cj.jdbc.Driver")
2. Create connection: DriverManager.getConnection(url, user, pass)
3. Create statement: connection.createStatement()
4. Execute query: statement.executeQuery("SELECT ...")
5. Get results: ResultSet
6. Close resources: statement.close(), connection.close()

Why close? 
- Prevent connection leaks
- Free resources
- Prevent "Too many connections" error
```

---

### 4. PASSWORD SECURITY

**Q: How did you secure passwords?**

**A:**
```java
❌ WRONG:
  Store plain text: password = "admin123"
  Problem: Hacked database = exposed all passwords

✓ CORRECT (What you did):
  Use BCrypt hashing:
  1. Hash password when user registers/changes password
     password_hash = BCrypt.hash("admin123", cost=12)
     Result: $2a$12$arotqLb8...
  
  2. When user logs in, verify:
     BCrypt.verify("admin123", stored_hash) → true/false
  
  3. BCrypt adds "salt" (random data) before hashing
     Why: Prevents rainbow table attacks
  
  4. Cost=12 means 2^12 iterations
     Why: Slow by design, prevents brute force
     Time: 0.3 seconds per password try (safe)
```

---

### 5. EVENT HANDLING

**Q: How do buttons work in Swing?**

**A:**
```java
// User clicks button
// JVM detects click event
// Calls the ActionListener you registered

button.addActionListener(new ActionListener() {
  public void actionPerformed(ActionEvent e) {
    // Code runs here
  }
});

Or with Lambda (Java 8+):
button.addActionListener(e -> {
  // Code runs here
});
```

---

### 6. TABLE UPDATES

**Q: How do you refresh data when database changes?**

**A:**
```java
// Method 1: Reload entire table
DefaultTableModel model = (DefaultTableModel) table.getModel();
model.setRowCount(0); // Clear
// Query database again
// Add new rows with table.addRow()

// Method 2: Update specific row
model.setValueAt(newValue, rowIndex, colIndex);

// Why this is important:
- User buys stock → Database updates
- But Swing table still shows old data
- Need to refresh so user sees their action
```

---

### 7. EXCEPTION HANDLING

**Q: What exceptions can occur?**

**A:**
```java
SQLException - Database connection/query failed
  Cause: MySQL not running, wrong password, table doesn't exist
  Handle: Show dialog to user, log error
  
ClassNotFoundException - JDBC driver not found
  Cause: mysql-connector-java jar missing from classpath
  Handle: Add jar to pom.xml, rebuild
  
NullPointerException - Accessing null variable
  Cause: User didn't enter email, code tries to use it
  Handle: Validate inputs before using them
  
NumberFormatException - Can't convert string to number
  Cause: User enters "abc" in quantity field, code does Integer.parseInt()
  Handle: Validate user input with try-catch
```

---

## PART 3: VIVA QUESTIONS CHEAT SHEET

### SQL Questions:
1. Why normalize tables? What problems does it solve?
2. What's the difference between PRIMARY KEY and UNIQUE?
3. What are Foreign Keys? Why use ON DELETE CASCADE?
4. What's a trigger? Give an example from your project.
5. What's normalization? Explain 1NF, 2NF, 3NF.
6. How do you prevent SQL injection?
7. What's the difference between INNER JOIN and LEFT JOIN?
8. Why use DECIMAL instead of FLOAT for money?
9. What's an index? When do you use it?
10. What's a view? Why use it?

### Swing Questions:
1. What's the difference between JFrame, JPanel, JDialog?
2. How do you add an ActionListener to a button?
3. What's JDBC? How do you connect to MySQL?
4. How do you refresh a JTable after database update?
5. What's DefaultTableModel? How do you use it?
6. How do you validate user input before saving?
7. What's password hashing? Why not store plain text?
8. How do you handle SQLException?
9. What's the MVC pattern in Swing?
10. How do you prevent database connection leaks?

### Project-Specific:
1. Why did you use triggers instead of app logic?
2. How does your login system work?
3. How do you calculate average buy price in portfolio?
4. What happens when a user deletes a stock? Why?
5. How do you prevent duplicate transactions?
6. How does audit logging work?
7. Why separate tables for users, stocks, transactions, portfolio_holdings?
8. How do you handle concurrent users buying same stock?

---

## PART 4: SAMPLE VIVA ANSWERS

### Q: Explain your table design
**A:** "I normalized the schema into 5 tables:

1. **users** - stores user credentials with BCrypt hashed passwords
   - Why separate: Follows principle of least privilege, easier to manage
   
2. **stocks** - master list of available stocks
   - Why separate: Avoids storing same stock data in multiple transactions
   
3. **transactions** - records every buy/sell action
   - Why: Historical audit trail, can regenerate portfolio anytime
   
4. **portfolio_holdings** - current position per user per stock
   - Why: Fast queries (sum values, check balance) without scanning all transactions
   
5. **audit_log** - who did what and when
   - Why: Compliance, troubleshooting, security
   
This follows 3NF (Third Normal Form) - each table has one purpose, no duplicate data, all fields depend on primary key."

---

### Q: How did you secure the login?
**A:** "Three layers:

1. **Password Storage**: I use BCrypt with cost=12
   - When user registers: hash = BCrypt.hash(password, 12)
   - Stored in database: $2a$12$arotqLb8...
   - Why: Even if database is hacked, passwords are unrecoverable
   
2. **Login Verification**: On login, compare provided password with hash
   - Code: if (BCrypt.verify(loginPassword, storedHash)) { allow login }
   - Why: BCrypt can't be reversed, so attacker can't get plain password
   
3. **Application Validation**: Check fields are not empty
   - Why: Prevent NULL pointer exceptions, reject invalid input early
"

---

### Q: Why use triggers?
**A:** "I use 2 triggers:

1. **update_portfolio_after_transaction**: Auto-updates portfolio_holdings
   - Problem it solves: If app crashes after INSERT transaction, portfolio wouldn't update
   - Solution: Trigger is in database, runs automatically
   - Why database, not app: Ensures consistency even if app bypassed
   
2. **create_audit_log**: Auto-logs every transaction
   - Problem it solves: Easy to forget logging if done in app
   - Solution: Trigger ensures audit log always created
   - Benefit: Can't accidentally disable logging
"

---

### Q: Edge case - what if user tries to buy negative quantity?
**A:** "Handled in 3 places:

1. **Database Constraint**: CHECK (quantity > 0)
   - Last line of defense
   
2. **Application Validation**: 
   ```java
   int qty = Integer.parseInt(quantityField.getText());
   if (qty <= 0) {
     JOptionPane.showMessageDialog(null, "Quantity must be > 0");
     return;
   }
   ```
   - Faster feedback to user, prevents unnecessary database call
   
3. **Try-Catch**: Catch NumberFormatException if user enters "abc"
   - Graceful error handling
"

---

**Good luck with your viva! Focus on WHY, not just WHAT. Examiners want to know your design reasoning.**
