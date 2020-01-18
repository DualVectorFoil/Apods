package com.erjiguan.apods.view.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.erjiguan.apods.R;

public class PodStatusView extends LinearLayout {

    private static final String TAG = "PodStatusView";

    private static final int SIZE = 10;

    private Context mContext = null;
    private ImageView mPodImageView = null;
    private BatteryView mBatteryView = null;
    private PercentageView mPercentage = null;

    public PodStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PodStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public PodStatusView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;

        mPercentage = new PercentageView(mContext);
        LayoutParams podNameParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        podNameParams.setMargins(30, 20, 0, 0);
        mPercentage.setTextSize(SIZE);
        mPercentage.setLayoutParams(podNameParams);

        mBatteryView = new BatteryView(mContext, attrs);
        LayoutParams batteryViewParams = new LayoutParams(SIZE * 8, SIZE * 2);
        batteryViewParams.setMargins(30, 31, 0, 0);
        mBatteryView.setLayoutParams(batteryViewParams);
        mBatteryView.setBattery(0);

        mPodImageView = new ImageView(mContext);
        LayoutParams podImageViewParams = new LayoutParams(SIZE * 8, SIZE * 8);
        mPodImageView.setLayoutParams(podImageViewParams);

        TypedArray attributes = mContext.obtainStyledAttributes(attrs, R.styleable.PodStatusView);
        switch (attributes.getInt(R.styleable.PodStatusView_pod_item, -1)) {
            case 0:
                mPercentage.setPercent(0);
                mPodImageView.setImageResource(R.drawable.airpod_l);
                break;
            case 1:
                mPercentage.setPercent(0);
                mPodImageView.setImageResource(R.drawable.airpod_r);
                break;
            case 2:
                mPercentage.setPercent(0);
                mPodImageView.setImageResource(R.drawable.airpod_case);
                break;
            default:
                Log.e(TAG, "Unknown pod style, init failed.");
        }

        attributes.recycle();

        this.addView(mPodImageView);
        this.addView(mBatteryView);
        this.addView(mPercentage);
    }
}
