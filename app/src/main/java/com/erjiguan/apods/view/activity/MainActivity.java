package com.erjiguan.apods.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.erjiguan.apods.R;
import com.erjiguan.apods.service.BtStatusMonitorService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int ANIM_VIEW_HEIGHT = 477;

    RelativeLayout mPodStatusLayout = null;
    FrameLayout mInteractionZoneLayout = null;
    LottieAnimationView mGuideAnimView = null;
    LottieAnimationView mConnectAnimView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }

        init();
    }

    private void init() {
        mPodStatusLayout = findViewById(R.id.pod_status);
        mInteractionZoneLayout = findViewById(R.id.interaction_zone);

        BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = bluetoothManager.getAdapter();
        if (adapter == null || !adapter.isEnabled()) {
            initDisconnectedView();
        } else {
            initConnectedView();
        }
    }

    private void initDisconnectedView() {
        Toast.makeText(this, R.string.bt_closed, Toast.LENGTH_SHORT).show();

        RelativeLayout.LayoutParams podStatusParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ANIM_VIEW_HEIGHT);

        mGuideAnimView = new LottieAnimationView(this);
        mGuideAnimView.setRepeatCount(-1);
        mGuideAnimView.setAnimation("bluetooth_direct.json");
        mGuideAnimView.setBackgroundColor(Color.WHITE);
        mPodStatusLayout.addView(mGuideAnimView, podStatusParams);
        mGuideAnimView.playAnimation();

        FrameLayout.LayoutParams interactiveParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ANIM_VIEW_HEIGHT);
        interactiveParams.setMargins(0, 600, 0, 0);

        mConnectAnimView = new LottieAnimationView(this);
        mConnectAnimView.setRepeatCount(-1);
        mConnectAnimView.setAnimation("bluetooth_connecting.json");
        mConnectAnimView.setBackgroundColor(Color.WHITE);
        mConnectAnimView.setSpeed(0f);
        mConnectAnimView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConnectionService();
                mConnectAnimView.setSpeed(1.5f);
            }
        });
        mConnectAnimView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent i = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(i);
                return false;
            }
        });

        mInteractionZoneLayout.addView(mConnectAnimView, interactiveParams);
        mConnectAnimView.playAnimation();
    }

    private void initConnectedView() {
        mPodStatusLayout.removeView(mGuideAnimView);
        mPodStatusLayout.removeView(mConnectAnimView);

        // TODO

    }

    private void startConnectionService() {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), BtStatusMonitorService.class);
        startService(intent);
    }
}
