package com.gemengine.system.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gemengine.component.Component;
import com.gemengine.entity.Entity;
import com.gemengine.listener.EntityListener;
import com.gemengine.system.ComponentSystem;
import com.gemengine.system.EntitySystem;
import com.gemengine.system.base.ComponentListenerSystem;
import com.gemengine.system.listener.ComponentTrackerListener;

import lombok.val;

public abstract class ComponentTrackerSystem<ComponentTracked extends Component, ComponentTracking extends Component>
		extends ComponentListenerSystem implements EntityListener {
	private final ComponentSystem componentSystem;
	private Map<Integer, ComponentTracked> entityToTrackedComponent = new HashMap<Integer, ComponentTracked>();
	private List<ComponentTrackerListener<ComponentTracked, ComponentTracking>> listeners = new ArrayList<ComponentTrackerListener<ComponentTracked, ComponentTracking>>();
	private final Class<ComponentTracked> componentTrackedType;
	private final Class<ComponentTracking> componentTrackingType;

	public ComponentTrackerSystem(ComponentSystem componentSystem, EntitySystem entitySystem, Set<String> configuration,
			boolean enable, int priority, Class<ComponentTracked> tracked, Class<ComponentTracking> tracking) {
		super(componentSystem, configuration, enable, priority);
		this.componentTrackedType = tracked;
		this.componentTrackingType = tracking;
		this.componentSystem = componentSystem;
		entitySystem.addEntityListener(this);
	}

	public void addListener(ComponentTrackerListener<ComponentTracked, ComponentTracking> listener) {
		listeners.add(listener);
	}

	public ComponentTracked getTrackedComponentUp(Entity ent) {
		return entityToTrackedComponent.get(ent.getId());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Component> void onChange(ComponentChangeType changeType, T component) {
		switch (changeType) {
		case ADD:
			addComponent((ComponentTracked) component);
			break;
		case DELETE:
			removeComponent(componentSystem.getOwner(component));
			break;
		}
	}

	@Override
	public void onChange(EntityChangeType change, Entity ent1, Entity ent2) {
		Entity ent = null;
		if (ent1.getComponent(componentTrackedType) != null) {
			ent = ent1;
		} else {
			Set<Entity> components = ent1.getPredecessorsOf(componentTrackedType);
			val iterator = components.iterator();
			if (!iterator.hasNext()) {
				return;
			}
			ent = iterator.next();
		}
		switch (change) {
		case DEPARENTED:
			removeComponent(ent2);
			break;
		case PARENTED:
			addComponent(ent.getComponent(componentTrackedType));
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Component> void onNotify(String event, T notifier) {
		addComponent((ComponentTracked) notifier);
	}

	@Override
	public <T extends Component> void onTypeChange(Class<T> type) {
	}

	public void removeListener(ComponentTrackerListener<ComponentTracked, ComponentTracking> listener) {
		listeners.remove(listener);
	}

	private void addComponent(ComponentTracked component) {
		Set<Entity> drawables = componentSystem.getOwner(component).getDescendantsOf(componentTrackingType);
		for (Entity ent : drawables) {
			for (ComponentTrackerListener<ComponentTracked, ComponentTracking> listener : listeners) {
				listener.onFound(this, component, ent);
			}
			entityToTrackedComponent.put(ent.getId(), component);
		}
	}

	private void removeComponent(Entity camera) {
		Set<Entity> drawables = camera.getDescendantsOf(componentTrackingType);
		for (Entity draw : drawables) {
			entityToTrackedComponent.remove(draw.getId());
			for (ComponentTrackerListener<ComponentTracked, ComponentTracking> listener : listeners) {
				listener.onLost(this, draw);
			}
		}
	}
}
