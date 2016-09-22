package com.ider.ytb_tv.ui.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextClock;

/**
 * Created by ider-eric on 2016/4/5.
 */
public class GoClock extends TextClock {

    public GoClock(Context context) {
        super(context);
        initStyle(context);
    }

    public GoClock(Context context, AttributeSet attr) {
        super(context, attr);
        initStyle(context);
    }

    public void initStyle(Context context) {
        Typeface fontFace = Typeface.createFromAsset(context.getAssets(),
                "Fonts/DIN-LIGHT.OTF");
        setTypeface(fontFace);
    }



    public boolean is24Format() {
        String timeFormat = android.provider.Settings.System.getString(this.getContext().getContentResolver(), android.provider.Settings.System.TIME_12_24);
        return timeFormat.equals("24");
    }

}
