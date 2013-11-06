package com.music.mp3player;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.music.R;

import com.music.constant.MusicContant;
import com.music.database.SearchHistoryDBManager;
import com.music.factory.model.SearchHistoryInfo;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class SearchHistoryActivity extends ListActivity{
	private AutoCompleteTextView AutoCompleteView = null;
	private ImageButton searchButton = null;
	private ImageButton clearButton = null;
	private View clearHistoryView = null;
	private ListView listView = null;
	
	private SearchHistoryDBManager dbManager = null;
	private HistoryListAdapter historyListAdapter = null;
	private List<SearchHistoryInfo> searchHistoryInfos = null;
	public List<String> suggest;   
	private ArrayAdapter<String> autoCompleteAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
		
		setContentView(R.layout.search_history);
		AutoCompleteView = (AutoCompleteTextView)findViewById(R.id.search_text);
		AutoCompleteView.addTextChangedListener(new AutoCompleteTextListener());
		AutoCompleteView.setOnItemClickListener(new AutoSuggestionItemListener());
		AutoCompleteView.requestFocus();
		//搜索按钮
		searchButton = (ImageButton)findViewById(R.id.search_button);
		searchButton.setOnClickListener(new SearchButtonListener());
		
		//listview底部清除所有历史记录的视图
		clearHistoryView = getLayoutInflater().inflate(R.layout.search_history_clear_item, null);
		clearHistoryView.setClickable(true);
		
		//清除所有历史记录的按钮
		clearButton = (ImageButton) clearHistoryView.findViewById(R.id.clear_history_button);
		clearButton.setOnClickListener(new ClearAllListener());
		
		//listview添加底部视图
		listView = getListView();
		listView.addFooterView(clearHistoryView);
		
		//打开数据库，并返回查询结果
		dbManager = new SearchHistoryDBManager(this);		
		searchHistoryInfos = dbManager.query();	
		
		//初始化BaseAdapter，以列表形式显示查询结果
		initAdapter(searchHistoryInfos,dbManager);
		
		
	}
	
	/**
	 * 当Activity不可见时，或被摧毁时调用，以关闭数据库和摧毁Activity，一回到搜索结果Activity
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		dbManager.DBClose();
		finish();
	}

	/**
	 * 初始化BaseAdapter
	 * @param searchHistoryInfos
	 * @param dbManager
	 */
	private void initAdapter(List<SearchHistoryInfo> searchHistoryInfos, SearchHistoryDBManager dbManager) {
		historyListAdapter = new HistoryListAdapter(this, searchHistoryInfos, dbManager);		
		this.setListAdapter(historyListAdapter);
	}
	
	/**
	 * 删掉所有搜索记录
	 */
	private class ClearAllListener implements OnClickListener {
		public void onClick(View v) {		
			historyListAdapter.canceAllItems();			
		}		
	}
	
	/**
	 * 发送搜索广播
	 * @param keyWord
	 */
	private void sendSearchBroadcast(String keyWord) {
		Intent intent = new Intent();							
		intent.putExtra("keyWord", keyWord);
		intent.setAction(MusicContant.SEARCH_KEY_WORD_ACTION);
		sendBroadcast(intent);
	}
	
	/**
	 * 搜索按钮监听器，点击即进行搜索输入的关键词
	 * @author Administrator
	 */
	private class SearchButtonListener implements OnClickListener {
		public void onClick(View v) {
			String keyWord = AutoCompleteView.getText().toString();//得到搜索关键词			
			//不为空时发广播给SearchActivit进行搜索,并摧毁当前SearchHistoryActivity，以便回到原先搜索SearchActivity
			if(!keyWord.equals("")) {								
				sendSearchBroadcast(keyWord);
				dbManager.add(keyWord);
				SearchHistoryActivity.this.finish();
			} else {
				Toast.makeText(SearchHistoryActivity.this, "输入为空", Toast.LENGTH_SHORT).show();
			}
		}		
	}
	
	/**
	 * 点击列表项，进行对该项历史记录进行查询
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		sendSearchBroadcast(searchHistoryInfos.get(position).getSearchKey());
		finish();
	}
	
	/**
	 * 自动提示输入框的监听器
	 * @author Administrator
	 */
	private class AutoCompleteTextListener implements TextWatcher {

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			String newText = s.toString();  
			new getAutoCompleteText().execute(newText);			
		}

		public void afterTextChanged(Editable s) {

		}		
	}
	
	private class getAutoCompleteText extends AsyncTask<String,String,String>{  
		  
		@Override  
		protected void onPostExecute(String result) {  
		    super.onPostExecute(result);  
		    autoCompleteAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.search_keyword_autocomplete_item,suggest);  
		    AutoCompleteView.setAdapter(autoCompleteAdapter);  
		    autoCompleteAdapter.notifyDataSetChanged();
		}

		@Override
		protected String doInBackground(String... key) {		 
			 try{  
			     HttpClient hClient = new DefaultHttpClient();  
			     HttpGet hGet = new HttpGet("http://tingapi.ting.baidu.com/v1/restserver/" +
			     		"ting?method=baidu.ting.search.suggestion&format=json&query=" + key[0].trim());  
			     ResponseHandler<String> rHandler = new BasicResponseHandler();  
			     String data = hClient.execute(hGet, rHandler);
			     
			     suggest = new ArrayList<String>();
			     JSONObject jo = new JSONObject(data);            
		         JSONArray jsonArray = (JSONArray) jo.get("suggestion_list");            
		         for (int i = 0; i < jsonArray.length(); ++i) {
		        	 suggest.add((String) jsonArray.get(i));          
		         }	         			    
			     } catch (Exception e) {  
			      Log.w("SuggestionError", e.getMessage());  
			   }  
			 return null;			   
		}		
	}
	
	private class AutoSuggestionItemListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			sendSearchBroadcast(suggest.get(position));
			dbManager.add(suggest.get(position));
			SearchHistoryActivity.this.finish();		
		}		
	}
}
