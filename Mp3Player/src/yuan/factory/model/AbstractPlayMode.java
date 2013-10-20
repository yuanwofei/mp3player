package yuan.factory.model;

/**
 * 获取前后一首歌的位置
 * @author Administrator
 *
 */
public abstract class AbstractPlayMode {

	/**获取前一首歌的位置*/
	public abstract int preSongIndex(int index, int size);
	/**获取后一首歌的位置*/
	public abstract int nextSongIndex(int index, int size);
}
