package com.gemengine.system.listener;

import java.util.Set;

import com.gemengine.component.Component;
import com.gemengine.entity.Entity;
import com.gemengine.listener.PriorityListener;
import com.gemengine.system.base.ComponentTrackerSystem;

public interface ComponentTrackerListener<ComponentTracked extends Component, ComponentTracking extends Component>
		extends PriorityListener {
	public Set<String> getConfiguration();

	public void onFound(ComponentTracked notifier, Entity tracker);

	public void onLost(Entity tracker);
}
