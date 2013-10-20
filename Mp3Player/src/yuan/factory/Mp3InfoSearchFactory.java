package yuan.factory;

import java.util.List;

import yuan.http.AbstractHttpApi;
import yuan.http.Mp3InfoHttpApi;
import yuan.model.Mp3Info;
import yuan.xml.AbstractParse;
import yuan.xml.Mp3InfoXmlParse;

public class Mp3InfoSearchFactory implements OnlineFactory {

	private String mp3Id = null;
	private Mp3Info mp3Info = null;
	
	public Mp3InfoSearchFactory(String mp3Id, Mp3Info mp3Info) {	
		this.mp3Id = mp3Id;
		this.mp3Info = mp3Info;
	}

	public AbstractParse getParse() {
		// TODO Auto-generated method stub
		return new Mp3InfoXmlParse();
	}

	public AbstractHttpApi getHttpApi() {
		// TODO Auto-generated method stub
		return new Mp3InfoHttpApi(mp3Id);
	}

	public List<Mp3Info> execute() {
		this.getParse().parseXml(this.getHttpApi().doHttpPost(), mp3Info);
		return null;
	}

}
