package com.d3m0li5h3r.apps.weather.models;

/**
 * Created by d3m0li5h3r on 23/4/16.
 */
public class Wind {
    private float speed;
    private float deg;
    private float gust;

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDegrees() {
        return deg;
    }

    public void setDegrees(float deg) {
        this.deg = deg;
    }

    public float getGust() {
        return gust;
    }

    public void setGust(float gust) {
        this.gust = gust;
    }
}
