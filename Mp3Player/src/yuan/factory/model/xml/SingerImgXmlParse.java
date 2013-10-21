package yuan.factory.model.xml;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import yuan.download.HttpDownloader;
import yuan.factory.XmlParseFactory;

public class SingerImgXmlParse extends AbstractParse {
	
	private SingerImgXmlParse() {		
	}

	private static String parseXML(String strContent) {
		String regExp = "(\\<song\\_id\\>)([0-9]+)(\\<\\/song\\_id\\>)";
		Pattern pattern = Pattern.compile(regExp);
		Matcher mather = pattern.matcher(strContent);
		ArrayList<String> songId = new ArrayList<String>();
		while(mather.find()) {
			songId.add(mather.group(2));
		}
		if(songId.size() <= 0) {
			return null;
		}
		return songId.get(getRandom(songId.size()));
	}
	
	/**获取一个30以内的随机数*/
	private static int getRandom(int range) {
		return (int) (Math.random()*range);
	}
	
	public static String findSingerImgUrl(String singerName){
		String songIdUrl = "http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting." +
				"search.common&format=xml&page_no=1&page_size=60&query=" + singerName;
		StringBuilder str = new StringBuilder("http://tingapi.ting.baidu.com/v1/restserver/" +
				"ting?method=baidu.ting.song.getInfo&format=xml&songid=")
			.append(parseXML(HttpDownloader.readURL(songIdUrl)));
		String regExp = "(http://)([a-z]+\\.)+([a-z0-9]+\\/)+([0-9a-z]+)(.jpg)";
		Pattern pattern = Pattern.compile(regExp);
		Matcher mather = pattern.matcher(HttpDownloader.readURL(str.toString()));
		ArrayList<String> singerImg = new ArrayList<String>();
		while(mather.find()) {
			singerImg.add(mather.group(2));
		}
		if(singerImg.size() <= 0) {
			return null;
		}
		return singerImg.get(singerImg.size() - 1);
	}
	
	public static XmlParseFactory factory = new XmlParseFactory() {	
		@Override
		public AbstractParse getXmlParse() {
			return new SingerImgXmlParse();
		}
	};
}
