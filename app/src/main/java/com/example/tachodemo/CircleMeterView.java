package com.example.tachodemo;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.security.PrivateKey;


public class CircleMeterView extends View {

    private Paint mPlatePaint, mContourPaint, mUnitTextPaint, mScalePaint, mScaleTextPaint, mInsidePaint, mTextPaint, mPointerPaint;

    // View宽,View高
    private int mWidth, mHeight;

    // 外刻度圆进度
    private float mProgress = 0;

    // 内刻度圆进度
    private float mInsideProgress = 0;

    // 中间显示的数值
    private float value = 0;

    private int contourColor, plateColor, scaleColor, scaleTextColor, insideCircleColor, textColor, pointerColor;

    private int textSize;

    private Context mContext;

    private final static String unitText = "RPM X 1000";

    public CircleMeterView(Context context) {
        this(context, null);
    }

    public CircleMeterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleMeterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray tya = context.obtainStyledAttributes(attrs, R.styleable.CircleMeterView);

        scaleColor = tya.getColor(R.styleable.CircleMeterView_scaleColor, Color.WHITE);
        contourColor = tya.getColor(R.styleable.CircleMeterView_contourColor, Color.DKGRAY);
        plateColor = tya.getColor(R.styleable.CircleMeterView_plateColor, Color.BLACK);
        scaleTextColor = tya.getColor(R.styleable.CircleMeterView_scaleTextColor, Color.WHITE);
        insideCircleColor = tya.getColor(R.styleable.CircleMeterView_insideCircleColor, Color.RED);
        textSize = tya.getDimensionPixelSize(R.styleable.CircleMeterView_textSize2, 36);
        textColor = tya.getColor(R.styleable.CircleMeterView_textColor2, Color.WHITE);
        pointerColor = tya.getColor(R.styleable.CircleMeterView_pointerColor, Color.RED);
        tya.recycle();

