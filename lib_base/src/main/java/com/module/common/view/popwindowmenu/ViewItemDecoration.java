package com.module.common.view.popwindowmenu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.module.common.R;
import com.module.common.utils.ScreenUtil;

public class ViewItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;     //分割线Drawable
        private int mDividerHeight;  //分割线高度
        private int inset;       //分割线缩进值

        /**
         * 使用line_divider中定义好的颜色
         *
         * @param context
         * @param dividerHeight 分割线高度
         */
        public ViewItemDecoration(Context context, int dividerHeight) {
            mDivider = ContextCompat.getDrawable(context, R.drawable.shape_popubwinow_line);
            mDividerHeight = ScreenUtil.dip2px(context,dividerHeight);
        }

        public ViewItemDecoration(Context context, int inset, int dividerHeight) {
            this.inset = ScreenUtil.dip2px(context,inset);
            mDivider = ContextCompat.getDrawable(context, R.drawable.shape_popubwinow_line);
            mDividerHeight = ScreenUtil.dip2px(context,dividerHeight);
        }

        /**
         * @param context
         * @param divider       分割线Drawable
         * @param dividerHeight 分割线高度
         */
        public ViewItemDecoration(Context context, Drawable divider, int dividerHeight) {
            if (divider == null) {
                mDivider = ContextCompat.getDrawable(context, R.drawable.shape_popubwinow_line);
            } else {
                mDivider = divider;
            }
            mDividerHeight = ScreenUtil.dip2px(context,dividerHeight);
        }


        //获取分割线尺寸
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0, 0, 0, mDividerHeight);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            //最后一个item不画分割线
            for (int i = 0; i < childCount - 1; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int top = child.getBottom() + params.bottomMargin;
//            int bottom = top + mDividerHeight;
                int bottom = top + mDivider.getIntrinsicHeight();
                if (inset > 0) {
                    mDivider.setBounds(left + inset, top, right - inset, bottom);
                } else {
                    mDivider.setBounds(left, top, right, bottom);
                }
                mDivider.draw(c);
            }
        }
}
