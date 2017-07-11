package com.gemengine.component.base;

import com.badlogic.gdx.InputProcessor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gemengine.component.Component;
import com.gemengine.entity.Entity;
import com.gemengine.system.ComponentSystem;
import com.gemengine.system.EntitySystem;
import com.gemengine.system.manager.SystemManager;

public abstract class InputScriptComponent extends ScriptComponent implements InputProcessor {
	public InputScriptComponent(EntitySystem entitySystem, ComponentSystem componentSystem,
			SystemManager systemManager) {
		super(entitySystem, componentSystem);
		systemManager.addInputProcessor(this);
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
