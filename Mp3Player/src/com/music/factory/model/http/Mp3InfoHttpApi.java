package com.music.factory.model.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.music.factory.HttpApiFactory;
import com.music.factory.model.xml.AbstractParse;
import com.music.factory.model.xml.Mp3InfoXmlParse;
import com.music.mp3player.Music;

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
	public ArrayList<Music> execute(Bundle bundle, Music mp3Info) {
		AbstractParse parse = Mp3InfoXmlParse.factory.getXmlParse();
		parse.parseXml(doHttpPost(bundle), mp3Info);
		return null;
	}
}
