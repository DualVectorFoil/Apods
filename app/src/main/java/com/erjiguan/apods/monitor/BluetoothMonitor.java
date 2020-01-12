package com.erjiguan.apods.monitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;

import com.erjiguan.apods.BuildConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BluetoothMonitor extends BroadcastReceiver {

    private static final String TAG = "BluetoothMonitor";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private boolean mIsConnect = false;

    private BluetoothLeScanner mBtScanner = null;
    private final UUIDHelper mUUIDHelper = new UUIDHelper();
    private final List<String> mBeacons = new ArrayList<String>();

    private static volatile BluetoothMonitor sInstance = null;

    public static BluetoothMonitor getInstance() {
        if (sInstance == null) {
            synchronized (BluetoothMonitor.class) {
                if (sInstance == null) {
                    sInstance = new BluetoothMonitor();
                }
            }
        }
        return sInstance;
    }

    public boolean isConnect() {
        return mIsConnect;
    }

    public void setConnectState(boolean state) {
        mIsConnect = state;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        String action = intent.getAction();
        if (action != null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            if (state == BluetoothAdapter.STATE_OFF || state == BluetoothAdapter.STATE_TURNING_OFF) {
                if (DEBUG) {
                    Log.d(TAG, "Bluetooth closed.");
                }
                mIsConnect = true;
                stopScanBt();
                mBeacons.clear();
            } else if (state == BluetoothAdapter.STATE_ON) {
                if (DEBUG) {
                    Log.d(TAG, "Bluetooth opened.");
                }
                mIsConnect = true;
                startScanBt();
            }
        }

        if (bluetoothDevice != null && action != null && !action.isEmpty() && mUUIDHelper.verifyUUID(bluetoothDevice)) {
            if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                if (DEBUG) {
                    Log.d(TAG, "Bluetooth acl connected.");
                }
                mIsConnect = true;
            } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                if (DEBUG) {
                    Log.d(TAG, "Bluetooth acl disconnected.");
                }
                mIsConnect = false;
                mBeacons.clear();
            }
        }
    }

    // TODO
    public void startScanBt() {
        if (DEBUG) {
            Log.d(TAG, "Start scan bluetooth.");
        }

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        mBtScanner = btAdapter.getBluetoothLeScanner();
        if (mBtScanner == null || !btAdapter.isEnabled()) {
            Log.d(TAG, "Uncorrected bluetooth status.");
        }

        List<ScanFilter> filters = getScanFilters();
        ScanSettings settings;
        settings = new ScanSettings.Builder().setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(2).build();
        mBtScanner.startScan(filters, settings, new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                Log.d("haha123_res", result.toString());

                super.onScanResult(callbackType, result);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                for (ScanResult res : results) {
                    Log.d("haha123_batch_res", res.toString());
                }

                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.d(TAG, "Bluetooth scan failed, errorcode: " + errorCode);
                super.onScanFailed(errorCode);
            }
        });
    }

    // TODO
    public void stopScanBt() {
        if (DEBUG) {
            Log.d(TAG, "Stop scan bluetooth.");
        }

        try {
            mBtScanner.stopScan(new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                }
            });
        } catch (Throwable t) {
        }
    }

    class UUIDHelper {

        private final ParcelUuid[] AIRPODS_UUID_LIST = {
                ParcelUuid.fromString("74ec2172-0bad-4d01-8f77-997b2be0722a"),
                ParcelUuid.fromString("2a72e02b-7b99-778f-014d-ad0b7221ec74")
        };

        public boolean verifyUUID(BluetoothDevice device) {
            ParcelUuid[] uuidList = device.getUuids();
            if (uuidList == null) {
                return false;
            }

            for (ParcelUuid uuid : uuidList) {
                for (ParcelUuid airpodsUuid : AIRPODS_UUID_LIST) {
                    if (uuid.equals(airpodsUuid)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private List<ScanFilter> getScanFilters() {
        byte[] manufacturerData = new byte[27];
        byte[] manufacturerDataMask = new byte[27];

        manufacturerData[0] = 7;
        manufacturerData[1] = 25;

        manufacturerDataMask[0] = -1;
        manufacturerDataMask[1] = -1;

        ScanFilter.Builder builder = new ScanFilter.Builder();
        builder.setManufacturerData(76, manufacturerData, manufacturerDataMask);
        return Collections.singletonList(builder.build());
    }
}
