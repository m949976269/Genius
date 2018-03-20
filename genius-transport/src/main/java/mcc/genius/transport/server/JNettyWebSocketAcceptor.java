package mcc.genius.transport.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;
import mcc.genius.common.util.JConstants;
import mcc.genius.transport.api.config.CodecConfig;
import mcc.genius.transport.coder.LowCopyProtocolDecoder;
import mcc.genius.transport.coder.ProtocolDecoder;
import mcc.genius.transport.handler.IdleStateChecker;
import mcc.genius.transport.websocket.handler.MessageHandler;
import mcc.genius.transport.websocket.handler.UserAuthHandler;

import java.net.SocketAddress;

public class JNettyWebSocketAcceptor extends NettyWebSocketAcceptor{

    public static final int DEFAULT_ACCEPTOR_PORT = 18090;

    public JNettyWebSocketAcceptor(int port) {
        super(port);
    }

    /**
     * Create a new {@link Channel} and bind it.
     *
     * @param localAddress
     */
    @Override
    protected ChannelFuture bind(SocketAddress localAddress) {
        ServerBootstrap boot = bootstrap();

        initChannelFactory();

        boot.childHandler(new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(
                        new HttpServerCodec(),   //请求解码器
                        new HttpObjectAggregator(65536),//将多个消息转换成单一的消息对象
                        new IdleStateHandler(60, 0, 0), //检测链路是否读空闲
                        new UserAuthHandler(), //处理握手和认证
                        new MessageHandler()
                );
            }
        });

        setOptions();

        return boot.bind(localAddress);
    }
}
