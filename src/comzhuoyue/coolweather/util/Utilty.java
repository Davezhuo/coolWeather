package comzhuoyue.coolweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import comzhuoyue.coolweather.db.CoolWeatherDB;
import comzhuoyue.coolweather.model.City;
import comzhuoyue.coolweather.model.County;
import comzhuoyue.coolweather.model.Province;

public class Utilty {
	/**
	 * 解析返回的天气json字符串
	 */
	public static void HandlerWeatherData(Context context ,String response){
		//获取天气信息
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String cityId = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publicTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context,cityName,cityId,temp1,temp2,weatherDesp,publicTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	
	
	/**
	 * 将天气信息保存到sharedPreferences文件中
	 * @param context
	 * @param cityName
	 * @param cityId
	 * @param temp1
	 * @param temp2
	 * @param weatherDesp
	 * @param publicTime
	 */
	private static void saveWeatherInfo(Context context,String cityName, String cityId,
			String temp1, String temp2, String weatherDesp, String publicTime) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日",Locale.CHINA);
		SharedPreferences preferences =context.getSharedPreferences("weather", Context.MODE_PRIVATE);
		
		Editor editor = preferences.edit();
		editor.putBoolean("citySelected", true);
		editor.putString("cityName", cityName);
		editor.putString("cityId", cityId);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weatherDesp", weatherDesp);
		editor.putString("publicTime", publicTime);
		editor.putString("current_date",sdf.format(new Date()));
		editor.commit();
	}




	/**
	 * 解析获取到的省份数据
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public synchronized  static boolean handlerProvinceData(CoolWeatherDB coolWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			//01|北京,02|上海,03|天津
			String[] allProvinces = response.split(",");
			if(allProvinces!=null &&allProvinces.length>0){
				for (int i = 0; i < allProvinces.length; i++) {
					String provinces = allProvinces[i];
					String[] array = provinces.split("\\|");
					Province province =new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
			
		}
		return false;
	}
	/**
	 * 解析获取到的市级数据
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public static boolean handlerCityData(CoolWeatherDB coolWeatherDB,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			
			String[] allCitys = response.split(",");
			if(allCitys!=null &&allCitys.length>0){
				for (int i = 0; i < allCitys.length; i++) {
					String citys = allCitys[i];
					String[] array = citys.split("\\|");
					City city =new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
			
		}
		return false;
	}
	/**
	 * 解析获取到的县级数据
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public static boolean handlerCountyData(CoolWeatherDB coolWeatherDB,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			//01|北京,02|上海,03|天津
			String[] allCounty = response.split(",");
			if(allCounty!=null &&allCounty.length>0){
				for (int i = 0; i < allCounty.length; i++) {
					String countys = allCounty[i];
					String[] array = countys.split("\\|");
					County county =new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
}
