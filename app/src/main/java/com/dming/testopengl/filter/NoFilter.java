package com.dming.testopengl.filter;

import android.content.Context;

import com.dming.testopengl.R;

public class NoFilter extends BaseFilter{

    public NoFilter(Context context,int orientation) {
        super(context, R.raw.process_frg,orientation);
    }

}
