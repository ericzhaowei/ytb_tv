package com.ider.ytb_tv.data;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.ider.ytb_tv.R;
import com.ider.ytb_tv.ui.fragment.MainFragment;
import com.ider.ytb_tv.utils.Constant;
import com.ider.ytb_tv.utils.Utils;

public class Application {
    private static boolean DEBUG = true;
    private static String TAG = "BoxLauncher_3368";
    private String pkgName;
    private String activity;
    private String label;
    private Drawable icon;


    private Application(Context context, String pkgName) throws NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        Log.i(TAG, "Application:  Application(Context context, String pkgName)"+pkgName);
        ApplicationInfo info = pm.getApplicationInfo(pkgName, 0);
        this.pkgName = pkgName;
        this.label = (String) info.loadLabel(pm);
        this.icon = info.loadIcon(pm);

    }

    private Application(Context context, String pkgName, String activity) throws NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        ActivityInfo activityInfo = pm.getActivityInfo(new ComponentName(pkgName, activity), PackageManager.GET_META_DATA);
        this.pkgName = pkgName;
        this.activity = activity;
        this.label = (String) activityInfo.loadLabel(pm);
        this.icon = activityInfo.loadIcon(pm);
    }


    public static Application make(Context context, String pkgName, String activity) {
        Application app = null;
        try {
            app = new Application(context, pkgName, activity);
        } catch (NameNotFoundException e) {
            if (DEBUG) Log.i(TAG, "NameNotFoundException");
            e.printStackTrace();
        }
        return app;
    }


    public static Application make(Context context, String pkgName) {
        Application app = null;
        try {
            app = new Application(context, pkgName);
        } catch (NameNotFoundException e) {
            if (DEBUG) Log.i(TAG, "NameNotFoundException");
            e.printStackTrace();
        }
        return app;
    }


    public String getLabel() {
        return label;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getActivity() {
        return this.activity;
    }

    @Override
    public boolean equals(Object o) {
        Application oApp = (Application) o;
        if (oApp.getActivity() == null || oApp.getActivity().equals("")) {
            return pkgName.equals(((Application) o).getPkgName());
        } else {
            return getPkgName().equals(oApp.getPkgName()) && getActivity().equals(oApp.getActivity());
        }
    }


    private static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object[] newArray(int i) {
            return new Object[0];
        }

        @Override
        public Object createFromParcel(Parcel parcel) {
            return null;
        }
    };

}
