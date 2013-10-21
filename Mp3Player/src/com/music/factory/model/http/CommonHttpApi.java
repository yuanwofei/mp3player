package com.music.factory.model.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.music.factory.HttpApiFactory;
import com.music.factory.model.Mp3Info;
import com.music.factory.model.xml.AbstractParse;
import com.music.factory.model.xml.ComonXmlParse;

import android.os.Bundle;

public class CommonHttpApi extends AbstractHttpApi{
	private CommonHttpApi() {	
	}

	public static HttpApiFactory factory = new HttpApiFactory() {	
		@Override
		public AbstractHttpApi getHttpApi() {
			return new CommonHttpApi();
		}
	};
	
	public List<NameValuePair> getNameValuePair(Bundle bundle){		
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();			
		nameValuePair.add(new BasicNameValuePair(METHOD, "baidu.ting.search.common"));
		nameValuePair.add(new BasicNameValuePair(FORMAT, XML));
		nameValuePair.add(new BasicNameValuePair(PAGE_NO, bundle.getString("page_no")));
		nameValuePair.add(new BasicNameValuePair(PAGE_SIZE, bundle.getString("page_size")));
		nameValuePair.add(new BasicNameValuePair(QUERY, bundle.getString("keyWord")));
		return nameValuePair;
	}

	@Override
	public List<Mp3Info> execute(Bundle bundle, Mp3Info mp3Info) {
		AbstractParse parse = ComonXmlParse.factory.getXmlParse();
		return parse.parseXML(doHttpPost(bundle));
	}	
}
