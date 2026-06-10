package com.aboudev.network.models;

public class Device {
    private String ip;
    private String mac;
    private String name;
    private String vendor;
    private String type;
    private boolean isOnline;
    private boolean isTrusted;
    private boolean isBlocked;
    private long lastSeen;
    private long firstSeen;

    public Device() {
        this.firstSeen = System.currentTimeMillis();
        this.lastSeen = System.currentTimeMillis();
        this.isOnline = true;
        this.isTrusted = false;
        this.isBlocked = false;
        this.type = "unknown";
        this.vendor = "Inconnu";
        this.name = "Appareil inconnu";
    }

    public Device(String ip, String mac) {
        this();
        this.ip = ip;
        this.mac = mac;
    }

    // GETTERS
    public String getIp() { return ip; }
    public String getMac() { return mac; }
    public String getName() { return name; }
    public String getVendor() { return vendor; }
    public String getType() { return type; }
    public boolean isOnline() { return isOnline; }
    public boolean isTrusted() { return isTrusted; }
    public boolean isBlocked() { return isBlocked; }
    public long getLastSeen() { return lastSeen; }
    public long getFirstSeen() { return firstSeen; }

    // SETTERS
    public void setIp(String ip) { this.ip = ip; }
    public void setMac(String mac) { this.mac = mac; }
    public void setName(String name) { this.name = name; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public void setType(String type) { this.type = type; }
    public void setOnline(boolean online) { isOnline = online; }
    public void setTrusted(boolean trusted) { isTrusted = trusted; }
    public void setBlocked(boolean blocked) { isBlocked = blocked; }
    public void setLastSeen(long lastSeen) { this.lastSeen = lastSeen; }
    public void setFirstSeen(long firstSeen) { this.firstSeen = firstSeen; }

    public String getIcon() {
        switch (type) {
            case "phone": return "📱";
            case "pc": return "💻";
            case "tv": return "📺";
            case "router": return "📡";
            case "camera": return "📷";
            case "console": return "🎮";
            default: return "🔌";
        }
    }
}
