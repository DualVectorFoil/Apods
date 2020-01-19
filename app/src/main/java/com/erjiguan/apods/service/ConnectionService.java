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

        if (mBtMonitor != null) {
            BluetoothStatusReceiver.getInstance().unregisterCallback(mBtMonitor);
            mBtMonitor.stopScanBt();
        }
        mBtMonitor = BluetoothMonitor.getInstance();
        BluetoothStatusReceiver.getInstance().registerCallback(mBtMonitor);

        if (mBtAdapter.isEnabled()) {
            mBtMonitor.startScanBt();
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
