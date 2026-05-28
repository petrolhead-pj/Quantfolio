package com.quantfolio.dao;

import com.quantfolio.model.Stock;
import com.quantfolio.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

    public List<Stock> findAll() throws SQLException {
        List<Stock> list = new ArrayList<>();
        String sql = "SELECT s.*, sec.sector_name FROM stocks s " +
                     "JOIN sectors sec ON s.sector_id = sec.sector_id ORDER BY s.symbol";
        try (Connection c = DBConnection.getConnection();
             Statement  st = c.createStatement();
             ResultSet  rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Stock findById(int stockId) throws SQLException {
        String sql = "SELECT s.*, sec.sector_name FROM stocks s " +
                     "JOIN sectors sec ON s.sector_id = sec.sector_id WHERE s.stock_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, stockId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public Stock findBySymbol(String symbol) throws SQLException {
        String sql = "SELECT s.*, sec.sector_name FROM stocks s " +
                     "JOIN sectors sec ON s.sector_id = sec.sector_id WHERE s.symbol = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, symbol.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public boolean updatePrice(int stockId, java.math.BigDecimal price) throws SQLException {
        String sql = "UPDATE stocks SET current_price = ? WHERE stock_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, price);
            ps.setInt(2, stockId);
            return ps.executeUpdate() == 1;
        }
    }

    private Stock mapRow(ResultSet rs) throws SQLException {
        Stock s = new Stock();
        s.setStockId(rs.getInt("stock_id"));
        s.setSymbol(rs.getString("symbol"));
        s.setCompanyName(rs.getString("company_name"));
        s.setSectorId(rs.getInt("sector_id"));
        s.setSectorName(rs.getString("sector_name"));
        s.setCurrentPrice(rs.getBigDecimal("current_price"));
        s.setUpdatedAt(rs.getTimestamp("updated_at"));
        return s;
    }
}
