package io.github.siketyan.monitor.object;

public class DataSet {
    private double temperature;
    private double humidity;
    private double pressure;

    public DataSet(double temperature, double humidity, double pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public double getHumidity() {
        return humidity;
    }
    
    public double getPressure() {
        return pressure;
    }
}
