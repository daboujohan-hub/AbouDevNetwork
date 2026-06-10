package com.aboudev.network.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.core.app.NotificationCompat;
import com.aboudev.network.R;
import com.aboudev.network.models.Device;
import com.aboudev.network.utils.NetworkUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NetworkScanService extends Service {

    public static final String ACTION_SCAN_RESULT = "com.aboudev.network.SCAN_RESULT";
    public static final String EXTRA_DEVICES = "devices_found";
    public static final String ACTION_SCAN_PROGRESS = "com.aboudev.network.SCAN_PROGRESS";
    public static final String EXTRA_PROGRESS = "progress";

    private static final String CHANNEL_ID = "AbouDevNetworkChannel";
    private static final int NOTIF_ID = 1001;

    private ExecutorService executor;
    private Handler mainHandler;
    private boolean isScanning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        executor = Executors.newFixedThreadPool(20);
        mainHandler = new Handler(Looper.getMainLooper());
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIF_ID, buildNotification("Scan réseau en cours..."));
        if (!isScanning) {
            startScan();
        }
        return START_NOT_STICKY;
    }

    private void startScan() {
        isScanning = true;
        new Thread(() -> {
            String localIp = NetworkUtils.getLocalIp(getApplicationContext());
            String subnet = NetworkUtils.getSubnet(localIp);
            List<String> ipRange = NetworkUtils.generateIpRange(subnet);
            List<Device> foundDevices = new ArrayList<>();
            List<Future<?>> futures = new ArrayList<>();
            int total = ipRange.size();
            int[] scanned = {0};

            for (String ip : ipRange) {
                Future<?> future = executor.submit(() -> {
                    if (NetworkUtils.isReachable(ip, 300)) {
                        Device device = new Device(ip, null);
                        String hostname = NetworkUtils.getHostName(ip);
                        device.setName(hostname.equals("Inconnu") ? "Appareil " + ip : hostname);
                        device.setOnline(true);
                        synchronized (foundDevices) {
                            foundDevices.add(device);
                        }
                    }
                    synchronized (scanned) {
                        scanned[0]++;
                        int progress = (scanned[0] * 100) / total;
                        broadcastProgress(progress);
                    }
                });
                futures.add(future);
            }

            for (Future<?> f : futures) {
                try { f.get(); } catch (Exception ignored) {}
            }

            broadcastResults(foundDevices);
            isScanning = false;
            stopSelf();

        }).start();
    }

    private void broadcastProgress(int progress) {
        Intent intent = new Intent(ACTION_SCAN_PROGRESS);
        intent.putExtra(EXTRA_PROGRESS, progress);
        sendBroadcast(intent);
    }

    private void broadcastResults(List<Device> devices) {
        Intent intent = new Intent(ACTION_SCAN_RESULT);
        intent.putExtra(EXTRA_DEVICES, devices.size());
        sendBroadcast(intent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "AbouDev Network",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Surveillance réseau active");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification(String text) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AbouDev Network")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null) executor.shutdownNow();
    }
}
