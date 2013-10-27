package com.music.factory.model.xml;

import java.io.InputStream;
import java.util.ArrayList;

import com.music.factory.model.Mp3Info;


/**解析的抽象类
 * @param <E>*/
public abstract class AbstractParse{

	public void parseXml(InputStream xmlContent, Mp3Info mp3Info) {
		
	}
	
	public ArrayList<Mp3Info> parseXML(InputStream xmlContent) {
		return null;
	};
	
	/**清除名字中包含的无用字符*/	 
	public static String handerName(String name) {
		while(name.indexOf("<em>") != -1) {
			name = name.replace("<em>", "").replace("</em>", "");
		}		
		if(name.indexOf("（") != -1){
			name = name.substring(0, name.indexOf("（"));
		}
		while(name.indexOf(" - ") != -1){
			name = name.substring(0, name.indexOf(" - "));
		}
		if(name.indexOf(":") != -1){
			name = name.substring(0, name.indexOf(":"));
		}
		if(name.indexOf("(") != -1){
			name = name.substring(0, name.indexOf("("));
		}		
		return name;		
	}
}
