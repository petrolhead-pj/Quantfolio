package com.quantfolio.dao;

import com.quantfolio.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AuditDAO {

    public void logAction(int userId, String action, String detail, String ipAddress) throws SQLException {
        String sql = "INSERT INTO audit_log (user_id, action, detail, ip_address) VALUES (?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setString(3, detail);
            ps.setString(4, ipAddress);
            ps.executeUpdate();
        }
    }

    public List<Map<String, Object>> latestAuditRows(int limit) throws SQLException {
        String sql = "SELECT a.log_id, a.user_id, u.name, a.action, a.detail, a.ip_address, a.logged_at " +
                     "FROM audit_log a LEFT JOIN users u ON a.user_id = u.user_id " +
                     "ORDER BY a.logged_at DESC, a.log_id DESC LIMIT ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                return rows(rs);
            }
        }
    }

    public List<Map<String, Object>> tableStats() throws SQLException {
        String sql = "SELECT 'users' table_name, COUNT(*) row_count FROM users " +
                     "UNION ALL SELECT 'stocks', COUNT(*) FROM stocks " +
                     "UNION ALL SELECT 'transactions', COUNT(*) FROM transactions " +
                     "UNION ALL SELECT 'portfolio_holdings', COUNT(*) FROM portfolio_holdings " +
                     "UNION ALL SELECT 'audit_log', COUNT(*) FROM audit_log " +
                     "UNION ALL SELECT 'price_history', COUNT(*) FROM price_history";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rows(rs);
        }
    }

    public List<Map<String, Object>> tableStatsForUser(int userId) throws SQLException {
        String sql = "SELECT 'users' table_name, COUNT(*) row_count FROM users WHERE user_id = ? " +
                     "UNION ALL SELECT 'stocks', COUNT(*) FROM stocks " +
                     "UNION ALL SELECT 'transactions', COUNT(*) FROM transactions WHERE user_id = ? " +
                     "UNION ALL SELECT 'portfolio_holdings', COUNT(*) FROM portfolio_holdings WHERE user_id = ? " +
                     "UNION ALL SELECT 'audit_log', COUNT(*) FROM audit_log WHERE user_id = ? " +
                     "UNION ALL SELECT 'price_history', COUNT(*) FROM price_history";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            ps.setInt(4, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rows(rs);
            }
        }
    }

    public List<Map<String, Object>> latestTransactions(int limit) throws SQLException {
        String sql = "SELECT t.transaction_id, u.name user_name, s.symbol, t.type, t.quantity, " +
                     "t.price, (t.quantity * t.price) total_value, t.transaction_date " +
                     "FROM transactions t " +
                     "JOIN users u ON t.user_id = u.user_id " +
                     "JOIN stocks s ON t.stock_id = s.stock_id " +
                     "ORDER BY t.transaction_date DESC LIMIT ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                return rows(rs);
            }
        }
    }

    public List<Map<String, Object>> latestTransactionsForUser(int userId, int limit) throws SQLException {
        String sql = "SELECT t.transaction_id, u.name user_name, s.symbol, t.type, t.quantity, " +
                     "t.price, (t.quantity * t.price) total_value, t.transaction_date " +
                     "FROM transactions t " +
                     "JOIN users u ON t.user_id = u.user_id " +
                     "JOIN stocks s ON t.stock_id = s.stock_id " +
                     "WHERE t.user_id = ? " +
                     "ORDER BY t.transaction_date DESC LIMIT ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                return rows(rs);
            }
        }
    }

    public List<Map<String, Object>> latestAuditRowsForUser(int userId, int limit) throws SQLException {
        String sql = "SELECT a.log_id, a.user_id, u.name, a.action, a.detail, a.ip_address, a.logged_at " +
                     "FROM audit_log a LEFT JOIN users u ON a.user_id = u.user_id " +
                     "WHERE a.user_id = ? " +
                     "ORDER BY a.logged_at DESC, a.log_id DESC LIMIT ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                return rows(rs);
            }
        }
    }

    public List<Map<String, Object>> tablePreview(String tableName, int limit) throws SQLException {
        String sql = switch (tableName) {
            case "users" -> "SELECT user_id, name, email, role, created_at FROM users ORDER BY user_id LIMIT ?";
            case "stocks" -> "SELECT stock_id, symbol, company_name, sector_id, current_price, updated_at FROM stocks ORDER BY stock_id LIMIT ?";
            case "transactions" -> "SELECT transaction_id, user_id, stock_id, type, quantity, price, transaction_date, notes FROM transactions ORDER BY transaction_id DESC LIMIT ?";
            case "portfolio_holdings" -> "SELECT holding_id, user_id, stock_id, quantity, avg_buy_price, updated_at FROM portfolio_holdings ORDER BY holding_id LIMIT ?";
            case "audit_log" -> "SELECT log_id, user_id, action, detail, ip_address, logged_at FROM audit_log ORDER BY log_id DESC LIMIT ?";
            default -> throw new SQLException("Unsupported table preview: " + tableName);
        };
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                return rows(rs);
            }
        }
    }

    public List<Map<String, Object>> tablePreviewForUser(String tableName, int userId, int limit) throws SQLException {
        String sql = switch (tableName) {
            case "users" -> "SELECT user_id, name, email, role, created_at FROM users WHERE user_id = ? ORDER BY user_id LIMIT ?";
            case "stocks" -> "SELECT stock_id, symbol, company_name, sector_id, current_price, updated_at FROM stocks ORDER BY stock_id LIMIT ?";
            case "transactions" -> "SELECT transaction_id, user_id, stock_id, type, quantity, price, transaction_date, notes FROM transactions WHERE user_id = ? ORDER BY transaction_id DESC LIMIT ?";
            case "portfolio_holdings" -> "SELECT holding_id, user_id, stock_id, quantity, avg_buy_price, updated_at FROM portfolio_holdings WHERE user_id = ? ORDER BY holding_id LIMIT ?";
            case "audit_log" -> "SELECT log_id, user_id, action, detail, ip_address, logged_at FROM audit_log WHERE user_id = ? ORDER BY log_id DESC LIMIT ?";
            default -> throw new SQLException("Unsupported table preview: " + tableName);
        };
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if ("stocks".equals(tableName)) {
                ps.setInt(1, limit);
            } else {
                ps.setInt(1, userId);
                ps.setInt(2, limit);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rows(rs);
            }
        }
    }

    public Map<String, Object> connectionStatus() throws SQLException {
        String sql = "SELECT DATABASE() database_name, USER() database_user, VERSION() mysql_version, NOW() server_time";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            Map<String, Object> status = row(rs);
            status.put("jdbc_url", DBConnection.getDisplayUrl());
            status.put("auto_commit", c.getAutoCommit());
            return status;
        }
    }

    private List<Map<String, Object>> rows(ResultSet rs) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        while (rs.next()) {
            result.add(row(rs));
        }
        return result;
    }

    private Map<String, Object> row(ResultSet rs) throws SQLException {
        Map<String, Object> row = new LinkedHashMap<>();
        ResultSetMetaData meta = rs.getMetaData();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            int type = meta.getColumnType(i);
            Object value = switch (type) {
                case Types.DATE -> rs.getDate(i);
                case Types.TIME, Types.TIME_WITH_TIMEZONE -> rs.getTime(i);
                case Types.TIMESTAMP, Types.TIMESTAMP_WITH_TIMEZONE -> rs.getTimestamp(i);
                default -> rs.getObject(i);
            };
            row.put(meta.getColumnLabel(i), value);
        }
        return row;
    }
}
