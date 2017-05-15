package com.gemengine.component.base;

import com.gemengine.component.Component;
import com.gemengine.listener.EntityComponentListener;
import com.gemengine.system.ComponentSystem;

import lombok.Getter;

/**
 * A notifier component extends an ({@link OwnedComponent} and implements an
 * {@link EntityComponentListener} and adds itself as the listener. It listens
 * to changes made to other components on the same entity as its owner.
 * 
 * @author Dragos
 *
 */
public abstract class NotifyComponent extends OwnedComponent implements EntityComponentListener {
	@Getter
	private final int priority;

	public NotifyComponent(ComponentSystem componentSystem, int priority) {
		super(componentSystem);
		this.priority = priority;
	}

	public NotifyComponent(ComponentSystem componentSystem) {
		super(componentSystem);
		priority = 0;
	}

	@Override
	public void onCreate() {
		componentSystem.addEntityComponentListener(this);
	}

	@Override
	public <T extends Component> void onNotify(String arg0, T arg1) {
	}
}
