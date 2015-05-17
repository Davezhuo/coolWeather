package comzhuoyue.coolweather.util;


public interface HttpCallbackListener {
	void OnFinished(String response);
	void onError(Exception e);
}
