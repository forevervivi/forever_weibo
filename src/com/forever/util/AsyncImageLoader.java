package com.forever.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

/**
 * 异步下载图片数据
 * @author hanfei.li
 *
 */
public class AsyncImageLoader {
	/**
	 * 图片数据缓存 key = url ,value = 图片资源对象
	 */
	private static HashMap<String, SoftReference<Drawable>> imageCache;

	static {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
	}

	/**
	 * 异步下载图片
	 * 
	 * @param url
	 *            图片的地址
	 * @param imageView
	 *            需要显示图片的组件
	 * @param callback
	 *            回调接口
	 * @return 图片资源
	 */
	public static Drawable loadDrawable(final int flag,final String url,
			final ImageView imageView,final int position, final ImageCallback callback) {
		// 判断是否已经下载过，如果下载过，直接获得并返回
		if (imageCache.containsKey(url)) {
			SoftReference<Drawable> soft = imageCache.get(url);
			Log.i("ImageCache000",url.toString()+"\nPosition:"+position);
			Drawable dra = soft.get();
			//Drawable dra = imageCache.get(url);
			if (dra != null) {
				return dra;
			}else{
				Log.i("ImageCache111",url.toString()+"\nPosition:"+position);
			}
		}

/*		final Handler handler = new Handler() {
			@Override
			public  synchronized void handleMessage(Message msg) {
				// 图片资源设置操作
				callback.imageSet((Drawable) msg.obj, imageView);
			}
		};*/
		final Handler handler = new Handler() {
			@Override
			public  synchronized void handleMessage(Message msg) {
				// 图片资源设置操作
				if(imageView != null &&!url.equals("")&&imageView.getTag().toString().equals(url)) {
					callback.imageSet((Drawable) msg.obj, imageView);
					Log.i("!!", " handleMessage" + url + "position : "+ position);
				}
				
			}
		};
		// 下载操作
		new Thread() {
			public void run() {
				Drawable drawable = Tools.getDrawableFromUrl(flag,url);
				// 设置缓存，避免重复下载相同的图片资源
				imageCache.put(url, new SoftReference<Drawable>(drawable));
				/*synchronized (handler) {
					
				}*/
				Message msg = handler.obtainMessage(0, drawable);
				handler.sendMessage(msg);
				
				Log.i("ImageCache222",url.toString()+"\nPosition:"+position);
			}
		}.start();
		return null;
	}
	

	/**
	 * 回调接口
	 * 
	 * @author hanfei.li
	 * 
	 */
	public interface ImageCallback {
		/**
		 * 图片资源设置
		 * 
		 * @param drawable
		 * @param iv
		 */
		public void imageSet(Drawable drawable, ImageView iv);

	}

}
