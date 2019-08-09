package com.dming.testopengl.filter;

import android.content.Context;

import com.dming.testopengl.R;

public class SubduedLightFilter extends BaseFilter {

    public SubduedLightFilter(Context context, int orientation) {
        super(context, R.raw.subdued_light_frg, orientation);
    }

}
