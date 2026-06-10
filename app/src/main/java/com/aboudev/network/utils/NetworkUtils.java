package com.aboudev.network.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class NetworkUtils {

    public static String getLocalIp(Context context) {
        try {
            WifiManager wifiManager = (WifiManager)
                context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ip = wifiInfo.getIpAddress();
            return String.format("%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));
        } catch (Exception e) {
            return "0.0.0.0";
        }
    }

    public static String getSubnet(String ip) {
        try {
            String[] parts = ip.split("\\.");
            return parts[0] + "." + parts[1] + "." + parts[2] + ".";
        } catch (Exception e) {
            return "192.168.1.";
        }
    }

    public static List<String> generateIpRange(String subnet) {
        List<String> ips = new ArrayList<>();
        for (int i = 1; i <= 254; i++) {
            ips.add(subnet + i);
        }
        return ips;
    }

    public static boolean isReachable(String ip, int timeout) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            return address.isReachable(timeout);
        } catch (Exception e) {
            return false;
        }
    }

    public static String getHostName(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            String host = address.getHostName();
            if (host.equals(ip)) return "Inconnu";
            return host;
        } catch (Exception e) {
            return "Inconnu";
        }
    }

    public static String guessDeviceType(String vendor) {
        if (vendor == null) return "unknown";
        String v = vendor.toLowerCase();
        if (v.contains("samsung") || v.contains("xiaomi") ||
            v.contains("huawei") || v.contains("apple") ||
            v.contains("oppo") || v.contains("vivo")) return "phone";
        if (v.contains("intel") || v.contains("dell") ||
            v.contains("hp") || v.contains("lenovo") ||
            v.contains("asus") || v.contains("acer")) return "pc";
        if (v.contains("sony") || v.contains("lg") ||
            v.contains("tcl") || v.contains("hisense")) return "tv";
        if (v.contains("tp-link") || v.contains("netgear") ||
            v.contains("cisco") || v.contains("mikrotik")) return "router";
        return "unknown";
    }
}
