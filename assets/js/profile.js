document.addEventListener("DOMContentLoaded", async function () {

    const token = localStorage.getItem("token");

    const response = await fetch("http://localhost:2003/plans/user-details", {
        method: "GET",
        headers: { "Authorization": `Bearer ${token}` }
    });

    let userDetails = {};
    if (response.ok) {
        userDetails = await response.json();
    }

    const userId = userDetails.userId;



    const profileForm = document.getElementById("profileForm");
    const editBtn = document.getElementById("editBtn");
    const saveBtn = document.getElementById("saveBtn");

    const emailField = document.getElementById("email");
    const dobField = document.getElementById("dob");

    // Fetch user profile
    async function fetchUserProfile() {
        try {
            const response = await fetch(`http://localhost:2003/users/${userId}`);
            if (!response.ok) throw new Error("Failed to fetch user profile");

            const user = await response.json();

            document.getElementById("username").value = user.username;
            document.getElementById("mobile").value = user.phoneNumber;
            emailField.value = user.email;
            dobField.value = user.dob;
        } catch (error) {
            console.error("Error fetching profile:", error);
        }
    }

    // Fetch recharge history
    async function fetchRechargeHistory() {
        try {
            const response = await fetch(`http://localhost:2003/users/${userId}/recharge-history`);
            if (!response.ok) throw new Error("Failed to fetch recharge history");

            const rechargeHistory = await response.json();
            const historySection = document.getElementById("rechargeHistory");
            historySection.innerHTML = "<h3>Recharge History</h3>";

            if (rechargeHistory.length === 0) {
                historySection.innerHTML += "<p>No recharge history found.</p>";
                return;
            }

            let table = `
                <table class="table">
                    <thead>
                        <tr>
                            <th>Plan ID</th>
                            <th>Recharge Date</th>
                            <th>Expiry Date</th>
                        </tr>
                    </thead>
                    <tbody>
            `;

            rechargeHistory.forEach(history => {
                table += `
                    <tr>
                        <td>${history.plan.planId}</td>
                        <td>${new Date(history.rechargeDate).toLocaleDateString()}</td>
                        <td>${history.expiryDate ? new Date(history.expiryDate).toLocaleDateString() : "N/A"}</td>
                    </tr>
                `;
            });

            table += "</tbody></table>";
            historySection.innerHTML += table;
        } catch (error) {
            console.error("Error fetching recharge history:", error);
        }
    }

    // Enable editing of email and DOB
    editBtn.addEventListener("click", () => {
        emailField.disabled = false;
        dobField.disabled = false;
        saveBtn.style.display = "block";
        editBtn.style.display = "none";
    });

    // Save updated user profile
    saveBtn.addEventListener("click", async () => {
        try {
            const updatedData = {
                email: emailField.value,
                dob: dobField.value
            };

            const response = await fetch(`http://localhost:2003/users/${userId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(updatedData)
            });

            if (!response.ok) throw new Error("Failed to update profile");

            alert("Profile updated successfully!");
            emailField.disabled = true;
            dobField.disabled = true;
            saveBtn.style.display = "none";
            editBtn.style.display = "block";
        } catch (error) {
            console.error("Error updating profile:", error);
        }
    });

    // Load profile and recharge history on page load
    await fetchUserProfile();
    await fetchRechargeHistory();
});
