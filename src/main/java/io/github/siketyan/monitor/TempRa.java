package io.github.siketyan.monitor;

import io.github.siketyan.monitor.http.HTTPHandler;
import io.github.siketyan.monitor.socket.SocketServlet;
import io.github.siketyan.monitor.task.CronTask;
import io.github.siketyan.monitor.util.*;
import it.sauronsoftware.cron4j.Scheduler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import java.io.*;
import java.sql.SQLException;
import java.util.Properties;

public class TempRa {
    public static final String HTTP_SOURCE = "/web";
    private static final String CONFIG_FILE = "monitor.properties";
    private static final Class<? extends ISensor> SENSOR_TYPE = BME280.class;
    
    private static Properties conf;
    private static ISensor sensor;
    private static SQLManager sql;
    private static Twitter twitter;
    
    public static void main(String[] args) {
        try {
            try (InputStream is = new FileInputStream(CONFIG_FILE);
                 BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                conf = new Properties();
                conf.load(br);
                Logger.success("Loaded config.");
            }
            
            sensor = SENSOR_TYPE.newInstance();
            Logger.success("Initialized sensor.");
            
            sql = new SQLManager(
                conf.getProperty("SQL_Type", "mysql"),
                conf.getProperty("SQL_Host", "localhost"),
                Integer.valueOf(conf.getProperty("SQL_Port", "3306")),
                conf.getProperty("SQL_Database", "monitor"),
                conf.getProperty("SQL_User", "monitor"),
                conf.getProperty("SQL_Password"),
                Integer.valueOf(conf.getProperty("SQL_Timeout", "1"))
            );
            Logger.success("Initialized SQL.");
        } catch (IOException e) {
            Logger.error("Failed to read config file.");
            e.printStackTrace();
            return;
        } catch (SQLException e) {
            Logger.error("Failed to connect to SQL.");
            e.printStackTrace();
            return;
        } catch (Exception e) {
            Logger.error("Failed to get sensor instance.");
            e.printStackTrace();
            return;
        }
        
        twitter = TwitterFactory.getSingleton();
        Logger.success("Initialized Twitter.");
    
        Scheduler sc = new Scheduler();
        sc.schedule("*/10 * * * *", new CronTask());
        sc.start();
    
        Log.setLog(new NoLogging());
    
        {
            Server server = new Server(Integer.valueOf(conf.getProperty("Socket_Port", "8888")));
            ResourceHandler rHandler = new ResourceHandler();
        
            SocketServlet servlet = new SocketServlet();
            ServletContextHandler cHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            cHandler.addServlet(new ServletHolder(servlet), "/");
        
            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[]{rHandler, cHandler});
            server.setHandler(handlers);
        
            try {
                server.start();
                Logger.success("Started WebSocket server.");
            } catch (Exception e) {
                Logger.error("Failed to start WebSocket server.");
                e.printStackTrace();
            }
        }
    
        {
            Server server = new Server(Integer.valueOf(conf.getProperty("HTTP_Port", "8080")));
            server.setHandler(new HTTPHandler());
    
            try {
                server.start();
                Logger.success("Started HTTP server.");
            } catch (Exception e) {
                Logger.error("Failed to start HTTP server.");
                e.printStackTrace();
            }
        }
    }
    
    public static Properties getConfig() {
        return conf;
    }
    
    public static ISensor getSensor() {
        return sensor;
    }
    
    public static SQLManager getSQL() {
        return sql;
    }
    
    public static Twitter getTwitter() {
        return twitter;
    }
}
