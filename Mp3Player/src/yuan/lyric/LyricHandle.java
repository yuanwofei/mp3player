package yuan.lyric;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 歌词处理类
 * @author Administrator
 */
public class LyricHandle {
	
	public LyricHandle() {
		
	}
	
	/**
	 * 对歌词文件输入流进行处理，每提取其中的每行歌词和对应的时间点，<br>
	 * 然后存放在一个歌词类型的LyricInfo数据结构中，再把此歌词类型的LyricInfo数据结构<br>
	 * 再存放在一个向量的Vector<LyricInfo>的对象
	 * @param lyricStream  歌词文件的输入流
	 * @param charset 按照此字符集进行识别lyricStream
	 * @return 存放经过排序的每行歌词和对应时间点的Vector<LyricInfo>的对象
	 */
	public List<LyricInfo> handleLyric(InputStream lyricStream, String charset) {
		List<LyricInfo> lyricInfos = new ArrayList<LyricInfo>();
		try {					
			String lineStr = null;
			Long time = null;
			String lyric = null;			
			Pattern pattern = Pattern.compile("\\[([0-9\\:\\.]+)\\]");		
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(lyricStream, charset));
			while ((lineStr = bufferReader.readLine()) != null) {
				Matcher matcher = pattern.matcher(lineStr);
				while(matcher.find()) {
					LyricInfo lyricInfo = new LyricInfo();

					//得到时间
					String timeStr = matcher.group();
					time = TimeToLong(timeStr.substring(1, timeStr
							.length() - 1));
					if(time == -1) {
						continue;//如果时间转换异常，将丢弃此行歌词
					}
					
					//得到歌词
					if(!lineStr.endsWith("]"))
						lyric = lineStr.substring(lineStr.lastIndexOf("]") + 1);
					else 
						lyric = "......"; //此行表示这行歌词为空
					
					lyricInfo.setTime(time);
					lyricInfo.setLyric(lyric.trim());
					lyricInfos.add(lyricInfo);				
				}
			}
			bufferReader.close();		
			
			//对歌词时间进行从小到大排序
			Collections.sort(lyricInfos, new SortLyricInfoByTime());
			//得到一行歌词持续的时间
			for(int i=0; i<lyricInfos.size(); i++) {			
				LyricInfo lyricInfoOne = lyricInfos.get(i);			
				if(i+1 < lyricInfos.size()) {			
					LyricInfo lyricInfoTwo = lyricInfos.get(i+1);
					lyricInfoOne.setDuration(lyricInfoTwo.getTime()-lyricInfoOne.getTime());
				}
			}			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lyricInfos;		
	}
	
	/**对两个歌词类型的LyricInfo的时间点进行从小到大排序*/
	private class SortLyricInfoByTime implements Comparator<LyricInfo> {		
		public int compare(LyricInfo lyricInfoOne, LyricInfo lyricInfoTwo) {
			return sortUp(lyricInfoOne, lyricInfoTwo);
		}
		
		//按照歌词时间点从小到大排序
		private int sortUp(LyricInfo lyricInfoOne, LyricInfo lyricInfoTwo) {
			if (lyricInfoOne.getTime() < lyricInfoTwo.getTime())
				return -1;
			else if (lyricInfoOne.getTime() > lyricInfoTwo.getTime())
				return 1;
			else
				return 0;
		}
	}
	
	/**把字符串类型的时间转换为Long类型的时间
	 * @return 如果字符串类型的时间合法，将返回long类型的时间，单位为毫秒<br>
	 * 		        否则返回-1
	 */
	private long TimeToLong(String Time) {
		try {
			String[] s1 = Time.split(":");
			int min = Integer.parseInt(s1[0]);
			String[] s2 = s1[1].split("\\.");
			int sec = Integer.parseInt(s2[0]);
			int mill = 0;
			if (s2.length > 1)
				mill = Integer.parseInt(s2[1]);
			return min * 60 * 1000 + sec * 1000 + mill * 10;
		} catch (Exception e) {
			return -1;
		}
	}
}
