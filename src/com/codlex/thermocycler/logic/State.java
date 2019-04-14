package com.codlex.thermocycler.logic;

public enum State {
	NotStarted, NotReady, HotBath, ColdBath, ToColdBathPause, ToHotBathPause, Finished, UnexpectedShutdown;
}
