package com.ider.ytb_tv.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;

/**
 * Created by ider-eric on 2016/9/8.
 */
public class BitmapUtil {

    public static Bitmap blur(Activity activity, Bitmap bkg) {
        float scaleFactor = 8;
        float radius = 2;
        Bitmap blurBitmap;
        blurBitmap = Bitmap.createBitmap(
                (int) (screenSize(activity)[0] / scaleFactor),
                (int) (screenSize(activity)[1] / scaleFactor),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(blurBitmap);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        blurBitmap = FastBlur.doBlur(blurBitmap, (int) radius, true);
        if (bkg != null && !bkg.isRecycled()) {
            bkg.recycle();
        }
        return blurBitmap;
    }

    private static int[] screenSize(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return new int[]{metric.widthPixels, metric.heightPixels};
    }

}
