package com.ider.ytb_tv.utils;


import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.ider.ytb_tv.R;
import com.ider.ytb_tv.data.Application;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/20.
 */
public class ProcessManager {

    Context context;
    ActivityManager manager;

    public ProcessManager(Context context) {
        this.context = context;
        manager = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
    }


    public long getAvailableMemory() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(mi);
        return mi.availMem;
    }

    public void cleanMemory(boolean forceStop) {
        List<ActivityManager.RunningAppProcessInfo> list = manager.getRunningAppProcesses();
        for (int i = 0; i < list.size(); i++) {
            ActivityManager.RunningAppProcessInfo appInfo = list.get(i);
            Log.i("clean", appInfo.processName);
            if (forceStop) {
                if (!appInfo.processName.equals(context.getPackageName()) && !appInfo.processName.equals("com.android.inputmethod.latin")) {
                    Application app = Application.make(context, appInfo.processName);
                    if (app != null) {
                        Log.i("clean", appInfo.processName + "/" + app.getLabel());
                        forceStop(context, appInfo.processName);
                    }
                }
            } else {
                killProcess(appInfo.processName);
            }
        }
    }

    public void killProcess(String pkgName) {
        manager.killBackgroundProcesses(pkgName);
    }

    public List<Application> getRunningProcess() {
        List<Application> list = new ArrayList<Application>();
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        PackageManager pkgManager = context.getPackageManager();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            String pkg = info.processName;
            Log.i("CLEAN", pkg);
            Intent intent = pkgManager.getLaunchIntentForPackage(pkg);
            if (intent != null) {
                Application app = Application.make(context, pkg);
                list.add(app);
            }
        }
        return list;
    }

    public static void forceStop(Context context, String packageName) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        try {
            Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.invoke(mActivityManager, packageName);  //packageName是需要强制停止的应用程序包名
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public static void clearDataForPackage(Context context, String packageName) {

        try {
            execCommand("pm clear " + packageName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        forceStop(context, packageName);
        Toast.makeText(context, R.string.Data_cleared, Toast.LENGTH_SHORT).show();
    }


    public static void execCommand(String command) throws IOException {

        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command);
        try {
            if (proc.waitFor() != 0) {
                Log.i("BoxLauncher_3368", "exit value = " + proc.exitValue());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
