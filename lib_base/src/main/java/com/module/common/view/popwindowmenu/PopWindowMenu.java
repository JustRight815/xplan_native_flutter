package com.module.common.view.popwindowmenu;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import androidx.core.widget.PopupWindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.module.common.R;
import java.util.ArrayList;
import java.util.List;

/**
 * 仿qq,微信，头条右上角弹出菜单
 */
public class PopWindowMenu {
    private Activity mContext;
    private PopupWindow mPopupWindow;
    private RecyclerView mRecyclerView;
    private View content;

    private PWMenuAdapter mAdapter;
    private List<MenuItem> menuItemList;

    private static final int DEFAULT_HEIGHT = 480;
    private int popHeight = RecyclerView.LayoutParams.WRAP_CONTENT;
    private int popWidth = RecyclerView.LayoutParams.WRAP_CONTENT;
    private boolean showIcon = true;
    private boolean dimBackground = false;
    private boolean needAnimationStyle = true;

    private static final int DEFAULT_ANIM_STYLE = R.style.base_pop_window_menu_style;
    private int animationStyle;

    private float alpha = 0.75f;

    public PopWindowMenu(Activity context) {
        this.mContext = context;
        init();
    }

    private void init() {
        content = LayoutInflater.from(mContext).inflate(R.layout.trm_popup_menu, null);
        mRecyclerView = (RecyclerView) content.findViewById(R.id.trm_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        //使用此种方法解决了在布局中写view实现分割线时 在9.0上宽度是自适应的，但是在5.0上宽度默认全屏的问题
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
//        dividerItemDecoration.setDrawable(mContext.getResources().getDrawable(R.drawable.shape_popubwinow_line));
        mRecyclerView.addItemDecoration(new ViewItemDecoration(mContext,16, 1));
        menuItemList = new ArrayList<>();
        mAdapter = new PWMenuAdapter(mContext, this, menuItemList, showIcon);
    }

    private PopupWindow getPopupWindow(){
        mPopupWindow = new PopupWindow(mContext);
        mPopupWindow.setContentView(content);
        mPopupWindow.setHeight(popHeight);
        mPopupWindow.setWidth(popWidth);
        if (needAnimationStyle){
            mPopupWindow.setAnimationStyle(animationStyle <= 0 ? DEFAULT_ANIM_STYLE : animationStyle);
        }

        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (dimBackground) {
                    setBackgroundAlpha(alpha, 1f, 300);
                }
            }
        });

        mAdapter.setData(menuItemList);
        mAdapter.setShowIcon(showIcon);
        mRecyclerView.setAdapter(mAdapter);
        return mPopupWindow;
    }

    public PopWindowMenu setHeight(int height){
        if (height <= 0 && height != RecyclerView.LayoutParams.MATCH_PARENT
                && height != RecyclerView.LayoutParams.WRAP_CONTENT){
            this.popHeight = DEFAULT_HEIGHT;
        }else {
            this.popHeight = height;
        }
        return this;
    }

    public PopWindowMenu setWidth(int width){
        if (width <= 0 && width != RecyclerView.LayoutParams.MATCH_PARENT){
            this.popWidth = RecyclerView.LayoutParams.WRAP_CONTENT;
        }else {
            this.popWidth = width;
        }
        return this;
    }

    /**
     * 是否显示菜单图标
     * @param show
     * @return
     */
    public PopWindowMenu showIcon(boolean show){
        this.showIcon = show;
        return this;
    }

    /**
     * 添加单个菜单
     * @param item
     * @return
     */
    public PopWindowMenu addMenuItem(MenuItem item){
        menuItemList.add(item);
        return this;
    }

    /**
     * 添加多个菜单
     * @param list
     * @return
     */
    public PopWindowMenu addMenuList(List<MenuItem> list){
        menuItemList.addAll(list);
        return this;
    }

    /**
     * 是否让背景变暗
     * @param b
     * @return
     */
    public PopWindowMenu dimBackground(boolean b){
        this.dimBackground = b;
        return this;
    }

    /**
     * 否是需要动画
     * @param need
     * @return
     */
    public PopWindowMenu needAnimationStyle(boolean need){
        this.needAnimationStyle = need;
        return this;
    }

    /**
     * 设置动画
     * @param style
     * @return
     */
    public PopWindowMenu setAnimationStyle(int style){
        this.animationStyle = style;
        return this;
    }

    public PopWindowMenu setOnMenuItemClickListener(OnMenuItemClickListener listener){
        mAdapter.setOnMenuItemClickListener(listener);
        return this;
    }

    public PopWindowMenu showAsDropDown(View anchor){
        showAsDropDown(anchor, 0, 0);
        return this;
    }


    public PopWindowMenu showAsDropDown(View anchor, int xoff, int yoff){
        if (mPopupWindow == null){
            getPopupWindow();
        }

        if (!mPopupWindow.isShowing()) {
            mPopupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
            int measuredWidth = mPopupWindow.getContentView().getMeasuredWidth();
            PopupWindowCompat.showAsDropDown(mPopupWindow,anchor,
                    -measuredWidth  +  xoff,
                    yoff,
                    Gravity.START);
            if (dimBackground){
                setBackgroundAlpha(1f, alpha, 240);
            }
        }
        return this;
    }

    private void setBackgroundAlpha(float from, float to, int duration) {
        final WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        ValueAnimator animator = ValueAnimator.ofFloat(from, to);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lp.alpha = (float) animation.getAnimatedValue();
                mContext.getWindow().setAttributes(lp);
            }
        });
        animator.start();
    }

    public void dismiss(){
        if (mPopupWindow != null && mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }
    }

    public interface OnMenuItemClickListener{
        void onMenuItemClick(int position);
    }
}
