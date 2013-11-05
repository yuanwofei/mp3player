package com.music.download;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.music.utils.FileUtils;


public class HttpDownloader {

	public static String readURL(String url) {
		StringBuilder sb;
		try {
		HttpURLConnection conn = (HttpURLConnection)(new URL(url)).openConnection();
		conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:8.0.1) Gecko/20100101 Firefox/8.0.1");
		conn.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String temp = null;
		sb = new StringBuilder();
		while ((temp = br.readLine()) != null)
			sb.append(temp).append("\n");
		br.close();
		conn.disconnect();
		} catch (IOException e) {
			return null;
		}		
		return sb.toString();
	}
	
	/**专门下载歌词的方法*/
	public void downloadLyricFile(String urlStr, String path, String fileName) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(path + fileName), "GBK"));
			bw.write(readURL(urlStr));
			bw.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * 该函数返回整形 -1：代表下载文件出错 0：代表下载文件成功 1：代表文件已经存在
	 */
	public int downFile(String urlStr, String path, String fileName) {
		InputStream inputStream = null;
		try {
			FileUtils fileUtils = new FileUtils();
			if (fileUtils.isFileExist(fileName,path)) {
				return 1;
			} else {			
				inputStream = getInputStreamFromUrl(urlStr);				
				File resultFile = fileUtils.write2SDFromInput(path, fileName, inputStream);				
				if (resultFile == null) {
					return -1;
				}
			}
		} catch (Exception e) {			
			return -1;
		} finally {
			try {
				if(inputStream != null)
					inputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	/**
	 * 根据URL得到输入流
	 * 
	 * @param urlStr
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public InputStream getInputStreamFromUrl(String urlStr){
		URL url = null;
		InputStream inputStream = null;
		try {
			url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			urlConn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:8.0.1) Gecko/20100101 Firefox/8.0.1");
			urlConn.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			inputStream = urlConn.getInputStream();			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}				
		return inputStream;
	}
	
	/**
     * 从一个流里面得到这个流的字符串
     * 表现形式
     * @param is 流
     * @return 字符串
     */
    public String getStringContent(InputStream is) {
        InputStreamReader r = null;
        try {
            StringBuilder sb = new StringBuilder();
            //TODO 这里是固定把网页内容的编码写在GBK,应该是可设置的
            r = new InputStreamReader(is, "utf-8");
            char[] buffer = new char[1024];
            int length = -1;
            while ((length = r.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, length));
            }
            return sb.toString();
        } catch (Exception ex) {         
            return "";
        } finally {
            try {
                r.close();
            } catch (Exception ex) {                
            }
        }
    }
}

