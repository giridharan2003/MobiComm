document.addEventListener("DOMContentLoaded", function () {

    console.log(localStorage.getItem("adminToken"));

    fetchPlans("active");
    fetchPlans("inactive");

    fetchCategoryList();  
});

// Fetch Plans (Active/Inactive)
function fetchPlans(status) {
    fetch(`http://localhost:2003/plans/${status}`)
        .then(response => response.json())
        .then(plans => {
            let tableBody = document.getElementById(status === "active" ? "activePlansTableBody" : "inactivePlansTableBody");
            tableBody.innerHTML = "";
            plans.forEach((plan, index) => {
                let row = `
                    <tr>
                        <td>${plan.planCode}</td>
                        <td>${plan.category.categoryName}</td>
                        <td>${plan.price}</td>
                        <td>${plan.dataLimit}</td>
                        <td>${plan.call}</td>
                        <td>${plan.validityDays} days</td>
                        <td>
                            <button class="btn btn-sm btn-primary" onclick="editPlan(${plan.planId})">Edit</button>
                            <button class="btn btn-sm btn-danger" onclick="togglePlanStatus(${plan.planId}, '${status}')">
                                ${status === "active" ? "Deactivate" : "Activate"}
                            </button>
                        </td>
                    </tr>
                `;
                tableBody.innerHTML += row;
            });
        })
        .catch(error => console.error(`Error fetching ${status} plans:`, error));
}

// Toggle Active () Inactive For Plans
function togglePlanStatus(planId, currentStatus) {
    let newStatus = currentStatus === "active" ? "inactive" : "active";
    fetch(`http://localhost:2003/admin/plans/${newStatus}/${planId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${localStorage.getItem("adminToken")}`
        }
    })
        .then(() => {
            fetchPlans("active");
            fetchPlans("inactive");
        })
        .catch(error => console.error("Error updating plan status:", error));
}

document.getElementById("addCategoryForm").addEventListener("submit", async function (event) {
    event.preventDefault();

    const categoryName = document.getElementById("categoryName").value;
    const enabledFields = Array.from(document.querySelectorAll('input[name="enabledFields"]:checked'))
        .map(checkbox => checkbox.value)
        .join(",");

    const categoryData = { categoryName, enabledFields };

    try {
        const response = await fetch("http://localhost:2003/admin/plans/newCategory", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${localStorage.getItem("adminToken")}`
            },
            body: JSON.stringify(categoryData)
        });

        if (response.ok) {
            alert("Category added successfully!");
            location.reload();
        } else {
            alert("Error adding category.");
        }
    } catch (error) {
        console.error("Error:", error);
    }
});

// Fetch Categories and Display in Table using async/await
async function fetchCategoryList() {
    try {
        const response = await fetch(`http://localhost:2003/admin/plans/categories`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${localStorage.getItem("adminToken")}`
            }
        });

        if (!response.ok) {
            throw new Error(`Error fetching categories: ${response.status} ${response.statusText}`);
        }

        const categories = await response.json();
        const categoryTableBody = document.getElementById("categoryTableBody");
        categoryTableBody.innerHTML = "";

        categories.forEach((category, index) => {
            const row = `
                <tr>
                    <td>${index + 1}</td>
                    <td>${category.categoryName}</td>
                </tr>
            `;
            categoryTableBody.innerHTML += row;
        });

    } catch (error) {
        console.error("Error fetching categories:", error);
    }
}


async function fetchPlanCategories() {
    try {
        const response = await fetch(`http://localhost:2003/admin/plans/categories`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${localStorage.getItem("adminToken")}`
            }
        });

        if (!response.ok) {
            throw new Error(`Error fetching plan categories: ${response.status} ${response.statusText}`);
        }

        const categories = await response.json();
        const categoryDropdown = document.getElementById("category");
        categoryDropdown.innerHTML = "<option value=''>Select Category</option>";

        let categoryCount = categories.length;

        for (let i = 0; i < categoryCount; i++) {
            let option = document.createElement("option");
            option.value = categories[i].categoryId;
            console.log(option.value);
            option.textContent = categories[i].categoryName;
            categoryDropdown.appendChild(option);
        }

    } catch (error) {
        console.error("Error fetching plan categories:", error);
    }
}

document.getElementById("addPlanModal").addEventListener("shown.bs.modal", function () {
    document.getElementById("addPlanForm").reset();
    toggleFields("");
    fetchPlanCategories();
});

document.getElementById("category").addEventListener("change", fetchCategoryFields);

async function fetchCategoryFields() {
    let categoryId = document.getElementById("category").value;

    if (!categoryId) return;

    try {
        const response = await fetch(`http://localhost:2003/admin/plans/categories/${categoryId}/enabled-fields`,{
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${localStorage.getItem("adminToken")}`
            }
        });
        const enabledFields = await response.text();

        toggleFields(enabledFields);

    } catch (error) {
        console.error("Error fetching category fields:", error);
    }
}

function toggleFields(enabledFields) {
    const allFields = ["badge", "price", "validityDays", "dataLimit", "call", "status", "sms", "additionalFeatures", "ott"];

    allFields.forEach(field => {
        let fieldDiv = document.getElementById(field + "Div");

        if (fieldDiv) {
            if (enabledFields.includes(field)) {
                fieldDiv.classList.remove("d-none");
                fieldDiv.classList.add("d-block");
            } else {
                fieldDiv.classList.add("d-none");
                fieldDiv.classList.remove("d-block");
                let fieldInput = document.getElementById(field);
                if (fieldInput) fieldInput.value = "";
            }
        }
    });
}


document.getElementById("addPlanForm").addEventListener("submit", async function (event) {
    event.preventDefault();

    let categoryId = document.getElementById("category").value;
    let badge = document.getElementById("badge").value;
    let price = document.getElementById("price").value;
    let validityDays = document.getElementById("validityDays").value;
    let dataLimit = document.getElementById("dataLimit").value;
    let call = document.getElementById("call").value;
    let status = document.getElementById("status").value;
    let sms = document.getElementById("sms").value;
    let additionalFeatures = document.getElementById("additionalFeatures").value;
    let ott = document.getElementById("ott").value;

    let planData = { 
        categoryId: categoryId,
        badge: badge || null,
        price: price || null,
        validityDays: validityDays || null,
        dataLimit: dataLimit || null,
        call: call || null,
        status: status || null,
        sms: sms || null,
        additionalFeatures: additionalFeatures || null,
        ott: ott || null
    };

    try {
        const response = await fetch("http://localhost:2003/admin/plans/add", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${localStorage.getItem("adminToken")}`
            },
            body: JSON.stringify(planData)
        });

        if (response.ok) {
            alert("Plan added successfully!");
            location.reload();
        } else {
            const errorMessage = await response.text();
            alert("Error adding plan: " + errorMessage);
        }
    } catch (error) {
        console.error("Error:", error);
    }
});
