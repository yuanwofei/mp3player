package yuan.factory.model;

public class SearchHistoryInfo {
	private int _id = 0;
	private String searchKey = null;
	
	
	public SearchHistoryInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SearchHistoryInfo(int _id, String searchKey) {
		super();
		this._id = _id;
		this.searchKey = searchKey;
	}
	
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getSearchKey() {
		return searchKey;
	}
	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	@Override
	public String toString() {
		return "SearchHistoryInfos [_id=" + _id + ", searchKey=" + searchKey
				+ "]";
	}		
}
