package com.music.factory.model.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.music.factory.XmlParseFactory;
import com.music.factory.model.Mp3Info;


public class ComonXmlParse extends AbstractParse {

	private ComonXmlParse() {		
	}

	@Override
	public List<Mp3Info> parseXML(InputStream xmlContent) {
		//如何xmlContent为空就返回
		if(xmlContent == null) {
			return null;
		}		
		//使用DOM（文档对象模型）来解析XML文件
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
		try {			
			Document doc = factory.newDocumentBuilder().parse(xmlContent);
			NodeList songNodes = doc.getElementsByTagName("song");		
			//得到MP3的总数
			NodeList mp3SumNodes = doc.getElementsByTagName("total");
			String mp3Sum = mp3SumNodes.item(0).getFirstChild().getNodeValue();
			
			for (int i = 0; i < songNodes.getLength(); i++) {					
				Mp3Info mp3Info = new Mp3Info();
				mp3Info.setMp3Sum(mp3Sum);
				Node parentNode = songNodes.item(i);
				NodeList childNodes = parentNode.getChildNodes();
				for (int j = 0; j < childNodes.getLength(); j++) {
					Node childNode = childNodes.item(j);
					String childName = childNode.getNodeName();
					if(childName.equals("title") && childNode.hasChildNodes()){							
						mp3Info.setMp3SimpleName(handerName(childNode.getTextContent()));
						mp3Info.setMp3Name(mp3Info.getMp3SimpleName() + ".mp3");
					} if(childName.equals("song_id") && childNode.hasChildNodes()) {
						mp3Info.setMp3IdCode(childNode.getFirstChild().getNodeValue());
					} if(childName.equals("author") && childNode.hasChildNodes()) {
						mp3Info.setSingerName(handerName(childNode.getTextContent()));
					} if(childName.equals("album_title") && childNode.hasChildNodes()) {
						mp3Info.setAlbumName(handerName(childNode.getTextContent()));
					} if(childName.equals("lrclink") && childNode.hasChildNodes()) {							
						mp3Info.setLrcURL(new StringBuffer("http://ting.baidu.com").
								append(handerName(childNode.getFirstChild().getNodeValue())).toString());
					}					
				}		
				mp3Infos.add(mp3Info);
			}			
		} catch (SAXException e) {
			System.out.println("解析XML出错" +"-------a");
		} catch (IOException e) {
			System.out.println("解析XML出错" +"-------b");
		} catch (ParserConfigurationException e) {
			System.out.println("解析XML出错" +"-------c");
		}						
		return mp3Infos;	
	}
	
	public static XmlParseFactory factory = new XmlParseFactory() {	
		@Override
		public AbstractParse getXmlParse() {
			return new ComonXmlParse();
		}
	};
}
