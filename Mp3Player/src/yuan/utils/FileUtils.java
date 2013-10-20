package yuan.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import yuan.factory.model.Mp3Info;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class FileUtils {
	
	public final static String SD_CARD_ROOT = Environment.getExternalStorageDirectory().
			getAbsolutePath() + File.separator;
	public final static String MP3EXTENSION = ".mp3";
	public final static String LYRICEXTENSION = ".lrc";
	public final static String IMAGEEXTENSION = ".jpg";
	public final static String MAINFOLDER = "mp3player" + File.separator;
	public final static String MP3FOLDER = "mp3" + File.separator;
	public final static String LYRICFOLDER = "lyric" + File.separator;
	public final static String IMAGEFOLDER = "images" + File.separator;
	public final static String CRASHFOLDER = "crash" + File.separator;
	public final static String MAINDIR = SD_CARD_ROOT + MAINFOLDER;
	public final static String MP3DIR = SD_CARD_ROOT + MAINFOLDER + MP3FOLDER;
	public final static String LYRICDIR = SD_CARD_ROOT + MAINFOLDER + LYRICFOLDER;
	public final static String IMAGESDIR = SD_CARD_ROOT + MAINFOLDER + IMAGEFOLDER;
	public final static String CRASHDIR = SD_CARD_ROOT + MAINFOLDER + CRASHFOLDER;
	
	/**
	 * 程序运行时，创建目录用于存放MP3 、lrc、images
	 */
	public static void createDefaultDir() {
		//scanSong(SD_CARD_ROOT);
		File mp3Dir = new File(MP3DIR);
		File lrcDir = new File(LYRICDIR);
		File imagesDir = new File(IMAGESDIR);
		File crash = new File(CRASHDIR);
		if(!mp3Dir.exists())
			mp3Dir.mkdirs();
		if(!lrcDir.exists())
			lrcDir.mkdirs();
		if(!imagesDir.exists())
			imagesDir.mkdirs();
		if(!crash.exists()) 
			crash.mkdirs();
	}
	
	/**
	 * 判断SD卡上的文件夹是否存在
	 */
	public boolean isFileExist(String fileName, String path) {
		File file = new File(path + fileName);
		return file.exists();
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public File write2SDFromInput(String path, String fileName,
			InputStream input) {

		File file = null;
		OutputStream output = null;
		try {
			file = new File(path + fileName);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[1024 * 100];
			int temp;
			while ((temp = input.read(buffer)) != -1) {
				output.write(buffer, 0, temp);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	/**
	 * 把毫秒时间转换为MP3的标准时间
	 * @param mp3Time : 字符串毫秒
	 * @return MP3的标准时间
	 */
	public static String timeConvert(String mp3Time) {
		if(mp3Time == null) {
			return null;
		}
		int millisecond = Integer.parseInt(mp3Time);
		int minute = millisecond/(60*1000);
		int secord = millisecond%(60*1000)/1000;
		//返回String类型的标准时间
		return "0" + minute + ":" + (secord >9 ? secord : "0" + secord);
	}
	
	/**
	 * 把毫秒时间转换为MP3的标准时间
	 * @param mp3Time : 整形毫秒
	 * @return MP3的标准时间
	 */
	public static String IntTimeConvert(int mp3Time) {
		int minute = mp3Time/(60*1000);
		int secord = mp3Time%(60*1000)/1000;
		//返回String类型的标准时间
		return "0" + minute + ":" + (secord >9 ? secord : "0" + secord);
	}
	
	public static void scanSong(String dir) {
		File[] file = new File(dir).listFiles(new SongFileFilter());
		if(file == null) {
			return;
		}
		for (int i = 0; i < file.length; i++) {						
			if(file[i].isDirectory()) {				
				scanSong(file[i].getAbsolutePath());
			} else {			
				Mp3Info mp3Info = Mp3ID3v1.Mp3ID3v1Info(file[i].getAbsolutePath());
			}
		}
	}
	
	public static List<Mp3Info> getMediaStoreMp3Infos(Context content){
		//更新系统歌曲数据库
		content.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + 
				Environment.getExternalStorageDirectory().getAbsolutePath())));
		
		List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
		int id = 0;
		String[] projections = {
				MediaStore.Audio.Media.ARTIST,        //歌手
				MediaStore.Audio.Media._ID,           //ID
				MediaStore.Audio.Media.DURATION,      //时间
				MediaStore.Audio.Media.SIZE,          //大小
				MediaStore.Audio.Media.DISPLAY_NAME,  //全名（含后缀名）
				MediaStore.Audio.Media.DATA};         //MP3所在的绝对路径
		String[] selections = {
				"204800", //大小大于200KB
				"%mp3"  //后缀为.mp3
		};	
		Cursor cursor = content.getContentResolver().query(
             MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projections,
             MediaStore.Audio.Media.SIZE + " >= ? and " +
             MediaStore.Audio.Media.DISPLAY_NAME + " like ?" , selections, null);
		while (cursor != null && cursor.moveToNext()) {//MediaStore.Audio.Media.IS_MUSIC + " = 1"			
			Mp3Info mp3Info = new Mp3Info();		
			
			mp3Info.setId(id++);
			mp3Info.setMp3Time(timeConvert(cursor.getString(2)));
			mp3Info.setMp3Size(cursor.getString(3));			
			//设置歌手名字
			if(cursor.getString(4).indexOf(" -") != -1)
				mp3Info.setSingerName(cursor.getString(4).substring(0, cursor.getString(4).indexOf(" -")));
			else if(cursor.getString(4).indexOf("-") != -1)
				mp3Info.setSingerName(cursor.getString(4).substring(0, cursor.getString(4).indexOf("-")));
			else  
				mp3Info.setSingerName(cursor.getString(0));
			//设置MP3名，含后缀名	
			if(cursor.getString(4).indexOf("- ") != -1)
				mp3Info.setMp3Name(cursor.getString(4).substring(cursor.getString(4).indexOf("- ") + 2));
			else if(cursor.getString(4).indexOf("-") != -1)
				mp3Info.setMp3Name(cursor.getString(4).substring(cursor.getString(4).indexOf("-") + 1));
			else 
				mp3Info.setMp3Name(cursor.getString(4));
			
			mp3Info.setMp3SimpleName(mp3Info.getMp3Name().replace(MP3EXTENSION, ""));
			mp3Info.setMp3URL(cursor.getString(5));			
			mp3Info.setLrcURL(FileUtils.LYRICDIR + cursor.getString(4).replace(MP3EXTENSION, LYRICEXTENSION));
			if(mp3Info.getSingerName() != null && !mp3Info.getSingerName().equals("<unknown>"))
				mp3Info.setSingerBigImageURL(FileUtils.IMAGESDIR + mp3Info.getSingerName() + IMAGEEXTENSION);
			else 
				mp3Info.setSingerBigImageURL(FileUtils.IMAGESDIR + mp3Info.getMp3SimpleName() + IMAGEEXTENSION);
			mp3Infos.add(mp3Info);
		}
		cursor.close();//不要忘记关闭cursor
      return mp3Infos;
	}
}