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
import android.os.SystemClock;
import android.util.Log;

import com.erjiguan.apods.BuildConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BluetoothMonitor extends BroadcastReceiver {

    private static final String TAG = "BluetoothMonitor";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final long RECENT_BEACONS_MAX_T_NS = 10000000000L;

    private boolean mIsConnect = false;

    private long mLastSeenConnected = 0;
    private int mLeftStatus = 15;
    private int mRightStatus = 15;
    private int mCaseStatus = 15;
    private boolean mLeftIsCharging = false;
    private boolean mRightIsCharging = false;
    private boolean mCaseIsCharging = false;
    private int mPodsType;

    private static final int AIRPODS_12 = 0;
    private static final int AIRPODS_PRO = 1;

    private BluetoothLeScanner mBtScanner = null;
    private final UUIDHelper mUUIDHelper = new UUIDHelper();
    private final List<ScanResult> mBeacons = new ArrayList<ScanResult>();

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
                    Log.d(TAG, "Bluetooth acl connected, addr: " + bluetoothDevice.getAddress());
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
                Log.d(TAG, "onScanResult addr: " + result.getDevice().getAddress());
                byte[] rawData = null;
                if (result.getScanRecord() != null) {
                    rawData = result.getScanRecord().getManufacturerSpecificData(76);
                }
                if (rawData == null || rawData.length != 27) {
                    return;
                }
                mBeacons.add(result);

                if (DEBUG) {
                    Log.d(TAG, "Rssi: " + result.getRssi() + "db");
                    Log.d(TAG, "This time hex data: " + decodeHex(rawData));
                }

                ScanResult strongestBeacon = null;
                for (int i = 0; i < mBeacons.size(); i++) {
                    if(SystemClock.elapsedRealtimeNanos() - mBeacons.get(i).getTimestampNanos() > RECENT_BEACONS_MAX_T_NS){
                        mBeacons.remove(i--);
                        continue;
                    }
                    if(strongestBeacon == null || strongestBeacon.getRssi() < mBeacons.get(i).getRssi()) {
                        strongestBeacon = mBeacons.get(i);
                    }
                }
                if (strongestBeacon != null && strongestBeacon.getDevice().getAddress().equals(result.getDevice().getAddress())) {
                    strongestBeacon = result;
                }

                if (strongestBeacon == null || strongestBeacon.getRssi() < -60) {
                    return;
                }

                String data = "";
                if (strongestBeacon.getScanRecord() != null && strongestBeacon.getScanRecord().getManufacturerSpecificData(76) != null) {
                    data = decodeHex(strongestBeacon.getScanRecord().getManufacturerSpecificData(76));
                }
                String leftStatusStr = ""; //left airpod (0-10 batt; 15=disconnected)
                String rightStatusStr = ""; //right airpod (0-10 batt; 15=disconnected)
                if (isFlipped(data)) {
                    leftStatusStr = "" + data.charAt(12);
                    rightStatusStr = "" + data.charAt(13);
                } else {
                    leftStatusStr = "" + data.charAt(13);
                    rightStatusStr = "" + data.charAt(12);
                }
                String caseStatusStr = "" + data.charAt(15);
                String chargeStatusStr = "" + data.charAt(14);

                mLeftStatus = Integer.parseInt(leftStatusStr, 16);
                mRightStatus = Integer.parseInt(rightStatusStr, 16);
                mCaseStatus = Integer.parseInt(caseStatusStr, 16);
                int chargeStatus = Integer.parseInt(chargeStatusStr, 16);
                mLeftIsCharging = (chargeStatus & 0b00000001) != 0;
                mRightIsCharging = (chargeStatus & 0b00000010) != 0;
                mCaseIsCharging = (chargeStatus & 0b00000100) != 0;
                mPodsType = (data.charAt(7) == 'E') ? AIRPODS_PRO : AIRPODS_12;
                mLastSeenConnected = System.currentTimeMillis();
                if (DEBUG) {
                    Log.d(TAG, "mLeftStatus: " + mLeftStatus + ", mRightStatus: " + mRightStatus
                            + ", mCaseStatus: " + mCaseStatus + ", mLeftIsCharging: " + mLeftIsCharging
                            + ", mRightIsCharging: " + mRightIsCharging + ", mCaseIsCharging: " + mCaseIsCharging
                            + ", mPodsType: " + mPodsType + ", mLastSeenConnected: " + mLastSeenConnected);
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                for (ScanResult res : results) {
                    onScanResult(-1, res);
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

    private final char[] hexCharset = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    private String decodeHex(byte[] bArr) {
        if (bArr == null || bArr.length == 0) {
            return "";
        }
        char[] ret = new char[bArr.length * 2];
        for (int i = 0; i < bArr.length; i++) {
            int b = bArr[i] & 0xFF;
            ret[i*2] = hexCharset[b >>> 4];
            ret[i*2+1] = hexCharset[b & 0x0F];
        }
        return new String(ret);
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

    private boolean isFlipped(String data) {
        return (Integer.toString(Integer.parseInt("" + data.charAt(10),16) + 0x10, 2)).charAt(3) == '0';
    }
}
