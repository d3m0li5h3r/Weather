package com.d3m0li5h3r.apps.weather.models;

/**
 * Created by d3m0li5h3r on 23/4/16.
 */
public class WeatherData {
    private int cod;
    private int dt;

    private float visibility;

    private long id;

    private String name;
    private String base;
    private String message;

    private Coordinate coord;
    private Main main;
    private Sys sys;
    private Weather[] weather;
    private Wind wind;

    public int getCode() {
        return cod;
    }

    public void setCode(int cod) {
        this.cod = cod;
    }

    public int getDate() {
        return dt;
    }

    public void setDate(int dt) {
        this.dt = dt;
    }

    public float getVisibility() {
        return visibility / 1000;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }
}
