package yuan.mp3player;

import yuan.notification.TrayNotification;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ViewFlipper;

public class LocalHomeActivity extends Activity{
	
	private ViewFlipper viewFlipper = null;
	private GridView gridview = null;
	private ListView listView = null;
	private View secondView = null; //
	private int[] imageId = null;
	private int[] titleId = null;
	private int[] titleStateId = null;
	
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
	
	protected void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.local_home);
		initComponent();
		registerListener();
		initAdapter();		
	}
			
	private void initAdapter() {
		gridViewAdapter = new LocalHomeGridViewAdapter(this, imageId, titleId, titleStateId);
		gridview.setAdapter(gridViewAdapter);
//		gridview.setSelector(new ColorDrawable(Color.TRANSPARENT)); 
	}
	
	private class GridViewItemClickListener implements OnItemClickListener{
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
		secondView = getLayoutInflater().inflate(R.layout.local_home_song_list, null);																
		viewFlipper.addView(secondView);				
		viewFlipper.showNext();
		listView = (ListView)findViewById(R.id.list_view);
		new LocalMp3List(LocalHomeActivity.this, listView);	
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
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(Intent.ACTION_MAIN); 
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意  
	        intent.addCategory(Intent.CATEGORY_HOME); 
	        startActivity(intent); 
	        return true; 
		}
		return super.onKeyUp(keyCode, event);
	}
	
	private void registerListener() {		
		gridview.setOnItemClickListener(new GridViewItemClickListener());	
	}
}
