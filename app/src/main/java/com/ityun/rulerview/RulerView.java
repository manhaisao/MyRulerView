package com.ityun.rulerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;
import java.util.ArrayList;
import java.util.List;

/**
 * @user xie
 * @date 2019/1/16 0016
 * @email 773675907@qq.com.
 */

public class RulerView extends View {

    private List<String> stringList = new ArrayList<>();


    //中间的三角形的画笔
    private Paint tagPaint = new Paint();

    //大圈圈的画笔
    private Paint circleBigPaint = new Paint();

    //文字的画笔
    private Paint txtPaint = new Paint();

    //小圆圆的半径
    private float circleRadius = DensityUtil.dip2px(getContext(), 3);

    //view的宽和高
    private int width, height;
    //view的最低高度
    private int minHeight = DensityUtil.dip2px(getContext(), 42);
    //view最高高度
    private int maxHeight = DensityUtil.dip2px(getContext(), 80);
    //文字的大小
    private int textSize = DensityUtil.sp2px(getContext(), 14);
    //小三角的高度
    private float tagHeight = DensityUtil.dip2px(getContext(), 2);
    //空格
    private int tagSpan = DensityUtil.dip2px(getContext(), 5);


    private float itemLength;
    /**
     * 滑动器
     */
    private Scroller scroller;
    /**
     * 速度跟踪器
     */
    private VelocityTracker mVelocityTracker;
    private int mDownX;
    private int mLastX;
    //中间小圆点的个数 加1的值
    private int centerPointNum = 10;

    /**
     * 惯性滑动最小、最大速度
     */
    private final int MIN_FLING_VELOCITY = 500;
    private final int MAX_FLING_VELOCITY = 1500;
    //起始点左边距
    private float mCurrentDistance;

    float bigCenterY;
    float bigCenterX;

    //   当前选中第几个
    private int position;

