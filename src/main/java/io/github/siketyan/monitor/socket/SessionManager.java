package io.github.siketyan.monitor.socket;

import io.github.siketyan.monitor.TempMonitor;
import io.github.siketyan.monitor.task.SocketTask;
import io.github.siketyan.monitor.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SessionManager {
    private static SessionManager instance = new SessionManager();
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static SocketTask task = new SocketTask();
    
    private boolean isTaskRunning;
    private ScheduledFuture future;
    private List<SocketListener> sessions = new ArrayList<>();
    
    void add(SocketListener socket){
        sessions.add(socket);
        
        if (!isTaskRunning) {
            future = scheduler.scheduleWithFixedDelay(
                         task, 0,
                         Integer.parseInt(
                             TempMonitor.getConfig()
                                        .getProperty("Socket_Interval", "1000")
                         ),
                         TimeUnit.MILLISECONDS
                     );
            isTaskRunning = true;
            
            Logger.info("WebSocket task started.");
        }
    }
    
    void remove(SocketListener socket){
        sessions.remove(socket);
        
        if (sessions.isEmpty()) {
            future.cancel(true);
            isTaskRunning = false;
            
            Logger.info("WebSocket task stopped.");
        }
    }
    
    public void broadcast(String message){
        for(SocketListener session: sessions){
            session.getSession().getRemote().sendStringByFuture(message);
        }
    }
    
    public static SessionManager getInstance(){
        return instance;
    }
}
