
package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/maintenance-list")
public class MaintenanceServlet extends HttpServlet {

    private String escapeJson(String value) {
        if (value == null) return "";

        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        String sql = "SELECT id, asset_code, maintenance_date, " +
                     "description, status FROM maintenance_records " +
                     "ORDER BY id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();
             PrintWriter out = response.getWriter()) {

            out.print("[");

            boolean first = true;

            while (rs.next()) {

                if (!first) out.print(",");
                first = false;

                out.print("{");

                out.print("\"id\":" + rs.getInt("id") + ",");

                out.print("\"asset_code\":\"" +
                        escapeJson(rs.getString("asset_code")) + "\",");

                out.print("\"maintenance_date\":\"" +
                        escapeJson(rs.getString("maintenance_date")) + "\",");

                out.print("\"description\":\"" +
                        escapeJson(rs.getString("description")) + "\",");

                out.print("\"status\":\"" +
                        escapeJson(rs.getString("status")) + "\"");

                out.print("}");
            }

            out.print("]");

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(500);
            response.getWriter().print(
                "{\"error\":\"Unable to load maintenance records\"}"
            );
        }
    }
}
