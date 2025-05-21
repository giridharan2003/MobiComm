// Function to display notification messages
function showNotification(message, type) {
      const notificationContainer = document.getElementById("notificationContainer");

      const notification = document.createElement("div");
      notification.className = `notification ${type} unfold`;
      notification.innerHTML = `
        <div class="notification-content">
          <span>${message}</span>
          <button class="close-btn">×</button>
        </div>
      `;

      notificationContainer.appendChild(notification);

      // Handle close button click
      notification.querySelector(".close-btn").addEventListener("click", () => {
        notification.classList.add("out");
        setTimeout(() => {
          notification.remove();
        }, 1300); // Match unfoldOut duration (1s + 0.3s delay)
      });

      // Auto-remove after 5 seconds
      setTimeout(() => {
        if (notification.isConnected) {
          notification.classList.add("out");
          setTimeout(() => {
            notification.remove();
          }, 1300);
        }
      }, 5000);
}

document.addEventListener("DOMContentLoaded", function () {
    initializeLoginState();
    fetchPlansByCategory("MOSTPOPULAR");

    document.getElementById("loginButton").addEventListener("click", handleLogin);
    document.getElementById("logoutBtn").addEventListener("click", handleLogout);
});

// Initialize login/logout state
function initializeLoginState() {
    const loginSection = document.getElementById("loginSection");
    const register = document.getElementById("register");
    const logOut = document.getElementById("logoutBtn");
    const hero = document.getElementById("hero");
    const header = document.getElementById("welcome-head");
    const para = document.getElementById("welcome-para");

    const token = localStorage.getItem("token");

    if (token) {
        loginSection.style.display = "none";
        register.style.display = "none";
        logOut.style.display = "block";
        hero.style.backgroundColor = "white";
        header.style.color = " #008374"
        para.style.color = " #008374"

    } else {
        logOut.style.display = "none";
    }
}

