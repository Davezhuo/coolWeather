package comzhuoyue.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection=null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setDoOutput(true);
					connection.setDoInput(true);
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(5000);
					connection.setReadTimeout(5000);
					int code = connection.getResponseCode();
					if(code==HttpURLConnection.HTTP_OK){
						InputStream is = connection.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(is));
						String line=null;
						StringBuilder sb = new StringBuilder();
						while((line=reader.readLine())!=null){
							sb.append(line);
						}
						String response = sb.toString();
						if(listener!=null){
							listener.OnFinished(response);
						}
					}
				} catch (Exception e) {
					if(listener!=null){
						listener.onError(e);
					}
				}finally{
					if(connection!=null){
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}
