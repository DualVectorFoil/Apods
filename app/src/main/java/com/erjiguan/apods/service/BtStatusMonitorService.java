package com.erjiguan.apods.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.erjiguan.apods.BuildConfig;
import com.erjiguan.apods.model.monitor.BluetoothMonitor;
import com.erjiguan.apods.receiver.BluetoothStatusReceiver;

public class BtStatusMonitorService extends Service {

    private static final String TAG = "BtStatusMonitorService";
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
        if (mBtAdapter == null || !mBtAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "当前设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Do not support bluetooth.");
            stopSelf();
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "Support bluetooth.");
        }

        if (mBtMonitor != null) {
            BluetoothStatusReceiver.getInstance().unregisterCallback(mBtMonitor);
            mBtMonitor.stopScanBt();
        }
        mBtMonitor = BluetoothMonitor.getInstance();

        if (mBtAdapter.isEnabled()) {
            mBtMonitor.startScanBt();
        } else {
            Log.e(TAG, "Bluetooth has been closed.");
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBtMonitor != null) {
            BluetoothStatusReceiver.getInstance().unregisterCallback(mBtMonitor);
            mBtMonitor.stopScanBt();
        }
    }
}
