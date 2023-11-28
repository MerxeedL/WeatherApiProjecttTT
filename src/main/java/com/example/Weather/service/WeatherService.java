package com.example.Weather.service;

import com.example.Weather.model.FiveDayHourlyWeather;
import com.example.Weather.model.Weather;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface WeatherService {

    public Weather getWeatherDataCity(String city, String country) throws IOException;

    public Map<String, List<FiveDayHourlyWeather>> getHourlyWeather(String city, String country) throws IOException;

}
