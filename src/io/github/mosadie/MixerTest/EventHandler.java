package io.github.mosadie.MixerTest;

import com.google.common.eventbus.Subscribe;
import com.mixer.interactive.event.control.input.ControlInputEvent;
import com.mixer.interactive.event.control.input.ControlMouseDownInputEvent;
import com.mixer.interactive.event.control.input.ControlMouseUpInputEvent;
import com.mixer.interactive.event.control.input.ControlMoveInputEvent;

public class EventHandler {
	
	@Subscribe
	public void TheOneThatWorks(ControlInputEvent event) {
		System.out.println("Control Input Event posted! ControlID: "+event.getControlInput().getControlID());
	}
	
	@Subscribe
	public void TheFirstOneThatDidNotWork(ControlMouseDownInputEvent event) {
		System.out.println("Control Mouse Down Input Event posted! ControlID: "+event.getControlInput().getControlID());
	}
	
	@Subscribe
	public void TheSecondOneThatDidNotWork(ControlMouseUpInputEvent event) {
		System.out.println("Control Mouse Up Input Event posted! ControlID: "+event.getControlInput().getControlID());
	}
	
	@Subscribe
	public void TheFinalOneThatDidNotWork(ControlMoveInputEvent event) {
		System.out.println("Control Move Input Event posted! ControlID: "+event.getControlInput().getControlID());
	}

}
