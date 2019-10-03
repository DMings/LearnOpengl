package com.dming.testopengl;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import com.dming.testopengl.camera.CameraSize;
import com.dming.testopengl.utils.DLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class CameraActivity extends AppCompatActivity implements CameraRenderer.GLRunnable {

    private GLSurfaceView mGLSurfaceView;
    private CameraRenderer mCameraRenderer;
    //
    private int mCameraId;
    private Camera mCamera;
    private Camera.Parameters mCameraParameters;
    private final Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
    private static final int INVALID_CAMERA_ID = -1;
    protected final List<CameraSize> mPreviewSizes = new ArrayList<>();
    private SurfaceTexture mSurfaceTexture;
    private float[] mCameraMatrix = new float[16];

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getPackageManager().checkPermission(Manifest.permission.CAMERA, getPackageName())
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 666);
            }
        }
        mGLSurfaceView = findViewById(R.id.gl_show);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mCameraRenderer = new CameraRenderer(mGLSurfaceView, this);
        mGLSurfaceView.setRenderer(mCameraRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        final GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                int w = mGLSurfaceView.getWidth();
                int h = mGLSurfaceView.getHeight();
                int w_3 = w / 3;
                int h_3 = h / 3;
                int x = (int) ((e.getX() - 5) / w_3);
                int y = (int) ((e.getY() - 5) / h_3);
                int index = x + y * 3;
                mCameraRenderer.chooseOneShaderOfNine(index);
                return true;
            }
        };
        final GestureDetector gestureDetector = new GestureDetector(this, listener);
        mGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    @Override
    public void onSurfaceCreated(final int textureId) {
        mSurfaceTexture = new SurfaceTexture(textureId);
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                mGLSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        if (mSurfaceTexture != null) {
                            mSurfaceTexture.updateTexImage();
                            mSurfaceTexture.getTransformMatrix(mCameraMatrix);
                            mCameraRenderer.setTexMatrix(mCameraMatrix);
                            mGLSurfaceView.requestRender();
                        }
                    }
                });
            }
        });
        DLog.i("chooseCamera run-");
        chooseCamera();
        openCamera();
        if (isCameraOpened()) {
            DLog.i("setUpPreview run222");
            try {
                mCamera.setPreviewTexture(mSurfaceTexture);
            } catch (IOException e) {
            }
            adjustCameraParameters();
            DLog.i("adjustCameraParameters run333");
        }
    }

    @Override
    public void onSurfaceChanged(final int width, final int height) {
        setCameraDisplayOrientation(this, mCamera, mCameraInfo);
        mCameraRenderer.onSurfaceCreated(width, height);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                if (isCameraOpened()) {
                    mCamera.stopPreview();
                }
                if (mSurfaceTexture != null) {
                    mSurfaceTexture.release();
                    mSurfaceTexture = null;
                }
                releaseCamera();
                mCameraRenderer.onDestroy();
            }
        });
        mGLSurfaceView.onPause();
        super.onPause();
    }

    private void chooseCamera() {
        for (int i = 0, count = Camera.getNumberOfCameras(); i < count; i++) {
            Camera.getCameraInfo(i, mCameraInfo);
            if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
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
        mPreviewSizes.clear();
        for (Camera.Size size : mCameraParameters.getSupportedPreviewSizes()) {
//            DLog.i("size->" + size.width + " " + size.height);
            mPreviewSizes.add(new CameraSize(size.width, size.height));
        }

    }

    private void adjustCameraParameters() {
        CameraSize suitableSize = getDealCameraSize(mCameraInfo.orientation);
        final CameraSize size = suitableSize.getSrcSize();
        mCameraParameters.setPreviewSize(size.getWidth(), size.getHeight());
//        mCameraParameters.setPreviewFormat(ImageFormat.NV21);
//        mCameraParameters.setRotation(calcCameraRotation(mDisplayOrientation));
//        setRotation()影响的是JPeg的那个PictureCallback，很多时候只是修改这里返回的exif信息，不会真的旋转图像数据。
        setAutoFocusInternal(true);
//        setFlashInternal(mFlash);
        mCamera.setParameters(mCameraParameters);
//        int frameSize = size.getWidth() * size.getHeight() * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8;
//        byte[] bytes = new byte[frameSize];
//        mCamera.addCallbackBuffer(bytes);
//        mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
//            @Override
//            public void onPreviewFrame(byte[] data, Camera camera) {
////                DLog.i("===onPreviewFrame==="+Thread.currentThread());
//                if (data != null && mCamera != null) {
////                        DLog.i("生产: ");
////                    mCallback.onCopyCamera1Data(data, mCameraInfo.orientation, size.getWidth(), size.getHeight(), getRatio());
////                        DLog.i("消费: ");
////                    mCallback.onDealCameraData(mCameraInfo.orientation);
//                    camera.addCallbackBuffer(data);
//                } else {
//                    DLog.i("data null");
//                }
//            }
//        });
        mCamera.startPreview();
    }

    private void releaseCamera() {
        if (mCamera != null) {
//            mCamera.addCallbackBuffer(null);
            mCamera.release();
            mCamera = null;
        }
    }

    public void setCameraDisplayOrientation(Activity activity, Camera camera, Camera.CameraInfo info) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;   // compensate the mirror
        } else {
            // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        DLog.i("result: " + result);
        camera.setDisplayOrientation(result);
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

    boolean isCameraOpened() {
        return mCamera != null;
    }

    protected CameraSize getDealCameraSize(int rotation) {
        SortedSet<CameraSize> greaterThanView = new TreeSet<>();
        List<CameraSize> lessThanView = new ArrayList<>();
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        if (wm == null) {
            point.x = 1080;
            point.y = 1920;
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
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
        DLog.i("suitableSize>" + cSize.toString());
        return cSize;
    }

    @Override
    protected void onDestroy() {
        DLog.e("onDestroy========================================>>>");
        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mSurfaceTexture != null) {
                    mSurfaceTexture.setOnFrameAvailableListener(null);
                    mSurfaceTexture = null;
                }
                mCameraRenderer.onDestroy();
            }
        });
        super.onDestroy();
    }

}
