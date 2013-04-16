package com.forever.weibo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.weibo.forever.R;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;


/**
 * OAuth类
 * @author NightwisH
 *
 */
public class OAuthActivity extends Activity {
	private Weibo mWeibo;
    private static final String CONSUMER_KEY = "3848744159";// 替换为开发者的appkey，例如"1646212860";
    private static final String REDIRECT_URL = "http://www.baidu.com";
    public static Oauth2AccessToken accessToken;
    private Dialog dialog = null;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oauth);
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
            	 mWeibo.authorize(OAuthActivity.this, new AuthDialogListener());
                 }
             });
         }

    /**
     * 返回的时候会执行
     */
      @Override
      protected void onNewIntent(Intent intent) {
          super.onNewIntent(intent);
          // 获得用户是授权数据
          
        /*  User u = Tools.getInstance().getUserInfo(user);
          
          Log.i(TAG, "----user2-------" + u.toString());
          Userxx xx = new Userxx(this);
          xx.inserUser(u);
          dialog.dismiss();
          startActivity(new Intent(this, LoginActivity.class));
          finish();*/
      }
   
      class AuthDialogListener implements WeiboAuthListener {

          @Override
          public void onComplete(Bundle values) {
              String token = values.getString("access_token");
              String expires_in = values.getString("expires_in");
              OAuthActivity.accessToken = new Oauth2AccessToken(token, expires_in);
              if (OAuthActivity.accessToken.isSessionValid()) {
                 
              Log.i("OAuthActivity", "取得token" + token);
                  try {
                      Class sso = Class
                              .forName("com.weibo.sdk.android.api.WeiboAPI");// 如果支持weiboapi的话，显示api功能演示入口按钮
                  } catch (ClassNotFoundException e) {
                      // e.printStackTrace();
                      Log.i("OAuthActivity", "com.weibo.sdk.android.api.WeiboAPI not found");

                  }
               /*   AccessTokenKeeper.keepAccessToken(OAuthActivity.this,
                          accessToken);*/
                  Toast.makeText(OAuthActivity.this, "认证成功", Toast.LENGTH_SHORT)
                          .show();
                  
                  dialog.dismiss();
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
