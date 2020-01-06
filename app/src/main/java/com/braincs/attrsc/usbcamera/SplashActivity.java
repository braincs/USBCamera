package com.braincs.attrsc.usbcamera;

import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.serenegiant.usb.DeviceFilter;
import com.serenegiant.usb.USBMonitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shuai
 * 21/08/2019.
 */
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    public static USBMonitor mUSBMonitor;
    public static List<UsbDevice> deviceList;
    public static Map<UsbDevice, USBMonitor.UsbControlBlock> cameraMap = new HashMap<>(8);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initCamera();
    }

    private void initCamera() {
        Log.d(TAG, "--initCamera--");
        mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);
        final List<DeviceFilter> filters = DeviceFilter.getDeviceFilters(this, R.xml.device_filter);
        mUSBMonitor.setDeviceFilter(filters);
        mUSBMonitor.register();
        deviceList = mUSBMonitor.getDeviceList();
    }

    private void destroyCamera(){
        Log.d(TAG, "--destroyCamera--");
        if (mUSBMonitor != null) {
            mUSBMonitor.unregister();
            mUSBMonitor.destroy();
            mUSBMonitor = null;
        }
        if (deviceList != null){
            deviceList.clear();
            deviceList = null;
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "--onDestroy--");
        super.onDestroy();
        destroyCamera();
    }

    //    @Override
//    protected void onStart() {
//        super.onStart();
//
//    }
    public void onClickCamera(View view) {
        startActivity(new Intent(SplashActivity.this, CameraActivity.class));
    }
    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            Log.d(TAG, "--onAttach--");

//            Toast.makeText(IRLivenessUVCActivity.this, "USB_DEVICE_ATTACHED", Toast.LENGTH_SHORT).show();
            final List<DeviceFilter> filters = DeviceFilter.getDeviceFilters(SplashActivity.this, R.xml.device_filter);
            for (final DeviceFilter filter : filters) {
                if ((filter != null) && filter.matches(device)) {
                    // when filter matches
                    if (!filter.isExclude && device.getProductId() == deviceList.get(0).getProductId()) {
//                        result.add(device);
                        mUSBMonitor.requestPermission(device);
                    }
                    break;
                }
            }

        }

        @Override
        public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
            Log.d(TAG, "--onConnect--");

            cameraMap.put(device, ctrlBlock);
            Log.d(TAG, device.toString());
            Log.d(TAG, ctrlBlock.toString());
//            queueEvent(new Runnable() {
//                @Override
//                public void run() {
//                    final UVCCamera camera = new UVCCamera();
//                    camera.open(ctrlBlock);
//                    if (mPreviewSurface != null) {
//                        mPreviewSurface.release();
//                        mPreviewSurface = null;
//                    }
//                    try {
//                        camera.setPreviewSize(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, UVCCamera.FRAME_FORMAT_MJPEG);
//                    } catch (final IllegalArgumentException e) {
//                        // fallback to YUV mode
//                        try {
//                            camera.setPreviewSize(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, UVCCamera.DEFAULT_PREVIEW_MODE);
//                        } catch (final IllegalArgumentException e1) {
//                            camera.destroy();
//                            return;
//                        }
//                    }
//                    final SurfaceTexture st = mUVCCameraView.getSurfaceTexture();
//                    if (st != null) {
//                        mPreviewSurface = new Surface(st);
//                        camera.setPreviewDisplay(mPreviewSurface);
////						camera.setFrameCallback(mIFrameCallback, UVCCamera.PIXEL_FORMAT_RGB565/*UVCCamera.PIXEL_FORMAT_NV21*/);
//                        camera.startPreview();
//                    }
//                    synchronized (mSync) {
//                        mUVCCamera = camera;
//                    }
//                }
//            }, 0);
        }

        @Override
        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
            // XXX you should check whether the coming device equal to camera device that currently using
            Log.d(TAG, "--onDisconnect--");

//            cameraMap.remove(device);
        }

        @Override
        public void onDettach(final UsbDevice device) {
            Log.d(TAG, "--onDettach--");

//            Toast.makeText(IRLivenessUVCActivity.this, "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(final UsbDevice device) {
        }
    };
}

