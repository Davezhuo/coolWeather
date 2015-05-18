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
<<<<<<< HEAD

=======
	
>>>>>>> 63e37dca693b44eeffe836205cd1666379825c30
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_layout);
<<<<<<< HEAD
		// 初始化各控件
		weatherInfo = (LinearLayout) findViewById(R.id.weather_info);
		cityName = (TextView) findViewById(R.id.city_name);
		temp1 = (TextView) findViewById(R.id.temp1);
		temp2 = (TextView) findViewById(R.id.temp2);
		publicText = (TextView) findViewById(R.id.publish_text);

		weatherDesp = (TextView) findViewById(R.id.weather_desp);
		currentDate = (TextView) findViewById(R.id.current_date);

		String countyCode = getIntent().getStringExtra("countyCode");
		// 先去share文件中查找，如果不存在，则联网查找
		// 通过县代号查找到该县的天气代号
		// http://www.weather.com.cn/data/cityinfo/101190404.html
		if (!TextUtils.isEmpty(countyCode)) {
			publicText.setText("同步中...");
			weatherInfo.setVisibility(View.INVISIBLE);
			cityName.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);// 查找天气代号
		} else {
			// 直接去shared文件中取
			showWeather();
		}

	}

=======
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
>>>>>>> 63e37dca693b44eeffe836205cd1666379825c30
	/**
	 * 显示天气信息
	 */
	private void showWeather() {
<<<<<<< HEAD
		SharedPreferences sdf = getSharedPreferences("weather", MODE_PRIVATE);
=======
		SharedPreferences  sdf =getSharedPreferences("weather", MODE_APPEND);
>>>>>>> 63e37dca693b44eeffe836205cd1666379825c30
		cityName.setText(sdf.getString("cityName", ""));
		temp1.setText(sdf.getString("temp1", ""));
		temp2.setText(sdf.getString("temp2", ""));
		weatherDesp.setText(sdf.getString("weatherDesp", ""));
<<<<<<< HEAD
		publicText.setText("今天" + sdf.getString("publicTime", "")+"发布");
=======
		publicText.setText("今天"+sdf.getString("publicTime"+"发布", ""));
>>>>>>> 63e37dca693b44eeffe836205cd1666379825c30
		currentDate.setText(sdf.getString("current_date", ""));
		cityName.setVisibility(View.VISIBLE);
		weatherInfo.setVisibility(View.VISIBLE);
	}
<<<<<<< HEAD

	/**
	 * 通过城市代号查询天气代号
	 * 
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	/**
	 * 从服务器上获取天气信息
	 * 
=======
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
>>>>>>> 63e37dca693b44eeffe836205cd1666379825c30
	 * @param address
	 * @param string
	 */
	private void queryFromServer(final String address, final String type) {
<<<<<<< HEAD

		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

=======
		
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
>>>>>>> 63e37dca693b44eeffe836205cd1666379825c30
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						publicText.setText("同步失败");
					}
				});
			}
<<<<<<< HEAD

			@Override
			public void OnFinished(String response) {
				if ("countyCode".equals(type)) {
					// 这里返回的是 县的代号|天气代号
					if (!TextUtils.isEmpty(response)) {
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];// 天气代号
							// 根据天气代号查找天气信息
							queryWeatherInfo(weatherCode);
						}

					}
				} else if ("weatherCode".equals(type)) {
					//这时返回的是天气的json字符串
					Utilty.HandlerWeatherData(WeatherActivity.this, response);//将天气信息保存到了本地文件中
=======
			
			@Override
			public void OnFinished(String response) {
				if("countyCode".equals(type)){
					//解析json字符串
					Utilty.HandlerWeatherData(WeatherActivity.this, response);
>>>>>>> 63e37dca693b44eeffe836205cd1666379825c30
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							showWeather();
						}
					});
<<<<<<< HEAD
				
				}

			}

		});
	}

	/**
	 * 根据天气代号查找天气信息
	 * 
	 * @param weatherCode
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}

=======
				}
				
				
				
			}
		});
	}
>>>>>>> 63e37dca693b44eeffe836205cd1666379825c30
}
