package com.gemengine.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.gemengine.component.TestComponent;
import com.gemengine.entity.Entity;
import com.gemengine.system.base.SystemBase;
import com.gemengine.system.base.TimedSystem;
import com.google.inject.Inject;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PerformanceTestSystem extends TimedSystem {
	private final EntitySystem entitySystem;
	private final ComponentSystem componentSystem;
	private int totalEntities = 1000000;
	private long startTime = 0;

	@Inject
	public PerformanceTestSystem(EntitySystem entitySystem, ComponentSystem componentSystem,
			ScriptSystem scriptSystem) {
		super();
		this.entitySystem = entitySystem;
		this.componentSystem = componentSystem;
	}

	private void startTest() {
		startTime = System.currentTimeMillis();
		log.debug("Test Started");
	}

	private void endTest() {
		log.debug("Test took : " + (System.currentTimeMillis() - startTime) + " ms");
	}

	@Override
	public void onInit() {
		boolean result = true;
		//startTest();
		//createMultipleComponents();
		//endTest();
	}

	boolean createMultipleEntities() {
		for (int i = 0; i < totalEntities; i++) {
			Entity ent = entitySystem.create("E" + i++);
			if (ent == null) {
				return false;
			}
		}
		return true;
	}

	boolean createMultipleComponents() {
		for (int i = 0; i < totalEntities; i++) {
			Entity ent = entitySystem.create("E" + i++);
			if (ent == null) {
				return false;
			}
			TestComponent testComponent = ent.createComponent(TestComponent.class);
			if (testComponent == null) {
				return false;
			}
		}
		return true;
	}

	boolean obtainMultipleEntities() {
		for (int i = 0; i < totalEntities; i++) {
			Entity ent = entitySystem.get("E" + i++);
			if (ent == null) {
				return false;
			}
		}
		return true;
	}

	boolean obtainMultipleComponents() {
		for (int i = 0; i < totalEntities; i++) {
			Entity ent = entitySystem.get("E" + i++);
			if (ent == null) {
				return false;
			}
			TestComponent testComponent = ent.getComponent(TestComponent.class);
			if (testComponent == null) {
				return false;
			}
		}
		return true;
	}
}
