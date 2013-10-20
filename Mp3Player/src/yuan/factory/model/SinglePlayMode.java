package yuan.factory.model;

public class SinglePlayMode extends AbstractPlayMode {

	@Override
	public int preSongIndex(int index, int size) {
		return index;
	}

	@Override
	public int nextSongIndex(int index, int size) {
		return index;
	}
}
