package com.dming.testopengl.filter;

import android.content.Context;

import com.dming.testopengl.R;

public class SmoothFilter extends BaseFilter{

    public SmoothFilter(Context context,int orientation) {
        super(context, R.raw.smooth_frg, orientation);
    }

}
