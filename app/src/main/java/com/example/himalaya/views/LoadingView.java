package com.example.himalaya.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import com.example.himalaya.R;

public class LoadingView extends androidx.appcompat.widget.AppCompatImageView {
   private int rotateDegree=0;
   private boolean mNeedRotate=false;
    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.mipmap.loading);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNeedRotate=true;
        post(new Runnable() {
            @Override
            public void run() {
                rotateDegree=+30;//旋转角度
                rotateDegree=rotateDegree<=360?rotateDegree:0;
                invalidate();
                if (mNeedRotate) {
                    postDelayed(this,200);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mNeedRotate=false;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.rotate(rotateDegree,getWidth()/2,getHeight()/2);
        super.onDraw(canvas);

    }
}
