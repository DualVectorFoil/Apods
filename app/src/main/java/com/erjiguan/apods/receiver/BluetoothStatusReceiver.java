package com.erjiguan.apods.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.erjiguan.apods.BuildConfig;

import java.util.List;
import java.util.ArrayList;

public class BluetoothStatusReceiver extends BroadcastReceiver {

    private static final String TAG = "BluetoothStatusReceiver";

    private List<BluetoothStatusCallback> mCallbacks = null;

    private static volatile BluetoothStatusReceiver sInstance = null;

    private BluetoothStatusReceiver() {
        mCallbacks = new ArrayList<BluetoothStatusCallback>();
    }

    public static BluetoothStatusReceiver getInstance() {
        if (sInstance == null) {
            synchronized (BluetoothStatusReceiver.class) {
                if (sInstance == null) {
                    sInstance = new BluetoothStatusReceiver();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onReceive action is null.");
            }
            return;
        }

        BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            if (state == BluetoothAdapter.STATE_OFF || state == BluetoothAdapter.STATE_TURNING_OFF) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Bluetooth closed.");
                }
                for (BluetoothStatusCallback callback : mCallbacks) {
                    callback.onClose(device);
                }
            } else if (state == BluetoothAdapter.STATE_ON) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Bluetooth opened.");
                }
                for (BluetoothStatusCallback callback : mCallbacks) {
                    callback.onOpen(device);
                }
            }
        }

        if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Bluetooth acl connected.");
            }
            for (BluetoothStatusCallback callback : mCallbacks) {
                callback.onConnect(device);
            }
        } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Bluetooth acl disconnected.");
            }
            for (BluetoothStatusCallback callback : mCallbacks) {
                callback.onDisConnect(device);
            }
        }
    }

    public void registerCallback(BluetoothStatusCallback callback) {
        mCallbacks.add(callback);
    }

    public void unregisterCallback(BluetoothStatusCallback callback) {
        mCallbacks.remove(callback);
    }

    public interface BluetoothStatusCallback {

        void onOpen(BluetoothDevice device);

        void onClose(BluetoothDevice device);

        void onConnect(BluetoothDevice device);

        void onDisConnect(BluetoothDevice device);
    }
}
