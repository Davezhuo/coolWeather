package comzhuoyue.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

	public static final String TABLE_PROVINCE = "create table province (id integer primary key autoincrement,province_name varchar(10) not null,province_code varchar(10) not null)";
	
	public static final String TABLE_CITY = "create table city (id integer primary key autoincrement,city_name varchar(10) not null,city_code varchar(10) not null,province_id integer not null)";
	
	public static final String TABLE_COUNTY = "create table county(id integer primary key autoincrement,county_name varchar(10) not null,county_code varchar(10) not null,city_id integer not null)";
	public CoolWeatherOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_PROVINCE);//创建省
		db.execSQL(TABLE_CITY);//创建市
		db.execSQL(TABLE_COUNTY);//创建县
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}


}
