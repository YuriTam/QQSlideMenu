package com.skey.qqslidemenu.test;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.skey.qqslidemenu.R;
import com.skey.qqslidemenu.utils.ColorUtil;

/**
 * Created by Administrator on 2016/10/30.
 *
 * @author ALion on 2016/10/30 14:08
 */

public class DragLayout extends FrameLayout {

    private View redView;
    private View blueView;

    private ViewDragHelper viewDragHelper;
    private Scroller scroller;

    public DragLayout(Context context) {
        super(context);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //当DragLayout的xml布局的结束标签被读取完成，会执行该方法，此时会知道自己有几个子View
    //一般用来初始化子View的引用
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        redView = getChildAt(0);
        blueView = getChildAt(1);

    }

    //继承FrameLayout，如果没特殊需求，就不重新此方法了
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        //测量子View
//////        int size = (int) getResources().getDimension(R.dimen.width);
////        int measureSpec = MeasureSpec.makeMeasureSpec(redView.getLayoutParams().width, MeasureSpec.EXACTLY);
////        redView.measure(measureSpec, measureSpec);
////        blueView.measure(measureSpec, measureSpec);
//        //如果没有特殊的对子View的测量需求，可以用如下方法
//        measureChild(redView, widthMeasureSpec, heightMeasureSpec);
//        measureChild(blueView, widthMeasureSpec, heightMeasureSpec);
//    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        int left = getPaddingLeft() + getMeasuredWidth() / 2 - redView.getMeasuredWidth() / 2;
        int left = getPaddingLeft();
        int top = getPaddingTop();
        redView.layout(left, top, left + redView.getMeasuredWidth(), top + redView.getMeasuredHeight());//指定位置
        blueView.layout(left, redView.getBottom(), left + blueView.getMeasuredWidth(),
                redView.getBottom() + blueView.getMeasuredHeight());//指定位置

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);//让viewDragHelper判断是否应该拦截
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);//将触摸事件交给viewDragHelper
//        return super.onTouchEvent(event);
        return true;//自己处理
    }

    private void init() {
        scroller = new Scroller(getContext());
        CallBack callBack = new CallBack();
        viewDragHelper = ViewDragHelper.create(this, 1.0f, callBack);
    }

    private class CallBack extends ViewDragHelper.Callback {
        /**
         * 用于是否捕获当前child的触摸事件
         * @param child 当前触摸的子View
         * @return true捕获并解析，false不处理
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == blueView || child == redView;//触摸蓝色的菜捕获
        }

        /**
         * 当View被开始捕获和解析的回调
         * @param capturedChild 当前被捕获的View
         * @param activePointerId
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
            Log.e("tag", "onViewCaptured");
        }

        /**
         * 获取View水平方向的拖拽范围，但是目前不能限制边界
         * @param child 子View
         * @return 换回的值目前用在手指抬起的时候View缓慢移动的动画的时间计算上面
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }

        /**
         * 控制child在水平方向的移动
         * @param child 触摸到的子View
         * @param left  表示ViewDragHelper认为你想让当前child的left改变的值，left = child.getLeft() + dx
         * @param dx 本次child水平方向移动的距离（正、负）
         * @return 表示你想让child的left变成的值
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //left - dx = child.getLeft()表示水平方向不移动
//                left = left - dx + dx / 3;//child移动距离为手指移动的dx的1/3
            if (left < 0) {//限制左边界
                left = 0;
            }
            if (left > getMeasuredWidth() - child.getMeasuredWidth()) {//限制右边界
                left = getMeasuredWidth() - child.getMeasuredWidth();
            }
            return left;//能在水平方向移动
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (top < 0) {//限制左边界
                top = 0;
            }
            if (top > getMeasuredHeight() - child.getMeasuredHeight()) {//限制右边界
                top = getMeasuredHeight() - child.getMeasuredHeight();
            }
            return top;//垂直方向移动
        }

        /**
         * 当child位置改变时执行，一般用来做其他子View的跟随移动
         * @param changedView 位置发生改变的child view
         * @param left child最新的left
         * @param top child最新的top
         * @param dx 本次水平移动的距离
         * @param dy 本次垂直移动的距离
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == blueView) {  //如果当前点击移动的是blueView
                //blueView移动的时候，要让redView跟着移动
                redView.layout(redView.getLeft() + dx, redView.getTop() + dy,
                        redView.getRight() + dx, redView.getBottom() + dy);
            } else if (changedView == redView) {
                blueView.layout(blueView.getLeft() + dx, blueView.getTop() + dy,
                        blueView.getRight() + dx, blueView.getBottom() + dy);
            }

            //1.计算view移动百分比
            float fraction = changedView.getLeft() * 1f / (getMeasuredWidth() - changedView.getMeasuredWidth());
//            Log.e("tag", "fraction" + fraction);
            //2.执行一系列伴随动画
            executeAnim(fraction);
        }

        /**
         * 手指抬起执行
         * @param releasedChild 当前手指抬起离开的view
         * @param xvel x方向移动的速度 正：向右 负：向左
         * @param yvel y方向的速度 正：向下 负：向上
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int centerLeft = getMeasuredWidth() / 2 - releasedChild.getMeasuredWidth() / 2;
            if (releasedChild.getLeft() < centerLeft) {
                //在左半边，应该向左缓慢移动
                viewDragHelper.smoothSlideViewTo(releasedChild, 0, releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);

            } else {
                //在右半边，应该向右缓慢移动
                viewDragHelper.smoothSlideViewTo(releasedChild, getMeasuredWidth() -
                        releasedChild.getMeasuredWidth(), releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);
            }
        }
    }

    /**
     * 执行伴随动画
     * @param fraction 0-1
     */
    private void executeAnim(float fraction) {
        //缩放
//        redView.setScaleX(1 + 0.5f * fraction);
//        redView.setScaleY(1 + 0.5f * fraction);
        //旋转
        redView.setRotationX(360 * fraction);//围绕X轴
//        redView.setRotationY(720 * fraction);
//        redView.setRotation(720 * fraction);
        blueView.setRotationX(360 * fraction);
        //平移
//        redView.setTranslationX(80 * fraction);
//        redView.setTranslationY();
//        redView.setTranslationZ();
        //透明度
//        redView.setAlpha(1 - fraction);

        //设置过度颜色的渐变
        redView.setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction, Color.RED, Color.GREEN));//红 -> 绿
        setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction, Color.YELLOW, Color.RED));

    }


    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(DragLayout.this);
        }
    }
}
