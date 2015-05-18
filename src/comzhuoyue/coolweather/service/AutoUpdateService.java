package comzhuoyue.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;

import comzhuoyue.coolweather.receiver.AutoUpdateReceiver;
import comzhuoyue.coolweather.util.HttpCallbackListener;
import comzhuoyue.coolweather.util.HttpUtil;
import comzhuoyue.coolweather.util.Utilty;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				updateWeather();
				
			}
		}).start();
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anHour = 8*60*60*1000;//8小时的毫秒数
		long triggerAtMillis = SystemClock.elapsedRealtime()+anHour;
		
		Intent inten = new Intent(this,AutoUpdateReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, inten, 0);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME, triggerAtMillis, pendingIntent);
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 更新天气信息
	 */
	private void updateWeather() {
		SharedPreferences  preferences = getSharedPreferences("weather", MODE_PRIVATE);
		String weatherCode = preferences.getString("cityId", "");
		String address= "http://www.weather.com.cn/data/cityinfo/" +
weatherCode + ".html";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
			
			@Override
			public void OnFinished(String response) {
				Utilty.HandlerWeatherData(AutoUpdateService.this, response);
			}
		});
	}
	
	
}
