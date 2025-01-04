document.getElementById("submitBtn").addEventListener("click", async function () {
    const userId = document.getElementById("userId").value.trim();
    const requestPath = document.getElementById("requestPath").value.trim();
  
    if (!userId || !requestPath) {
      alert("Please enter both User ID and Request Path.");
      return;
    }
  
    try {
      const response = await fetch("http://localhost:8082/api/limits", {
        method: "GET",
        headers: {
          "X-User-ID": userId,
          "X-Request-Path": requestPath
        }
      });
  
      const data = await response.json();
  
      if (response.status === 429) {
        document.getElementById("responseMessage").textContent = "Too many requests. Try again later.";
        document.getElementById("remainingQuota").textContent = "";
      } else {
        document.getElementById("responseMessage").textContent = "Request successful.";
        document.getElementById("remainingQuota").textContent = `Remaining quota: ${data.remainingQuota} requests`;
      }
    } catch (error) {
      document.getElementById("responseMessage").textContent = "An error occurred. Please try again.";
      document.getElementById("remainingQuota").textContent = "";
    }
  });
  