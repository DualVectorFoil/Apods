package com.erjiguan.apods.ui.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.erjiguan.apods.R;
import com.erjiguan.apods.view.activity.MainActivity;

public class WelcomeActivity extends Activity {

    private static final int DELAY = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        init();
    }

    private void init() {
        Handler h = new Handler();

        BluetoothAdapter adapter = ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE)).getAdapter();
        if (adapter == null) {
            Toast.makeText(this, R.string.not_support_bt, Toast.LENGTH_LONG).show();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, DELAY);
            return;
        }

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        }, DELAY);
    }
}
