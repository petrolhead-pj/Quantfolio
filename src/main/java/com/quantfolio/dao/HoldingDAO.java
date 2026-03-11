package com.quantfolio.dao;

import com.quantfolio.model.Holding;
import com.quantfolio.model.PortfolioSummary;
import com.quantfolio.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoldingDAO {

    public List<Holding> findByUser(int userId) throws SQLException {
        List<Holding> list = new ArrayList<>();
        String sql = "SELECT * FROM holdings_detail_view WHERE user_id = ? ORDER BY current_value DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public PortfolioSummary getSummary(int userId) throws SQLException {
        String sql = "SELECT * FROM portfolio_summary_view WHERE user_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
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

    private Holding mapRow(ResultSet rs) throws SQLException {
        Holding h = new Holding();
        h.setUserId(rs.getInt("user_id"));
        h.setSymbol(rs.getString("symbol"));
        h.setCompanyName(rs.getString("company_name"));
        h.setSectorName(rs.getString("sector_name"));
        h.setQuantity(rs.getInt("quantity"));
        h.setAvgBuyPrice(rs.getBigDecimal("avg_buy_price"));
        h.setCurrentPrice(rs.getBigDecimal("current_price"));
        return h;
    }
}
