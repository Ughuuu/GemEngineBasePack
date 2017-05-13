package com.gemengine.component.base;

import com.gemengine.component.Component;
import com.gemengine.entity.Entity;
import com.gemengine.system.ComponentSystem;

/**
 * 
 * @author Dragos
 *
 */
public abstract class OwnedComponent extends Component {
	protected final ComponentSystem componentSystem;

	public OwnedComponent(ComponentSystem componentSystem) {
		this.componentSystem = componentSystem;
	}

	public Entity getOwner() {
		return componentSystem.getOwner(getId());
	}

	protected void doNotify(String event) {
		componentSystem.notifyFrom(event, this);
	}
}
