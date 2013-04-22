package com.forever.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import com.weibo.forever.R;

public class Tools {

	public static String formatDate(String date_str) {
		Date date = new Date(date_str);
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
		return formatter.format(date);
	}

	/**
	 * 通过url 获得对应的Drawable资源
	 * 
	 * @param url
	 * @return
	 */
	public static Drawable getDrawableFromUrl(int flag,String url) {
		try {
			URLConnection urls = new URL(url).openConnection();
			if(flag == 0){
				return Drawable.createFromStream(urls.getInputStream(),"image");
			}
			else if(flag == 1) {
				BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
				bitmapOptions.inSampleSize = 4;

				Bitmap bitmap = BitmapFactory.decodeStream(urls.getInputStream(),
						null, bitmapOptions);
				BitmapDrawable bd = new BitmapDrawable(bitmap);
				return bd;
			}
			

		//	
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

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

		if (!isNetworkAvailable(context)) {
			TextView msg;// 对话框显示内容
			msg = new TextView(context);
			msg.setText("当前没有可以使用的网络，请设置网络");
			new AlertDialog.Builder(context)
					.setIcon(R.drawable.not)
					.setTitle("网络状态提示")
					.setView(msg)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(
											Settings.ACTION_WIRELESS_SETTINGS);
									context.startActivity(intent);
									((Activity) context).finish();
								}
							}).create().show();
		}
	}
}
