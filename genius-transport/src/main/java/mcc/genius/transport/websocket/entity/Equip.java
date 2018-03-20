package mcc.genius.transport.websocket.entity;

import java.util.concurrent.atomic.AtomicInteger;

public class Equip {

    private static AtomicInteger uidGener = new AtomicInteger(1000);

    private String equipmentId;//设备Id

    private boolean isAuth = false; // 是否认证

    private int registerId;

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public void setAuth(boolean auth) {
        isAuth = auth;
    }

    public int getRegisterId() {
        return registerId;
    }

    public void setRegisterId() {
        this.registerId = uidGener.incrementAndGet();
    }

    public void reduceId() {
        uidGener.decrementAndGet();
    }
}
