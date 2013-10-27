package com.music.lyric;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import com.music.constant.Music;
import com.music.download.HttpDownloader;
import com.music.factory.model.Mp3Info;
import com.music.utils.Network;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class LyricLoadThread extends Thread{

	private InputStream inputStream = null;
	private Mp3Info mp3Info = null;
	private String charset = null;
	private List<LyricInfo> lyricInfo = null;
	private Context context = null;
	
	public LyricLoadThread(Mp3Info mp3Info, Context context) {
		this.mp3Info = mp3Info;
		this.context = context;
	}
	
	@Override
	public void run() {
		loadLyric();		
	}
	
	/**加载歌词*/
	private void loadLyric(){
		lyricInfo = null;
		if(mp3Info.getLrcURL().indexOf("http") != -1) {		
			if(Network.isAccessNetwork(context)) {//获取网络上的歌词		
				inputStream = new HttpDownloader().getInputStreamFromUrl(mp3Info.getLrcURL());
				this.setDecode("UTF-8");
			} else {
				Toast.makeText(context, "当前还没有联网", Toast.LENGTH_SHORT).show();
			}	
						
		} else{
			File lyricFile = new File(mp3Info.getLrcURL());
			if(lyricFile.exists()) {//本地不存在歌词，即下载		
				loadLocalLyric();					
			} else {
				if(Network.isAccessNetwork(context)) {
					downloadLRC();
					loadLocalLyric();
				} else {
					Toast.makeText(context, "当前还没有联网", Toast.LENGTH_SHORT).show();
				}				
			}		
		}
		if(inputStream != null) {
			lyricInfo = new LyricHandle().handleLyric(inputStream, this.getDecode());
			sendLoadlyricOverBroadcast();
		}		
	}
	
	/**加载本地歌词*/
	private void loadLocalLyric() {
		try {
			inputStream = new FileInputStream(mp3Info.getLrcURL());
			this.setDecode("GBK");				
		} catch (FileNotFoundException e) {
			System.out.println(mp3Info.getLrcURL() + " 找不到歌词！");
		}
	}
	
	/**下载千千静听歌词*/		
	private void downloadLRC() {			
		List<QianQianJingTingLyricInfo> list;
		try {
			String lyricName = mp3Info.getSingerName() + " - " + mp3Info.getMp3SimpleName() + ".lrc";
			if(mp3Info.getSingerName().contains("<unknown>")) {
				mp3Info.setSingerName("");
				lyricName = mp3Info.getMp3SimpleName() + ".lrc";
			}
			list = QianQianJingTingLyricUtil.search(mp3Info.getSingerName(), mp3Info.getMp3SimpleName());
			if(list.size() > 0) {
				QianQianJingTingLyricInfo result = (QianQianJingTingLyricInfo)list.get(0);
				result.saveLRC(lyricName);			
				System.out.println(mp3Info.getMp3SimpleName() + "：歌词下载成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(mp3Info.getMp3SimpleName() + "千千静听歌词下载失败");
		}
	}
	
	private void setDecode(String charset) {		
		this.charset = charset;
	}

	public String getDecode() {
		return charset;
	}
	
	private void sendLoadlyricOverBroadcast() {
		Intent intent = new Intent();
		intent.setAction(Music.LOAD_LYRIC_OVER);
		context.sendBroadcast(intent);
	}
	
	public List<LyricInfo> getLyricInfos() {		
		return lyricInfo;
	}
}
