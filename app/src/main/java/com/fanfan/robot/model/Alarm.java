package com.fanfan.robot.model;

public class Alarm {

    private int Fog;
    private int Flame;
    private double Dust;
    private double Humidity;
    private double Temperature;

    public Alarm(int fog, int flame, double dust, double humidity, double temperature) {
        Fog = fog;
        Flame = flame;
        Dust = dust;
        Humidity = humidity;
        Temperature = temperature;
    }

    public int getFog() {
        return Fog;
    }

    public void setFog(int fog) {
        Fog = fog;
    }

    public int getFlame() {
        return Flame;
    }

    public void setFlame(int flame) {
        Flame = flame;
    }

    public double getDust() {
        return Dust;
    }

    public void setDust(double dust) {
        Dust = dust;
    }

    public double getHumidity() {
        return Humidity;
    }

    public void setHumidity(double humidity) {
        Humidity = humidity;
    }

    public double getTemperature() {
        return Temperature;
    }

    public void setTemperature(double temperature) {
        Temperature = temperature;
    }
}
