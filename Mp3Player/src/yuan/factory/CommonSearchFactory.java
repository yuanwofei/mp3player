package yuan.factory;

import java.util.List;

import yuan.factory.model.Mp3Info;
import yuan.http.AbstractHttpApi;
import yuan.http.CommonHttpApi;
import yuan.xml.AbstractParse;
import yuan.xml.ComonXmlParse;

/**
 * Ò»°ãËÑË÷¹¤³§
 * @author Administrator
 */
public class CommonSearchFactory implements OnlineFactory {

	private String page_no;
	private String page_size;
	private String keyWord;
	
	public CommonSearchFactory(String page_no, String page_size, String keyWord) {	
		this.page_no = page_no;
		this.page_size = page_size;
		this.keyWord = keyWord;
	}

	public AbstractParse getParse() {		
		return new ComonXmlParse();
	}

	public AbstractHttpApi getHttpApi() {		
		return new CommonHttpApi(page_no, page_size, keyWord);
	}

	public List<Mp3Info> execute() {
		return this.getParse().parseXML(this.getHttpApi().doHttpPost());	
	}

}
