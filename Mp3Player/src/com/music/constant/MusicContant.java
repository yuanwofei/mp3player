package com.music.constant;

public interface MusicContant {
	
	public final static class PlayState {
		/**代表开始播放*/
		public final static int INIT = 0x11;
		
		/**代表开始播放*/
		public final static int PLAY = 0x12;
		
		/**代表暂停播放*/
		public final static int PAUSE = 0x13;
		
		/**代表停止播放*/
		public final static int STOP = 0x14;
		
		/**代表播放前一首歌曲*/
		public final static int PREV = 0x15;
		
		/**代表播放后一首歌曲*/
		public final static int NEXT = 0x16;
	}
	
	public final static class PlayMode {	
	
		/**代表循环播放*/
		public final static int CYCLE = 0x11;
		
		/**代表列表播放*/
		public final static int SEQUENCE = 0x12;	
		
		/**代表随机播放*/
		public final static int SINGLE = 0x13;
		
		/**代表单曲循环播放*/
		public final static int RANDOM = 0x14;
	}
	
	public final static class MusicList {
		public final static int INSERT_MUSICS = 0x11;
		public final static int UPDATE_MUSICS = 0x12;
	}
	
	public final static String MUSIC = "com.music.MUSIC";
	
	public final static String MUSICS = "com.music.MUSICS";
	
	
	public final static String PLAY_STATE = "com.music.PLAY_STATE";
	
	
	public final static String PLAY_MODE = "com.music.PLAY_MODE";
	
	
	public final static String CURRENT_MUSIC_INDEX = "com.music.CURRENT_MUSIC_INDEX";
	
	/***/
	public final static String LRC_MESSAGE_ACTION = "com.music.LRC_MESSAGE.ACTION";
	
	/***/
	public final static String UNDATE_LIST_ACTION = "com.music.REMOTE_LIST_ACTION";
	
	/***/
	public final static String SEARCH_KEY_WORD_ACTION = "com.music.SEARCH_KEYWORD_ACTION";
	
	/**播放模式*/
	public final static String PLAY_MODE_ACTION = "com.music.PLAYMODE_ACTION";
	
	/**歌手图片加载完毕标志*/
	public final static String LOAD_SINGER_IMAGE_OVER = "com.music.LOAD_SINGER_IAMGE_OVER";
	
	/**歌词加载完毕*/
	public final static String LOAD_LYRIC_OVER = "com.music.LOAD_LYRIC_OVER";
	
	/**播放音乐标志*/
	public final static String PLAY_MUSIC_ACTION = "com.music.PLAY_MUSIC_ACTION";
	
	/**播放界面元素更新*/
	public final static String UPDATE_UI_ACTION = "com.music.UPDATE_UI_ACTION";
}
