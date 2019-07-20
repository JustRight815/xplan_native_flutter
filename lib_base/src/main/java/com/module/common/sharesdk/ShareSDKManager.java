package com.module.common.sharesdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.module.common.R;

/**
 * 自定义分享界面dialog
 * 分享：微信好友、朋友圈、QQ好友、QQ空间、新浪微博、复制链接、系统分享
 * 登录：微信登录、微博登录、QQ登录
 * @author zh
 */
public class ShareSDKManager {
	
	/**
	 * 显示友盟自定义分享界面
	 * @param context
	 */
	public static void show(final Activity context) {
        View view = LayoutInflater.from(context).inflate(R.layout.share_dialog, null);
        //用style控制默认dialog边距问题
        final Dialog dialog = new Dialog(context, R.style.share_dialog);
        dialog.setContentView(view);
        dialog.show();
        // 设置dialog的显示位置，一定要在 show()之后设置
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        
        // 监听
        OnClickListener listener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                int id = v.getId();
                if(id == R.id.ly_share_weichat_circle) {

                    // 分享到朋友圈
                }else if(id == R.id.ly_share_weichat) {
                    // 分享到微信
                }else if(id == R.id.ly_share_qq_zone) {
                    // 分享到qq空间
                }else if(id == R.id.ly_share_qq) {
                    // 分享到qq
                    //比如分享到QQ，其他平台则只需要更换平台类名，例如Wechat.NAME则是微信
                }else if(id == R.id.ly_share_sina_weibo) {
                    // 分享到qq
                    //比如分享到QQ，其他平台则只需要更换平台类名，例如Wechat.NAME则是微信
                }else if(id == R.id.ly_share_copy_link) {
                    // 复制链接
                    copyTextToBoard(context,"XPlan");
                }else if(id == R.id.ly_share_more_option) {
                    // 更多 调用系统分享
                    systemShareShow(context);
                }else if(id == R.id.tv_cancel_share) {
                    // 取消分享
                    dialog.dismiss();
                }
                dialog.dismiss();
            }
        };
        
        view.findViewById(R.id.ly_share_qq).setOnClickListener(listener);
        view.findViewById(R.id.ly_share_copy_link).setOnClickListener(listener);
        view.findViewById(R.id.ly_share_more_option).setOnClickListener(listener);
        view.findViewById(R.id.ly_share_qq_zone).setOnClickListener(listener);
        view.findViewById(R.id.ly_share_weichat).setOnClickListener(listener);
        view.findViewById(R.id.ly_share_weichat_circle).setOnClickListener(listener);
        view.findViewById(R.id.tv_cancel_share).setOnClickListener(listener);
        view.findViewById(R.id.ly_share_sina_weibo).setOnClickListener(listener);
    }
	
	 /**
     * 复制到剪贴板
     * @param string
     */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static void copyTextToBoard(Activity context,String string) {
    	if (TextUtils.isEmpty(string)){
    		 return;
    	}
    	ClipboardManager clip = (ClipboardManager) context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
    	clip.setText(string);
    	Toast.makeText(context.getApplicationContext(), "已复制至剪贴板", Toast.LENGTH_SHORT).show();
    }
	
	/**
     * 调用系统的应用分享
     * 
     * @param context
     * @param title
     * @param url
     */
    public static void showSystemShareDiglog(Activity context,
	    String title, String url) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);
		intent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
		context.startActivity(Intent.createChooser(intent, "选择分享"));
    }


    /**
     * 调用系统分享
     */
    public static void systemShareShow(Activity activity) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, "X Plan客户端");
		sendIntent.setType("text/plain");
        activity.startActivity(sendIntent);
    }

}
