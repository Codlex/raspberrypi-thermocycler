package com.codlex.thermocycler.logic.bath;

import com.codlex.thermocycler.logic.bath.cold.ColdBath;
import com.codlex.thermocycler.logic.bath.hot.HotBath;

public class BathFactory {
	public static ColdBath createCold() {
		return new ColdBath();
	}

	public static HotBath createHot() {
		return new HotBath();
	}

}
