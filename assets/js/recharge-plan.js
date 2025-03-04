async function fetchPlans() {
    try {
        let response = await fetch("http://localhost:3000/plans");
        let data = await response.json();

        if (data.length > 0) {
            const distinctCategories = [...new Set(data.map(plan => plan.category).filter(Boolean))];
            renderCategoryTabs(distinctCategories);

            // Load only "Unlimited Plans" by default
            const unlimitedPlans = data.filter(plan => plan.category === "Unlimited Plans");
            renderPlanCards(unlimitedPlans, "Unlimited Plans");

            addFilterControls(); // Add filter controls
        }
    } catch (error) {
        console.error("Error fetching plans:", error);
    }
}

function renderCategoryTabs(categories) {
    const tabContainer = document.getElementById("categoryTabs");
    tabContainer.innerHTML = "";
    tabContainer.classList.add("tab-container");

    categories.forEach((category, index) => {
        const tab = document.createElement("button");
        tab.className = `category-tab ${index === 0 ? 'active' : ''}`;
        tab.textContent = category;
        tab.onclick = () => {
            clearFilters(); // Clear filters when category is changed
            filterPlansByCategory(category, categories);
        };
        tabContainer.appendChild(tab);
    });
}

function filterPlansByCategory(category, categories) {
    document.querySelectorAll(".category-tab").forEach(tab => {
        tab.classList.remove("active");
        if (tab.textContent === category) {
            tab.classList.add("active");
        }
    });
    fetch("http://localhost:3000/plans")
        .then(response => response.json())
        .then(data => {
            const filteredPlans = data.filter(plan => plan.category === category);
            renderPlanCards(filteredPlans, category);
        });
}

function renderPlanCards(plansData, category) {
    const container = document.getElementById("plansContainer");
    container.innerHTML = "";

    plansData.forEach(plan => {
        const card = document.createElement("div");
        card.className = "col-lg-4";
        card.setAttribute("data-aos", "fade-up");
        card.setAttribute("data-aos-delay", "100");

        if (plan.special) {
            if (plan.special === "Most popular") {
                cardTagHTML = `<span class="card-tag most-popular">${plan.special}</span>`;
            } else {
                cardTagHTML = `<span class="card-tag budget">${plan.special}</span>`;
            }
        }

        card.innerHTML = `
            <div class="recharge-item position-relative">
            ${cardTagHTML}
                <div class="icon">
                    <i class="bi bi-currency-rupee"></i>
                </div>
                <h3>₹${plan.price} Plan</h3>
                <p><strong>Validity:</strong> ${plan.validity}</p>
                <p><strong>Plan Name:</strong> ${plan.plan_name}</p>
                <p><strong>Status:</strong> ${plan.call_sms}</p>
                <a href="#" class="btn-get-started explore-more stretched-link">Details</a>
            </div>
        `;
        container.appendChild(card);

        // Add event listener to the "Details" button
        const detailsButton = card.querySelector(".explore-more");
        detailsButton.addEventListener("click", (e) => {
            e.preventDefault();
            openModalWithPlanDetails(plan);
        });
    });
}

function openModalWithPlanDetails(plan) {
    // Populate the modal with plan details
    document.getElementById("modalPrice").textContent = `₹${plan.price}`;
    document.getElementById("modalValidity").textContent = plan.validity;
    document.getElementById("modalData").textContent = plan.data;
    document.getElementById("modalCallSms").textContent = plan.call_sms;
    document.getElementById("modalCategory").textContent = plan.category;
    document.getElementById("modalAdditionalFeatures").textContent = plan.additional_features;
    document.getElementById("modalSpecial").textContent = plan.special;

    // Show the modal
    const modal = new bootstrap.Modal(document.getElementById('planDetailsModal'));
    modal.show();

    // Add event listener to the "Confirm" button
    document.getElementById("confirmPayment").onclick = () => {

        localStorage.setItem("selectedPlan", JSON.stringify(plan));
        window.location.href = "payment.html"; // Redirect to payment page


    };
}

function addFilterControls() {
    const filterContainer = document.createElement("div");
    filterContainer.className = "filter-container";
    filterContainer.innerHTML = `
        <div class="filter-controls">
            <input type="number" id="amountFilter" placeholder="Filter by amount">
            <select id="sortFilter">
                <option value="">Sort by</option>
                <option value="lowToHigh">Price: Low to High</option>
                <option value="highToLow">Price: High to Low</option>
            </select>
        </div>
    `;
    document.querySelector("#recharges").prepend(filterContainer);

    // Add event listeners for filtering
    document.getElementById("amountFilter").addEventListener("input", applyFilters);
    document.getElementById("sortFilter").addEventListener("change", applyFilters);
}

function clearFilters() {
    // Reset filter input fields
    document.getElementById("amountFilter").value = "";
    document.getElementById("sortFilter").value = "";
}

function applyFilters() {
    const amountFilter = document.getElementById("amountFilter").value;
    const sortFilter = document.getElementById("sortFilter").value;

    fetch("http://localhost:3000/plans")
        .then(response => response.json())
        .then(data => {
            let filteredPlans = data;

            // Filter by amount
            if (amountFilter) {
                filteredPlans = filteredPlans.filter(plan => plan.price <= parseInt(amountFilter));
            }

            // Sort by price
            if (sortFilter === "lowToHigh") {
                filteredPlans.sort((a, b) => parseFloat(a.price) - parseFloat(b.price));
            } else if (sortFilter === "highToLow") {
                filteredPlans.sort((a, b) => parseFloat(b.price) - parseFloat(a.price));
            }

            // Get the active category
            const activeCategory = document.querySelector(".category-tab.active").textContent;
            const categoryFilteredPlans = filteredPlans.filter(plan => plan.category === activeCategory);
            renderPlanCards(categoryFilteredPlans, activeCategory);
        });
}

window.onload = fetchPlans;