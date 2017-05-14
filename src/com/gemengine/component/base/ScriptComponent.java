package com.gemengine.component.base;

import com.gemengine.entity.Entity;
import com.gemengine.system.ComponentSystem;
import com.gemengine.system.EntitySystem;

public abstract class ScriptComponent extends OwnedComponent {
	private EntitySystem entitySystem;

	public ScriptComponent(EntitySystem entitySystem, ComponentSystem componentSystem) {
		super(componentSystem);
		this.entitySystem = entitySystem;
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
