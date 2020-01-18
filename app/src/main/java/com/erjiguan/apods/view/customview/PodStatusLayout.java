package com.erjiguan.apods.view.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class PodStatusLayout extends RelativeLayout {

    public PodStatusLayout(Context context) {
        super(context);
    }

    public PodStatusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PodStatusLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PodStatusLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        LayoutParams layoutParams = new LayoutParams(200, 200);
        this.setLayoutParams(layoutParams);

    }
}
