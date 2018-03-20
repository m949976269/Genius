package mcc.genius.transport.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import mcc.genius.common.util.JConstants;
import mcc.genius.common.util.internal.logging.InternalLogger;
import mcc.genius.common.util.internal.logging.InternalLoggerFactory;
import mcc.genius.transport.api.config.JConfigGroup;
import mcc.genius.transport.channel.TcpChannelProvider;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ThreadFactory;

public abstract class NettyWebSocketAcceptor extends NettyAcceptor {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NettyWebSocketAcceptor.class);

    private final boolean isNative; // use native transport
    private final NettyConfig.NettyTcpConfigGroup configGroup = new NettyConfig.NettyTcpConfigGroup();

    public NettyWebSocketAcceptor(int port) {
        super(Protocol.WEBSOKET, new InetSocketAddress(port));
        isNative = false;
        init();
    }

    /**
     * Sets the percentage of the desired amount of time spent for I/O in the child event loops.
     * The default value is {@code 50}, which means the event loop will try to spend the same
     * amount of time for I/O as for non-I/O tasks.
     *
     * @param bossIoRatio
     * @param workerIoRatio
     */
    @Override
    public void setIoRatio(int bossIoRatio, int workerIoRatio) {
        EventLoopGroup boss = boss();
        if (boss instanceof EpollEventLoopGroup) {
            ((EpollEventLoopGroup) boss).setIoRatio(bossIoRatio);
        } else if (boss instanceof KQueueEventLoopGroup) {
            ((KQueueEventLoopGroup) boss).setIoRatio(bossIoRatio);
        } else if (boss instanceof NioEventLoopGroup) {
            ((NioEventLoopGroup) boss).setIoRatio(bossIoRatio);
        }

        EventLoopGroup worker = worker();
        if (worker instanceof EpollEventLoopGroup) {
            ((EpollEventLoopGroup) worker).setIoRatio(workerIoRatio);
        } else if (worker instanceof KQueueEventLoopGroup) {
            ((KQueueEventLoopGroup) worker).setIoRatio(workerIoRatio);
        } else if (worker instanceof NioEventLoopGroup) {
            ((NioEventLoopGroup) worker).setIoRatio(workerIoRatio);
        }
    }

    /**
     * Create a new instance using the specified number of threads, the given {@link ThreadFactory}.
     *
     * @param nThreads
     * @param tFactory
     */
    @Override
    protected EventLoopGroup initEventLoopGroup(int nThreads, ThreadFactory tFactory) {
        TcpChannelProvider.SocketType socketType = socketType();
        switch (socketType) {
            case NATIVE_EPOLL:
                return new EpollEventLoopGroup(nThreads, tFactory);
            case NATIVE_KQUEUE:
                return new KQueueEventLoopGroup(nThreads, tFactory);
            case JAVA_NIO:
                return new NioEventLoopGroup(nThreads, tFactory);
            default:
                throw new IllegalStateException("invalid socket type: " + socketType);
        }
    }

    /**
     * Acceptor options [parent, child].
     */
    @Override
    public JConfigGroup configGroup() {
        return configGroup;
    }

    /**
     * Start the server and wait until the server socket is closed.
     */
    @Override
    public void start() throws InterruptedException {
        start(true);
    }

    /**
     * Start the server.
     *
     * @param sync
     */
    @Override
    public void start(boolean sync) throws InterruptedException {
        // wait until the server socket is bind succeed.
        ChannelFuture future = bind(localAddress).sync();

        if (logger.isInfoEnabled()) {
            logger.info("Jupiter WebSocket server start" + (sync ? ", and waits until the server socket closed." : ".")
                    + JConstants.NEWLINE + " {}.", toString());
        }

        if (sync) {
            // wait until the server socket is closed.
            future.channel().closeFuture().sync();
        }
    }

    protected void initChannelFactory() {
        TcpChannelProvider.SocketType socketType = socketType();
        switch (socketType) {
            case NATIVE_EPOLL:
                bootstrap().channelFactory(TcpChannelProvider.NATIVE_EPOLL_ACCEPTOR);
                break;
            case NATIVE_KQUEUE:
                bootstrap().channelFactory(TcpChannelProvider.NATIVE_KQUEUE_ACCEPTOR);
                break;
            case JAVA_NIO:
                bootstrap().channelFactory(TcpChannelProvider.JAVA_NIO_ACCEPTOR);
                break;
            default:
                throw new IllegalStateException("invalid socket type: " + socketType);
        }
    }

    private TcpChannelProvider.SocketType socketType() {
        if (isNative && NativeSupport.isNativeEPollAvailable()) {
            // netty provides the native socket transport for Linux using JNI.
            return TcpChannelProvider.SocketType.NATIVE_EPOLL;
        }
        if (isNative && NativeSupport.isNativeKQueueAvailable()) {
            // netty provides the native socket transport for BSD systems such as MacOS using JNI.
            return TcpChannelProvider.SocketType.NATIVE_KQUEUE;
        }
        return TcpChannelProvider.SocketType.JAVA_NIO;
    }
}
