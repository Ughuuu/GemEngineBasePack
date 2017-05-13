package com.gemengine.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gemengine.component.Component;
import com.gemengine.component.TagComponent;
import com.gemengine.entity.Entity;
import com.gemengine.system.base.ComponentListenerSystem;
import com.gemengine.system.helper.ListenerHelper;
import com.google.inject.Inject;

public class TagSystem extends ComponentListenerSystem {
	private Map<String, List<Entity>> tags = new HashMap<String, List<Entity>>();
	private final ComponentSystem componentSystem;

	@Inject
	protected TagSystem(ComponentSystem componentSystem) {
		super(componentSystem, ListenerHelper.createConfiguration(TagComponent.class), true, 6);
		this.componentSystem = componentSystem;
	}

	public List<Entity> getEntities(String name) {
		List<Entity> entities = tags.get(name);
		if (entities == null) {
			return new ArrayList<Entity>();
		}
		return entities;
	}

	@Override
	public <T extends Component> void onChange(ComponentChangeType changeType, T component) {
		TagComponent tag = (TagComponent) component;
		switch (changeType) {
		case ADD:
			addTag(tag);
			break;
		case DELETE:
			removeTag(tag, tag.getName());
			break;
		default:
			break;
		}
	}

	public void onTagChange(TagComponent tag, String oldKey) {
		if (!tag.isEnable()) {
			return;
		}
		removeTag(tag, oldKey);
		addTag(tag);
	}

	private void addTag(TagComponent tag) {
		String name = tag.getName();
		List<Entity> sameTags = tags.get(name);
		if (sameTags == null) {
			sameTags = new ArrayList<Entity>();
			tags.put(name, sameTags);
		}
		sameTags.add(tag.getOwner());
	}

	private void removeTag(TagComponent tag, String name) {
		List<Entity> sameTags = tags.get(name);
		if (sameTags == null) {
			sameTags = new ArrayList<Entity>();
			tags.put(name, sameTags);
		}
		sameTags.remove(tag.getOwner());

	}
}
