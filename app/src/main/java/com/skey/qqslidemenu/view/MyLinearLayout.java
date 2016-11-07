package com.skey.qqslidemenu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 当SlideMenu打开的时候，拦截并消费触摸事件
 *
 * @author ALion on 2016/10/31 18:27
 */

public class MyLinearLayout extends LinearLayout {

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private SlideMenu slideMenu;

    public void setSlideMenu(SlideMenu slideMenu) {
        this.slideMenu = slideMenu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (slideMenu != null && slideMenu.getCurrentState() == SlideMenu.DragState.Open) {
            //如果slideMenu打开，应该拦截、消费事件
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (slideMenu != null && slideMenu.getCurrentState() == SlideMenu.DragState.Open) {
            //如果slideMenu打开，应该拦截、消费事件
            if (event.getAction() == MotionEvent.ACTION_UP) {
                //抬起则应该关闭slideMenu
                slideMenu.close();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }
}
