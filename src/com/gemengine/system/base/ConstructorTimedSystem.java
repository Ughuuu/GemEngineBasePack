package com.gemengine.system.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.gemengine.component.Component;
import com.gemengine.listener.ComponentListener;
import com.gemengine.system.ComponentSystem;
import com.gemengine.system.EntitySystem;
import com.gemengine.system.helper.ListenerHelper;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class ConstructorTimedSystem<T, U extends Component> extends TimedSystem implements ComponentListener {
	private final ComponentSystem componentSystem;
	private final Map<Integer, T> types;

	@Getter
	private final Set<String> configuration;

	protected ConstructorTimedSystem(ComponentSystem componentSystem, EntitySystem entitySystem, boolean enable,
			int priority, Class<U> cls) {
		configuration = ListenerHelper.createConfiguration(cls);
		componentSystem.addComponentListener(this);
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

	@Override
	public <T extends Component> void onTypeChange(Class<T> arg0) {
	}

	protected T add(U comp) {
		T resource = create(comp);
		if (resource != null) {
			types.put(comp.getId(), resource);
		}
		return resource;
	}

	protected abstract T create(U component);

	protected Map<Integer, T> getTypes() {
		return types;
	}

	protected void onEvent(String event, U notifier, T resource) {
	}

	protected void onRemove(U comp, T res) {
	}

	protected void remove(U comp) {
		T res = types.remove(comp.getId());
		if (res != null) {
			onRemove(comp, res);
		}
	}
}
