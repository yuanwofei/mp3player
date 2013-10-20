package yuan.model;

import java.io.Serializable;
import java.util.List;

/**
 * 获取前后一首歌的位置
 * @author Administrator
 *
 */
public abstract class AbstractPlayMode implements Serializable{
		
	private static final long serialVersionUID = 1L;
	/**获取前一首歌的位置*/
	public abstract int getBeforePosition(int position, List<Mp3Info> mp3Infos);
	/**获取后一首歌的位置*/
	public abstract int getAfterPosition(int position, List<Mp3Info> mp3Infos);
}
