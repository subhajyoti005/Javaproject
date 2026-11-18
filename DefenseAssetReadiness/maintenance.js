
async function loadAssetDropdown() {

    const dropdown = document.getElementById("assetCode");

    try {
        const response = await fetch("asset-list");

        if (!response.ok) {
            throw new Error("Unable to load assets");
        }

        const assets = await response.json();

        dropdown.innerHTML = '<option value="">Select Asset</option>';

        assets.forEach(asset => {
            const option = document.createElement("option");

            option.value = asset.asset_code;
            option.textContent =
                asset.asset_code + " - " + asset.asset_name;

            dropdown.appendChild(option);
        });

    } catch (error) {
        console.error("Asset Dropdown Error:", error);
        dropdown.innerHTML =
            '<option value="">Unable to load assets</option>';
    }
}


// Edit an existing maintenance record
async function editMaintenance(record) {

    const newDate = prompt(
        "Enter Maintenance Date (YYYY-MM-DD):",
        record.maintenance_date
    );

    if (newDate === null) return;

    const newDescription = prompt(
        "Enter Maintenance Description:",
        record.description
    );

    if (newDescription === null) return;

    const newStatus = prompt(
        "Enter Status: Pending, In Progress, or Completed",
        record.status
    );

    if (newStatus === null) return;

    const validStatuses = [
        "Pending",
        "In Progress",
        "Completed"
    ];

    const selectedStatus = validStatuses.find(
        status => status.toLowerCase() === newStatus.trim().toLowerCase()
    );

    if (!selectedStatus) {
        alert("Invalid status. Please use Pending, In Progress, or Completed.");
        return;
    }

    if (!newDate.trim() || !newDescription.trim()) {
        alert("Date and description cannot be empty.");
        return;
    }

    const formData = new URLSearchParams();

    formData.append("id", record.id);
    formData.append("maintenance_date", newDate.trim());
    formData.append("description", newDescription.trim());
    formData.append("status", selectedStatus);

    try {

        const response = await fetch("update-maintenance", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: formData.toString()
        });

        if (!response.ok) {
            throw new Error("Unable to update maintenance record");
        }

        alert("Maintenance record updated successfully!");

        await loadMaintenanceRecords();

    } catch (error) {

        console.error("Update Error:", error);

        alert("Unable to update maintenance record. Please check the server.");
    }
}


// Load maintenance records
async function loadMaintenanceRecords() {

    const tableBody =
        document.getElementById("maintenanceTableBody");

    try {

        const response = await fetch("maintenance-list");

        if (!response.ok) {
            throw new Error("Unable to load maintenance records");
        }

        const records = await response.json();

        tableBody.innerHTML = "";

        let pending = 0;
        let progress = 0;
        let completed = 0;

        records.forEach(record => {

            const status = (record.status || "").toLowerCase();

            if (status === "pending") pending++;
            else if (status === "in progress") progress++;
            else if (status === "completed") completed++;

        });

        document.getElementById("totalMaintenance").textContent =
            records.length;

        document.getElementById("pendingMaintenance").textContent =
            pending;

        document.getElementById("progressMaintenance").textContent =
            progress;

        document.getElementById("completedMaintenance").textContent =
            completed;

        if (records.length === 0) {

            tableBody.innerHTML = `
                <tr>
                    <td colspan="5">No maintenance records found.</td>
                </tr>
            `;

            return;
        }

        records.forEach(record => {

            const row = document.createElement("tr");

            const values = [
                record.asset_code,
                record.maintenance_date,
                record.description,
                record.status
            ];

            values.forEach((value, index) => {

                const cell = document.createElement("td");

                cell.textContent = value || "-";

                if (index === 3) {

                    const status = (value || "").toLowerCase();

                    if (status === "completed") {
                        cell.className = "status ready-text";
                    } else if (status === "pending") {
                        cell.className = "status unavailable-text";
                    } else if (status === "in progress") {
                        cell.className = "status maintenance-text";
                    }
                }

                row.appendChild(cell);

            });

            // Edit button
            const actionCell = document.createElement("td");

            const editButton = document.createElement("button");

            editButton.type = "button";
            editButton.textContent = "Edit";
            editButton.className = "edit-btn";

            editButton.addEventListener("click", () => {
                editMaintenance(record);
            });

            actionCell.appendChild(editButton);
            row.appendChild(actionCell);

            tableBody.appendChild(row);

        });

    } catch (error) {

        console.error("Maintenance Error:", error);

        tableBody.innerHTML = `
            <tr>
                <td colspan="5">Unable to load maintenance records.</td>
            </tr>
        `;
    }
}


// Load everything when page opens
loadAssetDropdown();
loadMaintenanceRecords();
