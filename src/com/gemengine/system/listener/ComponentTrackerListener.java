package com.gemengine.system.listener;

import java.util.Set;

import com.gemengine.component.Component;
import com.gemengine.entity.Entity;
import com.gemengine.system.base.ComponentTrackerSystem;

public interface ComponentTrackerListener<ComponentTracked extends Component, ComponentTracking extends Component> {
	public void onFound(ComponentTrackerSystem<ComponentTracked, ComponentTracking> issuingSystem,
			ComponentTracked notifier, Entity tracker);

	public void onLost(ComponentTrackerSystem<ComponentTracked, ComponentTracking> issuingSystem, Entity tracker);

	public Set<String> getConfiguration();
}
