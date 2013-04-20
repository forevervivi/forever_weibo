package com.forever.weibo;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.forever.customui.MyListView;
import com.forever.customui.MyListView.OnRefreshListener;
import com.forever.util.AsyncImageLoader;
import com.forever.util.AsyncImageLoader.ImageCallback;
import com.forever.util.Tools;
import com.forever.weibo.LoginActivity.UserCurrent;
import com.weibo.forever.R;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.keep.AccessTokenKeeper;
import com.weibo.sdk.android.net.RequestListener;

/**
 * @author NightwisH
 * 
 */
public class WeiboActivity extends Activity {

	private Handler handler;
	private MyListView listView;

	private MyAdapter myAdapter;
	boolean refresh = false;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weibo);
		
		dialog = new ProgressDialog(this);
		dialog.setTitle("正在获得数据……");
		//dialog.setIndeterminate(false);
		dialog.show();

		listView = (MyListView) findViewById(R.id.weibo_listview);


		handler = new Handler();

		Oauth2AccessToken o2at = AccessTokenKeeper
				.readAccessToken(WeiboActivity.this,UserCurrent.currentUser.getUser_id());

		final StatusesAPI statuses = new StatusesAPI(o2at);
		statuses.friendsTimeline(0l, 0l, 8, 1, false, WeiboAPI.FEATURE.ALL,
				false, new MyRequestListener());
		statuses.friendsTimeline(0l, 0l, 8, 1, false, WeiboAPI.FEATURE.ALL,
				false, new MyRequestListener());

		listView.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {

						statuses.friendsTimeline(0l, 0l, 8, 1, false,
								WeiboAPI.FEATURE.ALL, false,
								new MyRequestListener());
					//	refresh = true;
						myAdapter.notifyDataSetChanged();
						listView.onRefreshComplete();
					}

				}.execute();
			}
		});
	}

	class MyRequestListener implements RequestListener {

		@Override
		public void onComplete(String arg0) {
			// TODO Auto-generated method stub


		/*	if (refresh == true) {
				creat_at_times.clear();
				texts.clear();
				text_images.clear();
				retweeted_status_texts.clear();
				user_heads.clear();
				user_names.clear();
				reposts.clear();
				comments.clear();
				refresh = false;
			}*/

			dialog.dismiss();

				refresh(arg0);
				
				

			
/*			try {
				JSONObject weibo_json = new JSONObject(arg0);
				JSONArray weibo_array = weibo_json.getJSONArray("statuses");
				for (int i = 0; i < weibo_array.length(); i++) {
					JSONObject weibo = weibo_array.getJSONObject(i);
					
					creat_at_times.add(weibo.getString("created_at"));
					texts.add(weibo.getString("text"));
					user_heads.add(new JSONObject(weibo.getString("user"))
					.getString("profile_image_url"));
					user_names.add(new JSONObject(weibo.getString("user"))
					.getString("name"));
					reposts.add(String.valueOf(weibo.getInt("reposts_count")));
					comments.add(String.valueOf(weibo.getInt("comments_count")));
					
					// text_images.add(weibo.getString("thumbnail_pic")== null?
					// "blank" : weibo.getString("thumbnail_pic") );
					try {
						text_images.add(weibo.getString("thumbnail_pic"));
					} catch (Exception e) {
						text_images.add("BLANK");
					}
					
					
					try {
						retweeted_status_texts.add( weibo.getJSONObject("retweeted_status").getString("text"));
					} catch (Exception e) {
						retweeted_status_texts.add("BLANK");
					}
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					listView.setAdapter(myAdapter);
				}
			});
*/
			
			/*Log.i("WeiboActivity", "Times :"
					+ creat_at_times.toString().substring(0, 50));
			Log.i("WeiboActivity", "Texts :"
					+ texts.toString().substring(0, 50));
			Log.i("WeiboActivity", "TextImage :"
					+ text_images.toString());
			Log.i("WeiboActivity", "Head_URL :"
					+ user_heads.toString());
			Log.i("WeiboActivity",
					"Names :" + user_names.toString().substring(0, 50));*/
			 
		}

		private void refresh(String arg0) {
			JSONObject weibo_json;
			try {
				weibo_json = new JSONObject(arg0);
				JSONArray weibo_array = weibo_json.getJSONArray("statuses");
				myAdapter = new MyAdapter(getApplicationContext(), weibo_array);
				
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						listView.setAdapter(myAdapter);
					}
				});
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onError(WeiboException arg0) {
			// TODO Auto-generated method stub
			Log.i("WeiboActivity", "onError :" + arg0.getMessage());
		}

		@Override
		public void onIOException(IOException arg0) {
			// TODO Auto-generated method stub

		}

	}

	class MyAdapter extends BaseAdapter {

		private ViewHolder holder;
		private Context mContext;
		private JSONArray mJsonArray;
		
		public MyAdapter(Context context,JSONArray jsonArray) {
			mContext = context;
			mJsonArray = jsonArray;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mJsonArray.length();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mJsonArray.opt(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			holder = null;

			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.weibo_item, null);

				holder = new ViewHolder();
				holder.image_head = (ImageView) convertView
						.findViewById(R.id.weibo_item_headimage);

				holder.tv_name = (TextView) convertView
						.findViewById(R.id.weibo_item_name);
				holder.tv_text = (TextView) convertView
						.findViewById(R.id.weibo_item_text);

				holder.image_textImage = (ImageView) convertView
						.findViewById(R.id.weibo_item_textImage);
				
				holder.tv_retweeted_status_texts = (TextView) convertView
						.findViewById(R.id.weibo_item_retweeted_status_texts);

				holder.tv_time = (TextView) convertView
						.findViewById(R.id.weibo_item_time);
				holder.tv_repost = (TextView) convertView
						.findViewById(R.id.weibo_item_repost);
				holder.tv_comment = (TextView) convertView
						.findViewById(R.id.weibo_item_comment);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			try {
				holder.tv_time.setText(Tools
						.formatDate(((JSONObject) mJsonArray.get(position))
								.getString("created_at")));
				holder.tv_name.setText(new JSONObject(((JSONObject) mJsonArray
						.get(position)).getString("user")).getString("name"));
				holder.tv_text.setText(((JSONObject) mJsonArray.get(position))
						.getString("text"));
				holder.tv_repost.setText(String
						.valueOf(((JSONObject) mJsonArray.get(position))
								.getInt("reposts_count")));
				holder.tv_comment.setText(String
						.valueOf(((JSONObject) mJsonArray.get(position))
								.getInt("comments_count")));
				//微博原文
				if (((JSONObject) mJsonArray.get(position))
						.has("retweeted_status")) {

					holder.tv_retweeted_status_texts
							.setText(((JSONObject) mJsonArray.get(position))
									.getJSONObject("retweeted_status")
									.getJSONObject("user").getString("name")
									+ ":"
									+ ((JSONObject) mJsonArray.get(position))
											.getJSONObject("retweeted_status")
											.getString("text"));

					
				} else {
					// holder.tv_retweeted_status_texts.setVisibility(View.GONE);
					LinearLayout layout = (LinearLayout) convertView
							.findViewById(R.id.weibo_item_ll_retweeted_status);
					layout.setVisibility(View.GONE);
				}
				
				//头像图片
				Drawable head_image = AsyncImageLoader.loadDrawable(
						(new JSONObject(((JSONObject) mJsonArray
								.get(position)).getString("user"))
								.getString("profile_image_url")),
						holder.image_head, position, new ImageCallback() {
							@Override
							public void imageSet(Drawable drawable,
									ImageView iv) {
								iv.setImageDrawable(drawable);
							}
						});
				if (head_image != null) {
					holder.image_head.setImageDrawable(head_image);
				}
				
				//内容中图片
				if(((JSONObject) mJsonArray.get(position))
						.has("thumbnail_pic")){
					Drawable image_text = AsyncImageLoader.loadDrawable(
							((JSONObject) mJsonArray
									.get(position)).getString("thumbnail_pic")
									,
							holder.image_textImage, position, new ImageCallback() {
								@Override
								public void imageSet(Drawable drawable,
										ImageView iv) {
									iv.setImageDrawable(drawable);
								}
							});
					if (image_text != null) {
						holder.image_textImage.setImageDrawable(image_text);
					}
					
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			
			Log.i("Position:", "Position:" + String.valueOf(position));

			return convertView;
		}

	}

	public static class ViewHolder {
		public ImageView image_head, image_textImage;
		public TextView tv_name, tv_text, tv_time;
		public TextView tv_repost, tv_comment;
		public TextView tv_retweeted_status_texts;
	}

}
