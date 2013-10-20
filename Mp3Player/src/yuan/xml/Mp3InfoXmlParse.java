package yuan.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import yuan.factory.model.Mp3Info;
import yuan.utils.FileUtils;

public class Mp3InfoXmlParse extends AbstractParse {

	@Override
	public void parseXml(InputStream xmlContent, Mp3Info mp3Info) {
		//如何xmlContent为空就返回
		if(xmlContent == null) {
			return ;
		}		
		//使用DOM（文档对象模型）来解析XML文件
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {			
			Document doc = factory.newDocumentBuilder().parse(xmlContent);
			NodeList songUrl = doc.getElementsByTagName("file_link");
			NodeList lrcUrl = doc.getElementsByTagName("lrclink");
			NodeList tinguId = doc.getElementsByTagName("ting_uid");
			NodeList artistId = doc.getElementsByTagName("artist_id");
			NodeList albumId = doc.getElementsByTagName("album_id");
			NodeList picSmall = doc.getElementsByTagName("pic_small");
			NodeList picMedium = doc.getElementsByTagName("pic_big");			
			NodeList picBig = doc.getElementsByTagName("pic_radio");
			
			if(songUrl.item(0) != null && songUrl.item(0).getFirstChild() != null)
				mp3Info.setMp3URL(songUrl.item(0).getFirstChild().getNodeValue());			
			
			if(lrcUrl.item(0) != null && lrcUrl.item(0).getFirstChild() != null)
				mp3Info.setLrcURL(lrcUrl.item(0).getFirstChild().getNodeValue());
			
			if(tinguId.item(0) != null && tinguId.item(0).getFirstChild() != null)
				mp3Info.setTinguIdCode(tinguId.item(0).getFirstChild().getNodeValue());
			
			if(artistId.item(0) != null && artistId.item(0).getFirstChild() != null)
				mp3Info.setArtistIdCode(artistId.item(0).getFirstChild().getNodeValue());
			
			if(albumId.item(0) != null && albumId.item(0).getFirstChild() != null)
				mp3Info.setAlbumIdCode(albumId.item(0).getFirstChild().getNodeValue());
			
			if(picSmall.item(0) != null && picSmall.item(0).getFirstChild() != null)
				mp3Info.setSingerSmallImageURL(picSmall.item(0).getFirstChild().getNodeValue());
			
			if(picMedium.item(0) != null && picMedium.item(0).getFirstChild() != null)
				mp3Info.setSingerMediumImageURL(picMedium.item(0).getFirstChild().getNodeValue());
			
			if(picBig.item(0) != null && picBig.item(0).getFirstChild() != null)
				mp3Info.setSingerBigImageURL(FileUtils.IMAGESDIR + 
						mp3Info.getSingerName() + FileUtils.IMAGEEXTENSION);
				//mp3Info.setSingerBigImageURL(picBig.item(0).getFirstChild().getNodeValue());
			
		} catch (SAXException e) {
			System.out.println("解析XML出错" +"-------a");
		} catch (IOException e) {
			System.out.println("解析XML出错" +"-------b");
		} catch (ParserConfigurationException e) {
			System.out.println("解析XML出错" +"-------c");
		}
	}
}
