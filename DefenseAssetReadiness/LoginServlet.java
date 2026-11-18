
package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null ||
            username.trim().isEmpty() || password.isEmpty()) {

            response.sendRedirect("login.html?error=1");
            return;
        }

        String sql = "SELECT id, username, password, role " +
                     "FROM admin_users " +
                     "WHERE username = ? AND password = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    HttpSession oldSession = request.getSession(false);

                    if (oldSession != null) {
                        oldSession.invalidate();
                    }

                    HttpSession session = request.getSession(true);

                    session.setAttribute("adminId", rs.getInt("id"));
                    session.setAttribute("username",
                                         rs.getString("username"));
                    session.setAttribute("fullName",
                                         rs.getString("username"));
                    session.setAttribute("role",
                                         rs.getString("role"));

                    session.setMaxInactiveInterval(30 * 60);

                    response.sendRedirect("dashboard.html");

                } else {

                    response.sendRedirect("login.html?error=1");
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.setContentType("text/plain");
            response.getWriter().println(
                "Login error: " + e.getMessage()
            );
        }
    }
}
