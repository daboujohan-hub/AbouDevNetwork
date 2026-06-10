package com.aboudev.network.models;

public class NetworkAlert {
    public static final int TYPE_NEW_DEVICE = 1;
    public static final int TYPE_SUSPICIOUS = 2;
    public static final int TYPE_BLOCKED = 3;
    public static final int TYPE_DISCONNECTED = 4;

    private int type;
    private String message;
    private String deviceIp;
    private String deviceMac;
    private long timestamp;
    private boolean isRead;

    public NetworkAlert(int type, String message, String deviceIp, String deviceMac) {
        this.type = type;
        this.message = message;
        this.deviceIp = deviceIp;
        this.deviceMac = deviceMac;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    // GETTERS
    public int getType() { return type; }
    public String getMessage() { return message; }
    public String getDeviceIp() { return deviceIp; }
    public String getDeviceMac() { return deviceMac; }
    public long getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }

    // SETTERS
    public void setRead(boolean read) { isRead = read; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getIcon() {
        switch (type) {
            case TYPE_NEW_DEVICE: return "🆕";
            case TYPE_SUSPICIOUS: return "⚠️";
            case TYPE_BLOCKED: return "🚫";
            case TYPE_DISCONNECTED: return "📴";
            default: return "ℹ️";
        }
    }

    public String getTypeLabel() {
        switch (type) {
            case TYPE_NEW_DEVICE: return "NOUVEL APPAREIL";
            case TYPE_SUSPICIOUS: return "SUSPECT";
            case TYPE_BLOCKED: return "BLOQUÉ";
            case TYPE_DISCONNECTED: return "DÉCONNECTÉ";
            default: return "INFO";
        }
    }
}
