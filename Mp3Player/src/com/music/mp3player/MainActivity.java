package com.music.mp3player;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
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

import com.music.R;
import com.music.constant.MusicContant;
import com.music.image.LoadImageThread;
import com.music.lyric.LyricTextView;
import com.music.mp3player.service.PlayService;
import com.music.notification.TrayNotification;
import com.music.seekbar.SeekBarListener;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements OnClickListener{

	private static final String TAG = "MainActivity";
	
	private Music mMusic = null;

	
	private int modeCode = MusicContant.PlayMode.CYCLE;

	public ViewFlipper appFlipper = null;
	public static ViewFlipper viewFlipper = null;
	private RelativeLayout miniPlayControlBar = null;
	private String title[] = {
		"本地音乐", 
		"我的收藏", 
		"在线音乐" 
	};
	private View localView, onlineView, searchView;
	private View view[] = {
		localView, 
		onlineView, 
		searchView 
	};
	private Class<?> className[] = {
		LocalHomeActivity.class,
		OnlineActivity.class, 
		SearchActivity.class 
	};

	private TextView singerName;
	private TextView musicName;		
	private TextView countTime;
	private TextView mp3Time;
	
	private TextView miniSingerName;
	private TextView miniSongName;
	private TextView miniCountTime;	
	private TextView miniMp3Time;
				
	private ImageButton playBtn;
	private ImageButton prevBtn;
	private ImageButton nextBtn;
	
	private ImageButton miniPlayBtn;
	private ImageButton miniPrevBtn;
	private ImageButton miniNextBtn;
	
	private ImageButton returnBack;
	private ImageButton playMode;
	private ImageView miniSingerBg;
	private SeekBar timeSeekBar;
	private ImageView singerBg;
	
	private static LyricTextView lyricView = null;
	private boolean isMainUI = true;	
	
	UpdateUiReceiver mReceiver;
	LoadImageThread mLoadImageThread = null;

	class UpdateUiReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int flag = intent.getFlags();
			switch(flag) {
			case 0x11 :
				 playBtn.setImageResource(R.drawable.play);
				 miniPlayBtn.setImageResource(R.drawable.play);				
				 break;			 
			
			case 0x12 :
				 playBtn.setImageResource(R.drawable.pause);
				 miniPlayBtn.setImageResource(R.drawable.pause);
				 break;			 
			
			case 0x13 : //PlayService更新UI组件
				 String musicSimpleName = intent.getStringExtra("musicSimpleName");
				 String singerNames = intent.getStringExtra("singerName");
				 musicName.setText(musicSimpleName);
				 miniSongName.setText(musicSimpleName);
				 singerName.setText(singerNames);
				 miniSingerName.setText(singerNames);
				 singerBg.setImageBitmap(null);
				 miniSingerBg.setImageBitmap(null);

				 mMusic = (Music) intent.getSerializableExtra(MusicContant.MUSIC);
				 mLoadImageThread = new LoadImageThread(mMusic, MainActivity.this, false);
				 mLoadImageThread.start();
				 break;				 
			
			case 0x14 :
				 String duration = intent.getStringExtra("duration");
				 mp3Time.setText(duration);
				 miniMp3Time.setText(" - " + duration);
				 break;
				
			case 0x15 :
				 singerBg.setImageBitmap(mLoadImageThread.getBigBitmap());
				 miniSingerBg.setImageBitmap(mLoadImageThread.getMiniBitmap());
				 mLoadImageThread.freeBitmap();
				 break;	
				 
			case 0x16 :
				 float processRate = intent.getFloatExtra("processRate", 1f);
				 String currentMusicTime = intent.getStringExtra("currentMusicTime");
				 timeSeekBar.setProgress((int)(processRate * timeSeekBar.getMax()));
				 
				 countTime.setText(currentMusicTime);
				 miniCountTime.setText(currentMusicTime);
				 break;
			case 0x17 : //更换歌手图片，在LyricTextView中，mMusic来自0x13
				mLoadImageThread = new LoadImageThread(mMusic, MainActivity.this, true);
				mLoadImageThread.start();
			}
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {			
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
		
		//mMusics = FileUtils.getMusics(this);
		
		mReceiver = new UpdateUiReceiver();
		registerReceiver(mReceiver, new IntentFilter(MusicContant.UPDATE_UI_ACTION));
		
		initComponent();
		registerMonitor();
		TabHost tabHost = getTabHost();
		for (int i = 0; i < title.length; i++) {
			view[i] = (View) LayoutInflater.from(this).inflate(R.layout.tab_item, null);
			TextView text = (TextView) view[i].findViewById(R.id.tab_label);
			text.setText(title[i]);
			text.setTextSize(17);
			tabHost.addTab(tabHost.newTabSpec(title[i]).setIndicator(view[i])
					.setContent(new Intent(this, className[i])));
		}	
		startService(new Intent(this, PlayService.class)); //初始化PlayService
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

	@Override	
	public void onClick(View v) {
		Intent intent = new Intent();
		intent.setClass(this, PlayService.class);
		switch(v.getId()) {
			case R.id.mini_play :
			case R.id.play :
				 intent.putExtra(MusicContant.PLAY_STATE, MusicContant.PlayState.PLAY);
				 break;
				 
			case R.id.mini_prev :
			case R.id.prev :					
				 intent.putExtra(MusicContant.PLAY_STATE, MusicContant.PlayState.PREV);
				 break;	
				 
			case R.id.mini_next :
			case R.id.next :
				 intent.putExtra(MusicContant.PLAY_STATE, MusicContant.PlayState.NEXT);
				 break;
		}
		startService(intent);		
	}
	

	/** 初始化各种组件 */
	private void initComponent() {
		appFlipper = (ViewFlipper) findViewById(R.id.app_viewflipper);

		miniPlayControlBar = (RelativeLayout) findViewById(R.id.mini_play_control);
		
		miniPlayBtn = (ImageButton) findViewById(R.id.mini_play);
		miniPrevBtn = (ImageButton) findViewById(R.id.mini_prev);
		miniNextBtn = (ImageButton) findViewById(R.id.mini_next);
		miniSongName = (TextView) findViewById(R.id.mini_song_name);
		miniSingerName = (TextView) findViewById(R.id.mini_singer_name);
		miniCountTime = (TextView) findViewById(R.id.mini_counttime_name);
		miniMp3Time = (TextView) findViewById(R.id.mini_songtime_name);
		miniSingerBg = (ImageView) findViewById(R.id.mini_singer_bg);

		singerName = (TextView) findViewById(R.id.singer_name);
		musicName = (TextView) findViewById(R.id.song_name);
		playBtn = (ImageButton) findViewById(R.id.play);
		prevBtn = (ImageButton) findViewById(R.id.prev);
		nextBtn = (ImageButton) findViewById(R.id.next);

		playMode = (ImageButton) findViewById(R.id.play_mode);
		returnBack = (ImageButton) findViewById(R.id.return_back);
		singerBg = (ImageView) findViewById(R.id.singer_bg);
		lyricView = (LyricTextView) findViewById(R.id.lyric);
		timeSeekBar = (SeekBar) findViewById(R.id.seekbar);
		countTime = (TextView) findViewById(R.id.increase_time);
		mp3Time = (TextView) findViewById(R.id.mp3_time);

	}

	/** 为播放组件注册监听器 */
	private void registerMonitor() {
		miniPlayControlBar.setOnClickListener(new MiniPlayControlListener());
		miniPlayBtn.setOnClickListener(this);
		miniPrevBtn.setOnClickListener(this);
		miniNextBtn.setOnClickListener(this);
		playBtn.setOnClickListener(this);
		prevBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		
		playMode.setOnClickListener(new PlayModeListener());
		returnBack.setOnClickListener(new ReturnBackListener());
		timeSeekBar.setOnSeekBarChangeListener(new SeekBarListener());
	}

	/** 播放模式监听器 */
	class PlayModeListener implements OnClickListener {
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setAction(MusicContant.PLAY_MODE_ACTION);
			switch (modeCode) {
				case MusicContant.PlayMode.CYCLE :
					playMode.setImageResource(R.drawable.playmode_2);
					modeCode = MusicContant.PlayMode.SINGLE;		
					Toast.makeText(MainActivity.this, R.string.single_mode, Toast.LENGTH_SHORT).show();
					break;
				
				case MusicContant.PlayMode.SINGLE :
					playMode.setImageResource(R.drawable.playmode_3);
					modeCode = MusicContant.PlayMode.SEQUENCE;
					Toast.makeText(MainActivity.this, R.string.sequence_mode, Toast.LENGTH_SHORT).show();
					break;	
				
				case MusicContant.PlayMode.SEQUENCE :
					playMode.setImageResource(R.drawable.playmode_4);
					modeCode = MusicContant.PlayMode.RANDOM;
					Toast.makeText(MainActivity.this, R.string.random_mode, Toast.LENGTH_SHORT).show();
					break;

				case MusicContant.PlayMode.RANDOM :
					playMode.setImageResource(R.drawable.playmode_1);
					modeCode = MusicContant.PlayMode.CYCLE;
					Toast.makeText(MainActivity.this, R.string.cycle_mode, Toast.LENGTH_SHORT).show();
					break;	
				}
			intent.putExtra(MusicContant.PLAY_MODE, modeCode);
			sendBroadcast(intent);
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
		Log.i(TAG, "MainActivity");
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
		
		unregisterReceiver(mReceiver);
		
		Intent intent = new Intent(this, PlayService.class);
		stopService(intent);

		finish();
		System.exit(0);
	}
}
