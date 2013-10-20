package yuan.xml;

import java.io.InputStream;
import java.util.List;

import yuan.factory.model.Mp3Info;

/**
 * 解析基接口
 * @author Administrator
 * @param <E>
 */
public interface Parse {
	
	/***/
	public abstract List<Mp3Info> parseXML(InputStream xmlContent);
	
	/***/
	public abstract void parseXml(InputStream xmlContent, Mp3Info mp3Info);
}