        initUI();
    }

    private void initUI() {
        mContext = getContext();

        mContourPaint = new Paint();
        mContourPaint.setAntiAlias(true);
        mContourPaint.setStrokeWidth(DensityUtil.dp2px(mContext, DensityUtil.dp2px(mContext, 10)));
        mContourPaint.setColor(contourColor);
        mContourPaint.setStyle(Paint.Style.STROKE);

        mPlatePaint = new Paint();
        mPlatePaint.setAntiAlias(true);
        mPlatePaint.setStrokeWidth(DensityUtil.dp2px(mContext, DensityUtil.dp2px(mContext, 0)));
        mPlatePaint.setColor(plateColor);
        mPlatePaint.setStyle(Paint.Style.FILL);

        mUnitTextPaint = new Paint();
        mUnitTextPaint.setAntiAlias(true);
        mUnitTextPaint.setStrokeWidth(DensityUtil.dp2px(mContext, 2));
        mUnitTextPaint.setTextSize(30);
        mUnitTextPaint.setColor(textColor);
        mUnitTextPaint.setStrokeJoin(Paint.Join.ROUND);
        mUnitTextPaint.setStyle(Paint.Style.FILL);

        // 刻度圆画笔
        mScalePaint = new Paint();
        mScalePaint.setAntiAlias(true);
        mScalePaint.setStrokeWidth(DensityUtil.dp2px(mContext, 3));
        mScalePaint.setColor(scaleColor);
        mScalePaint.setStrokeCap(Paint.Cap.BUTT);
        mScalePaint.setStyle(Paint.Style.STROKE);

        // 刻度文字画笔
        mScaleTextPaint = new Paint();
        mScaleTextPaint.setAntiAlias(true);
        mScaleTextPaint.setStrokeWidth(DensityUtil.dp2px(mContext, 2));
        mScaleTextPaint.setColor(scaleTextColor);
        mScaleTextPaint.setTextSize(40);
        mScaleTextPaint.setStyle(Paint.Style.FILL);

        // 中间值的画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(DensityUtil.dp2px(mContext, 2));
        mTextPaint.setTextSize(50);
        mTextPaint.setColor(textColor);
        mTextPaint.setStrokeJoin(Paint.Join.ROUND);
        mTextPaint.setStyle(Paint.Style.FILL);

        // 内部扇形刻度画笔
        mInsidePaint = new Paint();
        mInsidePaint.setAntiAlias(true);
        mInsidePaint.setStrokeWidth(DensityUtil.dp2px(mContext, 1));
        mInsidePaint.setColor(insideCircleColor);
        mInsidePaint.setStyle(Paint.Style.FILL);

        // 指针画笔
        mPointerPaint = new Paint();
        mPointerPaint.setAntiAlias(true);
        mPointerPaint.setStrokeWidth(DensityUtil.dp2px(mContext, 5));
        mPointerPaint.setColor(pointerColor);
        mPointerPaint.setStrokeCap(Paint.Cap.ROUND);
        mPointerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
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

        drawDialPlate(canvas);
        drawArcScale(canvas);
        drawArcInside(canvas);
        drawInsideSumText(canvas);
        drawPointer(canvas);
    }

    private void drawDialPlate(Canvas canvas){
        canvas.save();

        canvas.drawCircle(mWidth/2, mHeight/2, mWidth/2 - mContourPaint.getStrokeWidth(), mContourPaint);
        canvas.drawCircle(mWidth/2, mHeight/2, (mWidth/2) - (mWidth/10) + mScalePaint.getStrokeWidth(), mPlatePaint);
        Rect textBound = new Rect();
        mUnitTextPaint.getTextBounds(unitText, 0, unitText.length(), textBound);    // 获取文字的矩形范围
        float textWidth = textBound.right - textBound.left;  // 获得文字宽
        float textHeight = textBound.bottom - textBound.top; // 获得文字高
        canvas.drawText(unitText, mWidth / 2 - textWidth / 2, mHeight / 2 - textHeight - DensityUtil.dp2px(mContext, 20), mUnitTextPaint);

        canvas.restore();
    }

    private void drawArcScale(Canvas canvas) {
        canvas.save();

        canvas.rotate(-30, mWidth / 2, mHeight / 2);

        // 最外圆的线条宽度，避免线条粗时被遮蔽
        float scaleWidth = mScalePaint.getStrokeWidth() + mWidth/10;

        canvas.drawArc(new RectF(scaleWidth, scaleWidth, mWidth - scaleWidth, mHeight - scaleWidth), 180, 240, false, mScalePaint);

        // 定义文字旋转回的角度
        int rotateValue = 30;
        // 总八个等分，每等分10个刻度，所以总共需要80个刻度
        for (int i = 0; i <= 80; i++) {
            if (i % 10 == 0) {
                canvas.drawLine(scaleWidth, mHeight / 2, mWidth/6, mHeight / 2, mScalePaint);

                // 画文字
                String text = String.valueOf(i / 10);
                Rect textBound = new Rect();
                mScaleTextPaint.getTextBounds(text, 0, text.length(), textBound);   // 获取文字的矩形范围
                int textWidth = textBound.right - textBound.left;  // 获得文字宽度
                int textHeight = textBound.bottom - textBound.top;  // 获得文字高度

                canvas.save();
                canvas.translate(mWidth/6 + textWidth + DensityUtil.dp2px(mContext, 5), mHeight / 2);  // 移动画布的圆点

                if (i == 0) {
                    // 如果刻度为0，则旋转度数为30度
                    canvas.rotate(rotateValue);
                } else {
                    // 大于0的刻度，需要逐渐递减30度
                    canvas.rotate(rotateValue);
                }
                rotateValue = rotateValue - 30;

                canvas.drawText(text, -textWidth / 2, textHeight / 2, mScaleTextPaint);
                canvas.restore();

            } else {
                canvas.drawLine(scaleWidth, mHeight / 2, mWidth/7, mHeight / 2, mScalePaint);
            }
            canvas.rotate(3, mWidth / 2, mHeight / 2);
        }
        canvas.restore();
    }

    /**
     * 画内圆刻度
     */
    private void drawArcInside(Canvas canvas) {
        canvas.save();

        canvas.rotate(-30, mWidth / 2, mHeight / 2);
        for (int i = 0; i <= 100; i++) {
            if (mInsideProgress >= i) {
                // 大于外圆刻度6时显示红色
                if (i <= 75) {
                    mInsidePaint.setColor(insideCircleColor);
                } else {
                    mInsidePaint.setColor(Color.RED);
                }
            } else {
                mInsidePaint.setColor(Color.LTGRAY);
            }
            canvas.drawLine(mWidth/4, mHeight / 2, mWidth*7/24, mHeight / 2, mInsidePaint);
            canvas.rotate(2.4f, mWidth / 2, mHeight / 2);
        }

        canvas.restore();
    }

    /**
     * 画内部数值
     */
    private void drawInsideSumText(Canvas canvas) {
        canvas.save();

        if (mInsideProgress > 75)
            mTextPaint.setColor(Color.RED);
        else
            mTextPaint.setColor(textColor);

        // 获取文字居中显示需要的参数
        String showValue = String.valueOf(value);
        Rect textBound = new Rect();
        mTextPaint.getTextBounds(showValue, 0, showValue.length(), textBound);    // 获取文字的矩形范围
        float textWidth = textBound.right - textBound.left;  // 获得文字宽
        float textHeight = textBound.bottom - textBound.top; // 获得文字高
        canvas.drawText(showValue, mWidth / 2 - textWidth / 2, mHeight / 2 + textHeight + DensityUtil.dp2px(mContext, 45), mTextPaint);

        canvas.restore();
    }

    /**
     * 画指针
     */
    private void drawPointer(Canvas canvas) {
        canvas.save();

        // 旋转到0的位置
        canvas.rotate(-30, mWidth / 2, mHeight / 2);
        canvas.rotate(mProgress, mWidth / 2, mHeight / 2);
        canvas.drawLine(mWidth*27/96, mHeight / 2, mWidth / 2, mHeight / 2, mPointerPaint);
        canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 30, mPointerPaint);

        canvas.restore();
    }


    /**
     * 设置进度
     */
    public void setProgress(int progress) {

        // 内部刻度的进度
        this.mInsideProgress = progress*0.1f;

        // 指针显示的进度
        this.mProgress = (float) progress * 0.24f;

        // 设置中间文字显示的数值
        this.value = (float) (progress * 0.008);

        invalidate();
    }
}
