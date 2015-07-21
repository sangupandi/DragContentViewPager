package com.cylee.dragcontentviewpager;


/**
 *Copyright (C) 2015 cylee
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software
 *distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and
 *limitations under the License.
 */

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    View bottomView = findViewById(R.id.bottom_view);
    final DragContentViewPager pager = (DragContentViewPager)findViewById(R.id.pager);
    bottomView.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        Log.d("cylee", "bottom view touch");
        return true;
      }
    });
    final InnerAdapter adapter = new InnerAdapter();
    pager.setAdapter(adapter);
    pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      }

      @Override public void onPageSelected(int position) {
        View v = adapter.getView(position);
        if (v != null) {
          View dragView = v.findViewById(R.id.vi_text);
          pager.setDragView(dragView);
        }
      }
      @Override public void onPageScrollStateChanged(int state) {
      }
    });
  }

  class InnerAdapter extends PagerAdapter {
    private SparseArray<View> mViews = new SparseArray<>();
    @Override public int getCount() {
      return 5;
    }

    @Override public boolean isViewFromObject(View view, Object object) {
      return view == object;
    }

    @Override public Object instantiateItem(ViewGroup container, int position) {
      final View v = View.inflate(MainActivity.this, R.layout.view_item, null);
      TextView view = (TextView) v.findViewById(R.id.vi_text);
      view.setText("I am position " + position);
      view.setBackgroundColor(0x88000000);
      container.addView(v);
      mViews.put(position, v);
      return v;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
      mViews.put(position, null);
      View view = (View) object;
      container.removeView(view);
    }

    public View getView(int position) {
      return mViews.get(position);
    }
  }
}
