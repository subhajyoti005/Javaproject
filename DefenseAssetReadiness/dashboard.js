
async function loadDashboardData() {

    try {

        const response = await fetch("dashboard-data");

        if (!response.ok) {
            throw new Error("Failed to load dashboard data");
        }

        const data = await response.json();

        document.getElementById("totalAssets").textContent = data.total;

        document.getElementById("readyAssets").textContent = data.ready;

        document.getElementById("maintenanceAssets").textContent = data.maintenance;

        document.getElementById("unavailableAssets").textContent = data.unavailable;

    } catch (error) {

        console.error("Dashboard Error:", error);

    }
}


// Load actual asset records from MySQL
async function loadAssetRecords() {

    try {

        const response = await fetch("asset-list");

        if (!response.ok) {
            throw new Error("Failed to load asset records");
        }

        const assets = await response.json();

        const tableBody = document.getElementById("assetTableBody");

        tableBody.innerHTML = "";

        if (assets.length === 0) {

            tableBody.innerHTML = `
                <tr>
                    <td colspan="5">No asset records found.</td>
                </tr>
            `;

            return;
        }

        assets.forEach(asset => {

            const row = document.createElement("tr");

            const values = [
                asset.asset_code,
                asset.asset_name,
                asset.category,
                asset.condition,
                asset.availability
            ];

            values.forEach((value, index) => {

                const cell = document.createElement("td");

                cell.textContent = value || "-";

                if (index === 4) {

                    const status = (value || "").toLowerCase();

                    if (status === "ready") {
                        cell.className = "status ready-text";
                    } else if (status === "under maintenance") {
                        cell.className = "status maintenance-text";
                    } else if (status === "unavailable") {
                        cell.className = "status unavailable-text";
                    }

                }

                row.appendChild(cell);

            });

            tableBody.appendChild(row);

        });

    } catch (error) {

        console.error("Asset Records Error:", error);

        document.getElementById("assetTableBody").innerHTML = `
            <tr>
                <td colspan="5">Unable to load asset records.</td>
            </tr>
        `;

    }
}


// Run both functions
loadDashboardData();
loadAssetRecords();
