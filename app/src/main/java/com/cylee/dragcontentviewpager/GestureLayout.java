package com.cylee.dragcontentviewpager;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

/**
 * @author cylee
 */
public class GestureLayout extends FrameLayout {
  private int mInitChildHeight = 100;
  /**
   * 最小速度,每秒移动的dip
   */
  private static final int MIN_FLING_VELOCITY = 400;
  /**
   * 默认的滑动临界值，滑动超过这个值并释放时，则认为滑动完成
   */
  private static final float DEFAULT_SCROLL_THRESHOLD = 0.5f;
  private float mScrollThreshold = DEFAULT_SCROLL_THRESHOLD;
  private View mContentView;
  private ViewDragHelper mViewGuestureHelper;
  private float mScrollPercent;

  private int mTrackingEdge;
  private int mMinFlippingVelocity;
  private int mChildShowHeight;
  private int mMaxTop;
  private int mMinTop;

  public GestureLayout(Context context) {
    super(context);
    init(context, null, 0);
  }

  public GestureLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs, 0);
  }

  public GestureLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context, attrs, defStyle);
  }

  private void init(Context context, AttributeSet attrs, int defStyle) {
    mViewGuestureHelper = ViewDragHelper.create(this, new ViewDragCallback());
    mViewGuestureHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_BOTTOM|ViewDragHelper.EDGE_TOP);
    final float density = getResources().getDisplayMetrics().density;
    final float minVel = MIN_FLING_VELOCITY * density;
    mInitChildHeight = (int)(density * mInitChildHeight);
    mMinFlippingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    mViewGuestureHelper.setMinVelocity(minVel);
    mTrackingEdge = ViewDragHelper.EDGE_BOTTOM;
    mChildShowHeight = mInitChildHeight;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent event) {
    try {
      return mViewGuestureHelper.shouldInterceptTouchEvent(event);
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    try {
       mViewGuestureHelper.processTouchEvent(event);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    mContentView = getChildAt(0);
    int height = getMeasuredHeight();
    mMaxTop = height - mInitChildHeight;
    mMinTop = height - mContentView.getMeasuredHeight();
  }

  @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    if (mViewGuestureHelper.getViewDragState() != ViewDragHelper.STATE_DRAGGING) {
      super.onLayout(changed, left, top, right, bottom);
      mContentView.offsetTopAndBottom(mMaxTop);
    }
  }

  @Override protected void dispatchDraw(Canvas canvas) {
    super.dispatchDraw(canvas);
    if (mViewGuestureHelper.continueSettling(true)) {
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }

  @Override protected void onAttachedToWindow() {
    mScrollPercent = 0;
    super.onAttachedToWindow();
  }

  private class ViewDragCallback extends ViewDragHelper.Callback {
    private boolean mIsScrollOverValid;

    @Override
    public void onEdgeDragStarted(int edgeFlags, int pointerId) {
      super.onEdgeDragStarted(edgeFlags, pointerId);
    }

    @Override
    public boolean tryCaptureView(View view, int i) {
      return mViewGuestureHelper.isEdgeTouched(ViewDragHelper.EDGE_BOTTOM, i) || mViewGuestureHelper.isEdgeTouched(ViewDragHelper.EDGE_TOP, i);
    }

    @Override
    public int getViewHorizontalDragRange(View child) {
      return 0;
    }

    @Override
    public int getViewVerticalDragRange(View child) {
      return child.getMeasuredHeight();
    }

    @Override
    public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
      super.onViewPositionChanged(changedView, left, top, dx, dy);
      if (dy != 0) {
        requestDisallowInterceptTouchEvent(true);
      }
      mScrollPercent = Math.abs((float) (top - mMinTop) / (mMaxTop - mMinTop));
      invalidate();
      if (mScrollPercent < mScrollThreshold && !mIsScrollOverValid) {
        mIsScrollOverValid = true;
      }
    }

    @Override
    public void onViewReleased(View releasedChild, float xvel, float yvel) {
      int left = 0;
      if ((mTrackingEdge & ViewDragHelper.EDGE_BOTTOM) != 0 || (mTrackingEdge & ViewDragHelper.EDGE_TOP) != 0) {
        if (Math.abs(yvel) > mMinFlippingVelocity && Math.abs(yvel) > Math.abs(xvel)) {
          mChildShowHeight = yvel > 0 ? mMaxTop : mMinTop;
        } else {
          mChildShowHeight = mScrollPercent > mScrollThreshold ? mMaxTop : mMinTop;
        }
      }
      mViewGuestureHelper.settleCapturedViewAt(left, mChildShowHeight);
      postInvalidate();
      requestDisallowInterceptTouchEvent(false);
    }

    @Override
    public int clampViewPositionVertical(View child, int top, int dy) {
      int ret = 0;
      if ((mTrackingEdge & ViewDragHelper.EDGE_BOTTOM) != 0 || (mTrackingEdge & ViewDragHelper.EDGE_TOP) != 0) {
        ret = top;
        ret = Math.max(ret, mMinTop);
        ret = Math.min(ret, mMaxTop);
      }
      return ret;
    }

    @Override public int getEdgeBottom(ViewDragHelper helper) {
      return mChildShowHeight;
    }

    @Override public int getEdgeTop(ViewDragHelper helper) {
      return mChildShowHeight;
    }
  }
}
