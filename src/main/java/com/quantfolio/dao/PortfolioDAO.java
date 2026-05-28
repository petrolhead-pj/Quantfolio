package com.quantfolio.dao;

import com.quantfolio.model.Holding;
import com.quantfolio.model.PortfolioSummary;
import com.quantfolio.model.Sector;
import com.quantfolio.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PortfolioDAO {

    public PortfolioSummary getSummary(int userId) throws SQLException {
        String sql = "SELECT * FROM portfolio_summary_view WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PortfolioSummary p = new PortfolioSummary();
                    p.setUserId(rs.getInt("user_id"));
                    p.setUserName(rs.getString("user_name"));
                    p.setTotalStocks(rs.getInt("total_stocks"));
                    p.setPortfolioValue(rs.getBigDecimal("portfolio_value"));
                    p.setTotalInvested(rs.getBigDecimal("total_invested"));
                    p.setUnrealizedPnl(rs.getBigDecimal("unrealized_pnl"));
                    p.setPnlPct(rs.getBigDecimal("pnl_pct"));
                    return p;
                }
            }
        }
        return new PortfolioSummary();
    }

    public List<Holding> getHoldings(int userId) throws SQLException {
        List<Holding> list = new ArrayList<>();
        String sql = "SELECT * FROM holdings_detail_view WHERE user_id = ? ORDER BY current_value DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapHolding(rs));
            }
        }
        return list;
    }

    public List<Holding> getTopPerformers(int userId, int limit) throws SQLException {
        List<Holding> list = new ArrayList<>();
        String sql = "SELECT * FROM holdings_detail_view WHERE user_id = ? ORDER BY return_pct DESC LIMIT ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapHolding(rs));
            }
        }
        return list;
    }

    public List<Sector> getSectorAllocation(int userId) throws SQLException {
        List<Sector> list = new ArrayList<>();
        String sql =
            "SELECT sa.sector_name, sa.sector_value, " +
            "ROUND(sa.sector_value / ps.portfolio_value * 100, 2) AS allocation_pct " +
            "FROM sector_allocation_view sa " +
            "JOIN portfolio_summary_view ps ON sa.user_id = ps.user_id " +
            "WHERE sa.user_id = ? ORDER BY allocation_pct DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sector s = new Sector();
                    s.setSectorName(rs.getString("sector_name"));
                    s.setSectorValue(rs.getBigDecimal("sector_value"));
                    s.setAllocationPct(rs.getBigDecimal("allocation_pct"));
                    list.add(s);
                }
            }
        }
        return list;
    }

    public List<Object[]> getMonthlyActivity(int userId) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql =
            "SELECT YEAR(transaction_date) AS yr, MONTH(transaction_date) AS mo, " +
            "SUM(CASE WHEN type='BUY' THEN quantity*price ELSE 0 END) AS bought, " +
            "SUM(CASE WHEN type='SELL' THEN quantity*price ELSE 0 END) AS sold " +
            "FROM transactions WHERE user_id = ? GROUP BY yr, mo ORDER BY yr, mo";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("yr"), rs.getInt("mo"),
                        rs.getBigDecimal("bought"), rs.getBigDecimal("sold")
                    });
                }
            }
        }
        return list;
    }

    private Holding mapHolding(ResultSet rs) throws SQLException {
        Holding h = new Holding();
        h.setUserId(rs.getInt("user_id"));
        h.setStockId(rs.getInt("stock_id"));
        h.setSymbol(rs.getString("symbol"));
        h.setCompanyName(rs.getString("company_name"));
        h.setSectorName(rs.getString("sector_name"));
        h.setQuantity(rs.getInt("quantity"));
        h.setAvgBuyPrice(rs.getBigDecimal("avg_buy_price"));
        h.setCurrentPrice(rs.getBigDecimal("current_price"));
        return h;
    }
}
