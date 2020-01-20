package com.erjiguan.apods.model.monitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.SystemClock;
import android.util.Log;

import com.erjiguan.apods.BuildConfig;
import com.erjiguan.apods.receiver.BluetoothStatusReceiver;
import com.erjiguan.apods.utils.UUIDUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BluetoothMonitor implements BluetoothStatusReceiver.BluetoothStatusCallback {

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

    public static final int AIRPODS_12 = 0;
    public static final int AIRPODS_PRO = 1;

    private BluetoothLeScanner mBtScanner = null;
    private UUIDUtil mUUIDHelper = null;
    private List<ScanResult> mBeacons = null;

    private static volatile BluetoothMonitor sInstance = null;

    private BluetoothMonitor() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        // mBtScanner should not be null, because if mBtScanner is null, app should be closed in launching.
        mBtScanner = btAdapter.getBluetoothLeScanner();
        mUUIDHelper = new UUIDUtil();
        mBeacons = new ArrayList<ScanResult>();
    }

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

    @Override
    public void onOpen(BluetoothDevice device) {
    }

    @Override
    public void onClose(BluetoothDevice device) {
    }

    @Override
    public void onConnect(BluetoothDevice device) {
        if (device != null && mUUIDHelper.verifyUUID(device)) {
            startScanBt();
        }
    }

    @Override
    public void onDisConnect(BluetoothDevice device) {
        if (device != null && mUUIDHelper.verifyUUID(device)) {
            stopScanBt();
            mBeacons.clear();
        }
    }

    public void startScanBt() {
        if (DEBUG) {
            Log.d(TAG, "Start scan bluetooth.");
        }

        if (mBtScanner == null) {
            Log.e(TAG, "mBtScanner is null, start scan bluetooth failed.");
            return;
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
                mIsConnect = true;
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

        mIsConnect = false;
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

    public boolean isConnect() {
        return mIsConnect;
    }

    public void setConnectState(boolean state) {
        mIsConnect = state;
    }

    public long getmLastSeenConnected() {
        return mLastSeenConnected;
    }

    public void setmLastSeenConnected(long mLastSeenConnected) {
        this.mLastSeenConnected = mLastSeenConnected;
    }

    public int getmLeftStatus() {
        return mLeftStatus;
    }

    public void setmLeftStatus(int mLeftStatus) {
        this.mLeftStatus = mLeftStatus;
    }

    public int getmRightStatus() {
        return mRightStatus;
    }

    public void setmRightStatus(int mRightStatus) {
        this.mRightStatus = mRightStatus;
    }

    public int getmCaseStatus() {
        return mCaseStatus;
    }

    public void setmCaseStatus(int mCaseStatus) {
        this.mCaseStatus = mCaseStatus;
    }

    public boolean ismLeftIsCharging() {
        return mLeftIsCharging;
    }

    public void setmLeftIsCharging(boolean mLeftIsCharging) {
        this.mLeftIsCharging = mLeftIsCharging;
    }

    public boolean ismRightIsCharging() {
        return mRightIsCharging;
    }

    public void setmRightIsCharging(boolean mRightIsCharging) {
        this.mRightIsCharging = mRightIsCharging;
    }

    public boolean ismCaseIsCharging() {
        return mCaseIsCharging;
    }

    public void setmCaseIsCharging(boolean mCaseIsCharging) {
        this.mCaseIsCharging = mCaseIsCharging;
    }

    public int getmPodsType() {
        return mPodsType;
    }

    public void setmPodsType(int mPodsType) {
        this.mPodsType = mPodsType;
    }
}
