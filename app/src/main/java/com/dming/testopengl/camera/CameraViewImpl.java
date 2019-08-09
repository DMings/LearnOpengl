/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dming.testopengl.camera;

import android.view.View;

import com.dming.testopengl.utils.DLog;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

abstract class CameraViewImpl {

    protected final Callback mCallback;

    protected final PreviewImpl mPreview;

    protected final List<CameraSize> mPreviewSizes = new ArrayList<>();

    private float mRatio = 1.0f;

    private CameraSize suitableSize = null;

    CameraViewImpl(Callback callback, PreviewImpl preview) {
        mCallback = callback;
        mPreview = preview;
    }

    protected void dealCameraSize(int rotation) {
        if (suitableSize != null) return;
        SortedSet<CameraSize> greaterThanView = new TreeSet<>();
        List<CameraSize> lessThanView = new ArrayList<>();
        final int viewWidth = mPreview.getWidth();
        final int viewHeight = mPreview.getHeight();
        DLog.i( "viewWidth>  " + viewWidth + " viewHeight>> " + viewHeight);
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
//        CameraSize cSize = null; //test
//        for (Camera.Size size : mPreviewSizes) {
//            if(size.width == 960 && size.height == 720)
//            cSize = new CameraSize(720, 960, size);
//        }

        if (cSize.getWidth() > viewWidth && cSize.getHeight() > viewHeight) { // 都大于
            this.mRatio = 1.0f;
        } else {
            float rw = 1.0f * cSize.getWidth() / viewWidth;
            float rh = 1.0f * cSize.getHeight() / viewHeight;
            if (rw < rh) {
                this.mRatio = rw;  //谁小放大谁
            } else {
                this.mRatio = rh;
            }
        }
        if (suitableSize == null ||
                suitableSize.getWidth() != cSize.getWidth() ||
                suitableSize.getHeight() != cSize.getHeight()) {
            if (mCallback != null) {
                DLog.e( "onNeedToRequestLayout>>>>>>>>>>>>>>>>>>");
                mCallback.onNeedToRequestLayout();
            }
        }
        suitableSize = cSize;
        DLog.i( "suitableSize>" + suitableSize.toString() + " mRatio> "+mRatio);
    }

    public float getRatio() {
        return mRatio;
    }

    CameraSize getSuitableSize() {
        return suitableSize;
    }

    public void clearSuitableSize() {
        suitableSize = null;
    }

    View getView() {
        return mPreview.getView();
    }

    /**
     * @return {@code true} if the implementation was able to start the camera session.
     */
    abstract boolean start();

    abstract void stop();

    abstract boolean isCameraOpened();

    abstract void setFacing(int facing);

    abstract int getFacing();

    abstract void setAutoFocus(boolean autoFocus);

    abstract boolean getAutoFocus();

    abstract void setFlash(int flash);

    abstract int getFlash();

    interface Callback {

        void onCameraOpened();

        void onCameraClosed();

        void onCopyCamera1Data(byte[] bytes, int rotation, int width, int height, float ratio);

        void onDealCameraData(int rotation);

        void onNeedToRequestLayout();
    }

}
