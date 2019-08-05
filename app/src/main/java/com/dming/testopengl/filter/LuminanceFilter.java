package com.dming.testopengl.filter;

import android.content.Context;

import com.dming.testopengl.R;

public class LuminanceFilter extends BaseFilter{

    public LuminanceFilter(Context context) {
        super(context, R.raw.luminance_frg);
    }

}
