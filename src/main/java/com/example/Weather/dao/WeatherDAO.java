package com.example.Weather.dao;

import java.io.IOException;

public interface WeatherDAO {

    public String getWeatherDataCity(String city, String country) throws IOException;

    public String getHourlyWeatherData(String ciy, String country) throws IOException;

}