// Handle login request
async function handleLogin() {
    const mobileInput = document.getElementById("mobileNumber").value.trim();
    const errorMessage = document.getElementById("errorMessage");
    const loadingSpan = document.getElementById("loading");

    if (!/^[6-9]\d{9}$/.test(mobileInput)) {
        errorMessage.textContent = "Invalid mobile number format!";
        errorMessage.style.display = "block";
        return;
    }

    errorMessage.style.display = "none";
    loadingSpan.style.display = "block";

    try {
        const response = await fetch(`http://localhost:2003/auth/login/${mobileInput}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" }
        });

        const data = await response.json();
        loadingSpan.style.display = "none";

        if (!response.ok) {
            errorMessage.textContent = "Invalid mobile number!";
            errorMessage.style.display = "block";
        } else {
            localStorage.setItem("token", data.token);
            setTimeout(() => location.reload(), 2000);
        }
    } catch (error) {
        console.error("Error:", error);
        errorMessage.textContent = "Something went wrong!";
        errorMessage.style.display = "block";
        loadingSpan.style.display = "none";
    }
}

// Fetch plans by category
async function fetchPlansByCategory(categoryName = "BUDGET FRIENDLY") {
    try {
        const response = await fetch(`http://localhost:2003/plans/badge/BUDGET FRIENDLY`);

        if (!response.ok) {
            throw new Error(`Error fetching plans for category: ${categoryName}`);
        }

        const plans = await response.json();
        renderPlans(plans);

    } catch (error) {
        showNotification("Failed to fetch Plans!", "error");
        console.error(error);
    }
}

// Render plans dynamically
function renderPlans(plans) {
    const plansContainer = document.getElementById("plansContainer");
    plansContainer.innerHTML = "";

    if (!plans || plans.length === 0) {
        plansContainer.innerHTML = `<p class="text-center text-muted">No plans available.</p>`;
        return;
    }

    plans.forEach((plan, index) => {
        const card = document.createElement("div");
        card.className = "col-lg-3 col-md-4 col-sm-6 col-12 d-flex justify-content-center mb-4";
        card.setAttribute("data-aos", "fade-up");
        card.setAttribute("data-aos-delay", `${index * 100}`);

        const badgeName = plan.badge?.badgeName ? `<div class="highlight-badge">${plan.badge.badgeName}</div>` : "";
        const categoryName = plan.category?.categoryName ? `<h5 class="category-name">${plan.category.categoryName}</h5>` : "";
        
        // List of details to display
        const planDetails = [
            { icon: "bi-calendar", label: "Validity", value: plan.validityDays ? `${plan.validityDays} Days` : null },
            { icon: "bi-wifi", label: "Data", value: plan.dataLimit ? `${plan.dataLimit}/Day` : null },
            { icon: "bi-telephone", label: "Calls", value: plan.call },
            { icon: "bi-chat-dots", label: "SMS", value: plan.sms },
            { icon: "bi-list-check", label: "Features", value: plan.additionalFeatures },
            { icon: "bi-tv", label: "OTT", value: plan.ott },
        ];

        // Generate the HTML for non-null fields
        const planDetailsHTML = planDetails
            .filter(detail => detail.value) // Only keep non-null values
            .map(detail => `<p><i class="bi ${detail.icon}"></i> ${detail.label}: ${detail.value}</p>`)
            .join("");

        card.innerHTML = `
            <div class="recharge-card">
                <div class="card-header">
                    <div class="icon-price-container">
                        <div class="icon">
                            <i class="bi bi-currency-rupee"></i>
                        </div>
                        <div class="price">${plan.price}</div>
                    </div>
                    <p class="m-3">${categoryName}</p>
                    <hr>
                    ${badgeName}
                </div>
                
                <div class="card-body">
                    ${planDetailsHTML}
                </div>
                <div class="card-footer">
                    <button class="buy-now btn btn-primary" onclick='showPlanDetails(${JSON.stringify(plan)})'>Buy Now</button>
                </div>
            </div>
        `;

        plansContainer.appendChild(card);
    });
}


async function showPlanDetails(plan) {
    const modalBody = document.getElementById("modalBody");
    modalBody.innerHTML = "Loading...";

    const token = localStorage.getItem("token");
    if (token == null){
        showNotification("Please Log In to Continue","error");
        return;
    }

    try {
        const token = localStorage.getItem("token");

        const response = await fetch("http://localhost:2003/plans/user-details", {
            method: "GET",
            headers: { "Authorization": `Bearer ${token}` }
        });

        let userDetails = {};
        if (response.ok) {
            userDetails = await response.json();
            console.log(response)
        }

        let userSection = `
            <h5>User Details</h5>
            <input type="text" id="userName" class="form-control mb-2" value="${userDetails.name}" disabled>
            <input type="text" id="userPhone" class="form-control mb-2" value="${userDetails.phoneNumber}">
            <input type="email" id="userEmail" class="form-control mb-2" value="${userDetails.email}">
            <hr>
        `;

        let planDetails = "<h5>Plan Details</h5>";
        const fields = {
            "Category": plan.category?.categoryName,
            "Badge": plan.badge?.badgeName,
            "Price": plan.price ? `₹${plan.price}` : null,
            "Validity": plan.validityDays ? `${plan.validityDays} Days` : null,
            "Data Limit": plan.dataLimit ? `${plan.dataLimit}/Day` : null,
            "Calls": plan.call,
            "SMS": plan.sms,
            "Additional Features": plan.additionalFeatures,
            "OTT": plan.ott
        };

        Object.entries(fields).forEach(([key, value]) => {
            if (value !== null && value !== undefined) {
                planDetails += `<p><strong>${key}:</strong> ${value}</p>`;
            }
        });

        let buyButton = `<button id="buyButton" class="btn btn-success w-100 mt-3" onclick='proceedToBuy(${JSON.stringify(plan)},${JSON.stringify(userDetails)})'>Proceed to Buy</button>`;

        modalBody.innerHTML = userSection + planDetails + buyButton;

        const planModal = new bootstrap.Modal(document.getElementById("planModal"));
        planModal.show();
    } catch (error) {
        showNotification("Failed to load plan details!", "error");
        console.error(error);
        modalBody.innerHTML = "<p>Error loading details.</p>";
    }
}

async function proceedToBuy(plan, user) {
    const token = localStorage.getItem("token");

    const phone = document.getElementById("userPhone").value;
    const email = document.getElementById("userEmail").value;

    if (!token) {
        showNotification("Please log in to proceed!", "error");
        return;
    }

    showNotification("Processing payment...", "success");

    try {
        const price = plan.price * 100;

        const options = {
            key: "rzp_test_2n3UDy0oZ1OE4W",
            amount: price,
            currency: "INR",
            name: "Mobi Comm",
            description: "Prepaid Recharge Plan Purchase",
            image: "",
            prefill: {
                email: email || user.email,
                contact: phone || user.phoneNumber
            },
            handler: async function (response) {
                showNotification("Payment Successful! ", "success");

                await saveRechargeHistory(
                    plan,
                    user, 
                    email, 
                    phone, 
                    response.razorpay_payment_id, 
                    response.method
                );
            },
            theme: {
                color: "#28a745"
            }
        };

        const rzp = new Razorpay(options);
        rzp.open();
    } catch (error) {
        console.error("Error in proceedToBuy:", error);
    }
}

async function saveRechargeHistory(plan, user, email, phoneNumber, transactionId, paymentMode) {
    const token = localStorage.getItem("token");

    const payload = {
        plan: { planId: plan.planId },
        user: { userId: user.userId },
        price: plan.price,
        phoneNumber: phoneNumber || null,
        email: email || null,
        transactionId: transactionId,
        paymentMode: "Null",
        paymentStatus: "Success"
    };

    console.log(payload);

    try {
        const response = await fetch("http://localhost:2003/recharge/create", {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            showNotification("Saving recharge history...", "success");
        } else {
            const errorMsg = await response.json();
            alert("Failed to save recharge history: " + (errorMsg.message || "Unknown error"));
        }
    } catch (error) {
        console.error("Error saving recharge history:", error);
    }
}