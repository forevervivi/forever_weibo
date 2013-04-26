package com.forever.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class Tools {

	public static String formatDate(String date_str) {
		Date date = new Date(date_str);
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
		return formatter.format(date);
	}

	/**
	 * 通过url 获得对应的Drawable资源
	 * 
	 * @param flag
	 *            是否压缩标记
	 * @param url
	 *            图片地址
	 * @return
	 */
	public static Drawable getDrawableFromUrl(int flag, String url) {
		try {
			URLConnection urlc = new URL(url).openConnection();
			URLConnection urlc2 = new URL(url).openConnection();
			if (flag == 0) {
				return Drawable
						.createFromStream(urlc.getInputStream(), "image");
			} else if (flag == 1) {
				Bitmap bitmap = Tools
						.decodeSampledBitmapFromStream(urlc.getInputStream(),
								urlc2.getInputStream(), 200, 200);
				BitmapDrawable bd = new BitmapDrawable(bitmap);
				return bd;
			} else if (flag == 2) {
				Bitmap bitmap = Tools
						.decodeSampledBitmapFromStream(urlc.getInputStream(),
								urlc2.getInputStream(), 800, 800);
				BitmapDrawable bd = new BitmapDrawable(bitmap);
				return bd;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap decodeSampledBitmapFromStream(InputStream is,
			InputStream is2, int reqWidth, int reqHeight) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		Log.i("Bitmap", "options.inSampleSize: " + options.inSampleSize
				+ "\noptions.outWidth:" + options.outWidth
				+ " options.outHeight:" + options.outHeight);
		return BitmapFactory.decodeStream(is2, null, options);

	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		Log.i("Bitmap", "reqWidth" + reqWidth + "reqHeight" + reqHeight);

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			Log.i("Bitmap", "heightRatio" + heightRatio + "widthRatio"
					+ widthRatio);

			inSampleSize = heightRatio <= widthRatio ? heightRatio : widthRatio;
			Log.i("Bitmap", "inSampleSize:" + inSampleSize);
		}
		return inSampleSize;
	}
}
