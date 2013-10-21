package yuan.factory.model;

import yuan.factory.PlayModeFactory;

/**
 * 随机获取前后一首歌的位置
 * @author Administrator
 */
public class RandomPlayMode extends AbstractPlayMode {
	
	private RandomPlayMode() {
	}

	@Override
	public int preSongIndex(int index, int size) {
		//Math.random()*(b-a)+a从a - b之间随机选取一个数
		index = (int) (Math.random()*(size - 1));
		return index;
	}

	@Override
	public int nextSongIndex(int index, int size) {
		index = (int) (Math.random()*(size - 1));
		return index;
	}
	
	public static PlayModeFactory factory = new PlayModeFactory() {	
		@Override
		public AbstractPlayMode createPlayMode() {			
			return new RandomPlayMode();
		}
	};
}
