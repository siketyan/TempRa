package io.github.siketyan.monitor.http;

import io.github.siketyan.monitor.TempMonitor;
import io.github.siketyan.monitor.object.APIType;
import io.github.siketyan.monitor.object.DataSet;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class HTTPHandler extends AbstractHandler {
    private final Map<String, String> mime = new HashMap<>();
    
    private Properties conf;
    private APIHandler api;
    
    public HTTPHandler() {
        mime.put("html", "text/html");
        mime.put("css", "text/css");
        mime.put("js", "text/javascript");
        mime.put("json", "application/json");
        mime.put("ico", "image/x-icon");
        mime.put("png", "image/png");
        mime.put("svg", "image/svg+xml");
        mime.put("xml", "application/xml");
        
        conf = TempMonitor.getConfig();
        api = new APIHandler();
    }
    
    @Override
    public void handle(String target, Request req, HttpServletRequest sReq,
                       HttpServletResponse res) throws IOException, ServletException {
        if (target.equals("/")) target = "/index.html";
        String mime = getMIME(target);
        DataSet ds = TempMonitor.getSensor().getData();
        res.setContentType(mime + ";charset=utf-8");

        if (target.startsWith("/api.json")) {
            try (PrintWriter bw = res.getWriter()) {
                String typeParam = req.getParameter("type");

                if (typeParam == null) {
                    bw.println(
                        String.format(
                            "{\"temp\":\"%s\",\"hum\":\"%s\",\"pres\":\"%s\",\"port\":%s}",
                            String.valueOf(ds.getTemperature()),
                            String.valueOf(ds.getHumidity()),
                            String.valueOf(ds.getPressure()),
                            conf.getProperty("Socket_Port", "8888")
                        )
                    );
                } else {
                    APIType type = getAPIType(typeParam);
                    String[] apiData = api.handle(type);

                    bw.println(
                        String.format(
                            "{\"format\":\"%s\",\"label1\":%s,\"label2\":%s,\"data\":[%s,%s,%s]}",
                            getFormat(type),
                            apiData[0],
                            apiData[1],
                            apiData[2],
                            apiData[3],
                            apiData[4]
                        )
                    );
                }
            }
        } else {
            if (TempMonitor.class.getResource(TempMonitor.HTTP_SOURCE + target) == null || mime.equals("")) {
                res.setStatus(404);
                return;
            }

            try (InputStream is = TempMonitor.class.getResourceAsStream(TempMonitor.HTTP_SOURCE + target);
                 OutputStream os = res.getOutputStream()) {
                byte[] buf = new byte[1000];
                for (int nChunk = is.read(buf); nChunk != -1; nChunk = is.read(buf))
                    os.write(buf, 0, nChunk);
            }
        }
    }
    
    private String getMIME(String path) {
        int index = path.lastIndexOf(".");
        if (index == -1) return "";
        
        String ext = path.substring(index + 1);
        return mime.get(ext);
    }

    private String getFormat(APIType type) {
        switch (type) {
            default:
            case HOUR: return conf.getProperty("Format_Hour");
            case DAY: return conf.getProperty("Format_Day");
            case MONTH: return conf.getProperty("Format_Month");
            case YEAR: return conf.getProperty("Format_Year");
            case RECORD: return conf.getProperty("Format_Record");
        }
    }
    
    private APIType getAPIType(String param) {
        switch (param.toLowerCase()) {
            default:
            case "hour": return APIType.HOUR;
            case "day": return APIType.DAY;
            case "month": return APIType.MONTH;
            case "year": return APIType.YEAR;
            case "record": return APIType.RECORD;
        }
    }
}
