package com.gemengine.system.base;

import java.util.HashMap;
import java.util.Map;

import com.gemengine.component.Component;
import com.gemengine.system.ComponentSystem;
import com.gemengine.system.EntitySystem;
import com.gemengine.system.helper.ListenerHelper;

import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class ConstructorSystem<T, U extends Component> extends ComponentListenerSystem {
	private final ComponentSystem componentSystem;
	private final Map<Integer, T> types;

	protected ConstructorSystem(ComponentSystem componentSystem, EntitySystem entitySystem, boolean enable,
			int priority, Class<U> cls) {
		super(componentSystem, ListenerHelper.createConfiguration(cls), enable, priority);
		this.componentSystem = componentSystem;
		types = new HashMap<Integer, T>();
	}

	public T get(U labelComponent) {
		Integer id = labelComponent.getId();
		return types.get(id);
	}

	@Override
	public <L extends Component> void onChange(ComponentChangeType change, L uncastedComponent) {
		U typeComponent = (U) uncastedComponent;
		switch (change) {
		case ADD:
			add(typeComponent);
			break;
		case DELETE:
			remove(typeComponent);
			break;
		}
	}

	@Override
	public <L extends Component> void onNotify(String event, L notifier) {
		U component = (U) notifier;
		if (get(component) == null) {
			T res = create(component);
			if (res == null) {
				return;
			}
			onEvent(event, component, res);
		}
	}

	protected T add(U comp) {
		T resource = create(comp);
		if (resource != null) {
			types.put(comp.getId(), resource);
		}
		return resource;
	}

	protected abstract T create(U component);

	protected void onEvent(String event, U notifier, T resource) {
	}

	protected void remove(U comp) {
		types.remove(comp.getId());
	}
}
