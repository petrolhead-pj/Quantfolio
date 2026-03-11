package com.quantfolio.dao;

import com.quantfolio.util.DBConnection;

import java.sql.*;
import java.util.*;

public class AnalyticsDAO {

    /** Returns list of {sector_name, sector_value, allocation_pct} maps. */
    public List<Map<String, Object>> getSectorAllocation(int userId) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT sector_name, sector_value, allocation_pct " +
                     "FROM sector_allocation_view WHERE user_id = ? ORDER BY sector_value DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("sector_name",    rs.getString("sector_name"));
                    row.put("sector_value",   rs.getBigDecimal("sector_value"));
                    row.put("allocation_pct", rs.getBigDecimal("allocation_pct"));
                    result.add(row);
                }
            }
        }
        return result;
    }

    /** Returns top N stocks by return %. */
    public List<Map<String, Object>> getTopPerformers(int userId, int limit) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT symbol, company_name, avg_buy_price, current_price, return_pct " +
                     "FROM top_performers_view WHERE user_id = ? LIMIT ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("symbol",        rs.getString("symbol"));
                    row.put("company_name",  rs.getString("company_name"));
                    row.put("avg_buy_price", rs.getBigDecimal("avg_buy_price"));
                    row.put("current_price", rs.getBigDecimal("current_price"));
                    row.put("return_pct",    rs.getBigDecimal("return_pct"));
                    result.add(row);
                }
            }
        }
        return result;
    }

    /** Returns monthly buy/sell activity for last 12 months. */
    public List<Map<String, Object>> getMonthlyActivity(int userId) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT month_label, buy_value, sell_value, txn_count " +
                     "FROM monthly_activity_view WHERE user_id = ? " +
                     "AND STR_TO_DATE(CONCAT('01 ', month_label), '%d %b %Y') >= DATE_SUB(NOW(), INTERVAL 12 MONTH)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("month_label", rs.getString("month_label"));
                    row.put("buy_value",   rs.getBigDecimal("buy_value"));
                    row.put("sell_value",  rs.getBigDecimal("sell_value"));
                    row.put("txn_count",   rs.getInt("txn_count"));
                    result.add(row);
                }
            }
        }
        return result;
    }
}
