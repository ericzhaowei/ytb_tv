package com.ider.ytb_tv.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.ider.ytb_tv.data.Application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApplicationUtil {
    static String TAG = "ApplicationUtil";
    static Context context;
    static ApplicationUtil util;

    private ApplicationUtil(Context context) {
        this.context = context;
    }

    public static ApplicationUtil getInstance(Context context) {
        if(util == null) {
            util = new ApplicationUtil(context);
        }
        return util;
    }

    public ArrayList<Application> loadAllApplication() {

        ArrayList<Application> list = new ArrayList<Application>();
        Intent main = new Intent(Intent.ACTION_MAIN, null);
        main.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(main, 0);
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));

        for (ResolveInfo info : resolveInfos) {
            String pkgName = info.activityInfo.applicationInfo.packageName;
            String activity = info.activityInfo.name;
            Application app = Application.make(context, pkgName, activity);
            if (app != null) {
                list.add(app);
            }
        }
        return list;
    }


    public String getPackageVersion(String packagename) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(packagename, 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void startApplication(Application app) {
        Intent intent;
        Log.i(TAG, app.getPkgName() + "/" + app.getActivity());
        if(app.getActivity() != null) {
            intent = new Intent();
            intent.setComponent(new ComponentName(app.getPkgName(), app.getActivity()));
            context.startActivity(intent);
        } else {
            PackageManager pm = context.getPackageManager();
            intent = pm.getLaunchIntentForPackage(app.getPkgName());
            if (intent != null) {
                context.startActivity(intent);
            }
        }

    }

    public  void startApplication(String packageName){
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        if(intent!=null){
            context.startActivity(intent);
        }
    }

}
