package com.cylee.dragcontentviewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by cylee on 15/7/11.
 */
public class DragContentViewPager extends ViewPager {
  private View mDragView;
  private int[] mCalInt;
  public DragContentViewPager(Context context) {
    super(context);
  }

  public DragContentViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (mDragView == null) {
      return super.onInterceptTouchEvent(ev);
    }
    float f1 = ev.getX();
    float f2 = ev.getY();
    if (!dragInset((int) f1, (int) f2)) {
      return false;
    }
    return super.onInterceptTouchEvent(ev);
  }

  @Override public boolean onTouchEvent(MotionEvent ev) {
    if (mDragView == null) {
      return super.onTouchEvent(ev);
    }
    float f1 = ev.getX();
    float f2 = ev.getY();
    if (!dragInset((int) f1, (int) f2)) {
      return false;
    }
    return super.onTouchEvent(ev);
  }

  private boolean dragInset(int x, int y) {
    if (mDragView == null) return false;
    if (mCalInt == null) {
      mCalInt = new int[2];
    }
    getLocationOnScreen(mCalInt);
    int px = mCalInt[0] + x;
    int py = mCalInt[1] + y;

    mDragView.getLocationOnScreen(mCalInt);
    int mDragViewX = mCalInt[0];
    int mDragViewY = mCalInt[1];

    return px > mDragViewX
        && px < mDragViewX + mDragView.getWidth()
        && py > mDragViewY
        && py < mDragViewY + mDragView.getHeight();
  }

  public void setDragView(View dragView) {
    mDragView = dragView;
  }

}
