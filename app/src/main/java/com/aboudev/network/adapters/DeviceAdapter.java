package com.aboudev.network.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aboudev.network.R;
import com.aboudev.network.models.Device;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private List<Device> devices;
    private OnDeviceClickListener listener;

    public interface OnDeviceClickListener {
        void onDeviceClick(Device device);
    }

    public DeviceAdapter(List<Device> devices, OnDeviceClickListener listener) {
        this.devices = devices;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_device, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device device = devices.get(position);
        holder.bind(device, listener);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void updateDevices(List<Device> newDevices) {
        this.devices.clear();
        this.devices.addAll(newDevices);
        notifyDataSetChanged();
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvName, tvIp, tvMac, tvStatus, tvVendor;

        DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tv_device_icon);
            tvName = itemView.findViewById(R.id.tv_device_name);
            tvIp = itemView.findViewById(R.id.tv_device_ip);
            tvMac = itemView.findViewById(R.id.tv_device_mac);
            tvStatus = itemView.findViewById(R.id.tv_device_status);
            tvVendor = itemView.findViewById(R.id.tv_device_vendor);
        }

        void bind(Device device, OnDeviceClickListener listener) {
            tvIcon.setText(device.getIcon());
            tvName.setText(device.getName());
            tvIp.setText(device.getIp());
            tvMac.setText(device.getMac() != null ? device.getMac() : "MAC inconnue");
            tvVendor.setText(device.getVendor());

            if (device.isOnline()) {
                tvStatus.setText("●");
                tvStatus.setTextColor(0xFF00FF88);
            } else {
                tvStatus.setText("●");
                tvStatus.setTextColor(0xFF7B8090);
            }

            if (device.isBlocked()) {
                tvStatus.setText("🚫");
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onDeviceClick(device);
            });
        }
    }
}
