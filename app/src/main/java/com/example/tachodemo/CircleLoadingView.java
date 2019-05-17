package com.example.tachodemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;


public class CircleLoadingView extends View {


    private Paint mScalePaint, mTextPaint, mDotPaint;
    int baseColor, indexColor, textColor, dotColor, textSize;

    private int progress = 0;
    private float mDotProgress;

    int mWidth, mHeight;
    private Context mContext;
    private ValueAnimator animator;

    public CircleLoadingView(Context context) {
        this(context, null);
    }

    public CircleLoadingView(Context context,AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLoadingView(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray tya = context.obtainStyledAttributes(attrs,R.styleable.CircleLoading);
        baseColor = tya.getColor(R.styleable.CircleLoading_baseColor, Color.LTGRAY);
        indexColor = tya.getColor(R.styleable.CircleLoading_indexColor, Color.BLUE);
        textColor = tya.getColor(R.styleable.CircleLoading_textColor, Color.BLUE);
        dotColor = tya.getColor(R.styleable.CircleLoading_dotColor, Color.RED);
        textSize = tya.getDimensionPixelSize(R.styleable.CircleLoading_textSize, 36);
        tya.recycle();

        initUI();
    }

    private void initUI() {
        mContext = getContext();

        // 刻度画笔
        mScalePaint = new Paint();
        mScalePaint.setAntiAlias(true);
        mScalePaint.setStrokeWidth(DensityUtil.dp2px(mContext, 1));
        mScalePaint.setStrokeCap(Paint.Cap.ROUND);
        mScalePaint.setColor(baseColor);
        mScalePaint.setStyle(Paint.Style.STROKE);

        // 小圆点画笔
        mDotPaint = new Paint();
        mDotPaint.setAntiAlias(true);
        mDotPaint.setColor(dotColor);
        mDotPaint.setStrokeWidth(DensityUtil.dp2px(mContext, 1));
        mDotPaint.setStyle(Paint.Style.FILL);

        // 文字画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setStrokeWidth(DensityUtil.dp2px(mContext, 1));
        mTextPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int myWidthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int myWidthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int myHeightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int myHeightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if(myHeightSpecMode == MeasureSpec.EXACTLY)
            mHeight = myHeightSpecSize;
        else
            mHeight = DensityUtil.dp2px(mContext, 120);

        if(myWidthSpecMode == MeasureSpec.EXACTLY)
            mWidth = myWidthSpecSize;
        else
            mWidth = DensityUtil.dp2px(mContext, 120);

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawArcscale(canvas);
        drawTextValue(canvas);
        drawRatateDot(canvas);
    }

    private void drawArcscale(Canvas canvas){
        canvas.save();

        for(int i = 0; i < 100; i++){

            if (progress > i)
                mScalePaint.setColor(indexColor);
            else
                mScalePaint.setColor(baseColor);

            canvas.drawLine(mWidth/2, 0, mHeight/2, DensityUtil.dp2px(mContext,10),mScalePaint);
            canvas.rotate(3.6f,mWidth/2,mHeight/2);
        }

        canvas.restore();
    }

    private void drawTextValue(Canvas canvas){
        canvas.save();

        String showValue = String.valueOf(progress);
        Rect textBound = new Rect();

        mTextPaint.getTextBounds(showValue,0,showValue.length(),textBound);
        float textWidth = textBound.right - textBound.left;
        float textHeight = textBound.bottom - textBound.top;
        canvas.drawText(showValue,mWidth/2 - textWidth/2, mHeight/2 + textHeight/2, mTextPaint);

        canvas.restore();
    }

    private void drawRatateDot(final Canvas canvas){
        canvas.save();

        canvas.rotate(mDotProgress*3.6f, mWidth/2,mHeight/2);
        canvas.drawCircle(mWidth/2, DensityUtil.dp2px(mContext,10) + DensityUtil.dp2px(mContext, 5),DensityUtil.dp2px(mContext,3),mDotPaint);

        canvas.restore();
    }

    public void startDotAnimator() {
        animator = ValueAnimator.ofFloat(0, 100);
        animator.setDuration(1500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 设置小圆点的进度，并通知界面重绘
                mDotProgress = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }
}
