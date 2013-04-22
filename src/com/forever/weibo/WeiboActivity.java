package com.forever.weibo;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.forever.app.App;
import com.forever.customui.MyListView;
import com.forever.customui.MyListView.OnRefreshListener;
import com.forever.util.AsyncImageLoader;
import com.forever.util.AsyncImageLoader.ImageCallback;
import com.forever.util.NetworkUtils;
import com.forever.util.Tools;
import com.forever.weibo.LoginActivity.UserCurrent;
import com.weibo.forever.R;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.api.WeiboAPI.COMMENTS_TYPE;
import com.weibo.sdk.android.keep.AccessTokenKeeper;
import com.weibo.sdk.android.net.RequestListener;

/**
 * 因为使用了自定义的MyListView,position0是下拉的viewhead，所以item各项从1开始。
 * @author NightwisH
 * 
 */
public class WeiboActivity extends Activity {

	private Handler handler;
	private MyListView listView;
	//private ListView listView;
	private Button bt_pop_r, bt_pop_c;
	
	private String textImage;

	private MyAdapter myAdapter;
	private Dialog dialog;
	private JSONArray weibo_array;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weibo);
		
		

		dialog = new ProgressDialog(this);
		dialog.setTitle("正在获得数据……");
		// dialog.setIndeterminate(false);
		dialog.show();

		listView = (MyListView) findViewById(R.id.weibo_listview);
			//listView = (ListView) findViewById(R.id.weibo_listview);
		
		/*
		 * 写在这里监听没有作用…… bt_pop_r = (Button) popview.findViewById(R.id.pop_bt_r);
		 * bt_pop_c = (Button) popview.findViewById(R.id.pop_bt_c);
		 * 
		 * bt_pop_r.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub Log.i("Weibo","点击了转发按键"); } });
		 * 
		 * bt_pop_c.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub Log.i("Weibo","点击了评论按键"); } });
		 */

		handler = new Handler();

		Oauth2AccessToken o2at = AccessTokenKeeper.readAccessToken(
				WeiboActivity.this, UserCurrent.currentUser.getUser_id());

		final StatusesAPI statuses = new StatusesAPI(o2at);
		statuses.friendsTimeline(0l, 0l, 20, 1, false, WeiboAPI.FEATURE.ALL,
				false, new MyRequestListener());

		listView.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							Thread.sleep(1000);
							statuses.friendsTimeline(0l, 0l, 20, 1, false,
									WeiboAPI.FEATURE.ALL, false,
									new MyRequestListener());
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {

						

						myAdapter.notifyDataSetChanged();
						listView.onRefreshComplete();
					}

				}.execute();
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				Log.i("repost_text","click position" + position);
				LayoutInflater li = getLayoutInflater();
				View pop = li.inflate(R.layout.popwindow, null);
				final PopupWindow pw = new PopupWindow(pop, 250, 100);
				pw.setBackgroundDrawable(new BitmapDrawable());
				pw.setOutsideTouchable(true);
				int[] location = new int[2];
				view.getLocationOnScreen(location);
				pw.showAtLocation(view, Gravity.NO_GRAVITY,
						location[0] + view.getWidth() / 2 - pw.getWidth() / 2,
						location[1] + view.getHeight() / 2 - pw.getHeight() / 2);
				pw.setFocusable(true);

				bt_pop_r = (Button) pop.findViewById(R.id.pop_bt_r);
				bt_pop_c = (Button) pop.findViewById(R.id.pop_bt_c);

				bt_pop_r.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Log.i("Weibo", "点击了转发按键");
						Log.i("repost_text","position" + position);
						
						long repost_id;
						try {
							//得到要转发的微博ID
							repost_id = ((JSONObject)weibo_array.get(position-1)).getLong("mid");
							String text = ((JSONObject)weibo_array.get(position-1)).getString("text");
							Log.i("repost_id", String.valueOf(repost_id));
							Log.i("repost_text", text);
							Log.i("weibo_array", weibo_array.get(position-1).toString());
							statuses.repost(repost_id, null, COMMENTS_TYPE.NONE, new RequestListener() {
								
								@Override
								public void onIOException(IOException arg0) {
									// TODO Auto-generated method stub
									Toast.makeText(WeiboActivity.this, "转发失败~", Toast.LENGTH_SHORT).show();
								}
								
								@Override
								public void onError(WeiboException arg0) {
									// TODO Auto-generated method stub
									Toast.makeText(WeiboActivity.this, "转发失败~", Toast.LENGTH_SHORT).show();
								}
								
								@Override
								public void onComplete(String arg0) {
									// TODO Auto-generated method stub
									Looper.prepare();
									Toast.makeText(WeiboActivity.this, "转发成功~", Toast.LENGTH_SHORT).show();
									Looper.loop();
								}
							});
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						pw.dismiss();
						
						
					}
				});
				bt_pop_c.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Log.i("Weibo", "点击了评论按键");
						long repost_id;
						try {
							repost_id = ((JSONObject)weibo_array.get(position-1)).getLong("id");
							String text = ((JSONObject)weibo_array.get(position-1)).getString("text");
							Intent intent = new Intent(WeiboActivity.this,WriteWeiboActivity.class);

							Bundle bd = new Bundle();
							bd.putLong("repost_id", repost_id);
							intent.putExtras(bd);
							WeiboActivity.this.startActivity(intent);
							pw.dismiss();
							App.repostFlag = true;
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
					}
				});
				return true;
			}
		});

	}

	class MyRequestListener implements RequestListener {

		@Override
		public void onComplete(String arg0) {
			// TODO Auto-generated method stub

			dialog.dismiss();

			refresh(arg0);

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

	private void refresh(String arg0) {
		JSONObject weibo_json;
		try {
			weibo_json = new JSONObject(arg0);
			weibo_array = weibo_json.getJSONArray("statuses");
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

	class MyAdapter extends BaseAdapter {

		private ViewHolder holder;
		private Context mContext;
		private JSONArray mJsonArray;

		public MyAdapter(Context context, JSONArray jsonArray) {
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
/*
				holder.tv_retweeted_status_texts = (TextView) convertView
						.findViewById(R.id.weibo_item_retweeted_status_texts);*/

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
			TextView tv_retweeted_status_texts = (TextView) convertView
					.findViewById(R.id.weibo_item_retweeted_status_texts);
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
				// 微博原文
				if (((JSONObject) mJsonArray.get(position))
						.has("retweeted_status")) {
					Log.i("position:", "Position^retweeted_status:" + position+"\n"
							+ ((JSONObject) mJsonArray.get(position))
							.getString("text"));
				/*	holder.tv_retweeted_status_texts*/
					tv_retweeted_status_texts.setText(((JSONObject) mJsonArray.get(position))
									.getJSONObject("retweeted_status")
									.getJSONObject("user").getString("name")
									+ ":"
									+ ((JSONObject) mJsonArray.get(position))
											.getJSONObject("retweeted_status")
											.getString("text"));
					LinearLayout layout = (LinearLayout) convertView
							.findViewById(R.id.weibo_item_ll_retweeted_status);
					layout.setVisibility(View.VISIBLE);

				} else {
					// holder.tv_retweeted_status_texts.setVisibility(View.GONE);
					LinearLayout layout = (LinearLayout) convertView
							.findViewById(R.id.weibo_item_ll_retweeted_status);
					layout.setVisibility(View.GONE);
				}

				// 头像图片
				Drawable head_image = AsyncImageLoader.loadDrawable(
						0, (new JSONObject(((JSONObject) mJsonArray.get(position))
								.getString("user"))
								.getString("profile_image_url")),
						holder.image_head, position, new ImageCallback() {
							@Override
							public void imageSet(Drawable drawable, ImageView iv) {
								iv.setImageDrawable(drawable);
							}
						});
				
				if (head_image != null) {
					holder.image_head.setImageDrawable(head_image);
				}
				

				// 内容中图片
				if(NetworkUtils.getNetworkState(WeiboActivity.this)==NetworkUtils.WIFI){
					textImage = "bmiddle_pic";
				}else if(NetworkUtils.getNetworkState(WeiboActivity.this)==NetworkUtils.MOBILE) {
					textImage = "thumbnail_pic";
				}
					
				if (((JSONObject) mJsonArray.get(position))
						.has(textImage)) {//thumbnail_pic bmiddle_pic
					Log.i("position:", "Position^thumbnail_pic:" + position+ "\n"
							+ ((JSONObject) mJsonArray.get(position))
							.getString("text"));
					Drawable image_text = AsyncImageLoader.loadDrawable(
							1, (((JSONObject) mJsonArray.get(position))
									.getString(textImage)),
							holder.image_textImage, position, new ImageCallback() {
								@Override
								public void imageSet(Drawable drawable, ImageView iv) {
									iv.setImageDrawable(drawable);
								}
							});
				/*	Drawable image_text = AsyncImageLoader.loadDrawable(1,
							(((JSONObject) mJsonArray.get(position))
									.getString("bmiddle_pic")),
							holder.image_textImage, position,
							new ImageCallback() {
								@Override
								public void imageSet(Drawable drawable,
										ImageView iv) {
									iv.setImageDrawable(drawable);
								}
							});*/
					if (image_text != null) {
						holder.image_textImage.setImageDrawable(image_text);
					}
					
					
					holder.image_textImage.setVisibility(View.VISIBLE);
					

				}else {
					// holder.tv_retweeted_status_texts.setVisibility(View.GONE);
					holder.image_textImage.setVisibility(View.GONE);
				}
			} catch (Exception e) {
				Log.i("Exception", "Try Exception:" + e.getMessage());
			}

			Log.i("position:", "Position:" + String.valueOf(position));

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
