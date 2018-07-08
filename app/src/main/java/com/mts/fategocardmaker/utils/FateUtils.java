package com.mts.fategocardmaker.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class FateUtils {

    public static int calculateNoOfColumns(Context context, int width) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / width);

    }

}
