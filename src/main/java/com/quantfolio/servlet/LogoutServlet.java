package com.quantfolio.servlet;

import com.quantfolio.util.SessionUtil;
import com.quantfolio.dao.AuditDAO;
import com.quantfolio.model.User;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private final AuditDAO auditDAO = new AuditDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = SessionUtil.getLoggedInUser(req);
        if (user != null) {
            try {
                auditDAO.logAction(user.getUserId(), "LOGOUT",
                        "User ended web session", req.getRemoteAddr());
            } catch (Exception ignored) {}
        }
        SessionUtil.logout(req);
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
