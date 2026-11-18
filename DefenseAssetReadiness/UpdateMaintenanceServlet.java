
package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;

@WebServlet("/update-maintenance")
public class UpdateMaintenanceServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String idValue = request.getParameter("id");
        String maintenanceDate = request.getParameter("maintenance_date");
        String description = request.getParameter("description");
        String status = request.getParameter("status");

        if (idValue == null || maintenanceDate == null ||
            description == null || status == null ||
            idValue.trim().isEmpty() ||
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

        String updateSQL =
            "UPDATE maintenance_records " +
            "SET maintenance_date = ?, description = ?, status = ? " +
            "WHERE id = ?";

        String updateAssetSQL =
            "UPDATE assets SET availability_status = " +
            "CASE " +
            "WHEN EXISTS (" +
            "SELECT 1 FROM maintenance_records " +
            "WHERE maintenance_records.asset_code = assets.asset_code " +
            "AND maintenance_records.status = 'In Progress'" +
            ") THEN 'Under Maintenance' " +
            "WHEN condition_status = 'Good' THEN 'Ready' " +
            "ELSE 'Unavailable' END " +
            "WHERE asset_code = ?";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                int recordId = Integer.parseInt(idValue.trim());
                String assetCode = null;

                // Find the asset belonging to this maintenance record
                String findSQL =
                    "SELECT asset_code FROM maintenance_records WHERE id = ?";

                try (PreparedStatement ps = con.prepareStatement(findSQL)) {

                    ps.setInt(1, recordId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {
                            assetCode = rs.getString("asset_code");
                        } else {
                            response.sendError(404, "Maintenance record not found.");
                            con.rollback();
                            return;
                        }
                    }
                }

                // Update maintenance record
                try (PreparedStatement ps = con.prepareStatement(updateSQL)) {

                    ps.setDate(1, Date.valueOf(maintenanceDate));
                    ps.setString(2, description.trim());
                    ps.setString(3, status);
                    ps.setInt(4, recordId);

                    ps.executeUpdate();
                }

                // Recalculate asset availability
                try (PreparedStatement ps =
                         con.prepareStatement(updateAssetSQL)) {

                    ps.setString(1, assetCode);
                    ps.executeUpdate();
                }

                con.commit();

                response.sendRedirect("maintenance.html?updated=1");

            } catch (Exception e) {

                con.rollback();
                throw e;
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                500,
                "Unable to update maintenance record."
            );
        }
    }
}
