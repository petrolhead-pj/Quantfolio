package com.quantfolio.desktop;

import com.quantfolio.dao.AuditDAO;
import com.quantfolio.dao.HoldingDAO;
import com.quantfolio.dao.StockDAO;
import com.quantfolio.dao.TransactionDAO;
import com.quantfolio.dao.UserDAO;
import com.quantfolio.model.Holding;
import com.quantfolio.model.PortfolioSummary;
import com.quantfolio.model.Stock;
import com.quantfolio.model.Transaction;
import com.quantfolio.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Quantfolio desktop application.
 *
 * This is the primary project UI: Java Swing frontend + JDBC DAO layer + MySQL backend.
 */
public class DesktopApp {

    private static final Color BG = new Color(15, 23, 42);
    private static final Color PANEL = new Color(30, 41, 59);
    private static final Color PANEL_2 = new Color(39, 53, 72);
    private static final Color BORDER = new Color(51, 65, 85);
    private static final Color TEXT = new Color(226, 232, 240);
    private static final Color MUTED = new Color(148, 163, 184);
    private static final Color PRIMARY = new Color(99, 102, 241);
    private static final Color GREEN = new Color(16, 185, 129);
    private static final Color RED = new Color(244, 63, 94);

    private static final UserDAO userDAO = new UserDAO();
    private static final StockDAO stockDAO = new StockDAO();
    private static final HoldingDAO holdingDAO = new HoldingDAO();
    private static final TransactionDAO transactionDAO = new TransactionDAO();
    private static final AuditDAO auditDAO = new AuditDAO();

    private JFrame frame;
    private JPanel root;
    private CardLayout cardLayout;
    private User currentUser;

    private final DefaultTableModel holdingsModel = model(
            "Symbol", "Company", "Sector", "Qty", "Avg Buy", "Current", "Invested", "Value", "P&L", "Return %");
    private final DefaultTableModel transactionsModel = model(
            "ID", "Date", "Stock", "Type", "Qty", "Price", "Total", "Notes");
    private final DefaultTableModel stocksModel = model(
            "ID", "Symbol", "Company", "Sector", "Current Price");
    private final DefaultTableModel sqlConnectionModel = model(
            "Connection Property", "Live Value");
    private final DefaultTableModel sqlTableStatsModel = model(
            "Database Table", "Current Rows", "What It Demonstrates");
    private final DefaultTableModel sqlQueriesModel = model(
            "Operation", "SQL Query", "Demo Point");
    private final DefaultTableModel sqlUsersModel = model(
            "user_id", "name", "email", "role", "created_at");
    private final DefaultTableModel sqlStocksModel = model(
            "stock_id", "symbol", "company_name", "sector_id", "current_price", "updated_at");
    private final DefaultTableModel sqlTransactionsRawModel = model(
            "transaction_id", "user_id", "stock_id", "type", "quantity", "price", "transaction_date", "notes");
    private final DefaultTableModel sqlHoldingsRawModel = model(
            "holding_id", "user_id", "stock_id", "quantity", "avg_buy_price", "updated_at");
    private final DefaultTableModel sqlAuditRawModel = model(
            "log_id", "user_id", "action", "detail", "ip_address", "logged_at");
    private final DefaultTableModel sqlRecentTransactionsModel = model(
            "Txn ID", "User", "Stock", "Type", "Qty", "Price", "Total", "Date");
    private final DefaultTableModel sqlAuditModel = model(
            "Log ID", "User", "Action", "Detail", "Logged At");

