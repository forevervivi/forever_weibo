package com.forever.weibo;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.forever.user.User;
import com.forever.util.Tools;
import com.forever.xx.Userxx;
import com.weibo.forever.R;

/**
 * 程序启动欢迎界面
 * @author NightwisH
 *
 */
public class LoadActivity extends Activity {

	ImageView loadImage;
	Animation animation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_load);

		initView();
		// loadImage动画效果，透明动画。
		animation = new AlphaAnimation(0.1f, 1.0f);
		animation.setDuration(1000);

		loadImage.setAnimation(animation);

		animation.setAnimationListener(new MyAnimationListener());

	}

	private void initView() {
		// TODO Auto-generated method stub
		loadImage = (ImageView) findViewById(R.id.loadImage);

	}

	private class MyAnimationListener implements AnimationListener {

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub

			init();

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

	}

	
	private void init() {
		Tools.checkNetwork(LoadActivity.this);
		Userxx userxx = new Userxx(LoadActivity.this);
		List<User> list_users = userxx.findAllUsers();
		
		/**
		 *判断数据库是否有user信息，如果有就直接跳转到登录界面，没有跳转到授权界面。
		 */
		if(list_users == null || list_users.isEmpty()){
			Toast.makeText(this, "没有用户信息", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(LoadActivity.this,OAuthActivity.class);
	        startActivity(intent);
	        this.finish();
	        
		}else{
			
			Intent intent = new Intent(LoadActivity.this,LoginActivity.class);
			startActivity(intent);
			this.finish();
			
		}
	}

}
