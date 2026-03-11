package com.quantfolio.dao;

import com.quantfolio.model.Sector;
import com.quantfolio.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectorDAO {

    public List<Sector> findAll() throws SQLException {
        List<Sector> list = new ArrayList<>();
        String sql = "SELECT * FROM sectors ORDER BY sector_name";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Sector s = new Sector();
                s.setSectorId(rs.getInt("sector_id"));
                s.setSectorName(rs.getString("sector_name"));
                s.setDescription(rs.getString("description"));
                list.add(s);
            }
        }
        return list;
    }
}
