package com.quantfolio.servlet;

import com.quantfolio.dao.StockDAO;
import com.quantfolio.model.Stock;
import com.quantfolio.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/stocks")
public class StockServlet extends HttpServlet {

    private final StockDAO stockDAO = new StockDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        SessionUtil.requireLogin(req, resp);
        if (resp.isCommitted()) return;

        try {
            List<Stock> stocks = stockDAO.findAll();
            req.setAttribute("stocks", stocks);
            req.getRequestDispatcher("/WEB-INF/views/stocks.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("Stocks list error", e);
        }
    }
}
