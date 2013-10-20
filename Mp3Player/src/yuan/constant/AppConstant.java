package yuan.constant;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public interface AppConstant {
	public class PlayerMsg {
		/**代表开始播放*/
		public final static int PLAY_MSG = 1;
		
		/**代表暂停播放*/
		public final static int PAUSE_MSG = 2;
		
		/**代表停止播放*/
		public final static int STOP_MSG = 3;
		
		/**代表播放前一首歌曲*/
		public final static int BEFORE_MSG = 4;
		
		/**代表播放后一首歌曲*/
		public final static int AFTER_MSG = 5;
	}
	
	public class PlayMode {	
		/**代表循环播放*/
		public final static int SEQUENCE_MODE = 1;
		/**代表随机播放*/
		public final static int RANDOM_MODE = 2;
		/**代表单曲循环播放*/
		public final static int SINGLE_MODE = 3;
	}
	
	public class PlayComponent {
		
		public static TextView singerName;
		public static TextView songName;		
		public static TextView countTime;	
		public static TextView mp3Time;
		
		public static TextView miniSingerName;
		public static TextView miniSongName;
		public static TextView miniCountTime;	
		public static TextView miniMp3Time;
					
		public static ImageButton playButton;
		public static ImageButton prevButton;
		public static ImageButton nextButton;
		
		public static ImageButton miniPlayBtn;
		public static ImageButton miniPrevBtn;
		public static ImageButton miniNextBtn;
		public static ImageButton stopButton;		
		
		public static ImageButton returnBack;
		public static ImageButton currentPlayList;
		public static ImageButton playMode;		
		public static ImageView singerImage;		
		public static ImageView miniSingerImage;
		public static SeekBar timeSeekBar;
		public static RelativeLayout playerBg;
	}
	
	public final static String DEBUG_TAG = "yuan.mp3player";
	
	//任务栏显示的信息
	static String title = new StringBuilder().append(AppConstant.PlayComponent.songName)
			.append(" - ").append(AppConstant.PlayComponent.singerName).toString();
	
	/***/
	public final static String LRC_MESSAGE_ACTION = "yuan.mp3player.lrcmessage.action";
	/***/
	public final static String UNDATE_LIST_ACTION = "yuan.mp3player.remotelist.action";
	/***/
	public final static String SEARCH_KEY_WORD_ACTION = "yuan.mp3player.searchkeyword.action";
	/**播放模式*/
	public final static String PLAY_MODE_ACTION = "yuan.mp3player.playmode.action";
	/**歌手图片加载完毕标志*/
	public final static String LOAD_SINGER_IMAGE_OVER = "yuan.mp3player.load.singerimage.over";
	/**歌词加载完毕*/
	public final static String LOAD_LYRIC_OVER = "yuan.mp3player.load.lyric.over";
}
