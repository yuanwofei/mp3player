package yuan.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class CommonHttpApi extends AbstractHttpApi{
	
	private String page_no;
	private String page_size;
	private String keyWord;

	public CommonHttpApi(String page_no, String page_size, String keyWord) {
		super();
		this.page_no = page_no;
		this.page_size = page_size;
		this.keyWord = keyWord;
	}

	public List<NameValuePair> getNameValuePair(){
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();			
		nameValuePair.add(new BasicNameValuePair(METHOD, "baidu.ting.search.common"));
		nameValuePair.add(new BasicNameValuePair(FORMAT, XML));
		nameValuePair.add(new BasicNameValuePair(PAGE_NO, page_no));
		nameValuePair.add(new BasicNameValuePair(PAGE_SIZE, page_size));
		nameValuePair.add(new BasicNameValuePair(QUERY, keyWord));
		return nameValuePair;
	}	
}
