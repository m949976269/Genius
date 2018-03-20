package mcc.genius.transport.websocket.handler;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import mcc.genius.common.util.internal.logging.InternalLogger;
import mcc.genius.common.util.internal.logging.InternalLoggerFactory;
import mcc.genius.transport.websocket.entity.Equip;
import mcc.genius.transport.websocket.proto.ChatProto;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Channel的管理器
 */
public class EquipsManager {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(EquipsManager.class);

    private static ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);

    private static ConcurrentMap<Channel, Equip> equips = new ConcurrentHashMap<>();

    private static AtomicInteger equipCount = new AtomicInteger(0);

    public static void addChannel(Channel channel) {
        if (!channel.isActive()) {
            logger.error("channel is not active, address: {}",channel.toString());
        }
        Equip equip=new Equip();
        equip.setRegisterId();
        equips.put(channel, equip);
    }

    public static boolean saveUser(Channel channel, String equipmentId) {
        Equip equip = equips.get(channel);
        if (equip == null) {
            return false;
        }
        if (!channel.isActive()) {
            logger.error("channel is not active, address: {}, nick: {}", equipmentId);
            return false;
        }
        // 增加一个认证用户

        equip.setEquipmentId(equipmentId);
        equip.setAuth(true);

        equipCount.incrementAndGet();
        return true;
    }

    /**
     * 从缓存中移除Channel，并且关闭Channel
     *
     * @param channel
     */
    public static void removeChannel(Channel channel) {
        try {
            logger.warn("channel will be remove, channel is :{}",channel);
            rwLock.writeLock().lock();
            channel.close();
            Equip equip = equips.get(channel);
            if (equip != null) {
                Equip tmp = equips.remove(channel);
                if (tmp != null && tmp.isAuth()) {
                    tmp.reduceId();
                    equipCount.decrementAndGet();
                }
            }
        } finally {
            rwLock.writeLock().unlock();
        }

    }

//    /**
//     * 广播普通消息
//     *
//     * @param message
//     */
//    public static void broadcastMess(int uid, String nick, String message) {
//        if (!BlankUtil.isBlank(message)) {
//            try {
//                rwLock.readLock().lock();
//                Set<Channel> keySet = userInfos.keySet();
//                for (Channel ch : keySet) {
//                    UserInfo userInfo = userInfos.get(ch);
//                    if (userInfo == null || !userInfo.isAuth()) continue;
//                    ch.writeAndFlush(new TextWebSocketFrame(ChatProto.buildMessProto(uid, nick, message)));
//                }
//            } finally {
//                rwLock.readLock().unlock();
//            }
//        }
//    }

//    /**
//     * 广播系统消息
//     */
//    public static void broadCastInfo(int code, Object mess) {
//        try {
//            rwLock.readLock().lock();
//            Set<Channel> keySet = userInfos.keySet();
//            for (Channel ch : keySet) {
//                UserInfo userInfo = userInfos.get(ch);
//                if (userInfo == null || !userInfo.isAuth()) continue;
//                ch.writeAndFlush(new TextWebSocketFrame(ChatProto.buildSystProto(code, mess)));
//            }
//        } finally {
//            rwLock.readLock().unlock();
//        }
//    }

    public static void broadCastPing() {
        try {
            rwLock.readLock().lock();
            Set<Channel> keySet = equips.keySet();
            for (Channel ch : keySet) {
                Equip userInfo = equips.get(ch);
                if (userInfo == null || !userInfo.isAuth()) continue;
                ch.writeAndFlush(new TextWebSocketFrame(ChatProto.buildPingProto()));
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * 发送系统消息
     *
     * @param code
     * @param mess
     */
    public static void sendInfo(Channel channel, int code, Object mess) {
        channel.writeAndFlush(new TextWebSocketFrame(ChatProto.buildSystProto(code, mess)));
    }

    public static void sendPong(Channel channel) {
        channel.writeAndFlush(new TextWebSocketFrame(ChatProto.buildPongProto()));
    }

    /**
     * 扫描并关闭失效的Channel
     */
    public static void scanNotActiveChannel() {
        Set<Channel> keySet = equips.keySet();
        for (Channel ch : keySet) {
            Equip equip = equips.get(ch);
            if (equip == null){
                equips.remove(ch);
                continue;
            }
            if (!ch.isOpen() || !ch.isActive()) {
                removeChannel(ch);
            }
        }
    }


    public static Equip getUserInfo(Channel channel) {
        return equips.get(channel);
    }

    public static ConcurrentMap<Channel, Equip> getEquips() {
        return equips;
    }

    public static int getEquipCount() {
        return equipCount.get();
    }

}
