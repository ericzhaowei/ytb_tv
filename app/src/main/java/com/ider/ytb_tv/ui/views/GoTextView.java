package com.ider.ytb_tv.ui.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ider-eric on 2016/4/6.
 */
public class GoTextView extends TextView {

    public GoTextView(Context context) {
        super(context);
        initStyle(context);
    }

    public GoTextView(Context context, AttributeSet attr) {
        super(context, attr);
        initStyle(context);
    }

    public void initStyle(Context context) {
        Typeface fontFace = Typeface.createFromAsset(context.getAssets(),
                "Fonts/DIN-MEDIUMALTERNATE.OTF");
        setTypeface(fontFace);


    }
}
