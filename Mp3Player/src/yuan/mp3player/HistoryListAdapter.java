package yuan.mp3player;

import java.util.List;

import yuan.database.SearchHistoryDBManager;
import yuan.model.SearchHistoryInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class HistoryListAdapter extends BaseAdapter{
	private List<SearchHistoryInfo> searchHistoryInfos = null;	
	private LayoutInflater inflater =null;
	private SearchHistoryDBManager dbManager = null;
	
	public HistoryListAdapter(Context context, List<SearchHistoryInfo> searchHistoryInfos,
			SearchHistoryDBManager dbManager){		
		this.searchHistoryInfos = searchHistoryInfos;
		this.dbManager = dbManager;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return searchHistoryInfos.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return searchHistoryInfos.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {  
			convertView = inflater.inflate(R.layout.search_history_item, null);  
		}
		ImageButton history_button_flag = (ImageButton) convertView.findViewById(R.id.search_history_flag);
		TextView history_search_word = (TextView)convertView.findViewById(R.id.search_history_word);
		ImageButton history_cancelBtn_item = (ImageButton) convertView.findViewById(R.id.clear_search_history_item);
		
		history_button_flag.setFocusable(false);
		history_search_word.setText(searchHistoryInfos.get(position).getSearchKey());
		history_cancelBtn_item.setFocusable(false);//这一句非常重要，有它才可以是列表项获得焦点
		
		//为删除按钮 添加监听器
		history_cancelBtn_item.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {								
				cancelItem(position);
			}			
		});
		
		return convertView;
	}
	
	/**
	 * 删去列表所有的项
	 * 同时也删去数据库中的表，然后再新建一个名字一样的表
	 * 最后更新列表
	 */
	public void canceAllItems(){
		dbManager.drop();
		searchHistoryInfos.clear();
		notifyDataSetChanged();
	}
	
	/**
	 * 删去列表中位置是position一项
	 * 同时也删去数据库中该项的记录
	 * 最后更新列表
	 * @param position
	 */
	public void cancelItem(int position){
		dbManager.cancel(searchHistoryInfos.get(position).getSearchKey());
		searchHistoryInfos.remove(position);
		notifyDataSetChanged();
	}	
}
