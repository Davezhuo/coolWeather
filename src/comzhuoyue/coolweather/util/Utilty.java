package comzhuoyue.coolweather.util;

import android.text.TextUtils;

import comzhuoyue.coolweather.db.CoolWeatherDB;
import comzhuoyue.coolweather.model.City;
import comzhuoyue.coolweather.model.County;
import comzhuoyue.coolweather.model.Province;

public class Utilty {
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
