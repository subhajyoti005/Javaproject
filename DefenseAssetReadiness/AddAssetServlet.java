
package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/add-asset")
public class AddAssetServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String assetCode = request.getParameter("asset_code");
        String assetName = request.getParameter("asset_name");
        String category = request.getParameter("category");
        String condition = request.getParameter("condition_status");
        String availability = request.getParameter("availability_status");

        if (assetCode == null || assetName == null ||
            category == null || condition == null ||
            availability == null ||
            assetCode.trim().isEmpty() ||
            assetName.trim().isEmpty()) {

            response.sendError(400, "Please fill all required fields.");
            return;
        }

        String sql = "INSERT INTO assets " +
                "(asset_code, asset_name, category, condition_status, availability_status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, assetCode.trim());
            ps.setString(2, assetName.trim());
            ps.setString(3, category);
            ps.setString(4, condition);
            ps.setString(5, availability);

            int result = ps.executeUpdate();

            if (result > 0) {

                response.sendRedirect("assets.html?added=1");

            } else {

                response.sendError(500, "Asset could not be saved.");

            }

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                500,
                "Unable to save asset. Check the asset code and database."
            );
        }
    }
}
