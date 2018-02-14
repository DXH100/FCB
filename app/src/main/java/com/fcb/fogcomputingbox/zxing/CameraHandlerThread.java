package com.fcb.fogcomputingbox.zxing;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import me.dm7.barcodescanner.core.CameraUtils;

/**
 * Created by hasee on 2017/guide1/20.
 */

public class CameraHandlerThread extends HandlerThread {
    private static final String LOG_TAG = "CameraHandlerThread";
    private ScannerView mScannerView;
    private Camera camera;

    public CameraHandlerThread(ScannerView scannerView) {
        super("CameraHandlerThread");
        this.mScannerView = scannerView;
        this.start();
    }

    public void startCamera(final int cameraId) {
        Handler localHandler = new Handler(this.getLooper());
        localHandler.post(new Runnable() {
            public void run() {
                camera = CameraUtils.getCameraInstance(cameraId);
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    public void run() {
                        CameraHandlerThread.this.mScannerView.setupCameraPreview(camera);
                    }
                });
            }
        });
    }

    public void stopCamera(){
        if(this.camera != null) {
            this.camera.release();
            this.camera = null;
        }
    }

}
