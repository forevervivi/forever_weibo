package com.forever.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import com.weibo.forever.R;

public class Tools {

	/**
	 * 判断网络是否可用
	 */
	public static Boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm == null) {
			Toast.makeText(context, "没有网络", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			NetworkInfo[] netInfos = cm.getAllNetworkInfo();
			if (netInfos != null) {
				for (NetworkInfo netInfo : netInfos) {
					if (netInfo.getState() == NetworkInfo.State.CONNECTED) {
						Toast.makeText(context, "网络可用", Toast.LENGTH_SHORT)
								.show();
						return true;
					}
				}
			}
		}
		return false;
	}

	// 网络不可用时候提示设置网络对话框
	public static void checkNetwork(final Context context) {
		
		if(!isNetworkAvailable(context)) {
		TextView msg;// 对话框显示内容
		msg = new TextView(context);
		msg.setText("当前没有可以使用的网络，请设置网络");
		new AlertDialog.Builder(context).setIcon(R.drawable.not)
				.setTitle("网络状态提示").setView(msg)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(
								Settings.ACTION_WIRELESS_SETTINGS);
						context.startActivity(intent);
						((Activity)context).finish();
					}
				}).create().show();
		}
	}
}
