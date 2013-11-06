package com.music.mp3player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.music.R;
import com.music.constant.MusicContant;
import com.music.constant.MusicPlayer;
import com.music.factory.HttpApiFactory;
import com.music.factory.model.http.CommonHttpApi;
import com.music.mp3player.service.PlayService;
import com.music.utils.Network;

public class SearchActivity extends ListActivity implements OnScrollListener {

	private final static int SEARCH_CODE = 1; // 第一次搜索时的识别码
	private final static int SEARCH_MORE_CODE = 2; // 第二次或第二次以后搜索的识别码

	private ArrayList<Music> mMusics = null; // 存放第一次以后搜索数据
	private ArrayList<Music> mMoreMusics = null; // 存放第二次或第二次以后搜索数据
	private ListView searchListVive = null;
	private SearchListAdapter searchListAdapter = null;
	private SearchBroadcastReceiver searchReceiver = null;
	private View selectView = null;
	private EditText editView = null;
	private TextView mp3NumbersTextView = null;
	private TextView search_more_text = null;
	private ImageButton searchButton = null;
	private ImageButton searchMoreButton = null;
	private View searchMoreView = null;
	private View searchResultStateView = null;

	private int visibleLastIndex = 0; // 最后的可视项索引
	private int visibleItemCount; // 当前窗口可见项总数

	private int page_no = 1;// 查询MP3返回的第一页xml文件
	private int page_size = 30;// 一个xml文件包含30首歌曲
	private String keyWord = null;// 搜索关键词

	boolean isPlaying = false;
	boolean isCanBackToDesktop = false; 
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		selectView = getLayoutInflater().inflate(
				R.layout.search_result_item_operate, null);
		// 搜索框
		editView = (EditText) findViewById(R.id.search_text);
		editView.setOnClickListener(new SearchEditListener());
		editView.setFocusable(false);

		// 搜索按钮
		searchButton = (ImageButton) findViewById(R.id.search_button);
		searchButton.setOnClickListener(new SearchButtonListener());

		// Listview的底部搜索更多视图
		searchMoreView = getLayoutInflater().inflate(R.layout.search_more_item,
				null);
		searchMoreView.setClickable(true);

		// Listview的头部视图，用于显示搜索结果总数
		searchResultStateView = getLayoutInflater().inflate(
				R.layout.search_result_info, null);

		// 在listview头部视图显示搜索结果总数
		mp3NumbersTextView = (TextView) searchResultStateView
				.findViewById(R.id.search_result_total);
		mp3NumbersTextView.setVisibility(View.INVISIBLE);

		// listview的底部视图的按钮和按钮上面的文字
		searchMoreButton = (ImageButton) searchMoreView
				.findViewById(R.id.search_more_button);
		searchMoreButton.setOnClickListener(new SearchMoreListener());
		search_more_text = (TextView) searchMoreView
				.findViewById(R.id.search_more_text);

		// 为listview添加头部和底部视图
		searchListVive = getListView();
		searchListVive.addHeaderView(searchResultStateView, null, false);
		searchListVive.setOnScrollListener(this);

