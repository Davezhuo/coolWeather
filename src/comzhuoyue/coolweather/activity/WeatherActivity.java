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
		// ��ʼ�����ؼ�
		weatherInfo = (LinearLayout) findViewById(R.id.weather_info);
		cityName = (TextView) findViewById(R.id.city_name);
		temp1 = (TextView) findViewById(R.id.temp1);
		temp2 = (TextView) findViewById(R.id.temp2);
		publicText = (TextView) findViewById(R.id.publish_text);

		weatherDesp = (TextView) findViewById(R.id.weather_desp);
		currentDate = (TextView) findViewById(R.id.current_date);

		String countyCode = getIntent().getStringExtra("countyCode");
		// ��ȥshare�ļ��в��ң���������ڣ�����������
		// ͨ���ش��Ų��ҵ����ص���������
		// http://www.weather.com.cn/data/cityinfo/101190404.html
		if (!TextUtils.isEmpty(countyCode)) {
			publicText.setText("ͬ����...");
			weatherInfo.setVisibility(View.INVISIBLE);
			cityName.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);// ������������
		} else {
			// ֱ��ȥshared�ļ���ȡ
			showWeather();
		}

	}

	/**
	 * ��ʾ������Ϣ
	 */
	private void showWeather() {
		SharedPreferences sdf = getSharedPreferences("weather", MODE_PRIVATE);
		cityName.setText(sdf.getString("cityName", ""));
		temp1.setText(sdf.getString("temp1", ""));
		temp2.setText(sdf.getString("temp2", ""));
		weatherDesp.setText(sdf.getString("weatherDesp", ""));
		publicText.setText("����" + sdf.getString("publicTime", "")+"����");
		currentDate.setText(sdf.getString("current_date", ""));
		cityName.setVisibility(View.VISIBLE);
		weatherInfo.setVisibility(View.VISIBLE);
	}

	/**
	 * ͨ�����д��Ų�ѯ��������
	 * 
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	/**
	 * �ӷ������ϻ�ȡ������Ϣ
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
						publicText.setText("ͬ��ʧ��");
					}
				});
			}

			@Override
			public void OnFinished(String response) {
				if ("countyCode".equals(type)) {
					// ���ﷵ�ص��� �صĴ���|��������
					if (!TextUtils.isEmpty(response)) {
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];// ��������
							// �����������Ų���������Ϣ
							queryWeatherInfo(weatherCode);
						}

					}
				} else if ("weatherCode".equals(type)) {
					//��ʱ���ص���������json�ַ���
					Utilty.HandlerWeatherData(WeatherActivity.this, response);//��������Ϣ���浽�˱����ļ���
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
	 * �����������Ų���������Ϣ
	 * 
	 * @param weatherCode
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}

}
