package com.gemengine.system.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gemengine.component.Component;
import com.gemengine.entity.Entity;
import com.gemengine.listener.EntityListener;
import com.gemengine.listener.PriorityListener;
import com.gemengine.system.ComponentSystem;
import com.gemengine.system.EntitySystem;
import com.gemengine.system.base.ComponentListenerSystem;
import com.gemengine.system.listener.ComponentTrackerListener;

import lombok.val;
import lombok.extern.log4j.Log4j2;

/**
 * Convenience System that tracks a component type being parented to another
 * type of component.
 * 
 * You can also listen to these events by registering as a listener for this
 * system.
 * 
 * Ex: Component Tracked is of type Ta and Component Tracking is of type Tb.
 * 
 * Entity A has component of type Ta and entity B has component of type Tb, and
 * A is parent of B, then you will receive notifications for this. You will also
 * receive notifications in case of a change of A or B or their components.
 * 
 * @author Dragos
 *
 * @param <ComponentTracked>
 * @param <ComponentTracking>
 */
@Log4j2
public abstract class ComponentTrackerSystem<ComponentTracked extends Component, ComponentTracking extends Component>
		extends ComponentListenerSystem implements EntityListener {
	private final ComponentSystem componentSystem;
	private Map<Integer, ComponentTracked> entityToTrackedComponent = new HashMap<Integer, ComponentTracked>();
	private List<ComponentTrackerListener<ComponentTracked, ComponentTracking>> listeners = new ArrayList<ComponentTrackerListener<ComponentTracked, ComponentTracking>>();
	private final Class<ComponentTracked> componentTrackedType;
	private final Class<ComponentTracking> componentTrackingType;
	private final boolean trackAllSubcomponents;

	public ComponentTrackerSystem(ComponentSystem componentSystem, EntitySystem entitySystem, Set<String> configuration,
			boolean enable, int priority, Class<ComponentTracked> tracked, Class<ComponentTracking> tracking,
			boolean trackAllSubcomponents) {
		super(componentSystem, configuration, enable, priority);
		this.componentTrackedType = tracked;
		this.componentTrackingType = tracking;
		this.componentSystem = componentSystem;
		this.trackAllSubcomponents = trackAllSubcomponents;
		entitySystem.addEntityListener(this);
	}

	public void addListener(ComponentTrackerListener<ComponentTracked, ComponentTracking> listener) {
		listeners.add(listener);
		Collections.sort(listeners, PriorityListener.getComparator());
	}

	/**
	 * Get the component that is tracked from the tracker component, located in
	 * the given entity
	 * 
	 * @param ent
	 * @return
	 */
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
			ent = ent1.getFirstPredecessorOf(componentTrackedType);
		}
		if (ent == null) {
			return;
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
		Set<Entity> trackees = null;
		if (trackAllSubcomponents) {
			trackees = componentSystem.getOwner(component).getDescendantsOf(componentTrackingType);
		} else {
			trackees = componentSystem.getOwner(component).getFirstDescendantsOf(componentTrackingType);
		}
		for (Entity ent : trackees) {
			entityToTrackedComponent.put(ent.getId(), component);
			for (ComponentTrackerListener<ComponentTracked, ComponentTracking> listener : listeners) {
				listener.onFound(component, ent);
			}
		}
	}

	private void removeComponent(Entity tracked) {
		Set<Entity> trackees = null;
		if (trackAllSubcomponents) {
			trackees = tracked.getDescendantsOf(componentTrackingType);
		} else {
			trackees = tracked.getFirstDescendantsOf(componentTrackingType);
		}
		if (trackees == null) {
			return;
		}
		for (Entity draw : trackees) {
			entityToTrackedComponent.remove(draw.getId());
			for (ComponentTrackerListener<ComponentTracked, ComponentTracking> listener : listeners) {
				listener.onLost(draw);
			}
		}
	}
}
