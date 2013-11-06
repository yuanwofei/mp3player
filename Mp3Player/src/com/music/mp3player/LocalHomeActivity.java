package com.music.mp3player;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.music.R;
import com.music.constant.MusicContant;
import com.music.constant.MusicPlayer;
import com.music.mp3player.service.PlayService;
import com.music.utils.FileUtils;

public class LocalHomeActivity extends Activity{
	
	private static final String TAG = "LocalHomeActivity";
	
	private ViewFlipper viewFlipper = null;
	private GridView gridview = null;
	private ListView listView = null;
	private View localMusicView = null; 
	
	private int[] imageId = null;
	private int[] titleId = null;
	private int[] titleStateId = null;
	
	private ArrayList<Music> mMusics = null;
	
	/**全部歌曲*/
	private final static int HOME_ALLMUSIC = 0;
	/**歌手*/
	private final static int HOME_SINGER = 1;
	/**专辑*/
	private final static int HOME_ALBUM = 2;
	/**文件夹*/
	private final static int HOME_FOLDER = 3;
	/**我的最爱*/
	private final static int HOME_FAVORITE = 4;
	/**最近添加*/
	private final static int HOME_CURRENTADD = 5;
	/**新建列表*/
	private final static int HOME_NEWLIST = 6;

	private LocalHomeGridViewAdapter gridViewAdapter = null;	
	
	boolean isPlaying = false;
	
	boolean isCanBackToDesktop = false; //标志用户按下返回键时，如果为true,就可以返回到桌面
	
	protected void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.local_home);
		initComponent();
		registerListener();
		initAdapter();
		mMusics = FileUtils.getMusics(this);		
	}
		
	class MusicListItemClick implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int index,
				long id) {
			if(mMusics != null) {		
				MusicPlayer.isFirstPlaying =true;
				
				Intent intent = new Intent();			
				intent.putExtra(MusicContant.PLAY_STATE, MusicContant.PlayState.PLAY);
				intent.putExtra(MusicContant.CURRENT_MUSIC_INDEX, index);				
				intent.setClass(LocalHomeActivity.this, PlayService.class);
				if (!isPlaying) {
					intent.addFlags(MusicContant.MusicList.UPDATE_MUSICS);
					intent.putExtra(MusicContant.MUSICS, mMusics);	
				}
				startService(intent);				
			}			
		}		
	}	

	class GridViewItemClickListener implements OnItemClickListener{
		public void onItemClick(AdapterView<?> parent,View view, int position,
				long id) {
			switch(position) {
				case HOME_ALLMUSIC : openAllMusicList(); break;
				case HOME_SINGER : break;
				case HOME_ALBUM : break;
				case HOME_FOLDER : break;
				case HOME_FAVORITE : break;
				case HOME_CURRENTADD : break;
				case HOME_NEWLIST : break;
			}
		}		
	}
	
	private void openAllMusicList() {
		localMusicView = getLayoutInflater().inflate(R.layout.local_home_music_list, null);		
		listView = (ListView)localMusicView.findViewById(R.id.song_listview);
		listView.setOnItemClickListener(new MusicListItemClick());
		viewFlipper.addView(localMusicView);	
		viewFlipper.showNext();
		mMusics = FileUtils.getMusics(this);
		LocalMusicList listAdapter = new LocalMusicList(this ,mMusics);		
		listView.setAdapter(listAdapter);		
	}
	
	class LocalMusicList extends BaseAdapter{
		
		private List<Music> mMusics = null;
		private LayoutInflater inflater = null;
		private TextView songName = null;
		private TextView singerName = null;
		private ImageView listMeunImg = null;
		
		public LocalMusicList(Context context, List<Music> musics) {
			this.mMusics = musics;
			this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {	
			return mMusics.size();
		}

		public Object getItem(int position) {
			return mMusics.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				convertView = inflater.inflate(R.layout.local_home_music_list_item, null);
			}
			initComponent(convertView);
			setComponent(position);
			return convertView;
		}
		
		private void initComponent(View convertView) {
			songName = (TextView)convertView.findViewById(R.id.list_song_name);
			singerName = (TextView)convertView.findViewById(R.id.list_singer_name);
			listMeunImg = (ImageView)convertView.findViewById(R.id.list_item_meun);
		}
		
		private void setComponent(int position) {
			songName.setText(mMusics.get(position).getMp3SimpleName());
			singerName.setText(mMusics.get(position).getSingerName());
			listMeunImg.setImageResource(R.drawable.list_item_menu_normal);
		} 
	}	
		
	private void initComponent() {		
		viewFlipper = (ViewFlipper) findViewById(R.id.local_home_viewflipper);
		gridview = (GridView) findViewById(R.id.local_home_gridview);	
		imageId = new int[] {R.drawable.home_allmusic, R.drawable.home_singer, R.drawable.home_album,
				R.drawable.home_folder, R.drawable.home_favorite, R.drawable.home_currentadd,
				R.drawable.home_newlist};
		titleId = new int[] {R.string.home_allmusic, R.string.home_singer, R.string.home_album,
				R.string.home_folder, R.string.home_favorite, R.string.home_currentadd,
				R.string.home_newlist};
		titleStateId = new int[] {R.string.home_allmusic_state, R.string.home_singer_state,
				R.string.home_album_state, R.string.home_folder_state, R.string.home_favorite_state,
				R.string.home_currentadd_state, R.string.home_newlist_state};
		MainActivity.viewFlipper = this.viewFlipper;
	}
	
	private void initAdapter() {
		gridViewAdapter = new LocalHomeGridViewAdapter(this, imageId, titleId, titleStateId);
		gridview.setAdapter(gridViewAdapter);		
	}	
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.i(TAG, "LocalHomeActivity");
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			if (viewFlipper.getChildCount() == 1) {
				if (!isCanBackToDesktop) {
					Toast.makeText(this, "再按一次返回到桌面", Toast.LENGTH_SHORT).show();
					isCanBackToDesktop = true;
					final Timer timer = new Timer(); //如果用户在两秒之内没有按两下返回键，就重置
					timer.schedule(new TimerTask() {						
						@Override
						public void run() {
							isCanBackToDesktop = false;
							timer.cancel();
						}
					}, 3000);
					return true;
				}
				Intent intent = new Intent(Intent.ACTION_MAIN); 
		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意  
		        intent.addCategory(Intent.CATEGORY_HOME); 
		        startActivity(intent); 
		        return true; 
	        } else if (viewFlipper.getChildCount() == 2) {
				viewFlipper.removeView(viewFlipper.getCurrentView());
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}
	
	private void registerListener() {		
		gridview.setOnItemClickListener(new GridViewItemClickListener());	
	}
}
