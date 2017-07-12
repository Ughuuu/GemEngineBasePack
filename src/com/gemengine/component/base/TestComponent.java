package com.gemengine.component.base;

import com.gemengine.system.ComponentSystem;
import com.gemengine.system.EntitySystem;
import com.gemengine.system.base.TagSystem;
import com.google.inject.Inject;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TestComponent extends ScriptComponent {

	@Inject
	public TestComponent(EntitySystem entitySystem, ComponentSystem componentSystem) {
		super(entitySystem, componentSystem);
	}
	
	@Override
	public void onUpdate(float delta){
		//log.debug("a");
	}
}
