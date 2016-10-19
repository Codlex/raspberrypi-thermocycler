package com.codlex.thermocycler.view.scenes;

import com.codlex.thermocycler.logic.bath.Bath;

public class HotBathConfigurationController extends AbstractBathController {

	@Override
	protected Bath getBath() {
		return this.thermocycler.getHotBath();
	}

}
