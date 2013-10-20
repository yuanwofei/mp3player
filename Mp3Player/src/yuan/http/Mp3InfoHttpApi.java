package yuan.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class Mp3InfoHttpApi extends AbstractHttpApi{
	String mp3Id = null;

	public Mp3InfoHttpApi(String mp3Id) {
		this.mp3Id = mp3Id;
	}

	@Override
	public List<NameValuePair> getNameValuePair() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();			
		nameValuePair.add(new BasicNameValuePair(METHOD, "baidu.ting.song.getInfo"));
		nameValuePair.add(new BasicNameValuePair(FORMAT, XML));
		nameValuePair.add(new BasicNameValuePair(SONG_ID, mp3Id));
		return nameValuePair;
	}

}
