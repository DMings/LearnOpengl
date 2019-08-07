package com.dming.testopengl.filter;

import android.content.Context;

import com.dming.testopengl.R;

public class SharpenFilter extends BaseFilter{

    public SharpenFilter(Context context) {
        super(context, R.raw.sharpen_frg);
    }

}
