package com.forever.weibo;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.forever.user.User;
import com.forever.weibo.LoginActivity.UserCurrent;
import com.forever.xx.Userxx;
import com.weibo.forever.R;


public class DetailActivity extends Activity {
	
	private final String USER_ID = UserCurrent.currentUser.getUser_id();
	
	
	private ImageView iv_head;
	private TextView tv_name,tv_description,tv_statuses,tv_followers,tv_friends;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
	
		
		iv_head =(ImageView) findViewById(R.id.detail_head);
		tv_name =(TextView) findViewById(R.id.detail_name);
		tv_description = (TextView) findViewById(R.id.detail_description);
		
		tv_statuses = (TextView) findViewById(R.id.detail_statuses);
		tv_followers = (TextView) findViewById(R.id.detail_followers);
		tv_friends = (TextView) findViewById(R.id.detail_friends);
		
		Userxx userxx = new Userxx(DetailActivity.this);
		
		User user = userxx.findUserByUserID(USER_ID);
		
		Log.i("user", "USER_ID" + USER_ID);
		
		iv_head.setBackground(user.getUser_head());
		tv_name.setText(user.getUser_name());
		tv_description.setText(user.getDescription());
		tv_statuses.setText(String.valueOf(user.getStatuses_count()));
		tv_followers.setText(String.valueOf(user.getFollowers_count()));
		tv_friends.setText(String.valueOf(user.getFriends_count()));
		
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
	}
}
