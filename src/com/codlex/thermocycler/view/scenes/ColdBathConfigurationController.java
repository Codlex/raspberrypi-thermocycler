package com.codlex.thermocycler.view.scenes;

import com.codlex.thermocycler.logic.bath.Bath;

public class ColdBathConfigurationController extends AbstractBathController {
	
	@Override
	protected Bath getBath() {
		return this.thermocycler.getColdBath();
	}
	
	
	@Override
	protected void onNextClick() {
		this.thermocycler.start();
	}
}
