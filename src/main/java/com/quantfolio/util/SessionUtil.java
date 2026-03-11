package com.quantfolio.util;

import com.quantfolio.model.User;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SessionUtil {

    public static final String USER_KEY = "loggedInUser";

    private SessionUtil() {}

    public static User getLoggedInUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        return (User) session.getAttribute(USER_KEY);
    }

    public static boolean isLoggedIn(HttpServletRequest req) {
        return getLoggedInUser(req) != null;
    }

    public static void requireLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }

    public static void setLoggedInUser(HttpServletRequest req, User user) {
        HttpSession session = req.getSession(true);
        session.setAttribute(USER_KEY, user);
        session.setMaxInactiveInterval(30 * 60); // 30 minutes
    }

    public static void logout(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
    }
}
