package com.dming.testopengl.filter;

import android.content.Context;

import com.dming.testopengl.R;

public class DarkenLightFilter extends BaseFilter{

    public DarkenLightFilter(Context context) {
        super(context, R.raw.darken_light_frg);
    }

}
