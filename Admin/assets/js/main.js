document.addEventListener("DOMContentLoaded", function () {
  const toggleSidebarBtn = document.querySelector(".toggle-sidebar-btn");
  const sidebar = document.querySelector("#sidebar");

  if (toggleSidebarBtn && sidebar) {
    toggleSidebarBtn.addEventListener("click", function () {
      document.body.classList.toggle("toggle-sidebar");
    });
  }

  if (!localStorage.getItem("adminToken")) {
    alert("You are not logged in.");
    window.location.href = "index.html"; // Redirect to login page 
  }

});



document.addEventListener("DOMContentLoaded", function () {
  const logoutBtn = document.getElementById("logoutBtn");

  if (logoutBtn) {
      logoutBtn.addEventListener("click", async function (event) {
          event.preventDefault();

          const adminToken = localStorage.getItem("adminToken");

          try {
              const response = await fetch("http://localhost:2003/auth/logout", {
                  method: "POST",
                  headers: {
                      "Authorization": `Bearer ${adminToken}`,
                      "Content-Type": "application/json",
                  },
              });

              if (!response.ok) {
                  throw new Error("Logout failed");
              }

              const message = await response.text();
              console.log(message);
              
              localStorage.removeItem("adminToken");
              alert("Logged out successfully!");
              window.location.href = "index.html";

          } catch (error) {
              console.error("Error during logout:", error);
              alert("Failed to log out. Try again.");
          }
      });
  }
});