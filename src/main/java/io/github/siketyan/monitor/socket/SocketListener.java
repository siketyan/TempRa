package io.github.siketyan.monitor.socket;

import io.github.siketyan.monitor.util.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.net.InetSocketAddress;

@WebSocket
public class SocketListener {
    private Session session;
    
    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
        SessionManager.getInstance().add(this);
        Logger.info(getRemoteAddress() + " joined.");
    }
    
    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        SessionManager.getInstance().remove(this);
        Logger.info(getRemoteAddress() + " left.");
    }
    
    private String getRemoteAddress() {
        InetSocketAddress addr = session.getRemoteAddress();
        return addr.getHostString() + ":" + addr.getPort();
    }
    
    Session getSession(){
        return this.session;
    }
}