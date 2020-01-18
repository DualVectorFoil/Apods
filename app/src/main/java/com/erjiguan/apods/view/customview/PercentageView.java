package com.erjiguan.apods.view.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class PercentageView extends TextView {

    public PercentageView(Context context) {
        super(context);
    }

    public PercentageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PercentageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PercentageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setPercent(int percent) {
        String batteryPercent = "";
        if (percent >= 0 && percent < 10) {
            batteryPercent = percent + "  %";
        } else if (percent >= 10 && percent < 100) {
            batteryPercent = percent + " %";
        } else if (percent == 100) {
            batteryPercent = percent + "%";
        }

        if (!batteryPercent.equals("")) {
            this.setText(batteryPercent);
        }
    }
}
