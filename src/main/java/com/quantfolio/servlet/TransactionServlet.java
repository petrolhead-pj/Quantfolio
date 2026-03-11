package com.quantfolio.servlet;

import com.quantfolio.dao.StockDAO;
import com.quantfolio.dao.TransactionDAO;
import com.quantfolio.model.*;
import com.quantfolio.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/transactions")
public class TransactionServlet extends HttpServlet {

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final StockDAO       stockDAO       = new StockDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        SessionUtil.requireLogin(req, resp);
        if (resp.isCommitted()) return;

        User user = SessionUtil.getLoggedInUser(req);

        try {
            List<Transaction> txns   = transactionDAO.findByUser(user.getUserId());
            List<Stock>       stocks = stockDAO.findAll();
            req.setAttribute("transactions", txns);
            req.setAttribute("stocks",       stocks);
            req.getRequestDispatcher("/WEB-INF/views/transactions.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("Transaction list error: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        SessionUtil.requireLogin(req, resp);
        if (resp.isCommitted()) return;

        User   user     = SessionUtil.getLoggedInUser(req);
        String stockIdStr = req.getParameter("stockId");
        String type       = req.getParameter("type");
        String qtyStr     = req.getParameter("quantity");
        String priceStr   = req.getParameter("price");
        String notes      = req.getParameter("notes");

        try {
            int        stockId  = Integer.parseInt(stockIdStr);
            int        quantity = Integer.parseInt(qtyStr);
            BigDecimal price    = new BigDecimal(priceStr);

            if (quantity <= 0 || price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Quantity and price must be positive.");
            }

            transactionDAO.insert(user.getUserId(), stockId, type, quantity, price, notes);
            resp.sendRedirect(req.getContextPath() + "/transactions?success=true");

        } catch (Exception e) {
            req.setAttribute("error", "Transaction failed: " + e.getMessage());
            try {
                List<Transaction> txns   = transactionDAO.findByUser(user.getUserId());
                List<Stock>       stocks = stockDAO.findAll();
                req.setAttribute("transactions", txns);
                req.setAttribute("stocks",       stocks);
            } catch (Exception ignored) {}
            req.getRequestDispatcher("/WEB-INF/views/transactions.jsp").forward(req, resp);
        }
    }
}
