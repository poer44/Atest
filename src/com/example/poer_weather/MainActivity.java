/*
 * 2017-5-22 22:47
 * github pull test
 */
package com.example.poer_weather;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private TextView city;
	private TextView date;
	private TextView weather;
	private TextView high;
	private EditText edit;
	private String city1;
	private String date1;
	private String weather1;
	private String high1;
	private String str;
	private String ce = "";
	private String cityid;
	private int count;
	private static String api = "http://weather.51wnl.com/weatherinfo/GetMoreWeather?cityCode=";
	private Handler handler = new Handler() {
		@Override
		// 当有消息发送出来的时候就执行Handler的这个方法
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 执行操作
			if(msg.what==0){    //发送的数据如果是0
				Toast.makeText(getApplicationContext(), "城市名错误",
					     Toast.LENGTH_SHORT).show();
			}
			else{
			city.setText(city1);
			date.setText("更新日期" + "\n" + date1);
			high.setText(high1);
			weather.setText(weather1);
			}
		}
	};
	private SQLiteDatabase db1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forecast);
		city = (TextView) findViewById(R.id.city);
		date = (TextView) findViewById(R.id.date);
		weather = (TextView) findViewById(R.id.weather);
		high = (TextView) findViewById(R.id.high);
		edit = (EditText) findViewById(R.id.editText1);
		// 初始化，只需要调用一次
		AssetsDatabaseManager.initManager(getApplication());
		// 获取管理对象，因为数据库需要通过管理对象才能够获取
		AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
		// 通过管理对象获取数据库
		db1 = mg.getDatabase("city.db");
	}

	public void onclick(View view) {
		// 获取用户输入的值
		ce = edit.getText().toString();
		Log.i("ce", ce);
		Thread t = new Thread() {
			public void run() {
				try {
					count = 0;
					// 对数据库进行操作
					Cursor c = db1.rawQuery("select AREAID from t_city where NAMECN ='" + ce + "' ;", new String[0]);
					while (c.moveToNext()) {
						cityid = c.getString(c.getColumnIndex("AREAID"));
						count++;
					}
					String www = api + cityid + "&weatherType=0";
					Log.i("www", www);
					if (count == 0 || cityid == "null") {//没有从数据库找到或输入框为空
						handler.sendEmptyMessage(0);
					} else {
						URL url = new URL(www);
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
							str=null;//重置str
							String line = null;
							InputStream in = conn.getInputStream();
							BufferedReader reader = new BufferedReader(new InputStreamReader(in, HTTP.UTF_8));
								while ((line = reader.readLine()) != null) {
									if (str == null) {
										str = line;
									}
								}
							in.close();
							Log.i("str", str);
							JSONObject jsonObject = new JSONObject(str);
							JSONObject weatherinfo = jsonObject.getJSONObject("weatherinfo");
							city1 = weatherinfo.getString("city");
							date1 = weatherinfo.getString("date_y");
							weather1 = weatherinfo.getString("weather1");
							high1 = weatherinfo.getString("temp1");
							handler.sendEmptyMessage(1);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
