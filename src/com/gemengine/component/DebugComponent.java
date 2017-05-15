package com.gemengine.component;

import java.util.Set;

import com.gemengine.component.base.OwnedComponent;
import com.gemengine.entity.Entity;
import com.gemengine.listener.ComponentListener;
import com.gemengine.listener.EntityListener;
import com.gemengine.system.ComponentSystem;
import com.gemengine.system.EntitySystem;
import com.gemengine.system.helper.ListenerHelper;
import com.google.inject.Inject;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DebugComponent extends OwnedComponent implements ComponentListener, EntityListener {
	@Getter
	private int priority = 0;
	@Getter
	private Set<String> configuration = ListenerHelper.createConfiguration(Component.class);
	private final ComponentSystem componentSystem;

	@Inject
	public DebugComponent(ComponentSystem componentSystem, EntitySystem entitySystem) {
		super(componentSystem);
		this.componentSystem = componentSystem;
		entitySystem.addEntityListener(this);
		componentSystem.addComponentListener(this);
	}

	@Override
	public <T extends Component> void onChange(ComponentChangeType arg0, T arg1) {
		debug(arg0.toString(), arg1);
	}

	@Override
	public void onChange(EntityChangeType changeType, Entity arg1, Entity arg2) {
		log.debug("--------------------------------------------------");
		log.debug("Entity Event : {}", changeType.toString().toLowerCase());
		switch (changeType) {
		case ADD:
		case DELETE:
			log.debug("Entity name : {}", arg1);
			break;
		case DEPARENTED:
		case PARENTED:
			log.debug("Entity parent : {} child : {}", arg1, arg2);
			break;
		}
	}

	@Override
	public <T extends Component> void onNotify(String arg0, T arg1) {
		debug(arg0, arg1);
	}

	@Override
	public <T extends Component> void onTypeChange(Class<T> arg0) {
	}

	private void debug(String change, Component component) {
		log.debug("--------------------------------------------------");
		log.debug("Component Event: " + change.toLowerCase());
		Entity owner = componentSystem.getOwner(component.getId());
		log.debug("Entity {}", owner);
		log.debug("Component {}", component);
	}
}
