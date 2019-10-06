package com.dming.testopengl.test;

import android.content.Context;

import com.dming.testopengl.R;
import com.dming.testopengl.filter.BaseFilter;

public class MosaicFilter extends BaseFilter {

    public MosaicFilter(Context context) {
        super(context, R.raw.mosaic_frg);
    }

}
