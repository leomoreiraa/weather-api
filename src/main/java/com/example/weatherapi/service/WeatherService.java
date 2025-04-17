package com.example.weatherapi.service;

import com.example.weatherapi.model.WeatherResponse;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    private final WebClient webClient;
    private final Cache<String, WeatherResponse> cache;

    public WeatherService(Cache<String, WeatherResponse> cache) {
        this.webClient = WebClient.create();
        this.cache = cache;
    }

    public Mono<WeatherResponse> getWeather(String city) {
        WeatherResponse cached = cache.getIfPresent(city.toLowerCase());
        if (cached != null) {
            System.out.println("Cache hit for city: " + city);
            return Mono.just(cached);
        }

        // Substitui os placeholders na URL
        String url = apiUrl.replace("{city}", city).replace("{key}", apiKey);
        System.out.println("Requesting weather data from URL: " + url); // Log da URL

        // Faz a requisição para a API de forma reativa
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(response -> {
                    if (response == null) {
                        throw new RuntimeException("Empty response");
                    }

                    System.out.println("Response from API: " + response); // Log da resposta

                    // Processa os dados da resposta
                    WeatherResponse weather = new WeatherResponse();
                    weather.setCity((String) response.get("name")); // Nome da cidade
                    weather.setTemperature(((Map<String, Double>) response.get("main")).get("temp")); // Temperatura
                    weather.setDescription(((Map<String, Object>) ((List<?>) response.get("weather")).get(0)).get("description").toString()); // Descrição do clima

                    // Armazena no cache
                    cache.put(city.toLowerCase(), weather);
                    return weather;
                })
                .onErrorMap(e -> {
                    System.err.println("Error fetching weather data: " + e.getMessage()); // Log do erro
                    return new RuntimeException("Erro ao buscar previsão: " + e.getMessage());
                });
    }
}
