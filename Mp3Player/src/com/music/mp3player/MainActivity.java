package com.music.mp3player;

import java.util.List;

import com.music.constant.AppConstant;
import com.music.factory.PlayModeFactory;
import com.music.factory.model.CopyMp3Infos;
import com.music.factory.model.Mp3Info;
import com.music.factory.model.playmode.AbstractPlayMode;
import com.music.factory.model.playmode.RandomPlayMode;
import com.music.factory.model.playmode.SequencePlayMode;
import com.music.factory.model.playmode.SinglePlayMode;
import com.music.lyric.LyricTextView;
import com.music.mp3player.service.PlayerService;
import com.music.notification.TrayNotification;
import com.music.seekbar.SeekBarListener;
import com.music.utils.FileUtils;

import com.music.R;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

	private List<Mp3Info> mp3Infos = null;
	private Mp3Info mp3Info = null;
	public static int index = 0;
	private int modeValue = AppConstant.PlayMode.SEQUENCE_MODE;

	public ViewFlipper appFlipper = null;
	public static ViewFlipper viewFlipper = null;
	private RelativeLayout miniPlayControlBar = null;
	private String title[] = { "本地音乐", "我的收藏", "在线音乐" };
	private View localView, onlineView, searchView;
	private View view[] = { localView, onlineView, searchView };
	private Class<?> className[] = { LocalHomeActivity.class,
			OnlineActivity.class, SearchActivity.class };

	private static LyricTextView lyricView = null;
	private boolean isMainUI = true;
	
	private static PlayModeFactory playModeFactory;

	@Override
	public void onCreate(Bundle savedInstanceState) {			
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
		mp3Infos = FileUtils.getMediaStoreMp3Infos(this);
		CopyMp3Infos.setMP3INFOS(mp3Infos);

		initComponent();
		registerMonitor();
		TabHost tabHost = getTabHost();
		for (int i = 0; i < title.length; i++) {
			view[i] = (View) LayoutInflater.from(this).inflate(
					R.layout.tab_item, null);
			TextView text = (TextView) view[i].findViewById(R.id.tab_label);
			text.setText(title[i]);
			text.setTextSize(17);
			tabHost.addTab(tabHost.newTabSpec(title[i]).setIndicator(view[i])
					.setContent(new Intent(this, className[i])));
		}
	}

	class MiniPlayControlListener implements OnClickListener {
		public void onClick(View v) {
			isMainUI = false;
			appFlipper.showNext();
		}
	}

	class ReturnBackListener implements OnClickListener {
		public void onClick(View v) {
			appFlipper.showPrevious();
		}
	}

	/** 开始播放按钮的监听器 */
	class PlayButtonListener implements OnClickListener {
		public void onClick(View v) {
			// 创建一个Intent对象，用于同时Service开始播放MP3
			StartService(R.id.play);
		}
	}

	/** 前一首歌按钮的监听器 */
	class PrevButtonListener implements OnClickListener {
		public void onClick(View v) {
			StartService(R.id.prev);
		}
	}

	/** 下一首歌按钮的监听器 */
	class NextButtonListener implements OnClickListener {
		public void onClick(View v) {
			StartService(R.id.next);
		}
	}

	/** 启动播放MP3的service */
	private void StartService(int id) {
		mp3Infos = CopyMp3Infos.getMP3INFOS();
		if (id == R.id.play) {
			mp3Info = mp3Infos.get(index);
			setIntent(AppConstant.PlayerMsg.PLAY_MSG);
		} else if (id == R.id.prev) {
			index = getPlayMode().preSongIndex(index, mp3Infos.size());
			mp3Info = mp3Infos.get(index);
			setIntent(AppConstant.PlayerMsg.BEFORE_MSG);
		} else if (id == R.id.next) {
			index = getPlayMode().nextSongIndex(index, mp3Infos.size());
			mp3Info = mp3Infos.get(index);
			setIntent(AppConstant.PlayerMsg.AFTER_MSG);
		}
	}

	public static AbstractPlayMode getPlayMode() {
		return playModeFactory.createPlayMode();
	}
	
	public void setPlayModeFactory(PlayModeFactory playModeFactory) {
		MainActivity.playModeFactory = playModeFactory;
	}

	/** 设置Intent */
	private void setIntent(int flag) {
		Intent intent = new Intent();
		intent.setClass(this, PlayerService.class);
		intent.putExtra("MSG", flag);
		intent.putExtra("mp3Info", mp3Info);
		intent.putExtra("index", index);
		startService(intent);
	}

	/** 初始化各种组件 */
	private void initComponent() {
		setPlayModeFactory(SequencePlayMode.factory);
		
		appFlipper = (ViewFlipper) findViewById(R.id.app_viewflipper);
		//appFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom));
		miniPlayControlBar = (RelativeLayout) findViewById(R.id.mini_play_control);
		
		AppConstant.PlayComponent.miniPlayBtn = (ImageButton) findViewById(R.id.mini_play);
		AppConstant.PlayComponent.miniPrevBtn = (ImageButton) findViewById(R.id.mini_prev);
		AppConstant.PlayComponent.miniNextBtn = (ImageButton) findViewById(R.id.mini_next);
		AppConstant.PlayComponent.miniSongName = (TextView) findViewById(R.id.mini_song_name);
		AppConstant.PlayComponent.miniSingerName = (TextView) findViewById(R.id.mini_singer_name);
		AppConstant.PlayComponent.miniCountTime = (TextView) findViewById(R.id.mini_counttime_name);
		AppConstant.PlayComponent.miniMp3Time = (TextView) findViewById(R.id.mini_songtime_name);
		AppConstant.PlayComponent.miniSingerImage = (ImageView) findViewById(R.id.mini_singerimage);

		AppConstant.PlayComponent.singerName = (TextView) findViewById(R.id.singer_name);
		AppConstant.PlayComponent.songName = (TextView) findViewById(R.id.song_name);
		AppConstant.PlayComponent.playButton = (ImageButton) findViewById(R.id.play);
		AppConstant.PlayComponent.prevButton = (ImageButton) findViewById(R.id.prev);
		AppConstant.PlayComponent.nextButton = (ImageButton) findViewById(R.id.next);

		AppConstant.PlayComponent.playMode = (ImageButton) findViewById(R.id.play_mode);
		AppConstant.PlayComponent.returnBack = (ImageButton) findViewById(R.id.return_back);
		AppConstant.PlayComponent.playerBg = (RelativeLayout) findViewById(R.id.play_view);
		lyricView = (LyricTextView) findViewById(R.id.lyric);
		AppConstant.PlayComponent.timeSeekBar = (SeekBar) findViewById(R.id.seekbar);
		AppConstant.PlayComponent.countTime = (TextView) findViewById(R.id.increase_time);
		AppConstant.PlayComponent.mp3Time = (TextView) findViewById(R.id.mp3_time);

	}

	/** 为播放组件注册监听器 */
	private void registerMonitor() {
		miniPlayControlBar.setOnClickListener(new MiniPlayControlListener());
		AppConstant.PlayComponent.miniPlayBtn
				.setOnClickListener(new PlayButtonListener());
		AppConstant.PlayComponent.miniPrevBtn
				.setOnClickListener(new PrevButtonListener());
		AppConstant.PlayComponent.miniNextBtn
				.setOnClickListener(new NextButtonListener());
		AppConstant.PlayComponent.playButton
				.setOnClickListener(new PlayButtonListener());
		AppConstant.PlayComponent.prevButton
				.setOnClickListener(new PrevButtonListener());
		AppConstant.PlayComponent.nextButton
				.setOnClickListener(new NextButtonListener());
		AppConstant.PlayComponent.playMode
				.setOnClickListener(new PlayModeListener());
		AppConstant.PlayComponent.returnBack
				.setOnClickListener(new ReturnBackListener());
		AppConstant.PlayComponent.timeSeekBar
				.setOnSeekBarChangeListener(new SeekBarListener());
	}

	/** 播放模式监听器 */
	class PlayModeListener implements OnClickListener {
		public void onClick(View v) {
			switch (modeValue) {
			case AppConstant.PlayMode.SEQUENCE_MODE:
				AppConstant.PlayComponent.playMode
						.setImageResource(R.drawable.random_mode);
				modeValue = AppConstant.PlayMode.RANDOM_MODE;
				setPlayModeFactory(RandomPlayMode.factory);
				Toast.makeText(MainActivity.this, R.string.random_mode, Toast.LENGTH_SHORT)
						.show();
				break;
			case AppConstant.PlayMode.RANDOM_MODE:
				AppConstant.PlayComponent.playMode
						.setImageResource(R.drawable.single_mode);
				modeValue = AppConstant.PlayMode.SINGLE_MODE;
				setPlayModeFactory(SinglePlayMode.factory);
				Toast.makeText(MainActivity.this, R.string.single_mode, Toast.LENGTH_SHORT)
						.show();
				break;
			case AppConstant.PlayMode.SINGLE_MODE:
				AppConstant.PlayComponent.playMode
						.setImageResource(R.drawable.sequence_mode);
				modeValue = AppConstant.PlayMode.SEQUENCE_MODE;
				setPlayModeFactory(SequencePlayMode.factory);
				Toast.makeText(MainActivity.this, R.string.sequence_mode, Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	}

	public static LyricTextView getLyricView() {
		return lyricView;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.exit);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0: exitApp();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && isMainUI) {
			if (viewFlipper.getChildCount() == 1) {
				Intent intent = new Intent(Intent.ACTION_MAIN); 
		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意  
		        intent.addCategory(Intent.CATEGORY_HOME); 
		        startActivity(intent); 
		        return true; 
			} else if (viewFlipper.getChildCount() == 2) {
				viewFlipper.removeView(viewFlipper.getCurrentView());
				return true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_BACK && !isMainUI) {
			appFlipper.showPrevious();
			isMainUI = true;
			return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	/** 退出程序 */
	private void exitApp() {
		TrayNotification.removeNotification(this);

		Intent intent = new Intent(this, PlayerService.class);
		stopService(intent);

		finish();
		System.exit(0);
	}
}