    private JComboBox<StockItem> tradeStockSelect;
    private JComboBox<String> tradeTypeSelect;
    private JTextField tradeQuantityField;
    private JTextField tradePriceField;
    private JTextField tradeNotesField;
    private JLabel statusLabel;
    private JPanel summaryPanel;
    private PortfolioPiePanel piePanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new DesktopApp().show();
        });
    }

    private void show() {
        frame = new JFrame("Quantfolio - Swing JDBC SQL Portfolio System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1400, 860));
        frame.setSize(1400, 860);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        root = new JPanel(cardLayout);
        root.add(buildAuthPanel(false), "login");
        root.add(buildAuthPanel(true), "register");
        frame.setContentPane(root);
        frame.setVisible(true);
    }

    private JPanel buildAuthPanel(boolean registerMode) {
        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(BG);

        JPanel card = panel(new BorderLayout(0, 18));
        card.setPreferredSize(new Dimension(420, registerMode ? 520 : 420));
        card.setBorder(new EmptyBorder(30, 34, 30, 34));

        JLabel title = new JLabel(registerMode ? "Create Account" : "Quantfolio Login");
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        JLabel subtitle = new JLabel("Java Swing frontend connected to MySQL through JDBC");
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel heading = transparentPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);
        card.add(heading, BorderLayout.NORTH);

        JPanel form = transparentPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JTextField name = field();
        JTextField email = field();
        JPasswordField password = passwordField();
        JPasswordField confirm = passwordField();

        if (!registerMode) {
            email.setText("admin@quantfolio.com");
            password.setText("admin123");
        }

        if (registerMode) {
            form.add(label("Full Name"));
            form.add(name);
            form.add(Box.createVerticalStrut(12));
        }
        form.add(label("Email"));
        form.add(email);
        form.add(Box.createVerticalStrut(12));
        form.add(label("Password"));
        form.add(password);
        form.add(Box.createVerticalStrut(12));
        if (registerMode) {
            form.add(label("Confirm Password"));
            form.add(confirm);
            form.add(Box.createVerticalStrut(12));
        }

        JButton primary = button(registerMode ? "Register User" : "Login");
        primary.addActionListener(e -> {
            if (registerMode) {
                register(name.getText(), email.getText(), new String(password.getPassword()), new String(confirm.getPassword()));
            } else {
                login(email.getText(), new String(password.getPassword()));
            }
        });

        JButton switchButton = secondaryButton(registerMode ? "Back to Login" : "Create New User");
        switchButton.addActionListener(e -> cardLayout.show(root, registerMode ? "login" : "register"));

        form.add(primary);
        form.add(Box.createVerticalStrut(10));
        form.add(switchButton);
        card.add(form, BorderLayout.CENTER);

        page.add(card);
        return page;
    }

    private void login(String email, String password) {
        try {
            User user = userDAO.authenticate(email.trim(), password);
            if (user == null) {
                showError("Invalid email or password.");
                return;
            }
            currentUser = user;
            tryLog("LOGIN", "Desktop login for " + user.getEmail());
            root.add(buildMainPanel(), "main");
            refreshAll();
            cardLayout.show(root, "main");
            frame.setTitle("Quantfolio - " + currentUser.getName());
        } catch (Exception ex) {
            showError("Login failed: " + ex.getMessage());
        }
    }

    private void register(String name, String email, String password, String confirm) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            showError("All fields are required.");
            return;
        }
        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }

        try {
            User user = userDAO.registerAndReturn(name.trim(), email.trim(), password);
            if (user == null) {
                showError("Email already registered.");
                return;
            }
            tryLog(user.getUserId(), "REGISTER_USER", "Desktop registration for " + user.getEmail());
            JOptionPane.showMessageDialog(frame, "User registered successfully. You can now login.");
            cardLayout.show(root, "login");
        } catch (Exception ex) {
            showError("Registration failed: " + ex.getMessage());
        }
    }

    private JPanel buildMainPanel() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(BG);
        page.add(buildSidebar(), BorderLayout.WEST);

        JTabbedPane tabs = new JTabbedPane();
        styleTabs(tabs);
        tabs.addTab("Dashboard", buildDashboardTab());
        tabs.addTab("Trade", buildTradeTab());
        tabs.addTab("Portfolio", tableTab("Current Holdings", holdingsModel));
        tabs.addTab("Transactions", tableTab("Transaction History", transactionsModel));
        tabs.addTab("Stocks", tableTab("Stock Universe", stocksModel));
        tabs.addTab("SQL/JDBC Demo", buildSqlDemoTab());
        colorTabText(tabs);
        page.add(tabs, BorderLayout.CENTER);

        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(MUTED);
        statusLabel.setBorder(new EmptyBorder(8, 14, 8, 14));
        page.add(statusLabel, BorderLayout.SOUTH);
        return page;
    }

    private JPanel buildSidebar() {
        JPanel side = panel();
        side.setPreferredSize(new Dimension(230, 0));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(new EmptyBorder(22, 18, 22, 18));

        JLabel brand = new JLabel("Quantfolio");
        brand.setForeground(PRIMARY);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JLabel stack = new JLabel("Swing + JDBC + MySQL");
        stack.setForeground(MUTED);

        side.add(brand);
        side.add(Box.createVerticalStrut(4));
        side.add(stack);
        side.add(Box.createVerticalStrut(24));
        side.add(smallText("Logged in as"));
        side.add(bigText(currentUser.getName()));
        side.add(Box.createVerticalStrut(4));
        side.add(smallText(currentUser.getEmail()));
        side.add(Box.createVerticalStrut(4));
        side.add(smallText("Role: " + currentUser.getRole().toUpperCase() +
                (currentUser.isAdmin() ? " - full SQL table access" : " - own records only")));
        side.add(Box.createVerticalStrut(24));

        JButton refresh = button("Refresh From SQL");
        refresh.addActionListener(e -> refreshAll());
        JButton logout = secondaryButton("Logout");
        logout.addActionListener(e -> logout());

        side.add(refresh);
        side.add(Box.createVerticalStrut(10));
        side.add(logout);
        side.add(Box.createVerticalGlue());
        side.add(smallText("Every table is loaded through DAO classes using JDBC PreparedStatements."));
        return side;
    }

    private JPanel buildDashboardTab() {
        JPanel tab = new JPanel(new BorderLayout(0, 16));
        tab.setBackground(BG);
        tab.setBorder(new EmptyBorder(20, 20, 20, 20));

        summaryPanel = new JPanel(new GridLayout(1, 4, 14, 14));
        summaryPanel.setOpaque(false);
        tab.add(summaryPanel, BorderLayout.NORTH);

        piePanel = new PortfolioPiePanel();
        JPanel pieCard = panel(new BorderLayout());
        pieCard.setBorder(new EmptyBorder(18, 18, 18, 18));
        JLabel pieTitle = new JLabel("Portfolio Allocation");
        pieTitle.setForeground(TEXT);
        pieTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pieCard.add(pieTitle, BorderLayout.NORTH);
        pieCard.add(piePanel, BorderLayout.CENTER);
        pieCard.setPreferredSize(new Dimension(390, 0));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pieCard, new JScrollPane(table(holdingsModel)));
        split.setResizeWeight(0.28);
        split.setDividerLocation(390);
        split.setBorder(null);
        split.setBackground(BG);
        tab.add(split, BorderLayout.CENTER);
        return tab;
    }

    private JPanel buildTradeTab() {
        JPanel tab = new JPanel(new BorderLayout(18, 18));
        tab.setBackground(BG);
        tab.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel form = panel();
        form.setLayout(new GridBagLayout());
        form.setBorder(new EmptyBorder(22, 22, 22, 22));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        tradeStockSelect = new JComboBox<>();
        tradeTypeSelect = new JComboBox<>(new String[]{"BUY", "SELL"});
        tradeQuantityField = field();
        tradePriceField = field();
        tradeNotesField = field();

        addFormRow(form, gbc, 0, "Stock", tradeStockSelect);
        addFormRow(form, gbc, 1, "Type", tradeTypeSelect);
        addFormRow(form, gbc, 2, "Quantity", tradeQuantityField);
        addFormRow(form, gbc, 3, "Price per Share", tradePriceField);
        addFormRow(form, gbc, 4, "Notes", tradeNotesField);

        JButton record = button("Record Trade In SQL");
        record.addActionListener(e -> recordTrade());
        gbc.gridx = 1;
        gbc.gridy = 5;
        form.add(record, gbc);

        JTextArea explanation = new JTextArea(
                "Demo flow:\n" +
                "1. Swing reads this form.\n" +
                "2. TransactionDAO inserts into MySQL using JDBC.\n" +
                "3. MySQL triggers update portfolio_holdings and audit_log.\n" +
                "4. Refresh reloads portfolio, transactions, and SQL/JDBC demo tables.");
        explanation.setEditable(false);
        explanation.setLineWrap(true);
        explanation.setWrapStyleWord(true);
        explanation.setForeground(TEXT);
        explanation.setBackground(PANEL);
        explanation.setBorder(new EmptyBorder(22, 22, 22, 22));

        tab.add(form, BorderLayout.NORTH);
        tab.add(explanation, BorderLayout.CENTER);
        return tab;
    }

    private JPanel tableTab(String title, DefaultTableModel model) {
        JPanel tab = new JPanel(new BorderLayout(0, 14));
        tab.setBackground(BG);
        tab.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel heading = new JLabel(title);
        heading.setForeground(TEXT);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tab.add(heading, BorderLayout.NORTH);
        tab.add(new JScrollPane(table(model)), BorderLayout.CENTER);
        return tab;
    }

    private JPanel buildSqlDemoTab() {
        JPanel tab = new JPanel(new BorderLayout(0, 14));
        tab.setBackground(BG);
        tab.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        JLabel heading = new JLabel("SQL/JDBC Demonstration Console");
        heading.setForeground(TEXT);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JButton refresh = button("Run SELECT Queries Again");
        refresh.addActionListener(e -> {
            try {
                refreshSqlDemo();
                setStatus("SQL demo tables refreshed with live JDBC queries.");
            } catch (SQLException ex) {
                showError("SQL refresh failed: " + ex.getMessage());
            }
        });
        header.add(heading, BorderLayout.WEST);
        header.add(refresh, BorderLayout.EAST);

        JTabbedPane sqlTabs = new JTabbedPane();
        styleTabs(sqlTabs);
        sqlTabs.addTab("Connection", sqlTable(sqlConnectionModel));
        sqlTabs.addTab("Database Tables", sqlTable(sqlTableStatsModel));
        sqlTabs.addTab("Queries", sqlTable(sqlQueriesModel));
        sqlTabs.addTab("SELECT * users", sqlTable(sqlUsersModel));
        sqlTabs.addTab("SELECT * stocks", sqlTable(sqlStocksModel));
        sqlTabs.addTab("SELECT * transactions", sqlTable(sqlTransactionsRawModel));
        sqlTabs.addTab("SELECT * holdings", sqlTable(sqlHoldingsRawModel));
        sqlTabs.addTab("SELECT * audit_log", sqlTable(sqlAuditRawModel));
        sqlTabs.addTab("Recent Transactions", sqlTable(sqlRecentTransactionsModel));
        sqlTabs.addTab("Audit Log", sqlTable(sqlAuditModel));
        colorTabText(sqlTabs);

        JTextArea guide = new JTextArea(
                "For demonstration: record a trade in the Trade tab, then return here and click Run SELECT Queries Again. " +
                "Open the SELECT * tabs to show complete table-style SQL results, just like a database client.");
        guide.setEditable(false);
        guide.setLineWrap(true);
        guide.setWrapStyleWord(true);
        guide.setForeground(TEXT);
        guide.setBackground(PANEL);
        guide.setBorder(new EmptyBorder(12, 14, 12, 14));

        tab.add(header, BorderLayout.NORTH);
        tab.add(sqlTabs, BorderLayout.CENTER);
        tab.add(guide, BorderLayout.SOUTH);
        return tab;
    }

    private JScrollPane sqlTable(DefaultTableModel model) {
        JTable table = table(model);
        table.setPreferredScrollableViewportSize(new Dimension(1050, 520));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        return scrollPane;
    }

    private void refreshAll() {
        try {
            refreshDashboardAndPortfolio();
            refreshTransactions();
            refreshStocks();
            refreshSqlDemo();
            setStatus("Refreshed from MySQL through JDBC.");
        } catch (Exception ex) {
            showError("Refresh failed: " + ex.getMessage());
        }
    }

    private void refreshDashboardAndPortfolio() throws SQLException {
        holdingsModel.setRowCount(0);
        summaryPanel.removeAll();

        PortfolioSummary summary = holdingDAO.getSummary(currentUser.getUserId());
        summaryPanel.add(statCard("Portfolio Value", money(summary.getPortfolioValue()), PRIMARY));
        summaryPanel.add(statCard("Total Invested", money(summary.getTotalInvested()), MUTED));
        BigDecimal unrealizedPnl = valueOrZero(summary.getUnrealizedPnl());
        summaryPanel.add(statCard("Unrealized P&L", money(unrealizedPnl), unrealizedPnl.signum() >= 0 ? GREEN : RED));
        summaryPanel.add(statCard("Stocks Held", String.valueOf(summary.getTotalStocks()), GREEN));

        List<Holding> holdings = holdingDAO.findByUser(currentUser.getUserId());
        for (Holding h : holdings) {
            holdingsModel.addRow(new Object[]{
                    h.getSymbol(),
                    h.getCompanyName(),
                    h.getSectorName(),
                    h.getQuantity(),
                    money(h.getAvgBuyPrice()),
                    money(h.getCurrentPrice()),
                    money(h.getInvestedValue()),
                    money(h.getCurrentValue()),
                    money(h.getUnrealizedPnl()),
                    h.getReturnPct()
            });
        }
        if (piePanel != null) {
            piePanel.setHoldings(holdings);
        }

        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    private void refreshTransactions() throws SQLException {
        transactionsModel.setRowCount(0);
        for (Transaction t : transactionDAO.findByUser(currentUser.getUserId())) {
            transactionsModel.addRow(new Object[]{
                    t.getTransactionId(),
                    t.getTransactionDate(),
                    t.getSymbol(),
                    t.getType(),
                    t.getQuantity(),
                    money(t.getPrice()),
                    money(t.getTotalValue()),
                    t.getNotes()
            });
        }
    }

    private void refreshStocks() throws SQLException {
        stocksModel.setRowCount(0);
        tradeStockSelect.removeAllItems();
        List<Stock> stocks = stockDAO.findAll();
        for (Stock s : stocks) {
            stocksModel.addRow(new Object[]{
                    s.getStockId(),
                    s.getSymbol(),
                    s.getCompanyName(),
                    s.getSectorName(),
                    money(s.getCurrentPrice())
            });
            tradeStockSelect.addItem(new StockItem(s));
        }
    }

    private void refreshSqlDemo() throws SQLException {
        boolean admin = currentUser.isAdmin();
        sqlConnectionModel.setRowCount(0);
        sqlTableStatsModel.setRowCount(0);
        sqlQueriesModel.setRowCount(0);
        sqlUsersModel.setRowCount(0);
        sqlStocksModel.setRowCount(0);
        sqlTransactionsRawModel.setRowCount(0);
        sqlHoldingsRawModel.setRowCount(0);
        sqlAuditRawModel.setRowCount(0);
        sqlRecentTransactionsModel.setRowCount(0);
        sqlAuditModel.setRowCount(0);

        for (Map.Entry<String, Object> entry : auditDAO.connectionStatus().entrySet()) {
            sqlConnectionModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }

        List<Map<String, Object>> stats = admin
                ? auditDAO.tableStats()
                : auditDAO.tableStatsForUser(currentUser.getUserId());
        for (Map<String, Object> row : stats) {
            String tableName = String.valueOf(row.get("table_name"));
            sqlTableStatsModel.addRow(new Object[]{tableName, row.get("row_count"), tablePurpose(tableName)});
        }

        addQueryRows();
        addSelectStarRows(admin);

        List<Map<String, Object>> recentTransactions = admin
                ? auditDAO.latestTransactions(12)
                : auditDAO.latestTransactionsForUser(currentUser.getUserId(), 12);
        for (Map<String, Object> row : recentTransactions) {
            sqlRecentTransactionsModel.addRow(new Object[]{
                    row.get("transaction_id"),
                    row.get("user_name"),
                    row.get("symbol"),
                    row.get("type"),
                    row.get("quantity"),
                    money(asBigDecimal(row.get("price"))),
                    money(asBigDecimal(row.get("total_value"))),
                    row.get("transaction_date")
            });
        }

        List<Map<String, Object>> auditRows = admin
                ? auditDAO.latestAuditRows(12)
                : auditDAO.latestAuditRowsForUser(currentUser.getUserId(), 12);
        for (Map<String, Object> row : auditRows) {
            sqlAuditModel.addRow(new Object[]{
                    row.get("log_id"),
                    row.get("name") + " #" + row.get("user_id"),
                    row.get("action"),
                    row.get("detail"),
                    row.get("logged_at")
            });
        }
    }

    private void addSelectStarRows(boolean admin) throws SQLException {
        addRows(sqlUsersModel, preview("users", admin, 20),
                "user_id", "name", "email", "role", "created_at");
        addRows(sqlStocksModel, preview("stocks", admin, 20),
                "stock_id", "symbol", "company_name", "sector_id", "current_price", "updated_at");
        addRows(sqlTransactionsRawModel, preview("transactions", admin, 25),
                "transaction_id", "user_id", "stock_id", "type", "quantity", "price", "transaction_date", "notes");
        addRows(sqlHoldingsRawModel, preview("portfolio_holdings", admin, 25),
                "holding_id", "user_id", "stock_id", "quantity", "avg_buy_price", "updated_at");
        addRows(sqlAuditRawModel, preview("audit_log", admin, 25),
                "log_id", "user_id", "action", "detail", "ip_address", "logged_at");
    }

    private List<Map<String, Object>> preview(String tableName, boolean admin, int limit) throws SQLException {
        if (admin) {
            return auditDAO.tablePreview(tableName, limit);
        }
        return auditDAO.tablePreviewForUser(tableName, currentUser.getUserId(), limit);
    }

    private void addRows(DefaultTableModel model, List<Map<String, Object>> rows, String... columns) {
        for (Map<String, Object> row : rows) {
            Object[] values = new Object[columns.length];
            for (int i = 0; i < columns.length; i++) {
                values[i] = row.get(columns[i]);
            }
            model.addRow(values);
        }
    }

    private void addQueryRows() {
        sqlQueriesModel.addRow(new Object[]{
                "Login",
                "SELECT * FROM users WHERE email = ?",
                "PreparedStatement verifies the user before opening the Swing dashboard."
        });
        sqlQueriesModel.addRow(new Object[]{
                "Register",
                "INSERT INTO users (name, email, password_hash, role) VALUES (?,?,?,'user')",
                "Creates a new user from the Swing registration form."
        });
        sqlQueriesModel.addRow(new Object[]{
                "Record Trade",
                "INSERT INTO transactions (user_id, stock_id, type, quantity, price, notes) VALUES (?,?,?,?,?,?)",
                "The Swing Trade tab writes directly into MySQL using JDBC."
        });
        sqlQueriesModel.addRow(new Object[]{
                "Portfolio",
                "SELECT * FROM holdings_detail_view WHERE user_id = ? ORDER BY current_value DESC",
                "Reads the user's holdings after MySQL triggers update portfolio_holdings."
        });
        sqlQueriesModel.addRow(new Object[]{
                "Transaction History",
                "SELECT t.*, s.symbol, s.company_name FROM transactions t JOIN stocks s ON t.stock_id = s.stock_id WHERE t.user_id = ?",
                "Shows a JOIN query feeding the Swing transactions table."
        });
        sqlQueriesModel.addRow(new Object[]{
                "Audit Demo",
                "SELECT a.*, u.name FROM audit_log a LEFT JOIN users u ON a.user_id = u.user_id ORDER BY logged_at DESC",
                "Displays backend activity created by login, register, trades, and triggers."
        });
        if (!currentUser.isAdmin()) {
            sqlQueriesModel.addRow(new Object[]{
                    "User Restriction",
                    "SELECT * FROM transactions WHERE user_id = ?",
                    "Normal users see only their own SQL rows; admins see full table previews."
            });
        }
    }

    private String tablePurpose(String tableName) {
        return switch (tableName) {
            case "users" -> "Authentication and registration data";
            case "stocks" -> "Available stock universe for trade form";
            case "transactions" -> "Every BUY/SELL submitted from Swing";
            case "portfolio_holdings" -> "Maintained automatically by MySQL triggers";
            case "audit_log" -> "Backend operation log for demo proof";
            case "price_history" -> "Historical prices for analytics";
            default -> "SQL backend table";
        };
    }

    private void recordTrade() {
        StockItem selected = (StockItem) tradeStockSelect.getSelectedItem();
        if (selected == null) {
            showError("Select a stock.");
            return;
        }

        try {
            int quantity = Integer.parseInt(tradeQuantityField.getText().trim());
            BigDecimal price = new BigDecimal(tradePriceField.getText().trim());
            String type = String.valueOf(tradeTypeSelect.getSelectedItem());
            String notes = tradeNotesField.getText().trim();

            if (quantity <= 0 || price.signum() <= 0) {
                showError("Quantity and price must be positive.");
                return;
            }

            transactionDAO.insert(currentUser.getUserId(), selected.stock.getStockId(), type, quantity, price, notes);
            tryLog(type + "_STOCK_DESKTOP", selected.stock.getSymbol() + ", qty=" + quantity + ", price=" + price);
            tradeQuantityField.setText("");
            tradePriceField.setText("");
            tradeNotesField.setText("");
            refreshAll();
            JOptionPane.showMessageDialog(frame, "Trade recorded in MySQL. Portfolio and SQL demo refreshed.");
        } catch (Exception ex) {
            showError("Trade failed: " + ex.getMessage());
        }
    }

    private void logout() {
        if (currentUser != null) {
            tryLog("LOGOUT", "Desktop logout for " + currentUser.getEmail());
        }
        currentUser = null;
        frame.setTitle("Quantfolio - Swing JDBC SQL Portfolio System");
        cardLayout.show(root, "login");
    }

    private void tryLog(String action, String detail) {
        if (currentUser != null) {
            tryLog(currentUser.getUserId(), action, detail);
        }
    }

    private void tryLog(int userId, String action, String detail) {
        try {
            auditDAO.logAction(userId, action, detail, "desktop");
        } catch (Exception ignored) {
        }
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String text, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        form.add(label(text), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(field, gbc);
    }

    private JPanel statCard(String label, String value, Color color) {
        JPanel card = panel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        JLabel l = smallText(label);
        JLabel v = new JLabel(value);
        v.setForeground(color);
        v.setFont(new Font("Segoe UI", Font.BOLD, 22));
        card.add(l);
        card.add(Box.createVerticalStrut(8));
        card.add(v);
        return card;
    }

    private JTable table(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(PANEL);
        table.setForeground(TEXT);
        table.setGridColor(BORDER);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setBackground(PANEL_2);
        table.getTableHeader().setForeground(TEXT);
        table.setSelectionBackground(PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        resizeColumns(table);
        return table;
    }

    private void resizeColumns(JTable table) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            String name = table.getColumnName(i).toLowerCase();
            int width = 130;
            if (name.contains("company") || name.contains("email") || name.contains("query") || name.contains("demo") || name.contains("detail")) {
                width = 280;
            } else if (name.contains("date") || name.contains("time") || name.contains("created") || name.contains("updated") || name.contains("logged")) {
                width = 190;
            } else if (name.contains("notes") || name.contains("value")) {
                width = 220;
            } else if (name.contains("id") || name.equals("qty") || name.equals("type") || name.equals("role")) {
                width = 95;
            }
            table.getColumnModel().getColumn(i).setPreferredWidth(width);
        }
    }

    private DefaultTableModel model(String... columns) {
        return new ReadOnlyTableModel(columns);
    }

    private void styleTabs(JTabbedPane tabs) {
        tabs.setBackground(new Color(226, 232, 240));
        tabs.setForeground(new Color(15, 23, 42));
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setOpaque(true);
    }

    private void colorTabText(JTabbedPane tabs) {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            tabs.setForegroundAt(i, new Color(15, 23, 42));
            tabs.setBackgroundAt(i, new Color(226, 232, 240));
        }
    }

    private JPanel panel() {
        return panel(new BorderLayout());
    }

    private JPanel panel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createLineBorder(BORDER));
        return panel;
    }

    private JPanel transparentPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return label;
    }

    private JLabel smallText(String text) {
        JLabel label = new JLabel("<html>" + text + "</html>");
        label.setForeground(MUTED);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return label;
    }

    private JLabel bigText(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        return label;
    }

    private JTextField field() {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setPreferredSize(new Dimension(240, 38));
        field.setBackground(PANEL_2);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 10, 8, 10)));
        return field;
    }

    private JPasswordField passwordField() {
        JPasswordField field = new JPasswordField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setPreferredSize(new Dimension(240, 38));
        field.setBackground(PANEL_2);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 10, 8, 10)));
        return field;
    }

    private JButton button(String text) {
        JButton button = new JButton(text);
        styleButton(button, new Color(6, 182, 212), Color.WHITE, new Color(34, 211, 238));
        return button;
    }

    private JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, new Color(51, 65, 85), Color.WHITE, new Color(100, 116, 139));
        return button;
    }

    private void styleButton(JButton button, Color background, Color foreground, Color border) {
        button.setUI(new BasicButtonUI());
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setRolloverEnabled(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border),
                new EmptyBorder(10, 12, 10, 12)));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    private String money(BigDecimal value) {
        if (value == null) return "Rs. 0.00";
        return "Rs. " + value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal asBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal decimal) return decimal;
        if (value instanceof Number number) return BigDecimal.valueOf(number.doubleValue());
        return new BigDecimal(String.valueOf(value));
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void setStatus(String text) {
        if (statusLabel != null) {
            statusLabel.setText(text);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Quantfolio", JOptionPane.ERROR_MESSAGE);
    }

    private static class StockItem {
        private final Stock stock;

        private StockItem(Stock stock) {
            this.stock = stock;
        }

        @Override
        public String toString() {
            return stock.getSymbol() + " - " + stock.getCompanyName();
        }
    }

    private static class ReadOnlyTableModel extends DefaultTableModel {
        private ReadOnlyTableModel(String[] columns) {
            super(columns, 0);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    private static class PortfolioPiePanel extends JPanel {
        private final Color[] colors = {
                new Color(99, 102, 241),
                new Color(16, 185, 129),
                new Color(245, 158, 11),
                new Color(244, 63, 94),
                new Color(34, 211, 238),
                new Color(168, 85, 247),
                new Color(251, 146, 60)
        };
        private List<Holding> holdings = List.of();

        private PortfolioPiePanel() {
            setBackground(PANEL);
            setPreferredSize(new Dimension(350, 420));
        }

        private void setHoldings(List<Holding> holdings) {
            this.holdings = holdings == null ? List.of() : holdings;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth() - 70, 250);
            int x = (getWidth() - size) / 2;
            int y = 30;

            BigDecimal total = holdings.stream()
                    .map(Holding::getCurrentValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (holdings.isEmpty() || total.compareTo(BigDecimal.ZERO) <= 0) {
                g2.setColor(MUTED);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.drawString("No holdings to chart", x + 40, y + size / 2);
                g2.dispose();
                return;
            }

            int start = 90;
            for (int i = 0; i < holdings.size(); i++) {
                Holding h = holdings.get(i);
                double pct = h.getCurrentValue().divide(total, 6, RoundingMode.HALF_UP).doubleValue();
                int angle = (int) Math.round(pct * 360);
                g2.setColor(colors[i % colors.length]);
                g2.fillArc(x, y, size, size, start, -angle);
                start -= angle;
            }

            g2.setColor(PANEL);
            int inner = (int) (size * 0.52);
            g2.fillOval(x + (size - inner) / 2, y + (size - inner) / 2, inner, inner);
            g2.setColor(TEXT);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.drawString("Value", x + size / 2 - 22, y + size / 2 - 4);
            g2.setColor(MUTED);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.drawString("by stock", x + size / 2 - 23, y + size / 2 + 16);

            int legendY = y + size + 34;
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            for (int i = 0; i < holdings.size() && i < 7; i++) {
                Holding h = holdings.get(i);
                double pct = h.getCurrentValue().divide(total, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue();
                int rowY = legendY + i * 24;
                g2.setColor(colors[i % colors.length]);
                g2.fillRoundRect(24, rowY - 11, 14, 14, 4, 4);
                g2.setColor(TEXT);
                g2.drawString(h.getSymbol(), 48, rowY);
                g2.setColor(MUTED);
                g2.drawString(String.format("%.1f%%", pct), 120, rowY);
            }

            g2.dispose();
        }
    }
}
