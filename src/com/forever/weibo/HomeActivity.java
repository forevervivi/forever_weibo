package com.forever.weibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.weibo.forever.R;

public class HomeActivity extends Activity {

	private View view01, view02, view03;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		initView();
		initListen();
	}

	private void initView() {
		view01 = findViewById(R.id.home_ll_01);
		view02 = findViewById(R.id.home_ll_02);
		view03 = findViewById(R.id.home_ll_03);
	}

	private void initListen() {
		view01.setOnClickListener(new MyOnClickListener());
		view02.setOnClickListener(new MyOnClickListener());
		view03.setOnClickListener(new MyOnClickListener());
	}

	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.home_ll_01:
				Intent intent1 = new Intent(getApplicationContext(),
						DetailActivity.class);
				HomeActivity.this.startActivity(intent1);
				break;
			case R.id.home_ll_02:
				Intent intent2 = new Intent(getApplicationContext(),
						WeiboActivity.class);
				HomeActivity.this.startActivity(intent2);
				break;
			case R.id.home_ll_03:
				Intent intent3 = new Intent(getApplicationContext(),
						WriteWeiboActivity.class);
				HomeActivity.this.startActivity(intent3);
				break;
			default:
				break;
			}
		}

	}
}
