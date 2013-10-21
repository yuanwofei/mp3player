package yuan.factory.model;

import yuan.factory.PlayModeFactory;

/**
 * 顺序获取前后一首歌的位置
 * @author Administrator
 */
public class SequencePlayMode extends AbstractPlayMode {

	private SequencePlayMode() {
	}

	@Override
	public int preSongIndex(int index, int size) {
		//System.out.println("当前歌曲的ID----before--->" + index);
		if(index - 1 >= 0) {
			index = index - 1;
		} else {
			index = size - 1;
		}
		//System.out.println("前一首歌曲的ID------->" + index);
		return index;
	}

	@Override
	public int nextSongIndex(int index, int size) {
		//System.out.println("当前歌曲的ID----after--->" + index);
		if(index + 1 < size) {
			index = index + 1;
		} else {
			index = 0;
		}
		//System.out.println("前一首歌曲的ID------->" + index);
		return index;		
	}

	public static PlayModeFactory factory = new PlayModeFactory() {		
		@Override
		public AbstractPlayMode createPlayMode() {
			return new SequencePlayMode();
		}
	};
}
