package com.quantfolio.servlet;

import com.quantfolio.util.SessionUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SessionUtil.logout(req);
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
