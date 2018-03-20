package mcc.genius;

import mcc.genius.transport.server.JNettyWebSocketAcceptor;

public class Application {

    public static void main(String[] args) throws InterruptedException {
        final JNettyWebSocketAcceptor server=new JNettyWebSocketAcceptor(18090);

        server.start();
    }
}
