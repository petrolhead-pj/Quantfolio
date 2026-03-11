package com.quantfolio.desktop;

import javax.swing.*;
import java.awt.*;

/**
 * Quantfolio Desktop Application — Admin Analytics Dashboard (Java Swing)
 *
 * Launch: java -cp quantfolio.jar:lib/* com.quantfolio.desktop.DesktopApp
 */
public class DesktopApp {

    public static void main(String[] args) {
        // Use system look & feel
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quantfolio — Admin Desktop");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1100, 700);
            frame.setLocationRelativeTo(null);
            frame.setJMenuBar(buildMenuBar(frame));
            frame.add(buildMainPanel());
            frame.setVisible(true);
        });
    }

    // ------------------------------------------------------------------ Menu
    private static JMenuBar buildMenuBar(JFrame frame) {
        JMenuBar bar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        JMenu viewMenu = new JMenu("View");
        JMenuItem refreshItem = new JMenuItem("Refresh Data");
        refreshItem.addActionListener(e -> JOptionPane.showMessageDialog(frame,
                "Data refreshed from database.", "Refresh", JOptionPane.INFORMATION_MESSAGE));
        viewMenu.add(refreshItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(frame,
                "Quantfolio Desktop v1.0\nAdmin Analytics Dashboard\n\nBuilt with Java Swing + MySQL",
                "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        bar.add(fileMenu);
        bar.add(viewMenu);
        bar.add(helpMenu);
        return bar;
    }

    // ------------------------------------------------------------------ Main Panel
    private static JPanel buildMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(15, 23, 42));

        // Sidebar
        JPanel sidebar = buildSidebar();
        panel.add(sidebar, BorderLayout.WEST);

        // Tabbed content
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(new Color(30, 41, 59));
        tabs.setForeground(Color.WHITE);
        tabs.addTab("📊 Analytics",    buildAnalyticsPanel());
        tabs.addTab("📋 Stocks",       buildStocksPanel());
        tabs.addTab("🔄 Transactions", buildTransactionsPanel());
        tabs.addTab("👤 Users",        buildUsersPanel());
        panel.add(tabs, BorderLayout.CENTER);

        return panel;
    }

    // ------------------------------------------------------------------ Sidebar
    private static JPanel buildSidebar() {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(200, 0));
        p.setBackground(new Color(15, 23, 42));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(51, 65, 85)));

        JLabel brand = new JLabel("📈 Quantfolio");
        brand.setForeground(new Color(99, 102, 241));
        brand.setFont(new Font("SansSerif", Font.BOLD, 16));
        brand.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 10));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(brand);

        JLabel adminBadge = new JLabel("Admin Console");
        adminBadge.setForeground(new Color(100, 116, 139));
        adminBadge.setFont(new Font("SansSerif", Font.PLAIN, 11));
        adminBadge.setBorder(BorderFactory.createEmptyBorder(0, 15, 20, 10));
        adminBadge.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(adminBadge);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(51, 65, 85));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        p.add(sep);
        p.add(Box.createVerticalGlue());
        return p;
    }

    // ------------------------------------------------------------------ Analytics Panel
    private static JPanel buildAnalyticsPanel() {
        JPanel p = new JPanel(new GridLayout(2, 2, 10, 10));
        p.setBackground(new Color(30, 41, 59));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        p.add(buildStatCard("Total Users",          "3",          new Color(99, 102, 241)));
        p.add(buildStatCard("Total Transactions",   "22",         new Color(16, 185, 129)));
        p.add(buildStatCard("Stocks in Universe",   "20",         new Color(245, 158, 11)));
        p.add(buildStatCard("Active Portfolios",    "2",          new Color(244, 63, 94)));
        return p;
    }

    private static JPanel buildStatCard(String label, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(30, 41, 59));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        JLabel lbl = new JLabel(label);
        lbl.setForeground(new Color(100, 116, 139));
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JLabel val = new JLabel(value);
        val.setForeground(accent);
        val.setFont(new Font("SansSerif", Font.BOLD, 32));
        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    // ------------------------------------------------------------------ Stocks Panel
    private static JScrollPane buildStocksPanel() {
        String[] cols = {"Symbol", "Company", "Sector", "Current Price"};
        Object[][] data = {
            {"AAPL","Apple Inc.","Technology","₹189.50"},
            {"MSFT","Microsoft Corporation","Technology","₹415.20"},
            {"GOOGL","Alphabet Inc.","Technology","₹175.80"},
            {"NVDA","NVIDIA Corporation","Technology","₹875.60"},
            {"JPM","JPMorgan Chase","Finance","₹198.70"},
            {"JNJ","Johnson & Johnson","Healthcare","₹152.30"},
            {"XOM","Exxon Mobil","Energy","₹98.60"},
            {"WMT","Walmart Inc.","Consumer Goods","₹66.80"},
        };
        JTable table = new JTable(data, cols);
        styleTable(table);
        return new JScrollPane(table);
    }

    // ------------------------------------------------------------------ Transactions Panel
    private static JScrollPane buildTransactionsPanel() {
        String[] cols = {"ID","User","Stock","Type","Qty","Price","Date"};
        Object[][] data = {
            {1,"Alice","AAPL","BUY", 10,"₹150.00","2023-01-15"},
            {2,"Alice","MSFT","BUY",  5,"₹280.00","2023-02-10"},
            {3,"Alice","NVDA","BUY",  8,"₹420.00","2023-03-05"},
            {4,"Bob","GOOGL","BUY",   8,"₹130.00","2023-03-01"},
            {5,"Alice","MSFT","SELL", 2,"₹330.00","2023-10-05"},
        };
        JTable table = new JTable(data, cols);
        styleTable(table);
        return new JScrollPane(table);
    }

    // ------------------------------------------------------------------ Users Panel
    private static JScrollPane buildUsersPanel() {
        String[] cols = {"ID","Name","Email","Role","Created"};
        Object[][] data = {
            {1,"Admin User","admin@quantfolio.com","admin","2024-01-01"},
            {2,"Alice Sharma","alice@example.com","user","2024-01-02"},
            {3,"Bob Kumar","bob@example.com","user","2024-01-03"},
        };
        JTable table = new JTable(data, cols);
        styleTable(table);
        return new JScrollPane(table);
    }

    private static void styleTable(JTable table) {
        table.setBackground(new Color(30, 41, 59));
        table.setForeground(new Color(226, 232, 240));
        table.setGridColor(new Color(51, 65, 85));
        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setBackground(new Color(15, 23, 42));
        table.getTableHeader().setForeground(new Color(100, 116, 139));
        table.setSelectionBackground(new Color(99, 102, 241));
    }
}
