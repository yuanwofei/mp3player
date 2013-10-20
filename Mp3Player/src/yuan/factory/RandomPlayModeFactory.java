package yuan.factory;

import yuan.factory.model.AbstractPlayMode;
import yuan.factory.model.RandomPlayMode;

public class RandomPlayModeFactory implements IPlayModeFactory {

	@Override
	public AbstractPlayMode createPlayMode() {
		// TODO Auto-generated method stub
		return new RandomPlayMode();
	}

}
