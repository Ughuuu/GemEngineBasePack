package com.gemengine.component.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gemengine.component.Component;
import com.gemengine.entity.Entity;
import com.gemengine.system.ComponentSystem;

/**
 * A component that knows which is it's owner entity. Has a function
 * {@link getOwner} used to get the owning entity. It also can notify the other
 * components the owner has with {@link doNotify}.
 * 
 * @author Dragos
 *
 */
public abstract class OwnedComponent extends Component {
	@JsonIgnore
	protected final ComponentSystem componentSystem;

	public OwnedComponent(ComponentSystem componentSystem) {
		this.componentSystem = componentSystem;
	}

	/**
	 * Get the entity that owns this component.
	 * 
	 * @return
	 */
	@JsonIgnore
	public Entity getOwner() {
		return componentSystem.getOwner(getId());
	}

	/**
	 * Use this if you make a change to an entity and want to notify other
	 * components under the same owner.
	 * 
	 * @param event
	 */
	protected void doNotify(String event) {
		componentSystem.notifyFrom(event, this);
	}
}
