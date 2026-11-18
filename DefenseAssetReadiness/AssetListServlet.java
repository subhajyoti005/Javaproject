
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

@WebServlet("/asset-list")
public class AssetListServlet extends HttpServlet {

    private String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        String sql = "SELECT asset_code, asset_name, category, " +
                     "condition_status, availability_status FROM assets";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            PrintWriter out = response.getWriter()
        ) {

            out.print("[");

            boolean first = true;

            while (rs.next()) {

                if (!first) {
                    out.print(",");
                }

                first = false;

                out.print("{");

                out.print("\"asset_code\":\"" +
                    escapeJson(rs.getString("asset_code")) + "\",");

                out.print("\"asset_name\":\"" +
                    escapeJson(rs.getString("asset_name")) + "\",");

                out.print("\"category\":\"" +
                    escapeJson(rs.getString("category")) + "\",");

                out.print("\"condition\":\"" +
                    escapeJson(rs.getString("condition_status")) + "\",");

                out.print("\"availability\":\"" +
                    escapeJson(rs.getString("availability_status")) + "\"");

                out.print("}");
            }

            out.print("]");

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().print(
                "{\"error\":\"Unable to fetch asset records\"}"
            );
        }
    }
}
