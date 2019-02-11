package com.dming.testopengl;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class ResReadUtils {

    public static String readResource(Context context, int id) {
        InputStream inputStream = context.getResources().openRawResource(id);
        StringBuilder out = new StringBuilder();
        byte[] b = new byte[4096];
        try {
            for (int n; (n = inputStream.read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
        }catch (IOException io){}
        return out.toString();
    }
}
