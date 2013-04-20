package com.forever.weibo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.Toast;

import com.forever.user.User;
import com.forever.weibo.LoginActivity.UserCurrent;
import com.forever.xx.Userxx;
import com.weibo.forever.R;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.UsersAPI;
import com.weibo.sdk.android.keep.AccessTokenKeeper;
import com.weibo.sdk.android.net.RequestListener;


/**
 * OAuth类
 * @author NightwisH
 *
 */
public class OAuthActivity extends Activity {
	
	private static final String TAG = "OAuthActivity";
	private Weibo mWeibo;
    private static final String CONSUMER_KEY = "3848744159";// 
    private static final String REDIRECT_URL = "http://www.baidu.com";
    
    public static Oauth2AccessToken accessToken;
    private String uid;
    private Dialog dialog = null;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);
        mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
        
     
        View dialogView = View.inflate(this, R.layout.oauth_dialog, null);
        dialog = new Dialog(this,R.style.oauth_style) ;
        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        
        
        Button oauth_start = (Button) dialogView.findViewById(R.id.oauth_start);
        oauth_start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	CookieSyncManager.createInstance(getApplicationContext());
            	CookieManager.getInstance().removeAllCookie();
            	 mWeibo.authorize(OAuthActivity.this, new AuthDialogListener());
            	 
                 }
             });
         }

   
      class AuthDialogListener implements WeiboAuthListener {

         

		@Override
          public void onComplete(Bundle values) {
              String token = values.getString("access_token");
              String expires_in = values.getString("expires_in");
              uid = values.getString("uid");
              OAuthActivity.accessToken = new Oauth2AccessToken(token, expires_in);
              Log.i("OAuthActivity", "OAuthActivity.accessToken:" + OAuthActivity.accessToken);
              
              
              if (OAuthActivity.accessToken.isSessionValid()) {
                 
              Log.i("OAuthActivity", "uid:   " + uid);
                  try {
                      Class sso = Class
                              .forName("com.weibo.sdk.android.api.WeiboAPI");// 如果支持weiboapi的话，显示api功能演示入口按钮
                  } catch (ClassNotFoundException e) {
                      // e.printStackTrace();
                      Log.i("OAuthActivity", "com.weibo.sdk.android.api.WeiboAPI not found");

                  }
                  
                  Toast.makeText(OAuthActivity.this, "认证成功", Toast.LENGTH_SHORT)
                          .show();
                  
                  dialog.dismiss();
                  Log.i("OAuthActivity", "OAuthActivity.accessToken:" + OAuthActivity.accessToken);
                  
                  UsersAPI users = new UsersAPI(OAuthActivity.accessToken) ;
             	 
             	 Log.i("OAuthActivity", "UIDSTR:" + uid);
             	 users.show(Long.parseLong(uid), new RequestListener() {
             		 
             		 
 					
 					@Override
 					public void onIOException(IOException arg0) {
 						// TODO Auto-generated method stub
 						 Log.i("OAuthActivity", "onIOException" + arg0.getMessage());
 					}
 					
 					@Override
 					public void onError(WeiboException arg0) {
 						// TODO Auto-generated method stub
 						 Log.i("OAuthActivity", "onError" + arg0.getMessage());
 					}
 					
 					@Override
 					public void onComplete(String arg0) {
 						// TODO Auto-generated method stub
 						Log.i(TAG, "arg0:  "+arg0);
 						try {
 							Log.i("usexx", "启动intent前………………");
							JSONObject user_json = new JSONObject(arg0);
							User user = new User();
							Log.i("OAuthActivity", "(user_json.getLong(id)" + user_json.getLong("id"));
							//user.setId(user_json.getLong("id"));
							user.setUser_id(user_json.getString("idstr"));
							user.setUser_name(user_json.getString("screen_name"));
							user.setDescription(user_json.getString("description"));
							user.setStatuses_count(user_json.getInt("statuses_count"));
							user.setFollowers_count(user_json.getInt("followers_count"));
							user.setFriends_count(user_json.getInt("friends_count"));
							
							UserCurrent.currentUser = user;
							 AccessTokenKeeper.keepAccessToken(OAuthActivity.this,user_json.getString("idstr"),
				                      accessToken);
							
							
							//根据json返回的url得到用户头像
							URL url = new URL(user_json.getString("profile_image_url"));
							HttpURLConnection httpconn =(HttpURLConnection) url.openConnection();
							InputStream is = httpconn.getInputStream();
							Drawable user_head = Drawable.createFromStream(is, "");
							
							user.setUser_head(user_head);
							Userxx userxx = new Userxx(OAuthActivity.this); 
							userxx.insertUser(user);
							
							Log.i("usexx", "启动intent前………………");
							
							Intent intent = new Intent(OAuthActivity.this,HomeActivity.class);
							startActivity(intent);
							OAuthActivity.this.finish();
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
						
							e.printStackTrace();
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
 					}
 				});
              }
          }

          @Override
          public void onError(WeiboDialogError e) {
              Toast.makeText(getApplicationContext(),
                      "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
          }

          @Override
          public void onCancel() {
              Toast.makeText(getApplicationContext(), "Auth cancel",
                      Toast.LENGTH_LONG).show();
          }

          @Override
          public void onWeiboException(WeiboException e) {
              Toast.makeText(getApplicationContext(),
                      "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
                      .show();
          }

      }
}