    public RulerView(Context context) {
        this(context, null);
        initView();
    }

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        initView();
    }

    public RulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.rulerview);
        if (ta != null) {
            tagHeight = ta.getDimension(R.styleable.rulerview_tagheight, DensityUtil.dip2px(getContext(), 2));
            textSize = (int) ta.getDimension(R.styleable.rulerview_textsize, DensityUtil.sp2px(getContext(), 14));
            centerPointNum = ta.getInt(R.styleable.rulerview_point_num, 10);
            ta.recycle();
        }
    }


    public void setDataString(List<String> stringList) {
        this.stringList = stringList;
    }

    public void setCenterPointNum(int centerPointNum) {
        this.centerPointNum = centerPointNum;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private void initView() {
        tagPaint.setColor(Color.WHITE);
        circleBigPaint.setColor(Color.WHITE);
        txtPaint.setColor(Color.WHITE);
        scroller = new Scroller(getContext());
        txtPaint.setTextSize(textSize);
        stringList = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        if (height < minHeight) {
            height = minHeight;
        }
        if (height > maxHeight) {
            height = maxHeight;
        }
        bigCenterY = tagHeight + tagSpan + circleRadius;
        bigCenterX = width / 2;
        itemLength = circleRadius * (centerPointNum - 1) + circleRadius * 2 + circleRadius * centerPointNum * 2;
        mCurrentDistance = bigCenterX - itemLength * position - circleRadius / 2;
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(heightMeasureSpec)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if ((stringList != null && stringList.size() != 0)) {
            drawTag(canvas);
            drawCircleAndTxt(canvas);
        }
    }


    /**
     * 绘制中间的小三角
     *
     * @param canvas
     */
    private void drawTag(Canvas canvas) {
        //上箭头
        Path topPath = new Path();
        topPath.moveTo(width / 2 - tagHeight, tagSpan);
        topPath.lineTo(width / 2 + tagHeight, tagSpan);
        topPath.lineTo(width / 2, tagHeight + tagSpan);
        canvas.drawPath(topPath, tagPaint);
        //下箭头
        Path bottomPath = new Path();
        bottomPath.moveTo(width / 2, height - tagHeight - tagSpan);
        bottomPath.lineTo(width / 2 - tagHeight, height - tagSpan);
        bottomPath.lineTo(width / 2 + tagHeight, height - tagSpan);
        canvas.drawPath(bottomPath, tagPaint);
    }

    private float allRulerLength;

    /**
     * 绘制圆点和文字
     *
     * @param canvas
     */
    private void drawCircleAndTxt(Canvas canvas) {
        //这玩意用来计算绘制后的文字的宽高
        Rect rect = new Rect();
        float viewToLeft = 0;
        for (int i = 0; i < (stringList.size() + (centerPointNum - 1) * (stringList.size() - 1)); i++) {
            if (i == 0) {
                viewToLeft = 0;
            } else if (i % centerPointNum == (centerPointNum - 1) || i % centerPointNum == 0 || i == 1) {
                viewToLeft = viewToLeft + circleRadius * 3 + circleRadius / 2;
            } else {
                viewToLeft = viewToLeft + circleRadius * 3;
            }
            if (i % centerPointNum == 0) {
                String showString = stringList.get(i / centerPointNum);
                txtPaint.getTextBounds(stringList.get(i / centerPointNum), 0, stringList.get(i / centerPointNum).length(), rect);
                canvas.drawText(showString, mCurrentDistance + viewToLeft - rect.width() / 2, height / 2 + rect.height() / 2, txtPaint);
                canvas.drawCircle(mCurrentDistance + viewToLeft, bigCenterY + tagSpan, circleRadius, circleBigPaint);
                canvas.drawCircle(mCurrentDistance + viewToLeft, height - (bigCenterY + tagSpan), circleRadius, circleBigPaint);
            } else {
                canvas.drawCircle(mCurrentDistance + viewToLeft, bigCenterY + tagSpan, circleRadius / 2, circleBigPaint);
                canvas.drawCircle(mCurrentDistance + viewToLeft, height - (bigCenterY + tagSpan), circleRadius / 2, circleBigPaint);
            }
        }
        allRulerLength = viewToLeft;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final int x = (int) event.getX();
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                scroller.forceFinished(true);
                mDownX = x;
                mLastX = mDownX;
                break;
            case MotionEvent.ACTION_MOVE:
                final int dx = x - mLastX;
                mCurrentDistance = mCurrentDistance + dx;
                calculateValue();
                mLastX = x;
                break;
            case MotionEvent.ACTION_UP:
                // 计算速度：使用1000ms为单位
                mVelocityTracker.computeCurrentVelocity(1000, MAX_FLING_VELOCITY);
                // 获取速度。速度有方向性，水平方向：左滑为负，右滑为正
                int xVelocity = (int) mVelocityTracker.getXVelocity();
                // 达到速度则惯性滑动，否则缓慢滑动到刻度
                if (Math.abs(xVelocity) >= MIN_FLING_VELOCITY) {
                    scroller.fling((int) mCurrentDistance, 0, xVelocity, 0, (int) (bigCenterX - allRulerLength), (int) bigCenterX, 0, 0);
                } else {
                    if (isGradation) {
                        isGradation = false;
                    } else {
                        scrollToGradation();
                    }
                }
                invalidate();
                break;
        }
        return true;
    }

    //给个上下限 防止滑过头了
    private void calculateValue() {
        if (mCurrentDistance > bigCenterX) {
            mCurrentDistance = bigCenterX;
        }
        if (mCurrentDistance < (bigCenterX - allRulerLength)) {
            mCurrentDistance = bigCenterX - allRulerLength;
        }
        invalidate();
    }

    /**
     * 滑动到最近的大圈圈那边
     */
    private void scrollToGradation() {
        if (stringList == null || stringList.size() == 0) {
            return;
        }
        isGradation = true;
        float scrollLength = bigCenterX - mCurrentDistance;

        for (int i = 0; i < (stringList.size()); i++) {
            if (scrollLength > (itemLength * i - itemLength / 2) && scrollLength <= (itemLength * i + itemLength / 2)) {
                int moveX;
                if (i == 0) {
                    moveX = (int) (bigCenterX - (i * itemLength) - mCurrentDistance);
                } else {
                    moveX = (int) (bigCenterX - (i * itemLength) - mCurrentDistance - circleRadius / 2);
                }
                position = i;
                if (onItemScrollListener != null)
                    onItemScrollListener.OnScroll(i);
                //调用了这笔之后 刷新界面 会走computeScroll()方法
                scroller.startScroll((int) mCurrentDistance, 0, moveX, 0, 300);
            }
        }
        invalidate();
    }

    //防止无线循环
    private boolean isGradation;

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            if (scroller.getCurrX() != scroller.getFinalX()) {
                mCurrentDistance = scroller.getCurrX();
                calculateValue();
            } else {
                if (isGradation) {
                    isGradation = false;
                } else {
                    scrollToGradation();
                }

            }
        }
    }

    OnItemScrollListener onItemScrollListener;

    public void setOnItemScrollListener(OnItemScrollListener onItemScrollListener) {
        this.onItemScrollListener = onItemScrollListener;
    }


    public interface OnItemScrollListener {
        void OnScroll(int position);
    }
}
