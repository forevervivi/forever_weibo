package com.forever.xx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.forever.db.DBHelper;
import com.forever.db.DBInfo;
import com.forever.user.User;

/**
 * 用户信息操作类
 * @author NightwisH
 *
 */
public class Userxx {
	//User表需要查询的字段
	String[] columns = { DBInfo.Table._ID, DBInfo.Table.USER_ID,
            DBInfo.Table.USER_NAME, DBInfo.Table.TOKEN,
            DBInfo.Table.TOKEN_SECRET, DBInfo.Table.DESCRIPTION,
            DBInfo.Table.USER_HEAD };
	
	DBHelper dbHelper = null;
	SQLiteDatabase db = null;
	
	public Userxx(Context context) {
		dbHelper = new DBHelper(context);
	}

	public long insertUser(User user) {
		db = dbHelper.getReadableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(DBInfo.Table.USER_NAME, user.getUser_name());
		cv.put(DBInfo.Table.USER_ID, user.getUser_id());
		cv.put(DBInfo.Table.TOKEN, user.getToken());
		cv.put(DBInfo.Table.TOKEN_SECRET, user.getToken_secret());
		cv.put(DBInfo.Table.DESCRIPTION, user.getDescription());
		
		 // 将图片类型的数据进行存储的时候，需要进行转换才能存储到BLOB类型中
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 为了实现数据存储，需要将数据类型进行转换
        BitmapDrawable newHead = (BitmapDrawable) user.getUser_head();
        // 将数据进行压缩成PNG编码数据，存储质量100%
        newHead.getBitmap().compress(CompressFormat.PNG, 100, os);
        // 存储图片类型数据
        cv.put(DBInfo.Table.USER_HEAD, os.toByteArray());
		
        
		long rowId = db.insert(DBInfo.Table.USER_TABLE, DBInfo.Table.USER_NAME, cv);
		db.close();
		
		return rowId;
	}
	
	public int update(User user) {
		return 1;
	}
	
	public int deleteUser(String user_id) {
		return 1;
	}
	
	public User findUserByUserID(String user_id) {
		return null;
	}
	
	public List<User> findAllUsers() {
		db = dbHelper.getReadableDatabase();
		
		List<User> list_users=null;
		User user = null;
		
		Cursor cursor = db.query(DBInfo.Table.USER_TABLE, columns, null, null, null, null, null, null);
		if(cursor != null && cursor.getCount()>0) {
			list_users = new ArrayList<User>(cursor.getCount());
			while(cursor.moveToNext()) {
				 user = new User();
				 
				user.setId(cursor.getLong(cursor
						.getColumnIndex(DBInfo.Table._ID)));
				user.setUser_id(cursor.getString(cursor
						.getColumnIndex(DBInfo.Table.USER_ID)));
				user.setUser_name(cursor.getString(cursor
						.getColumnIndex(DBInfo.Table.USER_NAME)));
				user.setToken(cursor.getString(cursor
						.getColumnIndex(DBInfo.Table.TOKEN)));
				user.setToken_secret(cursor.getString(cursor
						.getColumnIndex(DBInfo.Table.TOKEN_SECRET)));
				user.setDescription(cursor.getString(cursor
						.getColumnIndex(DBInfo.Table.DESCRIPTION)));
				
				byte[] byteHead = cursor.getBlob(cursor
						.getColumnIndex(DBInfo.Table.USER_HEAD));

				ByteArrayInputStream is = new ByteArrayInputStream(byteHead);
				Drawable userHead = Drawable.createFromStream(is, "srcName");
				user.setUser_head(userHead);

				list_users.add(user);
			}
		}
		
		 cursor.close();
	     db.close();
		
		return null;
	}
}
