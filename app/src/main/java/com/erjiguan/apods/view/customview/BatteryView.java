package com.erjiguan.apods.view.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class BatteryView extends View {

    private static final String TAG = "BatteryView";

    private static final float OUTLINE_THICKNESS = 2.0f;//电池框厚度
    private static final float CAP_WIDTH = 2.0f;//电池盖宽度
    private static final float CAP_HEIGHT = 8.0f;//电池盖高度
    private static final float GAP_OF_SHAPE_BODY = 3.0f;//电池体与外框之间的间隙
    private static final float ROUND_CORNER_RADIUS = 3;

    private float fullPowerWidth; //满电量时电池体的宽度。

    private int battery = 1;

    private Paint batteryBodyPainter;
    private Paint batteryHeadPainter;
    private Paint mPowerPaint;//电量画笔

    private RectF outlineRect;//电池矩形
    private RectF mCapRect;//电池盖矩形
    private RectF batteryRect;//电量矩形

    public BatteryView(Context context) {
        this(context, null);
    }

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);

        batteryBodyPainter = new Paint();
        batteryBodyPainter.setColor(Color.BLACK);
        batteryBodyPainter.setAntiAlias(true);
        batteryBodyPainter.setStyle(Paint.Style.STROKE);
        batteryBodyPainter.setStrokeWidth(OUTLINE_THICKNESS);

        batteryHeadPainter = new Paint();
        batteryHeadPainter.setColor(Color.GRAY);
        batteryHeadPainter.setAntiAlias(true);
        batteryHeadPainter.setStyle(Paint.Style.FILL);

        mPowerPaint = new Paint();
        mPowerPaint.setAntiAlias(true);
        mPowerPaint.setColor(Color.GREEN);
        mPowerPaint.setStyle(Paint.Style.FILL);

        outlineRect = new RectF();
        outlineRect.left = OUTLINE_THICKNESS;
        outlineRect.top = OUTLINE_THICKNESS;

        mCapRect = new RectF();
        batteryRect = new RectF();
    }

    public void setBattery(int battery){
        this.battery = battery > 100 ? 100 : battery < 1 ? 1 : battery;
        Log.d(TAG, "setting battery:" + battery + ",applied battery:" + this.battery);

        invalidate();
    }

    public int getBattery(){
        return battery;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);//宽
        int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);//高

        //设置电池外框
        outlineRect.right = specWidthSize - OUTLINE_THICKNESS - CAP_WIDTH;
        outlineRect.bottom = specHeightSize - OUTLINE_THICKNESS;

        //设置电池盖矩形
        mCapRect.left = outlineRect.right;
        mCapRect.top = (float)specHeightSize / 2 - CAP_HEIGHT / 2;
        mCapRect.right = specWidthSize;
        mCapRect.bottom = (float)specHeightSize / 2 + CAP_HEIGHT / 2;

        //设置电池体
        batteryRect.left = outlineRect.left + GAP_OF_SHAPE_BODY;
        batteryRect.top = outlineRect.top + GAP_OF_SHAPE_BODY;
        batteryRect.bottom = outlineRect.bottom - GAP_OF_SHAPE_BODY;

        fullPowerWidth = outlineRect.right - GAP_OF_SHAPE_BODY - batteryRect.left;

        setMeasuredDimension(specWidthSize, specHeightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置电量矩形
        batteryRect.right = (float)battery / 100 * fullPowerWidth + batteryRect.left;

        canvas.drawRoundRect(outlineRect, ROUND_CORNER_RADIUS, ROUND_CORNER_RADIUS, batteryBodyPainter);
        canvas.drawRoundRect(mCapRect, 1, 1, batteryHeadPainter);
        canvas.drawRoundRect(batteryRect,ROUND_CORNER_RADIUS,ROUND_CORNER_RADIUS, mPowerPaint);
    }

}
