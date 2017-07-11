package com.gemengine.system.base;

import java.util.HashMap;
import java.util.Map;

import com.gemengine.component.Component;
import com.gemengine.system.ComponentSystem;
import com.gemengine.system.EntitySystem;
import com.gemengine.system.helper.ListenerHelper;

import lombok.extern.log4j.Log4j2;

/**
 * This system is used to hold actual implementation for components. If your
 * component only holds data(as it should) and the system holds the class that
 * represents the constructed object, this system keeps the link between
 * component id and you actual object. It gives you a create function which you
 * have to implement, and you have to give it a component type as well as a
 * object type of which the component represents in your system.
 * 
 * Ex: FileComponent represents a File(but FileComponent only has a string, the
 * name).
 * 
 * So this system would make a link between the FileComponent and the File.
 * 
 * @author Dragos
 *
 * @param <T>
 *            The representation of the component
 * @param <U>
 *            The component
 */
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

	/**
	 * Get the object represented by this component.
	 * 
	 * @param component
	 *            The component
	 * @return
	 */
	public T get(U component) {
		Integer id = component.getId();
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
		T res = get(component);
		if (res == null) {
			res = create(component);
			if (res == null) {
				return;
			}
		}
		onEvent(event, component, res);
	}

	protected T add(U comp) {
		T resource = create(comp);
		if (resource != null) {
			types.put(comp.getId(), resource);
			// componentSystem.notifyFrom("create", comp);
		}
		return resource;
	}

	/**
	 * Called when the system considers the component representation needs to be
	 * created(or by you, if you think the object needs to be recreated)
	 * 
	 * @param component
	 * @return
	 */
	protected abstract T create(U component);

	protected void onEvent(String event, U notifier, T resource) {
	}

	protected void remove(U comp) {
		types.remove(comp.getId());
	}
}
