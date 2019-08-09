package com.dming.testopengl.filter;

import android.content.Context;

import com.dming.testopengl.R;

public class MosaicFilter extends BaseFilter{

    public MosaicFilter(Context context,int orientation) {
        super(context, R.raw.mosaic_frg, orientation);
    }

}
