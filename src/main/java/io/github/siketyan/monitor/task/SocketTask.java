package io.github.siketyan.monitor.task;

import io.github.siketyan.monitor.TempMonitor;
import io.github.siketyan.monitor.object.DataSet;
import io.github.siketyan.monitor.socket.SessionManager;
import io.github.siketyan.monitor.util.ISensor;

public class SocketTask implements Runnable {
    private ISensor sensor;
    
    public SocketTask() {
        this.sensor = TempMonitor.getSensor();
    }
    
    @Override
    public void run() {
        DataSet ds = sensor.getData();
        SessionManager sm = SessionManager.getInstance();
        
        sm.broadcast(
            ds.getTemperature() + ","
                + ds.getHumidity() + ","
                + ds.getPressure()
        );
    }
}
