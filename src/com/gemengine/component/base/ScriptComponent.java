package com.gemengine.component.base;

import com.badlogic.gdx.utils.Align;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gemengine.entity.Entity;
import com.gemengine.listener.EntityComponentListener;
import com.gemengine.system.ComponentSystem;
import com.gemengine.system.EntitySystem;

/**
 * Convenience class to be extended if you make a custom script component. It
 * extends owned component, so you can also query the owner, as well as some
 * helper functions that query the {@link EntitySystem} and the
 * {@link ComponentSystem}.
 * 
 * @author Dragos
 *
 */
public abstract class ScriptComponent extends OwnedComponent {
	@JsonIgnore
	protected final EntitySystem entitySystem;
	@JsonIgnore
	protected final ComponentSystem componentSystem;

	public ScriptComponent(EntitySystem entitySystem, ComponentSystem componentSystem) {
		super(componentSystem);
		this.entitySystem = entitySystem;
		this.componentSystem = componentSystem;
	}

	public void onInit() {
	}

	public void onUpdate(float delta) {
	}

	protected Entity createEntity(String name) {
		return entitySystem.create(name);
	}

	protected Entity findEntity(int id) {
		return entitySystem.get(id);
	}

	protected Entity findEntity(String name) {
		return entitySystem.get(name);
	}

	protected void removeEntity(int id) {
		entitySystem.delete(id);
	}

	protected void removeEntity(String name) {
		entitySystem.delete(name);
	}
}
