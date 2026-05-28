package com.quantfolio.dao;

import com.quantfolio.model.Transaction;
import com.quantfolio.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public List<Transaction> findByUser(int userId) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.*, s.symbol, s.company_name " +
                     "FROM transactions t JOIN stocks s ON t.stock_id = s.stock_id " +
                     "WHERE t.user_id = ? ORDER BY t.transaction_date DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean insert(int userId, int stockId, String type, int quantity, BigDecimal price, String notes)
            throws SQLException {
        String sql = "INSERT INTO transactions (user_id, stock_id, type, quantity, price, notes) " +
                     "VALUES (?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, stockId);
            ps.setString(3, type);
            ps.setInt(4, quantity);
            ps.setBigDecimal(5, price);
            ps.setString(6, notes);
            return ps.executeUpdate() == 1;
        }
    }

    public List<Transaction> findAll() throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.*, s.symbol, s.company_name FROM transactions t " +
                     "JOIN stocks s ON t.stock_id = s.stock_id ORDER BY t.transaction_date DESC";
        try (Connection c = DBConnection.getConnection();
             Statement  st = c.createStatement();
             ResultSet  rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setTransactionId(rs.getInt("transaction_id"));
        t.setUserId(rs.getInt("user_id"));
        t.setStockId(rs.getInt("stock_id"));
        t.setSymbol(rs.getString("symbol"));
        t.setCompanyName(rs.getString("company_name"));
        t.setType(rs.getString("type"));
        t.setQuantity(rs.getInt("quantity"));
        t.setPrice(rs.getBigDecimal("price"));
        t.setTransactionDate(rs.getTimestamp("transaction_date"));
        t.setNotes(rs.getString("notes"));
        return t;
    }
}
