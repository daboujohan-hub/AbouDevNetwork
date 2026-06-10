package com.aboudev.network.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aboudev.network.R;
import com.aboudev.network.adapters.DeviceAdapter;
import com.aboudev.network.models.Device;
import com.aboudev.network.services.NetworkScanService;
import com.aboudev.network.utils.NetworkUtils;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private TextView tvStatus, tvDeviceCount, tvAlertCount;
    private Button btnScan;
    private RecyclerView rvDevices;
    private DeviceAdapter deviceAdapter;
    private List<Device> deviceList = new ArrayList<>();
    private int alertCount = 0;

    private BroadcastReceiver scanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetworkScanService.ACTION_SCAN_RESULT.equals(intent.getAction())) {
                int count = intent.getIntExtra(NetworkScanService.EXTRA_DEVICES, 0);
                tvDeviceCount.setText(String.valueOf(deviceList.size()));
                btnScan.setText("⟳ SCANNER LE RÉSEAU");
                btnScan.setEnabled(true);
                tvStatus.setText("● EN LIGNE");
                tvStatus.setTextColor(getColor(R.color.accent_green));
                Toast.makeText(MainActivity.this,
                    count + " appareil(s) trouvé(s)", Toast.LENGTH_SHORT).show();

            } else if (NetworkScanService.ACTION_SCAN_PROGRESS.equals(intent.getAction())) {
                int progress = intent.getIntExtra(NetworkScanService.EXTRA_PROGRESS, 0);
                btnScan.setText("Scan... " + progress + "%");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        checkPermissions();
        loadDemoDevices();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tv_status);
        tvDeviceCount = findViewById(R.id.tv_device_count);
        tvAlertCount = findViewById(R.id.tv_alert_count);
        btnScan = findViewById(R.id.btn_scan);
        rvDevices = findViewById(R.id.rv_devices);

        btnScan.setOnClickListener(v -> startScan());
    }

    private void setupRecyclerView() {
        deviceAdapter = new DeviceAdapter(deviceList, device -> {
            Toast.makeText(this, "Appareil : " + device.getIp(), Toast.LENGTH_SHORT).show();
        });
        rvDevices.setLayoutManager(new LinearLayoutManager(this));
        rvDevices.setAdapter(deviceAdapter);
    }

    private void startScan() {
        btnScan.setText("Scan... 0%");
        btnScan.setEnabled(false);
        tvStatus.setText("● SCAN EN COURS");
        tvStatus.setTextColor(getColor(R.color.accent_orange));
        deviceList.clear();
        deviceAdapter.notifyDataSetChanged();

        String localIp = NetworkUtils.getLocalIp(this);
        String subnet = NetworkUtils.getSubnet(localIp);

        new Thread(() -> {
            List<String> ipRange = NetworkUtils.generateIpRange(subnet);
            for (String ip : ipRange) {
                if (NetworkUtils.isReachable(ip, 300)) {
                    Device device = new Device(ip, null);
                    String hostname = NetworkUtils.getHostName(ip);
                    device.setName(hostname.equals("Inconnu") ? "Appareil " + ip : hostname);
                    device.setVendor(NetworkUtils.guessDeviceType(hostname));
                    device.setOnline(true);
                    runOnUiThread(() -> {
                        deviceList.add(device);
                        deviceAdapter.notifyItemInserted(deviceList.size() - 1);
                        tvDeviceCount.setText(String.valueOf(deviceList.size()));
                    });
                }
            }
            runOnUiThread(() -> {
                btnScan.setText("⟳ SCANNER LE RÉSEAU");
                btnScan.setEnabled(true);
                tvStatus.setText("● EN LIGNE");
                tvStatus.setTextColor(getColor(R.color.accent_green));
            });
        }).start();
    }

    private void loadDemoDevices() {
        Device d1 = new Device("192.168.1.1", "AA:BB:CC:DD:EE:01");
        d1.setName("Routeur principal");
        d1.setVendor("TP-Link");
        d1.setType("router");
        d1.setOnline(true);

        Device d2 = new Device("192.168.1.100", "AA:BB:CC:DD:EE:02");
        d2.setName("Mon téléphone");
        d2.setVendor("Samsung");
        d2.setType("phone");
        d2.setOnline(true);

        deviceList.add(d1);
        deviceList.add(d2);
        deviceAdapter.notifyDataSetChanged();
        tvDeviceCount.setText(String.valueOf(deviceList.size()));
    }

    private void checkPermissions() {
        String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
        };
        boolean allGranted = true;
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this, perm)
                != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }
        if (!allGranted) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(NetworkScanService.ACTION_SCAN_RESULT);
        filter.addAction(NetworkScanService.ACTION_SCAN_PROGRESS);
        registerReceiver(scanReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try { unregisterReceiver(scanReceiver); } catch (Exception ignored) {}
    }
}
