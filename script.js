document.getElementById('getWeather').addEventListener('click', async () => {
    const city = document.getElementById('city').value;
    const resultDiv = document.getElementById('result');

    if (!city) {
        resultDiv.innerHTML = 'Please enter a city name.';
        return;
    }

    try {
        const response = await fetch(`https://tired-evvy-leomoreiraa-64566b5e.koyeb.app/api/weather/${city}`);
        if (!response.ok) {
            throw new Error('City not found or API error');
        }

        const data = await response.json();
        resultDiv.innerHTML = `
            <p><strong>City:</strong> ${data.city}</p>
            <p><strong>Temperature:</strong> ${data.temperature}°C</p>
            <p><strong>Description:</strong> ${data.description}</p>
        `;
    } catch (error) {
        resultDiv.innerHTML = `<p style="color: red;">Error: ${error.message}</p>`;
    }
});