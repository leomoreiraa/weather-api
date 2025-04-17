package com.example.weatherapi.controller;

import com.example.weatherapi.model.WeatherResponse;
import com.example.weatherapi.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService service;

    public WeatherController(WeatherService service) {
        this.service = service;
    }

    @GetMapping("/{city}")
    public Mono<ResponseEntity<WeatherResponse>> getWeather(@PathVariable String city) {
        return service.getWeather(city)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    System.err.println("Error in controller: " + e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    @GetMapping("/")
    public String home() {
        return "Welcome to the Weather API!";
    }
}
