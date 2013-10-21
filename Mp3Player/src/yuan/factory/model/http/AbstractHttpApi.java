package yuan.factory.model.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import yuan.factory.model.Mp3Info;
import yuan.factory.model.xml.AbstractParse;

import android.os.Bundle;

public abstract class AbstractHttpApi implements HttpApi{
	
	/**服务器地址*/
	public final static String SERVER_BASE_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting?";
	public final static String XML = "xml";
	public final static String METHOD = "method";
	public final static String FORMAT = "format";
	public final static String PAGE_NO = "page_no";
	public final static String PAGE_SIZE = "page_size";
	public final static String QUERY = "query";
	public final static String SONG_ID = "songid";	

	public HttpPost createHttpPost(String serverUrl, List<NameValuePair> nameValuePair) {
		HttpPost httppost = new HttpPost(serverUrl);
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
		}
		catch (UnsupportedEncodingException unsupportedencodingexception) {
			System.out.println("HttpApi解码出错----->unsupportedencodingexception");
		}		
		return httppost;
	}

	public InputStream doHttpPost(Bundle bundle) {
		HttpPost httpRequest = createHttpPost(SERVER_BASE_URL, getNameValuePair(bundle));
		HttpClient httpClient = createHttpClient();
		HttpClientParams.setCookiePolicy(httpClient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);
		HttpResponse response = executeHttpRequest(httpRequest, httpClient);	
		return getInputStream(response);	
	}	
	
	public DefaultHttpClient createHttpClient() {
		SchemeRegistry schemeregistry = new SchemeRegistry();
		schemeregistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeregistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		HttpParams httpparams = createHttpParams(60);		
		HttpClientParams.setRedirecting(httpparams, false);
		return new DefaultHttpClient(new ThreadSafeClientConnManager(httpparams, schemeregistry), httpparams);
	}
	
	private HttpParams createHttpParams(int i) {
		BasicHttpParams basichttpparams = new BasicHttpParams();
		HttpConnectionParams.setStaleCheckingEnabled(basichttpparams, false);
		HttpConnectionParams.setConnectionTimeout(basichttpparams, 60000);
		HttpConnectionParams.setSoTimeout(basichttpparams, i * 1000);
		HttpConnectionParams.setSocketBufferSize(basichttpparams, 8192);
		return basichttpparams;
	}
	
	public HttpResponse executeHttpRequest(HttpPost httpRequest, HttpClient httpClient) {		
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpRequest);
		} catch (ClientProtocolException e) {
			System.out.println("协议异常------->" + e.getMessage());		
		} catch (IOException e) {
			System.out.println("协议异常------->" + e.getMessage());
		}	
		return response;		
	}
	
	private InputStream getInputStream(HttpResponse response) {
		InputStream inputStream = null;
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			try {
				inputStream = response.getEntity().getContent();
			} catch (IllegalStateException e) {
				System.out.println("getInputStream异常------->" + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("getInputStream异常------->" + e.getMessage());
				e.printStackTrace();
			}
		}
		return inputStream;		
	}
	public abstract List<NameValuePair> getNameValuePair(Bundle bundle);
	
	public abstract List<Mp3Info> execute(Bundle bundle,Mp3Info mp3Info);
}
