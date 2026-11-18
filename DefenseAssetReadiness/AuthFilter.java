
package servlet;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter({
    "/dashboard.html",
    "/assets.html",
    "/add-assets.html",
    "/maintenance.html",
    "/asset-list",
    "/add-asset",
    "/maintenance-list",
    "/add-maintenance",
    "/update-maintenance",
    "/dashboard-data"
})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);

        if (session != null &&
            session.getAttribute("adminId") != null) {

            chain.doFilter(request, response);

        } else {

            res.sendRedirect(req.getContextPath() + "/login.html");
        }
    }
}
