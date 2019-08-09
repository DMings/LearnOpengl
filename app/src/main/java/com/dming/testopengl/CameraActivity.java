package com.dming.testopengl;

import android.Manifest;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.dming.testopengl.camera.CameraSize;
import com.dming.testopengl.camera.CameraThread;
import com.dming.testopengl.camera.Constants;
import com.dming.testopengl.utils.DLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class CameraActivity extends AppCompatActivity implements CameraRenderer.OnPreviewListener {

    private GLSurfaceView mGLSurfaceView;
    private CameraRenderer mCameraRenderer;
    //
    private int mCameraId;
    private Camera mCamera;
    private Camera.Parameters mCameraParameters;
    private final Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
    private static final int INVALID_CAMERA_ID = -1;
    private final int mDisplayOrientation = 0;
    protected final List<CameraSize> mPreviewSizes = new ArrayList<>();
    private boolean mShowingPreview;
    private SurfaceTexture mSurfaceTexture;
    //
    private CameraSize suitableSize = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},666);
        }
        mGLSurfaceView = findViewById(R.id.gl_show);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mCameraRenderer = new CameraRenderer(this, this);
        mGLSurfaceView.setRenderer(mCameraRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceChanged(int textureId, final CameraRenderer.GLRunnable runnable) {
        DLog.i("onSurfaceChanged");
        mSurfaceTexture = new SurfaceTexture(textureId);
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(final SurfaceTexture surfaceTexture) {
                DLog.i("onFrameAvailable ");
                mGLSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        surfaceTexture.updateTexImage();
                        mGLSurfaceView.requestRender();
                    }
                });
            }
        });
        CameraThread.getInstance().makeSurePost(new Runnable() {
            @Override
            public void run() {
                DLog.i("onSurfaceChanged run");
                chooseCamera();
                openCamera();
                if (isCameraOpened() && !mShowingPreview) {
                    DLog.i("setUpPreview run");
                    setUpPreview();
                    adjustCameraParameters();
                    mShowingPreview = true;
                }
                runnable.run(1.0f * suitableSize.getWidth() / suitableSize.getHeight());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
//        CameraThread.getInstance().makeSurePost(new Runnable() {
//            @Override
//            public void run() {
//                chooseCamera();
//                openCamera();
//                DLog.i("onResume run");
//                if (isCameraOpened() && !mShowingPreview) {
//                    setUpPreview();
//                    adjustCameraParameters();
//                    mShowingPreview = true;
//                }
//            }
//        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mSurfaceTexture != null){
            mSurfaceTexture.setOnFrameAvailableListener(null);
            mSurfaceTexture = null;
        }
        CameraThread.getInstance().stopBackgroundThread(new Runnable() {
            @Override
            public void run() {
                if (mCamera != null) {
                    mCamera.stopPreview();
                }
                releaseCamera();
            }
        });
        mGLSurfaceView.onPause();
    }

    private void setUpPreview() {
        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void chooseCamera() {
        for (int i = 0, count = Camera.getNumberOfCameras(); i < count; i++) {
            Camera.getCameraInfo(i, mCameraInfo);
            if (mCameraInfo.facing == Constants.FACING_BACK) {
                mCameraId = i;
                return;
            }
        }
        mCameraId = INVALID_CAMERA_ID;
    }

    private void openCamera() {
        if (mCamera != null) {
            releaseCamera();
        }
        mCamera = Camera.open(mCameraId);
        mCameraParameters = mCamera.getParameters();
        // Supported preview sizes
        mPreviewSizes.clear();
        for (Camera.Size size : mCameraParameters.getSupportedPreviewSizes()) {
//            DLog.i("size->" + size.width + " " + size.height);
            mPreviewSizes.add(new CameraSize(size.width, size.height));
        }
        mCamera.setDisplayOrientation(calcDisplayOrientation(mDisplayOrientation));
    }

    private void adjustCameraParameters() {
        dealCameraSize(mCameraInfo.orientation);
        final CameraSize size = suitableSize.getSrcSize();
        if (mShowingPreview) {
            mCamera.stopPreview();
        }
        mCameraParameters.setPreviewSize(size.getWidth(), size.getHeight());
        mCameraParameters.setPreviewFormat(ImageFormat.NV21);
        mCameraParameters.setRotation(calcCameraRotation(mDisplayOrientation));
        setAutoFocusInternal(true);
//        setFlashInternal(mFlash);
        mCamera.setParameters(mCameraParameters);
        int frameSize = size.getWidth() * size.getHeight() * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8;
//        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(frameSize);
//        byte[] bytes = byteBuffer.array();
        byte[] bytes = new byte[frameSize];
        mCamera.addCallbackBuffer(bytes);
        mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
//                DLog.i("===onPreviewFrame==="+Thread.currentThread());
                if (data != null && mCamera != null) {
//                        DLog.i("生产: ");
//                    mCallback.onCopyCamera1Data(data, mCameraInfo.orientation, size.getWidth(), size.getHeight(), getRatio());
//                        DLog.i("消费: ");
//                    mCallback.onDealCameraData(mCameraInfo.orientation);
                    camera.addCallbackBuffer(data);
                } else {
                    DLog.i("data null");
                }
            }
        });
        mCamera.startPreview();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.addCallbackBuffer(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private int calcDisplayOrientation(int screenOrientationDegrees) {
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return (360 - (mCameraInfo.orientation + screenOrientationDegrees) % 360) % 360;
        } else {  // back-facing
            return (mCameraInfo.orientation - screenOrientationDegrees + 360) % 360;
        }
    }

    private boolean setAutoFocusInternal(boolean autoFocus) {
        if (isCameraOpened()) {
            final List<String> modes = mCameraParameters.getSupportedFocusModes();
            if (autoFocus && modes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (modes.contains(Camera.Parameters.FOCUS_MODE_FIXED)) {
                mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
            } else if (modes.contains(Camera.Parameters.FOCUS_MODE_INFINITY)) {
                mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
            } else {
                mCameraParameters.setFocusMode(modes.get(0));
            }
            return true;
        } else {
            return false;
        }
    }

    private int calcCameraRotation(int screenOrientationDegrees) {
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return (mCameraInfo.orientation + screenOrientationDegrees) % 360;
        } else {  // back-facing
            final int landscapeFlip = isLandscape(screenOrientationDegrees) ? 180 : 0;
            return (mCameraInfo.orientation + screenOrientationDegrees + landscapeFlip) % 360;
        }
    }

    private boolean isLandscape(int orientationDegrees) {
        return (orientationDegrees == Constants.LANDSCAPE_90 ||
                orientationDegrees == Constants.LANDSCAPE_270);
    }

    boolean isCameraOpened() {
        return mCamera != null;
    }

    protected void dealCameraSize(int rotation) {
        if (suitableSize != null) return;
        SortedSet<CameraSize> greaterThanView = new TreeSet<>();
        List<CameraSize> lessThanView = new ArrayList<>();
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        if (point.x > 700 && point.x < 800) {
            point.x = 720;
            point.y = 1280;
        } else if (point.x > 1000 && point.x < 1100) {
            point.x = 1080;
            point.y = 1920;
        }
        final int viewWidth = point.x;
        final int viewHeight = point.y;
        DLog.i("viewWidth>  " + viewWidth + " viewHeight>> " + viewHeight);
        for (CameraSize size : mPreviewSizes) {
            if (rotation == 90 || rotation == 270) { // width > height normal
                if (size.getWidth() >= viewHeight && size.getHeight() >= viewWidth) {
                    greaterThanView.add(new CameraSize(size.getHeight(), size.getWidth(), size));
                } else {
                    lessThanView.add(new CameraSize(size.getHeight(), size.getWidth(), size));
                }
            } else { // width < height normal  0 180
                if (size.getWidth() >= viewWidth && size.getHeight() >= viewHeight) {
                    greaterThanView.add(new CameraSize(size.getWidth(), size.getHeight(), size));
                } else {
                    lessThanView.add(new CameraSize(size.getWidth(), size.getHeight(), size));
                }
            }
        }
        CameraSize cSize = null;
        if (greaterThanView.size() > 0) {
            cSize = greaterThanView.first();
        } else {
            int diffMinValue = Integer.MAX_VALUE;
            for (CameraSize size : lessThanView) {
                int diffWidth = Math.abs(viewWidth - size.getWidth());
                int diffHeight = Math.abs(viewHeight - size.getHeight());
                int diffValue = diffWidth + diffHeight;
                if (diffValue < diffMinValue) {  // 找出差值最小的数
                    diffMinValue = diffValue;
                    cSize = size;
                }
            }
            if (cSize == null) {
                cSize = lessThanView.get(0);
            }
        }
        suitableSize = cSize;
        DLog.i("suitableSize>" + suitableSize.toString());
    }
    @Override
    protected void onDestroy() {
        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraRenderer.onDestroy();
            }
        });
        super.onDestroy();
    }

}
