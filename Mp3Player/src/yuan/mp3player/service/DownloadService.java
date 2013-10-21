package yuan.mp3player.service;

import yuan.download.HttpDownloader;
import yuan.factory.HttpApiFactory;
import yuan.factory.model.Mp3Info;
import yuan.factory.model.http.Mp3InfoHttpApi;
import yuan.notification.DownloadNotification;
import yuan.utils.FileUtils;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;

public class DownloadService extends Service{
	private Mp3Info mp3Info = null;
	private HttpDownloader downloader = new HttpDownloader();	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");			
		startDownload();
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * 启动下载线程开始下载
	 */
	private void startDownload() {
		DownloadThread downloadThread = new DownloadThread();
		Thread thread = new Thread(downloadThread);		
		thread.start();	
	}
	
	/**
	 * 下载MP3和歌词的线程
	 */
	class DownloadThread implements Runnable {
		
		public void run() {												
		    prepare();
		    if(mp3Info.getLrcURL() != null) {
		    	downloadLyric(downloader);
		    }	
		    /*if(mp3Info.getSingerBigImageURL() != null) {
		    	downloadImage(downloader);
		    }*/
		    if(mp3Info.getMp3URL() != null) {
		    	downloadMp3(downloader);
		    }
				
		}
		
		/**下载之前先搜索MP3的相关信息*/
		private void prepare() {
			HttpApiFactory factory = Mp3InfoHttpApi.factory;
			Bundle bundle = new Bundle();
			bundle.putString("mp3Id", mp3Info.getMp3IdCode());
			factory.getHttpApi().execute(bundle, mp3Info);
		}				
		
		/**下载听百度歌词*/
		private void downloadLyric(HttpDownloader downloader) {
			downloader.downloadLyricFile(mp3Info.getLrcURL(), FileUtils.LYRICDIR,
					mp3Info.getSingerName()+ " - " + mp3Info.getMp3SimpleName() + FileUtils.LYRICEXTENSION);						
		}
		
		/*
		    下载歌手图片
		private void downloadImage(HttpDownloader downloader) {
			downloader.downFile(mp3Info.getSingerBigImageURL(), FileUtils.IMAGESDIR,
					mp3Info.getSingerName() + FileUtils.IMAGEEXTENSION);			
		}*/
		
		/**下载MP3*/
		private void downloadMp3(HttpDownloader downloader) {
			int result = downloader.downFile(mp3Info.getMp3URL(), FileUtils.MP3DIR,
					mp3Info.getSingerName()+ " - " + mp3Info.getMp3SimpleName() + FileUtils.MP3EXTENSION);					
			downloadResult(result);
		}
		
		/**
		 * MP3下载结果，以notification的形式来通知用户
		 * @param result 0：代表下载成功</br>
		 * @param result 1：代表文件已存在</br>
		 * @param result 2：代表下载失败</br>
		 */
		private void downloadResult(int result) {
			String downloadMsg = null;
			if(result == 0) {
				downloadMsg = mp3Info.getMp3Name() + ":下载成功";
			}
			else if(result == 1) {
				downloadMsg = mp3Info.getMp3Name() + ":文件已存在";
				
			}
			else if(result == -1) {
				downloadMsg = mp3Info.getMp3Name() + ":文件下载失败";
			}
			System.out.println("downloadmp3Result-------->" + downloadMsg);
			//更新系统歌曲数据库
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
					Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath())));
			//提醒用户下载情况
			DownloadNotification.showNotification(DownloadService.this,downloadMsg);
		} 
	}	
}
