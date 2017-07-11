package com.gemengine.component;

import com.gemengine.component.base.OwnedComponent;
import com.gemengine.system.ComponentSystem;
import com.gemengine.system.TagSystem;
import com.google.inject.Inject;

import lombok.Getter;

/**
 * Tag component. You may also use different queries on the tags, such as find
 * all entities that have a specific tag(from the {@link TagSystem})
 * 
 * @author Dragos
 *
 */
public class TagComponent extends OwnedComponent {
	@Getter
	private String name = "";
	private final TagSystem tagSystem;

	@Inject
	public TagComponent(ComponentSystem componentSystem, TagSystem tagSystem) {
		super(componentSystem);
		this.tagSystem = tagSystem;
	}

	public TagComponent setName(String name) {
		final String oldName = this.name;
		this.name = name;
		tagSystem.onTagChange(this, oldName);
		return this;
	}

	@Override
	public String toString() {
		return "TagComponent [name=" + name + "]";
	}
}
