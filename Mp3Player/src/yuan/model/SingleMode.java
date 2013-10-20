package yuan.model;

import java.util.List;

public class SingleMode extends AbstractPlayMode {

	private static final long serialVersionUID = 1L;

	@Override
	public int getBeforePosition(int position, List<Mp3Info> mp3Infos) {
		return position;
	}

	@Override
	public int getAfterPosition(int position, List<Mp3Info> mp3Infos) {
		return position;
	}
	
	/**返回单曲循环播放的那一首歌的位置*/
	public int getPosition(int position) {	
		return position;	
	}
}
