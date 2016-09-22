package com.ider.ytb_tv.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.ider.ytb_tv.data.CustService;
import com.ider.ytb_tv.utils.Constant;

/**
 * Created by ider-eric on 2016/8/17.
 */
abstract class ServiceActivity extends Activity {

    public CustService custService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(custService == null) {
            bindServices();
        }
    }

    private void bindServices() {
        Intent intent = new Intent(this, CustService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);

    }


    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            CustService.MyBinder mBinder = (CustService.MyBinder)iBinder;
            custService = mBinder.getService();
            serviceConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bindServices();
        }
    };

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }

    abstract void serviceConnected();
}
