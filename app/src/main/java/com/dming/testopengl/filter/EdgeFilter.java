package com.dming.testopengl.filter;

import android.content.Context;

import com.dming.testopengl.R;

public class EdgeFilter extends BaseFilter{

    public EdgeFilter(Context context) {
        super(context, R.raw.edge_frg);
    }

}
