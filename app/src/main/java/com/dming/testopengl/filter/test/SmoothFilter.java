package com.dming.testopengl.filter.test;

import android.content.Context;

import com.dming.testopengl.R;
import com.dming.testopengl.filter.BaseFilter;

public class SmoothFilter extends BaseFilter {

    public SmoothFilter(Context context) {
        super(context, R.raw.smooth_frg);
    }

}
