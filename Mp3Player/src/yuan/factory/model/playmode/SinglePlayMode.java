package yuan.factory.model.playmode;

import yuan.factory.PlayModeFactory;

public class SinglePlayMode extends AbstractPlayMode {

	private SinglePlayMode() {
	}

	@Override
	public int preSongIndex(int index, int size) {
		return index;
	}

	@Override
	public int nextSongIndex(int index, int size) {
		return index;
	}
	
	public static PlayModeFactory factory = new PlayModeFactory() {		
		@Override
		public AbstractPlayMode createPlayMode() {
			return new SinglePlayMode();
		}
	};
}
