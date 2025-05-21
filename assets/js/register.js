document.addEventListener('DOMContentLoaded', async function () {
    suggestionsMobileNo();
});


const reloadButton = document.getElementById('reloadButton');
reloadButton.addEventListener('click', () => {
    suggestionsMobileNo();
});

async function suggestionsMobileNo(){
    const suggestMobileNoDiv = document.getElementById('suggest-mobileNo');

    try {
        const response = await fetch('http://localhost:2003/auth/register/suggest-numbers', {
            method: "GET",
            headers: {
                'Content-Type': 'application/json',
            }
        });

        const data = await response.json();

        suggestMobileNoDiv.innerHTML = '';
        if (data.length > 0) {
            data.forEach(number => {
                const suggestion = document.createElement('div');
                suggestion.textContent = number;
                suggestion.style.cursor = 'pointer';
                suggestion.style.padding = '10px';
                suggestion.style.border = '1px solid #ccc';
                suggestion.style.borderRadius = '5px';
                suggestion.style.flex = '1 1 calc(50% - 10px)'; // Two cards per row with gap
                suggestion.style.boxSizing = 'border-box';
                suggestion.addEventListener('click', () => {
                    document.getElementById('phone').value = number;
                    suggestMobileNoDiv.innerHTML = '';
                });
                suggestMobileNoDiv.appendChild(suggestion);
            });
        }
    } catch (error) {
        console.error('Error fetching suggestions:', error);
    }
}

document.querySelector('form').addEventListener('submit', async function (event) {
    event.preventDefault();
    
    const formData = {
        name: document.getElementById('fullName').value,
        email: document.getElementById('email').value,
        phoneNumber: document.getElementById('phone').value,
        dob: document.getElementById('dob').value
    };

    try {
        const response = await fetch('http://localhost:2003/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData),
        });

        const textResponse = await response.text(); // Read the response as text

        const data = JSON.parse(textResponse); // Try parsing JSON
        alert(data.message || "User registered successfully!");
    } catch (error) {
        alert(textResponse);
    }
});