		// 注册第一次搜索广播监听器
		searchReceiver = new SearchBroadcastReceiver();
		registerReceiver(searchReceiver, searchReceiver.getIntentFilter());
	}

	/**
	 * 点击EditView跳转到SearchActivity
	 */
	private class SearchEditListener implements OnClickListener {
		public void onClick(View v) {
			if (Network.isAccessNetwork(SearchActivity.this)) {
				Intent intent = new Intent();
				intent.setClass(SearchActivity.this,
						SearchHistoryActivity.class);
				SearchActivity.this.startActivity(intent);

			} else {
				Toast.makeText(SearchActivity.this, "当前还没联网",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 搜索按钮监听器，点击即进行搜索输入的关键词
	 * 
	 * @author Administrator
	 */
	private class SearchButtonListener implements OnClickListener {
		public void onClick(View v) {

		}
	}

	/**
	 * 从SearchActivity中发送搜索广播 进行第一次搜索的广播接收器
	 */
	private class SearchBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getStringExtra("keyWord") != null) {
				// 重置数据
				page_no = 1;
				keyWord = intent.getStringExtra("keyWord");
				// 开始搜索
				Toast.makeText(SearchActivity.this, "正在搜索...",
						Toast.LENGTH_SHORT).show();
				new SearchAsyncTask(SEARCH_CODE).execute("第一次");
			}
		}

		/** 广播过滤器，接收特定的广播 */
		public IntentFilter getIntentFilter() {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(MusicContant.SEARCH_KEY_WORD_ACTION);
			return intentFilter;
		}
	}

	/**
	 * 开始第一次搜索音乐，并把搜索到的结果以列表形式显示出来
	 * 
	 * @throws Exception
	 */
	private void search(String page_no, String page_size, String keyWord) {
		HttpApiFactory factory = CommonHttpApi.factory;
		Bundle bundle = new Bundle();
		bundle.putString("page_no", page_no);
		bundle.putString("page_size", page_size);
		bundle.putString("keyWord", keyWord);
		mMusics = factory.getHttpApi().execute(bundle, null);
	}

	private void updateList() {
		if (mMusics != null && !mMusics.isEmpty()) {
			initAdapter(mMusics); // 初始化SearchListAdapter,把Mp3Info中的数据添加在List中

			// 显示搜索结果
			mp3NumbersTextView.setVisibility(View.VISIBLE);
			mp3NumbersTextView.setText("找到相关结果 " + mMusics.get(0).getMp3Sum()
					+ " 篇");

			// 判断是否还有更多搜索，来设置listview底部视图是否显示
			if (searchListAdapter.getCount() < Integer.parseInt(mMusics.get(0)
					.getMp3Sum())) {
				searchListVive.removeFooterView(searchMoreView);
				searchListVive.addFooterView(searchMoreView);
			} else {
				searchListVive.removeFooterView(searchMoreView);
			}
		} else {
			Toast.makeText(this, "没有找到相关的内容", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 第一次搜索后，初始化SearchListAdapter,把Mp3Info中的数据添加在List中
	 * 
	 * @param mMusics
	 */
	private void initAdapter(List<Music> mMusics) {
		searchListAdapter = new SearchListAdapter(this, mMusics, selectView);
		this.setListAdapter(searchListAdapter);
	}

	/**
	 * 第二次以上的搜索
	 */
	private void searchMore() {
		HttpApiFactory factory = CommonHttpApi.factory;
		Bundle bundle = new Bundle();
		bundle.putString("page_no", ++page_no + "");
		bundle.putString("page_size", page_size + "");
		bundle.putString("keyWord", keyWord);
		mMoreMusics = factory.getHttpApi().execute(bundle, null);
	}

	/**
	 * 第二次以上搜索歌曲的 监听器
	 */
	public class SearchMoreListener implements OnClickListener {

		public void onClick(View v) {
			search_more_text.setText("载入中,请稍侯...");
			new SearchAsyncTask(SEARCH_MORE_CODE).execute("搜索更多");
		}
	}

	private void updateMoreList() {
		// 往searchlistAdapter添加搜索结果
		for (Iterator<Music> iterator = mMoreMusics.iterator(); iterator
				.hasNext();) {
			Music mp3Info = iterator.next();
			searchListAdapter.addItem(mp3Info);
		}
		search_more_text.setText("更多");
		searchListAdapter.notifyDataSetChanged(); // 数据集变化后,通知earchlistAdapter刷新列表项
		searchListVive.setSelection(visibleLastIndex - visibleItemCount + 1); // 设置选中项

		mMusics.addAll(mMoreMusics);
		
		Intent intent = new Intent();			
		intent.setClass(SearchActivity.this, PlayService.class);
		intent.addFlags(MusicContant.MusicList.INSERT_MUSICS);
		intent.putExtra(MusicContant.MUSICS, mMoreMusics);
		startService(intent);

		if (searchListAdapter.getCount() == Integer.parseInt(mMusics.get(0).getMp3Sum())) {
			searchListVive.removeFooterView(searchMoreView);
		}
	}

	/**
	 * 列表状态改变时调用
	 */
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		int itemsLastIndex = searchListAdapter.getCount() - 1; // 数据集最后一项的索引
		int lastIndex = itemsLastIndex + 1; // 加上底部的searchMoreView项
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& visibleLastIndex == lastIndex) {
			// 如果是自动加载,可以在这里放置异步加载数据的代码
			System.out.println("------------>自动加载");
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
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
	}

	/**
	 * 滑动列表时调用
	 */
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.visibleItemCount = visibleItemCount;
		visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
	}

	/**
	 * 点击列表项的相关操作
	 * list前面加一个preView, index从1开始，不是从0开始
	 */
	protected void onListItemClick(ListView l, View v, int index, long id) {
		super.onListItemClick(l, v, index, id);	
		
		if(mMusics != null) {		
			MusicPlayer.isFirstPlaying =true;
			
			Intent intent = new Intent();			
			intent.putExtra(MusicContant.PLAY_STATE, MusicContant.PlayState.PLAY);
			intent.putExtra(MusicContant.CURRENT_MUSIC_INDEX, index - 1); 
			intent.setClass(this, PlayService.class);
			if (!isPlaying) {		
				intent.addFlags(MusicContant.MusicList.UPDATE_MUSICS);
				intent.putExtra(MusicContant.MUSICS, mMusics);	
			}
			startService(intent);	
		}
	}

	private class SearchAsyncTask extends AsyncTask<String, String, String> {
		int code = 0;

		public SearchAsyncTask(int code) {
			super();
			this.code = code;
		}

		@Override
		protected String doInBackground(String... params) {
			if (code == SEARCH_CODE) {
				// 第一次搜索
				search(Integer.toString(page_no), Integer.toString(page_size),
						keyWord);
			} else if (code == SEARCH_MORE_CODE) {
				// 第二次以上搜索
				searchMore();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (code == SEARCH_CODE) {
				// 第一次更新列表
				updateList();
			} else if (code == SEARCH_MORE_CODE) {
				// 第二次以上更新列表
				updateMoreList();
			}
		}
	}
}
