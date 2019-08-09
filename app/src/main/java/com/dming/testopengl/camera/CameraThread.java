package com.dming.testopengl.camera;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.UiThread;
import android.support.annotation.VisibleForTesting;

public class CameraThread {

    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private volatile boolean mFinish = false;
    private static volatile CameraThread sCameraThread;

    @UiThread
    public static CameraThread getInstance(){
        if(sCameraThread == null){
            sCameraThread = new CameraThread();
        }
        return sCameraThread;
    }

    @VisibleForTesting
    public void join(){
        try {
            if (mBackgroundThread != null && mFinish) {
                mBackgroundThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void makeSureStartBackgroundThread() {
        try {
            if (mBackgroundThread != null && mFinish) {
                mBackgroundThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mBackgroundThread == null) {
            mFinish = false;
            mBackgroundThread = new HandlerThread("CameraBackground");
            mBackgroundThread.start();
            mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        }
    }

    @UiThread
    public void stopBackgroundThread(Runnable r) {
        if (mBackgroundThread != null) {
            mFinish = true;
            mBackgroundHandler.post(r);
            mBackgroundHandler.post(new Runnable() {
                @Override
                public void run() {
                    mBackgroundThread.quit();
                    mBackgroundThread = null;
                    mBackgroundHandler = null;
                    mFinish = false;
                }
            });
        }
    }

    @UiThread
    public boolean makeSurePost(Runnable r){
        makeSureStartBackgroundThread();
        if(mFinish)return false;
        return mBackgroundHandler.post(r);
    }

}
