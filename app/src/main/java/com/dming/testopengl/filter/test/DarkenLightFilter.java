package com.dming.testopengl.filter.test;

import android.content.Context;

import com.dming.testopengl.R;
import com.dming.testopengl.filter.BaseFilter;

public class DarkenLightFilter extends BaseFilter {

    public DarkenLightFilter(Context context) {
        super(context, R.raw.darken_light_frg);
    }

}
