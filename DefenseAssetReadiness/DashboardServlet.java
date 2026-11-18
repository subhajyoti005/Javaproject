
package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/dashboard-data")
public class DashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        String sql = "SELECT " +
                     "COUNT(*) AS total, " +
                     "SUM(CASE WHEN availability_status='Ready' THEN 1 ELSE 0 END) AS ready, " +
                     "SUM(CASE WHEN availability_status='Under Maintenance' THEN 1 ELSE 0 END) AS maintenance, " +
                     "SUM(CASE WHEN availability_status='Unavailable' THEN 1 ELSE 0 END) AS unavailable " +
                     "FROM assets";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            PrintWriter out = response.getWriter()
        ) {

            if (rs.next()) {

                int total = rs.getInt("total");
                int ready = rs.getInt("ready");
                int maintenance = rs.getInt("maintenance");
                int unavailable = rs.getInt("unavailable");

                out.print("{");
                out.print("\"total\":" + total + ",");
                out.print("\"ready\":" + ready + ",");
                out.print("\"maintenance\":" + maintenance + ",");
                out.print("\"unavailable\":" + unavailable);
                out.print("}");

            }

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().print(
                "{\"error\":\"Database connection failed\"}"
            );
        }
    }
}
