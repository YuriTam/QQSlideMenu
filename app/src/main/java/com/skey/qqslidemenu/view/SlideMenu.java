package com.skey.qqslidemenu.view;

import android.animation.FloatEvaluator;
import android.animation.IntEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.skey.qqslidemenu.utils.ColorUtil;

/**
 * 自定义SlideMenu
 *
 * @author ALion on 2016/10/31 13:50
 */

public class SlideMenu extends FrameLayout {

    private View menuView;//菜单view
    private View mainView;//主页view
    private ViewDragHelper viewDragHelper;
    private int width;
    private float dragRange;//拖拽范围
    private FloatEvaluator floatEvaluator;//float的计算器
    private IntEvaluator intEvaluator;

    public SlideMenu(Context context) {
        super(context);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //定义状态常量
    enum DragState {
        Open, Close;
    }

    private DragState currentState = DragState.Close;//当前SlideMenu的状态是关闭的

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, callback);
        floatEvaluator = new FloatEvaluator();
        intEvaluator = new IntEvaluator();
    }

    /**
     * 获取当前的状态
     * @return Open, Close
     */
    public DragState getCurrentState() {
        return currentState;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //简单的有异常处理
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("SlideMenu only have two children!");
        }
        menuView = getChildAt(0);
        mainView = getChildAt(1);
    }

    /**
     * 该方法在onMeasure执行完后执行，那么可以在该方法中初始化自己和子view的宽高
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getMeasuredWidth();
        dragRange = width * 0.6f;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == menuView || child == mainView;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return (int) dragRange;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mainView) {
                if (left < 0) left = 0;//限制mainView左边
                if (left > dragRange) left = (int) dragRange;//限制mainView右边
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == menuView) {
                //固定住menuView
                menuView.layout(0, 0, menuView.getMeasuredWidth(), menuView.getMeasuredHeight());
                //让mainView移动起来
                int newLeft = mainView.getLeft() + dx;
                if (newLeft < 0) newLeft = 0;//限制mainView左边
                if (newLeft > dragRange) newLeft = (int) dragRange;//限制mainView右边
                mainView.layout(newLeft, mainView.getTop() + dy,
                        newLeft + menuView.getMeasuredWidth(), mainView.getBottom() + dy);
            }

            //1.计算滑动百分比
            float fraction = mainView.getLeft() / dragRange;
            if (fraction > 0.9999999f) fraction = 1;
            //2.执行伴随动画
            executeAnim(fraction);
            //3.更改状态，回调listener
            if (listener != null) {
                if (fraction == 0 && currentState != DragState.Close) {
                    currentState = DragState.Close;//更改状态
                    listener.onClose();//回调关闭的方法
                } else if (fraction == 1 && currentState != DragState.Open) {
                    currentState = DragState.Open;
                    listener.onOpen();
                }
                listener.onDragging(fraction);//将drag的fraction暴露给外界
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (mainView.getLeft() < dragRange / 2) {
                //在左半边
                close();//关闭菜单
            } else {
                //在右半边
                open();//打开菜单
            }

            //处理用户的轻微滑动，xvel是抬起时滑动的速度
//            Log.e("tag", "xvel = " + xvel);
            if (xvel > 200 && currentState != DragState.Open) {
                open();
            } else if (xvel < -200 && currentState != DragState.Close) {
                close();
            }
        }
    };

    public void close() {
        viewDragHelper.smoothSlideViewTo(mainView, 0, mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);//刷新
    }

    public void open() {
        viewDragHelper.smoothSlideViewTo(mainView, (int) dragRange, mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);//刷新
    }

    /**
     * 执行伴随动画
     *
     * @param fraction 百分比0-1
     */
    private void executeAnim(float fraction) {
        //缩小mainView
//        float scaleValue = 0.8f + 0.2f * (1 - fraction);
        mainView.setScaleX(floatEvaluator.evaluate(fraction, 1f, 0.8f));//floatEvaluator和上面的scaleValue一个效果
        mainView.setScaleY(floatEvaluator.evaluate(fraction, 1f, 0.8f));

        //移动menuView
        menuView.setTranslationX(intEvaluator.evaluate(fraction, -menuView.getMeasuredWidth() / 2, 0));
        //放大menuView
        menuView.setScaleX(floatEvaluator.evaluate(fraction, 0.5f, 1));
        menuView.setScaleY(floatEvaluator.evaluate(fraction, 0.6f, 1));
        //改变menuView的透明度
        menuView.setAlpha(floatEvaluator.evaluate(fraction, 0.3f, 1));

        //给SlideMenu的背景添加黑色的遮罩效果
        getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(
                fraction, Color.BLACK, Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);//刷新
        }
    }



    private OnDragStateChangeListener listener;

    public void setOnDragStateChangeListener(OnDragStateChangeListener listener) {
        this.listener = listener;
    }

    public interface OnDragStateChangeListener {
        /**
         * 打开的回调
         */
        void onOpen();

        /**
         * 关闭的回调
         */
        void onClose();

        /**
         * 拖拽过程中的回调
         */
        void onDragging(float fraction);
    }
}
