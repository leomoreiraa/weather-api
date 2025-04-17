document.getElementById('getWeather').addEventListener('click', async () => {
    const city = document.getElementById('city').value;
    const resultDiv = document.getElementById('result');

    if (!city) {
        resultDiv.innerHTML = 'Please enter a city name.';
        return;
    }

    try {
        // Chamada para a API de clima
        const weatherResponse = await fetch(`https://tired-evvy-leomoreiraa-64566b5e.koyeb.app/api/weather/${city}`);
        if (!weatherResponse.ok) {
            throw new Error('City not found or API error');
        }

        const weatherData = await weatherResponse.json();

        // Dados do clima
        const weatherText = `
            City: ${weatherData.city}
            Temperature: ${weatherData.temperature}°C
            Description: ${weatherData.description}
        `;

        // Chamada para a API de tradução
        const translateResponse = await fetch(`https://api.mymemory.translated.net/get?q=${encodeURIComponent(weatherText)}&langpair=en|pt`);
        const translateData = await translateResponse.json();

        // Exibir o resultado traduzido
        resultDiv.innerHTML = `
            <p><strong>Translated Weather Info:</strong></p>
            <p>${translateData.responseData.translatedText}</p>
        `;
    } catch (error) {
        resultDiv.innerHTML = `<p style="color: red;">Error: ${error.message}</p>`;
    }
});