package com.braincs.attrsc.usbcamera;

import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.serenegiant.usb.DeviceFilter;
import com.serenegiant.usb.USBMonitor;

import java.util.List;

/**
 * Created by changshuai on 05/09/2018.
 */

public class SettingsActivity extends AppCompatActivity {

    private LinearLayout llLeftView;
    private LinearLayout llRightView;

    private TextView tvLeftView, tvRightView;
    private int i = 0,j = 0;
    private USBMonitor mUSBMonitor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        llLeftView = findViewById(R.id.ll_camera_view1);
        llLeftView.setOnClickListener(onButtonClickView);
        llRightView = findViewById(R.id.ll_camera_view2);
        llRightView.setOnClickListener(onButtonClickView);

        tvLeftView = findViewById(R.id.tv_left_view);
        tvRightView = findViewById(R.id.tv_right_view);
    }


    View.OnClickListener onButtonClickView = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            List<UsbDevice> usbDevices = updateDevices();
            switch (view.getId()){
                case R.id.ll_camera_view1:
                    UsbDevice device = usbDevices.get(i % usbDevices.size());
                    String deviceName = device.getDeviceName();
                    tvLeftView.setText(deviceName);
                    i++;
                    break;
                case R.id.ll_camera_view2:
                    UsbDevice device1 = usbDevices.get(j % usbDevices.size());
                    deviceName = device1.getDeviceName();
                    tvRightView.setText(deviceName);
                    j++;
                    break;
            }
        }
    };

    public List<UsbDevice> updateDevices() {
//		mUSBMonitor.dumpDevices();
        final List<DeviceFilter> filter = DeviceFilter.getDeviceFilters(SettingsActivity.this, com.serenegiant.uvccamera.R.xml.device_filter);
        return mUSBMonitor.getDeviceList(filter);
//        mDeviceListAdapter = new CameraDialog.DeviceListAdapter(getActivity(), mUSBMonitor.getDeviceList(filter.get(0)));
//        mSpinner.setAdapter(mDeviceListAdapter);

    }
}
