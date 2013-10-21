package yuan.factory.model.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import yuan.factory.HttpApiFactory;
import yuan.factory.model.Mp3Info;
import yuan.factory.model.xml.AbstractParse;
import yuan.factory.model.xml.Mp3InfoXmlParse;
import android.os.Bundle;

public class Mp3InfoHttpApi extends AbstractHttpApi{
	
	private Mp3InfoHttpApi() {
	}

	public static HttpApiFactory factory = new HttpApiFactory() {	
		@Override
		public AbstractHttpApi getHttpApi() {
			return new Mp3InfoHttpApi();
		}
	};
	
	@Override
	public List<NameValuePair> getNameValuePair(Bundle bundle) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();			
		nameValuePair.add(new BasicNameValuePair(METHOD, "baidu.ting.song.getInfo"));
		nameValuePair.add(new BasicNameValuePair(FORMAT, XML));
		nameValuePair.add(new BasicNameValuePair(SONG_ID, bundle.getString("mp3Id")));
		return nameValuePair;
	}

	@Override
	public List<Mp3Info> execute(Bundle bundle, Mp3Info mp3Info) {
		AbstractParse parse = Mp3InfoXmlParse.factory.getXmlParse();
		parse.parseXml(doHttpPost(bundle), mp3Info);
		return null;
	}
}
