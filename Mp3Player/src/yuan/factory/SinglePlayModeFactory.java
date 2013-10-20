package yuan.factory;

import yuan.factory.model.AbstractPlayMode;
import yuan.factory.model.SinglePlayMode;

public class SinglePlayModeFactory implements IPlayModeFactory {

	@Override
	public AbstractPlayMode createPlayMode() {		
		return new SinglePlayMode();
	}

}
