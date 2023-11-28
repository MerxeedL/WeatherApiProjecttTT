package com.example.Weather.service;

import com.example.Weather.countrycodes.CountryCodes;
import com.example.Weather.dao.WeatherDAO;
import com.example.Weather.model.FiveDayHourlyWeather;
import com.example.Weather.model.Weather;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOError;
import java.io.IOException;
import java.util.*;

@Service
public class WeatherServiceImpl implements WeatherService {

    @Autowired
    WeatherDAO wDAO;

    private String json;
    private Weather weather;
    private Map<String, List<FiveDayHourlyWeather>> weatherForFiveDays;

    @Override
    public Weather getWeatherDataCity(String city, String country) throws IOException {
        return jsonParseCityWeather(city, country);
    }

    private Weather jsonParseCityWeather(String city, String country) throws IOException {

        this.json = this.wDAO.getWeatherDataCity(city, country);
        setWeatherParameters();

        return this.weather;

    }

    private void setWeatherParameters() {

        try {
            JSONObject obj = new JSONObject(this.json);

            String name = obj.getString("name").toString();
            String country = obj.getJSONObject("sys").getString("country");
            double humidity = obj.getJSONObject("main").getInt("humidity");
            double pressure = obj.getJSONObject("main").getInt("pressure");
            double temperature = obj.getJSONObject("main").getInt("temp");
            double tempFeelsLike = obj.getJSONObject("main").getInt("feels_like");
            double tempMax = obj.getJSONObject("main").getInt("temp_max");
            double tempMin = obj.getJSONObject("main").getInt("temp_min");
            double timeZone = obj.getDouble("timezone");
            String weather = obj.getJSONArray("weather").getJSONObject(0).getString("main");
            String weatherDesc = obj.getJSONArray("weather").getJSONObject(0).getString("main");

            this.weather = new Weather();

            this.weather.setCity(name);
            this.weather.setCountry(new CountryCodes().getCountry(country));
            this.weather.setCountryISOCode(country);
            this.weather.setHumidity(humidity);
            this.weather.setPressure(pressure);
            this.weather.setTemperature(temperature);
            this.weather.setTempFeelsLike(tempFeelsLike);
            this.weather.setTempMax(tempMax);
            this.weather.setTempMin(tempMin);
            this.weather.setTimeZone(timeZone);
            this.weather.setWeather(weather);
            this.weather.setWeatherDesc(weatherDesc);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Map<String, List<FiveDayHourlyWeather>> getHourlyWeather(String city, String country) throws IOException {
        return jsonParseHourlyWeather(city, country);
    }

    private Map<String, List<FiveDayHourlyWeather>> jsonParseHourlyWeather(String city, String country) throws IOException {

        this.json = this.wDAO.getHourlyWeatherData(city, country);
        setHourlyWeatherParameters();

        return this.weatherForFiveDays;

    }

    private void setHourlyWeatherParameters() {
        try {

            List<FiveDayHourlyWeather> weatherPerThreeHoursPerDay = new ArrayList<>();
            this.weatherForFiveDays = new LinkedHashMap<>();
            FiveDayHourlyWeather hourlyWeather;
            JSONObject obj = new JSONObject(this.json);
            DateTime dt = new DateTime(new Date());
            DateTime.Property dtp = dt.dayOfWeek();
            String day = dtp.getAsText();

            int count = 0;

            for (int i = 0; i < obj.getJSONArray("list").length(); i++) {

                hourlyWeather = new FiveDayHourlyWeather();

                String time = obj.getJSONArray("list").getJSONObject(i).getString("dt_txt").split(" ")[1];
                double humidity = obj.getJSONArray("list").getJSONObject(i).getJSONObject("main").getInt("humidity");
                double pressure = obj.getJSONArray("list").getJSONObject(i).getJSONObject("main").getInt("pressure");
                double temperature = obj.getJSONArray("list").getJSONObject(i).getJSONObject("main").getDouble("temp");
                double tempMax = obj.getJSONArray("list").getJSONObject(i).getJSONObject("main").getDouble("temp_max");
                double tempMin = obj.getJSONArray("list").getJSONObject(i).getJSONObject("main").getDouble("temp_min");
                String weather = obj.getJSONArray("list").getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main");
                String weatherDesc = obj.getJSONArray("list").getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description");

                hourlyWeather.setDay(day);
                hourlyWeather.setCity(getCity(obj));
                hourlyWeather.setCountry(new CountryCodes().getCountry(getCountry(obj)));
                hourlyWeather.setCountryISOCode(getCountry(obj));
                hourlyWeather.setTime(time);
                hourlyWeather.setHumidity(humidity);
                hourlyWeather.setPressure(pressure);
                hourlyWeather.setTemperature(temperature);
                hourlyWeather.setTempMax(tempMax);
                hourlyWeather.setTempMin(tempMin);
                hourlyWeather.setWeather(weather);
                hourlyWeather.setWeatherDesc(weatherDesc);

                weatherPerThreeHoursPerDay.add(hourlyWeather);

                if (time.equals("21:00:00")) {
                    this.weatherForFiveDays.put(day.toString(), weatherPerThreeHoursPerDay);
                    count++;
                    dtp = dt.plusDays(count).dayOfWeek();
                    day = dtp.getAsText();
                    weatherPerThreeHoursPerDay = new ArrayList<>();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getCountry(JSONObject obj) {

        String name = obj.getJSONObject("city").getString("name");

        return name;

    }

    private String getCity(JSONObject obj) {

        String country = obj.getJSONObject("city").getString("country");

        return country;

    }


}
