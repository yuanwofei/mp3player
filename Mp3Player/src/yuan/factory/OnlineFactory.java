package yuan.factory;

import java.util.List;

import yuan.factory.model.Mp3Info;
import yuan.http.AbstractHttpApi;
import yuan.xml.AbstractParse;

/**
 * 抽象工厂接口
 * @author Administrator
 */
public interface OnlineFactory {
	
	/**创建具体的解析对象*/
	public AbstractParse getParse();
	
	/**创建具体的http协议对象*/
	public AbstractHttpApi getHttpApi();
	
	/**执行上面解析对象、http协议对象的行为*/
	public List<Mp3Info> execute();
}
