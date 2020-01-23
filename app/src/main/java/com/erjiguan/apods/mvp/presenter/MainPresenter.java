package com.erjiguan.apods.mvp.presenter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.erjiguan.apods.base.BaseApplication;
import com.erjiguan.apods.base.BasePresenter;
import com.erjiguan.apods.base.OnLoadDataListener;
import com.erjiguan.apods.mvp.model.IMainModel;
import com.erjiguan.apods.mvp.model.bean.BluetoothStatusBean;
import com.erjiguan.apods.mvp.model.impl.MainModelImpl;
import com.erjiguan.apods.mvp.view.IMainView;
import com.erjiguan.apods.receiver.BluetoothStatusReceiver;
import com.erjiguan.apods.receiver.BluetoothStatusReceiver.BluetoothStatusCallback;
import com.erjiguan.apods.service.BtStatusMonitorService;
import com.erjiguan.apods.ui.activity.MainActivity;

public class MainPresenter extends BasePresenter<IMainView> {

    private IMainModel mMainModel;

    private BluetoothManager mBtManager;
    private BluetoothAdapter mBtAdapter;
    private BluetoothStatusCallback mBtStatusCallback;
    private OnLoadDataListener<BluetoothStatusBean> mOnLoadDataListner;
    private Intent mScanServiceIntent;

    public MainPresenter() {
        this.mMainModel = new MainModelImpl();
        mScanServiceIntent = new Intent();
        mScanServiceIntent.setClass(BaseApplication.getContext(), BtStatusMonitorService.class);
    }

    public void init() {
        if (mWeakReferenceView == null) return;

        mBtManager = (BluetoothManager) BaseApplication.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBtManager == null) {
            Log.e(MainActivity.TAG, "Bluetooth manager is null, device do not support bluetooth.");
            getView().finishSelf();
            return;
        } else {
            mBtAdapter = mBtManager.getAdapter();
        }

        if (mBtAdapter == null) {
            Log.e(MainActivity.TAG, "Bluetooth adapter is null, device do not support bluetooth.");
            getView().finishSelf();
            return;
        } else if (!mBtAdapter.isEnabled()) {
            getView().showViewWhenBtNotConnected();
        } else {
            getView().showViewWhenBtConnected();
        }

        mOnLoadDataListner = new OnLoadDataListener<BluetoothStatusBean>() {
            @Override
            public void onSuccess(BluetoothStatusBean statusBean) {
                getView().onBtStatusUpdated(statusBean);
            }

            @Override
            public void onFailure(String error) {
                Log.w(MainActivity.TAG, "registerBtStatusListner failed, err: " + error);
            }
        };

        mBtStatusCallback = new BluetoothStatusCallback() {
            @Override
            public void onOpen(BluetoothDevice device) {
                getView().playBtSearchAnimation();
            }

            @Override
            public void onClose(BluetoothDevice device) {
                getView().stopBtSearchAnimation();
            }

            @Override
            public void onConnect(BluetoothDevice device) {
                getView().showViewWhenBtConnected();
                mMainModel.registerBtStatusListner(mOnLoadDataListner);
                startScanService();

            }

            @Override
            public void onDisConnect(BluetoothDevice device) {
                getView().showViewWhenBtNotConnected();
                mMainModel.unRegisterBtStatusListner(mOnLoadDataListner);
                stopScanService();
            }
        };
    }

    public void handleBtOpen() {
        if (mWeakReferenceView == null) return;

        BluetoothStatusReceiver.getInstance().registerCallback(mBtStatusCallback);
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        enableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Toast.makeText(BaseApplication.getContext(), "请求打开蓝牙", Toast.LENGTH_SHORT).show();
        BaseApplication.getContext().startActivity(enableIntent);

        if (!mBtAdapter.isEnabled()) {
            BluetoothStatusReceiver.getInstance().unregisterCallback(mBtStatusCallback);
        }
    }

    public void handleDestroy() {

    }

    private void startScanService() {
        BaseApplication.getContext().startService(mScanServiceIntent);
    }

    private void stopScanService() {
        BaseApplication.getContext().stopService(mScanServiceIntent);
    }
}
