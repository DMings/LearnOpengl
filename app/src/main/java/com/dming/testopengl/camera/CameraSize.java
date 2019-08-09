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

import android.support.annotation.NonNull;

/**
 * Immutable class for describing width and height dimensions in pixels.
 */
public class CameraSize implements Comparable<CameraSize> {

    private final int mWidth;
    private final int mHeight;
    private CameraSize mSrcSize;

    public CameraSize(int size1, int size2) {
        this.mWidth = size1;
        this.mHeight = size2;
    }


    public CameraSize(int size1, int size2, CameraSize mSrcSize) {
        this.mWidth = size1;
        this.mHeight = size2;
        this.mSrcSize = mSrcSize;
    }


    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public CameraSize getSrcSize() {
        return mSrcSize;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o instanceof CameraSize) {
            CameraSize size = (CameraSize) o;
            return mWidth == size.mWidth && mHeight == size.mHeight;
        }
        return false;
    }

    @Override
    public String toString() {
        return mWidth + "x" + mHeight;
    }

    @Override
    public int hashCode() {
        // assuming most sizes are <2^16, doing a rotate will give us perfect hashing
        return mHeight ^ ((mWidth << (Integer.SIZE / 2)) | (mWidth >>> (Integer.SIZE / 2)));
    }

    @Override
    public int compareTo(@NonNull CameraSize another) {
        return mWidth * mHeight - another.mWidth * another.mHeight;
    }

}
