package com.example.poer_weather;



import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView city;
	private TextView date;
	private TextView weather;
	private TextView high;
	private String city1;
	private String date1;
	private String weather1;
	private String high1;
   private String str;
   private HttpURLConnection urlConnection = null;
    private static String URL= "http://weather.51wnl.com/weatherinfo/GetMoreWeather?cityCode=101230601&weatherType=0";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forecast);
		city=(TextView)findViewById(R.id.city);
		date=(TextView)findViewById(R.id.date);
		weather=(TextView)findViewById(R.id.weather);
		high=(TextView)findViewById(R.id.high);
		Thread t= new Thread(){
			public void run(){
				try{
					HttpGet request=new HttpGet(URL);
					HttpResponse response=new DefaultHttpClient().execute(request);
					if(response.getStatusLine().getStatusCode()==200){
						str=EntityUtils.toString(response.getEntity());
						Log.i("msg",str);
						JSONObject jsonObject=new JSONObject(str);
						JSONObject weatherinfo=jsonObject.getJSONObject("weatherinfo");
						city1=weatherinfo.getString("city");
						date1=weatherinfo.getString("date_y");
						weather1=weatherinfo.getString("weather1");
						high1=weatherinfo.getString("temp1");
					}
					}catch(Exception e){
						e.printStackTrace();
					}
			}
		};t.start();
	}

	public void onclick(View view) {
		city.setText(city1);
		date.setText(date1);
		high.setText(high1);
		weather.setText(weather1);
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
