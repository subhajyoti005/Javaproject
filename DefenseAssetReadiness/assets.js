
async function loadAssets() {

    const tableBody = document.getElementById("assetTableBody");

    try {

        const response = await fetch("asset-list");

        if (!response.ok) {
            throw new Error("Failed to load assets");
        }

        const assets = await response.json();

        tableBody.innerHTML = "";

        if (assets.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="5">No assets found.</td>
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

        console.error("Assets Error:", error);

        tableBody.innerHTML = `
            <tr>
                <td colspan="5">Unable to load assets.</td>
            </tr>
        `;
    }
}


// Search assets
document.getElementById("searchAsset").addEventListener("input", function () {

    const searchValue = this.value.toLowerCase();

    const rows = document.querySelectorAll("#assetTableBody tr");

    rows.forEach(row => {

        const rowText = row.textContent.toLowerCase();

        row.style.display = rowText.includes(searchValue) ? "" : "none";

    });

});


// Load assets when page opens
loadAssets();
