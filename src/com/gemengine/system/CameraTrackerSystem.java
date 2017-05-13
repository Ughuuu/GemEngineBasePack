package com.gemengine.system;

import com.gemengine.component.CameraComponent;
import com.gemengine.component.base.DrawableComponent;
import com.gemengine.system.base.ComponentTrackerSystem;
import com.gemengine.system.helper.ListenerHelper;
import com.google.inject.Inject;

public class CameraTrackerSystem extends ComponentTrackerSystem<CameraComponent, DrawableComponent> {
	private final ComponentSystem componentSystem;

	@Inject
	public CameraTrackerSystem(ComponentSystem componentSystem, EntitySystem entitySystem) {
		super(componentSystem, entitySystem, ListenerHelper.createConfiguration(CameraComponent.class), true, 6,
				CameraComponent.class, DrawableComponent.class);
		this.componentSystem = componentSystem;
	}
}
