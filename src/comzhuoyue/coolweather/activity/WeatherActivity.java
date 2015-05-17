package comzhuoyue.coolweather.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import comzhuoyue.coolweather.R;
import comzhuoyue.coolweather.util.HttpCallbackListener;
import comzhuoyue.coolweather.util.HttpUtil;
import comzhuoyue.coolweather.util.Utilty;

public class WeatherActivity extends Activity {
	private LinearLayout weatherInfo;
	private TextView cityName;
	private TextView temp1;
	private TextView temp2;
	private TextView publicText;
	private TextView weatherDesp;
	private TextView currentDate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_layout);
		//初始化各控件
		weatherInfo = (LinearLayout) findViewById(R.id.weather_info);
		cityName = (TextView) findViewById(R.id.city_name);
		temp1 = (TextView) findViewById(R.id.temp1);
		temp2= (TextView) findViewById(R.id.temp2);
		publicText = (TextView) findViewById(R.id.publish_text);
		
		weatherDesp = (TextView) findViewById(R.id.weather_desp);
		currentDate = (TextView) findViewById(R.id.current_date);
		
		String countyCode = getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(countyCode)){
			publicText.setText("同步中...");
			weatherInfo.setVisibility(View.INVISIBLE);
			cityName.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}else{
			//直接去shared文件中取
			showWeather();
		}
		
		
		
	}
	/**
	 * 显示天气信息
	 */
	private void showWeather() {
		SharedPreferences  sdf =getSharedPreferences("weather", MODE_APPEND);
		cityName.setText(sdf.getString("cityName", ""));
		temp1.setText(sdf.getString("temp1", ""));
		temp2.setText(sdf.getString("temp2", ""));
		weatherDesp.setText(sdf.getString("weatherDesp", ""));
		publicText.setText("今天"+sdf.getString("publicTime"+"发布", ""));
		currentDate.setText(sdf.getString("current_date", ""));
		cityName.setVisibility(View.VISIBLE);
		weatherInfo.setVisibility(View.VISIBLE);
	}
	/**
	 * 通过城市代号查询天气
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address,"countyCode");
	}
	
	/**
	 * 从服务器上获取天气信息
	 * @param address
	 * @param string
	 */
	private void queryFromServer(final String address, final String type) {
		
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						publicText.setText("同步失败");
					}
				});
			}
			
			@Override
			public void OnFinished(String response) {
				if("countyCode".equals(type)){
					//解析json字符串
					Utilty.HandlerWeatherData(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							showWeather();
						}
					});
				}
				
				
				
			}
		});
	}
}
