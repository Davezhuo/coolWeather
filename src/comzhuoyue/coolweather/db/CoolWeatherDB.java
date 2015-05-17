package comzhuoyue.coolweather.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import comzhuoyue.coolweather.model.City;
import comzhuoyue.coolweather.model.County;
import comzhuoyue.coolweather.model.Province;

public class CoolWeatherDB {
	/**
	 * 数据库名
	 */
	public static final String DB_NAME = "cool_weather";
	/**
	 * 数据库版本
	 */
	public static final int VERSION = 1;

	private SQLiteDatabase db;

	private static CoolWeatherDB coolWeatherDB;

	/**
	 * 将构造方法私有化
	 * 
	 * @param context
	 */
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * 获取CoolWeatherDB的实例 这里采用同步代码块的方式
	 */
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	/**
	 * 将Province实例
	 * 
	 * @param province存储到数据库
	 */
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("province", null, values);
		}
	}

	/**
	 * 将City存储到数据库中
	 * 
	 * @param city
	 */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("city", null, values);
		}
	}

	/**
	 * 将County存储到数据库中
	 * @param county
	 */
	public void saveCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("county", null, values);
		}
	}
	
	
	/**
	 * 获取所有的省份信息
	 * @return
	 */
	public List<Province> loadProvince(){
		List<Province> provinceList = new ArrayList<Province>();
		Cursor cursor = db.query("province", null, null, null, null, null, null);
		while(cursor.moveToNext()){
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String provinceName = cursor.getString(cursor.getColumnIndex("province_name"));
			String provinceCode = cursor.getString(cursor.getColumnIndex("province_code"));
			Province province = new Province(id, provinceName, provinceCode);
			provinceList.add(province);
		}
		return provinceList;
	}
	/**
	 * 获取所有城市的信息
	 * @return
	 */
	public List<City> loadCity(int provinceId ){
		List<City> cityList = new ArrayList<City>();
		Cursor cursor = db.query("city", null, "province_id=?", new String[]{provinceId+""}, null, null, null);
		while(cursor.moveToNext()){
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String cityName = cursor.getString(cursor.getColumnIndex("city_name"));
			String cityCode = cursor.getString(cursor.getColumnIndex("city_code"));
			City city = new City(id, cityName, cityCode, provinceId);
			cityList.add(city);
		}
		return cityList;
	}
	
	/**
	 * 查询所有县的信息
	 * @return
	 */
	
	public List<County> loadCounty(int cityId){
		List<County> countyList = new ArrayList<County>();
		Cursor cursor = db.query("county", null, "city_id=?", new String[]{cityId+""}, null, null, null);
		while(cursor.moveToNext()){
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String countyName = cursor.getString(cursor.getColumnIndex("county_name"));
			String countyCode = cursor.getString(cursor.getColumnIndex("county_code"));
			County county = new County(id, countyName, countyCode, cityId);
			countyList.add(county);
		}
		return countyList;
	}

}
