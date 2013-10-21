package com.music.lyric;

public class LyricInfo {
	
	/**这一行歌词对应的时间点*/
	private Long time;
	
	/**这一行歌词持续的时间*/
	private Long duration = 0L;
	
	/**这一行歌词的内容*/
	private String lyric;
	
	public LyricInfo() {
		
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public Long getDuration() {
		return duration;
	}
	public void setDuration(Long duration) {
		this.duration = duration;
	}
	public String getLyric() {
		return lyric;
	}
	public void setLyric(String lyric) {
		this.lyric = lyric;
	}
	@Override
	public String toString() {
		return "LyricInfo [time=" + time + ", duration=" + duration
				+ ", lyric=" + lyric + "]";
	}	
}
