package com.braincs.attrsc.usbcamera;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.braincs.attrsc.usbcamera.widget.SimpleUVCCameraTextureView;
import com.serenegiant.common.BaseActivity;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;

/**
 * Created by Shuai
 * 21/08/2019.
 */
public class CameraActivity extends BaseActivity {
    private static final String TAG = CameraActivity.class.getSimpleName();
    private SimpleUVCCameraTextureView mUVCCameraView;
    private Surface mPreviewSurface;
    private UVCCamera mUVCCamera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uvc_camera);

        mUVCCameraView = (SimpleUVCCameraTextureView) findViewById(R.id.camera_tv);
        mUVCCameraView.setAspectRatio(UVCCamera.DEFAULT_PREVIEW_WIDTH / (float) UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        mUVCCameraView.setSurfaceTextureListener(CameraViewTextureListener);
    }

    private TextureView.SurfaceTextureListener CameraViewTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            releaseCamera();
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (mUVCCamera != null) {
            mUVCCamera.startPreview();
        }
    }

    private void openCamera(){
        Log.d(TAG, "--openCamera--");
        if (SplashActivity.cameraMap.size() > 0){
//            Collection<USBMonitor.UsbControlBlock> usbControlBlocks = SplashActivity.cameraMap.values();
//            final USBMonitor.UsbControlBlock ctrlBlock = (USBMonitor.UsbControlBlock) usbControlBlocks.toArray()[0];
            final USBMonitor.UsbControlBlock ctrlBlock = SplashActivity.cameraMap.get(SplashActivity.deviceList.get(0));
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    final UVCCamera camera = new UVCCamera();
                    camera.open(ctrlBlock);
                    if (mPreviewSurface != null) {
                        mPreviewSurface.release();
                        mPreviewSurface = null;
                    }
                    try {
                        camera.setPreviewSize(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, UVCCamera.FRAME_FORMAT_MJPEG);
                    } catch (final IllegalArgumentException e) {
                        // fallback to YUV mode
                        try {
                            camera.setPreviewSize(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, UVCCamera.DEFAULT_PREVIEW_MODE);
                        } catch (final IllegalArgumentException e1) {
                            camera.destroy();
                            return;
                        }
                    }
                    final SurfaceTexture st = mUVCCameraView.getSurfaceTexture();
                    if (st != null) {
                        mPreviewSurface = new Surface(st);
                        camera.setPreviewDisplay(mPreviewSurface);
//						camera.setFrameCallback(mIFrameCallback, UVCCamera.PIXEL_FORMAT_RGB565/*UVCCamera.PIXEL_FORMAT_NV21*/);
                        camera.startPreview();
                    }
                    mUVCCamera = camera;
                }
            },0);
        }
    }
    private synchronized void releaseCamera(){
        try {
            mUVCCamera.stopPreview();
            mUVCCamera.setStatusCallback(null);
            mUVCCamera.setButtonCallback(null);
            mUVCCamera.close();
            mUVCCamera.destroy();
        } catch (final Exception e) {
            //
        }
        mUVCCamera = null;
        if (mPreviewSurface != null) {
            mPreviewSurface.release();
            mPreviewSurface = null;
        }
    }

    @Override
    protected synchronized void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    //    @Override
//    public USBMonitor getUSBMonitor() {
//        return SplashActivity.mUSBMonitor;
//    }
//
//    @Override
//    public void onDialogResult(boolean canceled) {
//
//    }
}
