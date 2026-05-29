package com.quantfolio.servlet;

import com.quantfolio.dao.AuditDAO;
import com.quantfolio.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/sql-demo")
public class SqlDemoServlet extends HttpServlet {

    private final AuditDAO auditDAO = new AuditDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        SessionUtil.requireLogin(req, resp);
        if (resp.isCommitted()) return;

        try {
            req.setAttribute("connectionStatus", auditDAO.connectionStatus());
            req.setAttribute("tableStats", auditDAO.tableStats());
            req.setAttribute("auditRows", auditDAO.latestAuditRows(15));
            req.setAttribute("latestTransactions", auditDAO.latestTransactions(10));
            req.getRequestDispatcher("/WEB-INF/views/sql-demo.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("SQL demo error: " + e.getMessage(), e);
        }
    }
}
