
package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;

@WebServlet("/add-maintenance")
public class AddMaintenanceServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String assetCode = request.getParameter("asset_code");
        String maintenanceDate = request.getParameter("maintenance_date");
        String description = request.getParameter("description");
        String status = request.getParameter("status");

        if (assetCode == null || maintenanceDate == null ||
            description == null || status == null ||
            assetCode.trim().isEmpty() ||
            maintenanceDate.trim().isEmpty() ||
            description.trim().isEmpty()) {

            response.sendError(400, "Please fill all required fields.");
            return;
        }

        if (!status.equals("Pending") &&
            !status.equals("In Progress") &&
            !status.equals("Completed")) {

            response.sendError(400, "Invalid maintenance status.");
            return;
        }

        String insertSQL =
            "INSERT INTO maintenance_records " +
            "(asset_code, maintenance_date, description, status) " +
            "VALUES (?, ?, ?, ?)";

        String updateInProgressSQL =
            "UPDATE assets SET availability_status = 'Under Maintenance' " +
            "WHERE asset_code = ?";

        String updateCompletedSQL =
            "UPDATE assets SET availability_status = " +
            "CASE WHEN condition_status = 'Good' " +
            "THEN 'Ready' ELSE 'Unavailable' END " +
            "WHERE asset_code = ?";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                // Save maintenance record
                try (PreparedStatement ps = con.prepareStatement(insertSQL)) {

                    ps.setString(1, assetCode.trim());
                    ps.setDate(2, Date.valueOf(maintenanceDate));
                    ps.setString(3, description.trim());
                    ps.setString(4, status);

                    ps.executeUpdate();
                }

                // Update asset availability
                if (status.equals("In Progress")) {

                    try (PreparedStatement ps =
                             con.prepareStatement(updateInProgressSQL)) {

                        ps.setString(1, assetCode.trim());
                        ps.executeUpdate();
                    }

                } else if (status.equals("Completed")) {

                    try (PreparedStatement ps =
                             con.prepareStatement(updateCompletedSQL)) {

                        ps.setString(1, assetCode.trim());
                        ps.executeUpdate();
                    }
                }

                con.commit();

                response.sendRedirect("maintenance.html?added=1");

            } catch (Exception e) {

                con.rollback();
                throw e;
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                500,
                "Unable to save maintenance record or update asset status."
            );
        }
    }
}
