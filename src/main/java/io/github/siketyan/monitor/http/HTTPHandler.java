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
        mime.put("otf", "font/otf");
        mime.put("ttf", "font/ttf");
        mime.put("woff", "font/woff");
        mime.put("woff2", "font/woff2");
        mime.put("eot", "application/vnd.ms-fontobject");
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
        
        if (TempMonitor.class.getResource(TempMonitor.HTTP_SOURCE + target) == null || mime.equals("")) {
            res.setStatus(404);
            return;
        }
        
        String[] apiData =
            target.startsWith("/api.json")
                ? api.handle(getAPIType(req.getParameter("type")))
                : new String[5];
    
        if (mime.startsWith("text") || mime.equals("application/json")) {
            try (InputStream is = TempMonitor.class.getResourceAsStream(TempMonitor.HTTP_SOURCE + target);
                 BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                 PrintWriter bw = res.getWriter()) {
                String line;
                while ((line = br.readLine()) != null)
                    bw.println(
                        line.replaceAll("\\{\\{NOW_TEMP}}", String.valueOf(ds.getTemperature()))
                            .replaceAll("\\{\\{NOW_HUM}}", String.valueOf(ds.getHumidity()))
                            .replaceAll("\\{\\{NOW_PRES}}", String.valueOf(ds.getPressure()))
                            .replaceAll("\\{\\{FORMAT_RECORD}}", conf.getProperty("Format_Record"))
                            .replaceAll("\\{\\{FORMAT_HOUR}}", conf.getProperty("Format_Hour"))
                            .replaceAll("\\{\\{FORMAT_DAY}}", conf.getProperty("Format_Day"))
                            .replaceAll("\\{\\{FORMAT_MONTH}}", conf.getProperty("Format_Month"))
                            .replaceAll("\\{\\{FORMAT_YEAR}}", conf.getProperty("Format_Year"))
                            .replaceAll("\\{\\{SOCKET_PORT}}", conf.getProperty("Socket_Port", "8888"))
                            .replaceAll("\\{\\{API_LABEL1}}", apiData[0])
                            .replaceAll("\\{\\{API_LABEL2}}", apiData[1])
                            .replaceAll("\\{\\{API_TEMP}}", apiData[2])
                            .replaceAll("\\{\\{API_HUM}}", apiData[3])
                            .replaceAll("\\{\\{API_PRES}}", apiData[4])
                    );
            }
        } else {
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