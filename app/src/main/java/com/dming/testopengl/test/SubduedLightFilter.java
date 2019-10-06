package com.dming.testopengl.test;

import android.content.Context;

import com.dming.testopengl.R;
import com.dming.testopengl.filter.BaseFilter;

public class SubduedLightFilter extends BaseFilter {

    public SubduedLightFilter(Context context) {
        super(context, R.raw.subdued_light_frg);
    }

}
