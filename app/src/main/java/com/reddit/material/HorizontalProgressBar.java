package com.reddit.material;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

/**
 * Created by Rushil Perera on 11/6/2015.
 */
public class HorizontalProgressBar extends View {

    private int barHeight;
    private int progress;
    private int max;
    private int deviceWidth;
    private Paint rectanglePaint;

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        deviceWidth = metrics.widthPixels;
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HorizontalProgressBar,
                0, 0);
        max = typedArray.getInt(R.styleable.HorizontalProgressBar_max, 100);
        rectanglePaint = new Paint();
        rectanglePaint.setColor(typedArray.getColor(R.styleable.HorizontalProgressBar_bar_color, Color.BLACK));
        barHeight = typedArray.getInt(R.styleable.HorizontalProgressBar_bar_height, 1);
        progress = typedArray.getInt(R.styleable.HorizontalProgressBar_progress, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, getY(), progress == 0 ? 0 : (float) ((double) max / (double) progress == 0 ? deviceWidth :
                (double) deviceWidth / ((double) max / (double) progress)), getY() + barHeight, rectanglePaint);
    }

    public void setProgress(int progress) {
        if (progress != this.progress) {
            this.progress = progress;
            Log.d("progress", String.valueOf(progress));
            Log.d("width", String.valueOf(progress == 0 ? 0 : (double) max / (double) progress == 0 ? deviceWidth :
                    (double) deviceWidth / ((double) max / (double) progress)));
            invalidate();
        }
    }

    public int getBarHeight() {
        return barHeight;
    }

    public void setBarHeight(int height) {
        this.barHeight = height;
        requestLayout();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
        requestLayout();
    }
}
