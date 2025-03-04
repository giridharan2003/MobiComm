function renderPlanCardsHome(plansData) {
  const container = document.getElementById("plansContainer");
  container.innerHTML = "";

  // Slice the array to get only the first 6 elements
  const firstSixPlans = plansData.slice(0, 6);

  firstSixPlans.forEach((plan) => {
      const card = document.createElement("div");
      card.className = "col-lg-4 col-md-6";
      card.setAttribute("data-aos", "fade-up");
      card.setAttribute("data-aos-delay", "100");

      card.innerHTML = `
          <div class="recharge-item position-relative">
              <div class="icon">
                  <i class="bi bi-currency-rupee"></i>
              </div>
              <h3>₹${plan.price} Plan</h3>
              <p><strong>Category:</strong> ${plan.category}</p>
              <p><strong>Validity:</strong> ${plan.validity}</p>
              <p><strong>Status:</strong> ${plan.call_sms}</p>
              <a href="" class="btn-get-started explore-more">Details</a>
              <a href="recharge-plans.html" class="explore-more ms-5">Explore More <i class="bi bi-arrow-right"></i></a>
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

// Fetch plans data from the JSON server.
async function fetchPlans() {
  try {
      let response = await fetch("http://localhost:3000/plans");
      let data = await response.json();
      plans = data;
      renderPlanCardsHome(data); // Call the new render function
      updateCategoryFilter();
      // Update modal dropdowns with distinct categories.
      const distinctCategories = [
          ...new Set(
              data
                  .map((plan) => plan.category)
                  .filter((cat) => cat && cat.trim() !== "")
          ),
      ];
      updateModalCategoryDropdowns(distinctCategories);
      updatePlanStatusCounts();
  } catch (error) {
      console.error("Error fetching plans:", error);
  }
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
        window.location.href = `payment.html?planId=${plan.id}`; // Redirect to payment page with plan ID
    };
}


// Example usage:
// Call fetchPlans() when the page loads.
window.onload = fetchPlans;