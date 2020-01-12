package com.erjiguan.apods.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.erjiguan.apods.BuildConfig;
import com.erjiguan.apods.monitor.BluetoothMonitor;

public class ConnectionService extends Service {

    private static final String TAG = "ConnectionService";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    BluetoothAdapter mBtAdapter = null;
    BluetoothMonitor mBtMonitor = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(getApplicationContext(), "当前设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            stopSelf();
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "Support bluetooth.");
        }

        if (!mBtAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Toast.makeText(getApplicationContext(), "请求打开蓝牙", Toast.LENGTH_SHORT).show();
            startActivity(enableIntent);
        }

        IntentFilter monitorIntentFilter = new IntentFilter();
        monitorIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        monitorIntentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        monitorIntentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        monitorIntentFilter.addCategory("android.bluetooth.headset.intent.category.companyid.76");
        if (mBtMonitor != null) {
            unregisterReceiver(mBtMonitor);
        }
        mBtMonitor = BluetoothMonitor.getInstance();
        registerReceiver(mBtMonitor, monitorIntentFilter);

        IntentFilter screenIntentFilter = new IntentFilter();
        screenIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        if (mBtAdapter.isEnabled()) {
            mBtMonitor.startScanBt();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBtMonitor != null) {
            unregisterReceiver(mBtMonitor);
        }
    }
}
