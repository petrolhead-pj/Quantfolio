package com.quantfolio.servlet;

import com.quantfolio.dao.*;
import com.quantfolio.model.*;
import com.quantfolio.util.SessionUtil;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final HoldingDAO     holdingDAO     = new HoldingDAO();
    private final AnalyticsDAO   analyticsDAO   = new AnalyticsDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final Gson           gson           = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        SessionUtil.requireLogin(req, resp);
        if (resp.isCommitted()) return;

        User user = SessionUtil.getLoggedInUser(req);
        int  uid  = user.getUserId();

        try {
            PortfolioSummary summary    = holdingDAO.getSummary(uid);
            List<Holding>    holdings   = holdingDAO.findByUser(uid);
            List<Map<String, Object>> sectors     = analyticsDAO.getSectorAllocation(uid);
            List<Map<String, Object>> performers  = analyticsDAO.getTopPerformers(uid, 5);
            List<Map<String, Object>> monthly     = analyticsDAO.getMonthlyActivity(uid);
            List<Transaction>         recentTxns  = transactionDAO.findByUser(uid);

            req.setAttribute("summary",     summary);
            req.setAttribute("holdings",    holdings);
            req.setAttribute("sectorsJson",    gson.toJson(sectors));
            req.setAttribute("performersJson",  gson.toJson(performers));
            req.setAttribute("monthlyJson",     gson.toJson(monthly));
            req.setAttribute("recentTxns",  recentTxns.size() > 5 ? recentTxns.subList(0, 5) : recentTxns);

            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);

        } catch (Exception e) {
            throw new ServletException("Dashboard error: " + e.getMessage(), e);
        }
    }
}
