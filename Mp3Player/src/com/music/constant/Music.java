package com.music.constant;

public interface Music {
	
	public final static class PlayState {
		/**代表开始播放*/
		public final static int PLAY = 1;
		
		/**代表暂停播放*/
		public final static int PAUSE = 2;
		
		/**代表停止播放*/
		public final static int STOP = 3;
		
		/**代表播放前一首歌曲*/
		public final static int PRE = 4;
		
		/**代表播放后一首歌曲*/
		public final static int NEXT = 5;
	}
	
	public final static class PlayMode {	
		/**代表循环播放*/
		public final static int SEQUENCE_MODE = 1;
		/**代表随机播放*/
		public final static int RANDOM_MODE = 2;
		/**代表单曲循环播放*/
		public final static int SINGLE_MODE = 3;
	}
	
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
