document.addEventListener("DOMContentLoaded", () => {
    fetchCategories();
});

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


async function fetchCategories() {
    try {
        const response = await fetch("http://localhost:2003/plans/categories");
        const categories = await response.json();

        if (!categories || categories.length === 0) {
            console.error("No categories found.");
            showNotification("No categories available.", "error");
            return;
        }

        renderCategoryTabs(categories);

        if (categories.length > 0) {
            fetchPlansByCategory(categories[0].categoryName);
        }
    } catch (error) {
        console.error("Error fetching categories:", error);
        showNotification("Failed to load categories.", "error");
    }
}

function renderCategoryTabs(categories) {
    const categoryTabs = document.getElementById("categoryTabs");
    categoryTabs.innerHTML = "";

    categories.forEach(category => {
        const categoryName = category.categoryName || "Unknown";
        const tab = document.createElement("button");
        tab.className = "btn category";
        tab.textContent = categoryName;
        tab.addEventListener("click", () => {
            document.querySelectorAll("#categoryTabs .btn.category").forEach(tab => {
                tab.classList.remove("active");
            });
            tab.classList.add("active");
            fetchPlansByCategory(categoryName);
        });
        categoryTabs.appendChild(tab);
    });

    if (categories.length > 0) {
        categoryTabs.querySelector(".btn.category").classList.add("active");
    }
}

async function fetchPlansByCategory(categoryName) {
    try {
        const response = await fetch(`http://localhost:2003/plans/category/${categoryName}`);

        if (!response.ok) {
            throw new Error(`Error fetching plans for category: ${categoryName}`);
        }

        const plans = await response.json();
        renderPlans(plans);
    } catch (error) {
        console.error("Error fetching plans:", error);
        showNotification(`Failed to load plans for ${categoryName}.`, "error");
    }
}

function renderPlans(plans) {
    const plansContainer = document.getElementById("plansContainer");
    plansContainer.innerHTML = "";

    plans.forEach((plan, index) => {
        const card = document.createElement("div");
        card.className = "col-lg-3 col-md-4 col-sm-6 col-12 d-flex justify-content-center mb-4";
        card.setAttribute("data-aos", "fade-up");
        card.setAttribute("data-aos-delay", `${index * 100}`);

        const badgeName = plan.badge?.badgeName ? `<div class="highlight-badge">${plan.badge.badgeName}</div>` : "";
        const price = plan.price ? `<p class="price">${plan.price}</p>` : "";
        const validity = plan.validityDays ? `<p><i class="bi bi-calendar"></i><strong>  Validity :</strong> ${plan.validityDays} Days</p>` : "";
        const dataLimit = plan.dataLimit ? `<p><i class="bi bi-wifi"></i><strong>  Data  :</strong> ${plan.dataLimit}/Day</p>` : "";
        const calls = plan.call ? `<p><i class="bi bi-telephone"></i><strong>  Calls  :</strong> ${plan.call}</p>` : "";
        const sms = plan.sms ? `<p><i class="bi bi-chat-dots"></i><strong>  SMS  :</strong> ${plan.sms}/Day</p>` : "";
        const extraFeature = plan.ott ? `<p><i class="bi bi-tv"></i><strong>  OTT  :</strong> ${plan.ott}</p>` : 
                        plan.additionalFeatures ? `<p><i class="bi bi-card-list"></i><strong> Features: </strong> ${plan.additionalFeatures}</p>` : "";

        card.innerHTML = `
            <div class="recharge-card">
                ${badgeName}
                <div class="card-header">
                    <div class="icon-price-container">
                        <div class="icon">
                            <i class="bi bi-currency-rupee"></i>
                        </div>
                        <div class="price">${price}</div>
                    </div>
                    <hr>
                </div>
                <div class="card-body">
                    ${validity}
                    ${dataLimit}
                    ${calls}
                    ${sms}
                    ${extraFeature}
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
        
        const response = await fetch("http://localhost:2003/plans/user-details", {
            method: "GET",
            headers: { "Authorization": `Bearer ${token}` }
        });

        // let userDetails = {};
        if (response.ok) {
            userDetails = await response.json();
            console.log(response)
        }

        let userSection = `
            <h5>User Details</h5>
            <input type="text" id="userName" class="form-control mb-2" value="${userDetails.name || ''}" disabled>
            <input type="text" id="userPhone" class="form-control mb-2" value="${userDetails.phoneNumber || ''}">
            <input type="email" id="userEmail" class="form-control mb-2" value="${userDetails.email || ''}">
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
            "SMS": plan.sms ? `${plan.sms}/Day` : null,
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
        console.error("Error fetching user details:", error);
        modalBody.innerHTML = "<p>Error loading details.</p>";
        showNotification("Failed to load user details.", "error");
    }
}

async function proceedToBuy(plan, user) {
    const planModal = new bootstrap.Modal(document.getElementById("planModal"));
    planModal.hide();
    const token = localStorage.getItem("token");

    const phone = document.getElementById("userPhone").value;
    const email = document.getElementById("userEmail").value;

    if (!token) {
        showNotification("Please log in to proceed!", "error");
        return;
    }

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
                email: email || user.email || '',
                contact: phone || user.phoneNumber || ''
            },
            handler: async function (response) {
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
        showNotification("Error processing payment.", "error");
    }
}

async function saveRechargeHistory(plan, user, email, phoneNumber, transactionId, paymentMode) {
    const token = localStorage.getItem("token");

    const payload = {
        plan: { planId: plan.planId },
        user: { userId: user.userId || null },
        price: plan.price,
        phoneNumber: phoneNumber || null,
        email: email || null,
        transactionId: transactionId,
        paymentMode: paymentMode || "Null",
        paymentStatus: "Success"
    };

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
            showNotification("Successfully Recharged!", "success");
        } else {
            const errorMsg = await response.json();
            showNotification("Failed to save recharge history: " + (errorMsg.message || "Unknown error"), "error");
        }
    } catch (error) {
        console.error("Error saving recharge history:", error);
        showNotification("Error saving recharge history.", "error");
    }
}