package com.assassin.webviewlibrary.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.WebView;

/**
 * @author: Shay-Patrick-Cormac
 * @date: 2017/12/20 14:16
 * @version: 1.0
 * @description: 加载webiview，降低灵敏度
 */

public class SwipeRefreshWebViewLayout extends SwipeRefreshLayout 
{
    private float mInitialDownY;
    private int mTouchSlop;

    public SwipeRefreshWebViewLayout(Context context) {
        this(context, null);
    }

    public SwipeRefreshWebViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //降低敏感度
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop()+2000;
        //设置可以滑动的解决方案
        setOnChildScrollUpCallback(new OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child)
            {
                if (child instanceof WebView)
                {
                    int y = child.getScrollY();
                    Log.i("webview的滚动距离为", y+"");
                    return y>0;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) 
    {

        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitialDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float yDiff = ev.getY() - mInitialDownY;
                if (yDiff < mTouchSlop) {
                    return false;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }


    /**
     * @return 返回灵敏度数值
     */
    public int getTouchSlop() {
        return mTouchSlop;
    }

    /**
     * 设置下拉灵敏度 
     *
     * @param mTouchSlop dip值 
     */
    public void setTouchSlop(int mTouchSlop) {
        this.mTouchSlop = mTouchSlop;
    }

    //解决WebView一直未getScrollY==0
    @Override
    public boolean canScrollVertically(int direction) 
    {
        View mTargetView = getChildAt(0);
        if (mTargetView!=null && mTargetView instanceof WebView)
        {
            return direction < 0 ? mTargetView.getScrollY() > 0 : mTargetView.getScrollY() < mTargetView.getMeasuredHeight();
        } else {
            return ViewCompat.canScrollVertically(mTargetView, direction);
        }
  
    }
}
