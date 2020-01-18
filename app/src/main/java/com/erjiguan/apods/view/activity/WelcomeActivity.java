package com.erjiguan.apods.view.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.erjiguan.apods.R;

public class WelcomeActivity extends Activity {

    private static final int DELAY = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Handler h = new Handler();

        BluetoothAdapter adapter = ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE)).getAdapter();
        if (adapter == null) {
            Toast.makeText(this, R.string.not_support_bt, Toast.LENGTH_LONG).show();
            // TODO open
//            h.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    finish();
//                }
//            }, DELAY);
//            return;
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
