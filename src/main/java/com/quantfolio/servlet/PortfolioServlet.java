package com.quantfolio.servlet;

import com.quantfolio.dao.HoldingDAO;
import com.quantfolio.dao.StockDAO;
import com.quantfolio.model.*;
import com.quantfolio.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/portfolio")
public class PortfolioServlet extends HttpServlet {

    private final HoldingDAO holdingDAO = new HoldingDAO();
    private final StockDAO   stockDAO   = new StockDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        SessionUtil.requireLogin(req, resp);
        if (resp.isCommitted()) return;

        User user = SessionUtil.getLoggedInUser(req);

        try {
            List<Holding> holdings = holdingDAO.findByUser(user.getUserId());
            List<Stock>   stocks   = stockDAO.findAll();
            PortfolioSummary summary = holdingDAO.getSummary(user.getUserId());

            req.setAttribute("holdings", holdings);
            req.setAttribute("stocks",   stocks);
            req.setAttribute("summary",  summary);
            req.getRequestDispatcher("/WEB-INF/views/portfolio.jsp").forward(req, resp);

        } catch (Exception e) {
            throw new ServletException("Portfolio error: " + e.getMessage(), e);
        }
    }
}
