package comzhuoyue.coolweather.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import comzhuoyue.coolweather.R;
import comzhuoyue.coolweather.db.CoolWeatherDB;
import comzhuoyue.coolweather.model.City;
import comzhuoyue.coolweather.model.County;
import comzhuoyue.coolweather.model.Province;
import comzhuoyue.coolweather.util.HttpCallbackListener;
import comzhuoyue.coolweather.util.HttpUtil;
import comzhuoyue.coolweather.util.Utilty;

public class ChooseAreaActivity extends Activity  {
	private TextView title;
	private ListView listView;
	private ArrayAdapter<String> mAdapter;
	private final static int LEVEL_PROVINCE = 0;
	private final static int LEVEL_CITY = 1;
	private final static int LEVEL_COUNTY = 2;
	private List<String> dataList = new ArrayList<String>();
	private ProgressDialog progressDialog;
	private CoolWeatherDB coolWeatherDB;
	private int currentLevel;// 当前选中的级别

	private List<Province> provinceList;// 省列表
	private List<County> countyList;// 省列表
	private Province selectedProvince;// 选中的省
	private City selectedCity;// 选中的市
	private County selectedCounty;// 选中的县
	private List<City> citiesList;
	
	//判断是否从WeatherActivity中跳转
	private boolean isFromWeatherActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences preferences= getSharedPreferences("weather", MODE_PRIVATE);
		if(preferences.getBoolean("citySelected", false)&&!isFromWeatherActivity){
			Intent intent = new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		
		
		title = (TextView) findViewById(R.id.title_text);
		listView = (ListView) findViewById(R.id.listView);

		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);

		listView.setAdapter(mAdapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (LEVEL_PROVINCE == currentLevel) {
					selectedProvince = provinceList.get(position);
					queryCity();
				}else if(LEVEL_CITY==currentLevel){//市级列表被选中，查找县级列表
					selectedCity = citiesList.get(position);
					queryCounties();
				}else if(LEVEL_COUNTY==currentLevel){
					County county = countyList.get(position);
					Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					intent.putExtra("countyCode", county.getCountyCode());
					startActivity(intent);
					finish();
				}
				
			}
		});
		queryProvinces();
	}
	
	/**
	 * 查询所有省的数据
	 */
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvince();
		if(provinceList.size()>0){
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			mAdapter.notifyDataSetChanged();
			listView.setSelection(0);
			title.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFormServer(null, "province");
		}
	}


	/**
	 * 查询所有市的列表，先从数据库中找，找不到，在上网找
	 */
	private void queryCity() {
		citiesList = coolWeatherDB.loadCity(selectedProvince.getId());
		if(citiesList.size()>0){//在数据库中找到了
			dataList.clear();
			for (City city : citiesList) {
				dataList.add(city.getCityName());
			}
			mAdapter.notifyDataSetChanged();//更新ui
			listView.setSelection(0);
			title.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else{//联网查找
			queryFormServer(selectedProvince.getProvinceCode(), "city");
			
		}
	}
	
	

	/**
	 * 查询所有县
	 */
	private void queryCounties() {
		countyList = coolWeatherDB.loadCounty(selectedCity.getId());
		if(countyList.size()>0){
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			mAdapter.notifyDataSetChanged();
			title.setText(selectedCity.getCityName());
			currentLevel =LEVEL_COUNTY;
		}else{
			queryFormServer(selectedCity.getCityCode(), "county");
		}
		
	}

	/**
	 * 查询服务器上的数据
	 * @param code 上一级的代号
	 * 这是查找市的url，0101是省的代号
	 * http://www.weather.com.cn/data/list3/city0101.xml
	 */
	private void queryFormServer(final String code,final String type) {
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			
			@Override
			public void OnFinished(String response) {
				boolean result = false;
				if("province".equals(type)){//如果类型是省级
					result = Utilty.handlerProvinceData(coolWeatherDB, response);
				}else if("city".equals(type)){
					result = Utilty.handlerCityData(coolWeatherDB, response,selectedProvince.getId());
				}else if("county".equals(type)){
					result = Utilty.handlerCountyData(coolWeatherDB, response,selectedCity.getId());
				}
				if(result){
					//通过runUiThread（）方法回调主线程处理逻辑
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeprogressDialog();
							if("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type)){
								queryCity();
							}else if("county".equals(type)){
								queryCounties();
							}
						}
						
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeprogressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
			
		});
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if(progressDialog==null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			//设置点击对话框外面的时候对话框不消失
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	/**
	 * 关闭进度条对话框
	 */
	private void closeprogressDialog() {
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	
	
	/**
	 * 不会back返回键，根据当前的级别来判断，此时应该返回市，省，县列表还是退出
	 */
	@Override
	public void onBackPressed() {
		if(currentLevel==LEVEL_COUNTY){
			queryCity();
		}else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		}else{
			if(isFromWeatherActivity){
				Intent intent = new Intent(this,WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}


}
