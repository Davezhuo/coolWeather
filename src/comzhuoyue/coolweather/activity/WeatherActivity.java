package comzhuoyue.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import comzhuoyue.coolweather.R;
import comzhuoyue.coolweather.util.HttpCallbackListener;
import comzhuoyue.coolweather.util.HttpUtil;
import comzhuoyue.coolweather.util.Utilty;

public class WeatherActivity extends Activity implements OnClickListener {
	private LinearLayout weatherInfo;
	private TextView cityName;
	private TextView temp1;
	private TextView temp2;
	private TextView publicText;
	private TextView weatherDesp;
	private TextView currentDate;
	private Button switchCity,refresh;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_layout);
		// 初始化各控件
		weatherInfo = (LinearLayout) findViewById(R.id.weather_info);
		cityName = (TextView) findViewById(R.id.city_name);
		temp1 = (TextView) findViewById(R.id.temp1);
		temp2 = (TextView) findViewById(R.id.temp2);
		publicText = (TextView) findViewById(R.id.publish_text);

		weatherDesp = (TextView) findViewById(R.id.weather_desp);
		currentDate = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		refresh = (Button) findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refresh.setOnClickListener(this);
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

	/**
	 * 显示天气信息
	 */
	private void showWeather() {
		SharedPreferences sdf = getSharedPreferences("weather", MODE_PRIVATE);
		cityName.setText(sdf.getString("cityName", ""));
		temp1.setText(sdf.getString("temp1", ""));
		temp2.setText(sdf.getString("temp2", ""));
		weatherDesp.setText(sdf.getString("weatherDesp", ""));
		publicText.setText("今天" + sdf.getString("publicTime", "")+"发布");
		currentDate.setText(sdf.getString("current_date", ""));
		cityName.setVisibility(View.VISIBLE);
		weatherInfo.setVisibility(View.VISIBLE);
	}

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

	/**
	 * 按钮被点击的时候调用
	 */
		@Override
		public void onClick(View v) {
			int id = v.getId();
			SharedPreferences preferences = getSharedPreferences("weather", MODE_PRIVATE);
			switch (id) {
			case R.id.switch_city:
				Intent intent = new Intent(this,ChooseAreaActivity.class);
				intent.putExtra("from_weather_activity", true);
//				Editor editor = preferences.edit();
//				editor.putBoolean("citySelected", false);
//				editor.commit();
				startActivity(intent);
				finish();
				break;
			case R.id.refresh_weather://刷新天气
				publicText.setText("同步中...");
				
				//重新从服务器上查找天气信息
				String weatherCode = preferences.getString("cityId", "");
				//根据城市code找到县的code
				if(!TextUtils.isEmpty(weatherCode)){
					queryWeatherInfo(weatherCode);
				}
				break;
			default:
				break;
			}
		}

}
