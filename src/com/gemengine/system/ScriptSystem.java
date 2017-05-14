package com.gemengine.system;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.gemengine.component.Component;
import com.gemengine.component.base.ScriptComponent;
import com.gemengine.entity.Entity;
import com.gemengine.listener.ComponentListener;
import com.gemengine.system.base.ComponentUpdaterSystem;
import com.gemengine.system.helper.ListenerHelper;
import com.google.inject.Inject;

import lombok.extern.log4j.Log4j2;

@Log4j2
/**
 * Processes all components that extend ScriptComponent. The components are
 * called in the onInit when they are started and then in the onUpdate function,
 * like if they were in an infinite while.
 * 
 * @author Dragos
 *
 */
public class ScriptSystem extends ComponentUpdaterSystem implements ComponentListener {

	private final ComponentSystem componentSystem;
	private final List<ScriptComponent> toInit = new ArrayList<ScriptComponent>();

	@SuppressWarnings("unchecked")
	@Inject
	public ScriptSystem(ComponentSystem componentSystem) {
		super(componentSystem, ListenerHelper.createConfiguration(ScriptComponent.class), true, 1);
		this.componentSystem = componentSystem;
		componentSystem.addComponentListener(this);
	}

	@Override
	public <T extends Component> void onChange(ComponentChangeType change, T comp) {
		switch (change) {
		case ADD:
			toInit.add((ScriptComponent) comp);
			break;
		case DELETE:
			break;
		}
	}

	@Override
	public void onNext(Entity ent) {
		List<ScriptComponent> scripts = componentSystem.get(ent, ScriptComponent.class);
		if (toInit.size() != 0) {
			for (ScriptComponent script : toInit) {
				try {
					script.setEnable(true);
					script.onInit();
				} catch (Throwable t) {
					log.warn("Script System update", t);
				}
			}
			toInit.clear();
		}
		for (ScriptComponent script : scripts) {
			if (!script.isEnable()) {
				continue;
			}
			try {
				script.onUpdate(Gdx.graphics.getDeltaTime());
			} catch (Throwable t) {
				script.setEnable(false);
				log.warn("Script System update", t);
			}
		}
	}

	@Override
	public <T extends Component> void onNotify(String arg0, T arg1) {
	}

	@Override
	public <T extends Component> void onTypeChange(Class<T> arg0) {
	}
}
