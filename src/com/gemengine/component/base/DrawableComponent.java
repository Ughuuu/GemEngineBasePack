package com.gemengine.component.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gemengine.component.base.NotifyComponent;
import com.gemengine.system.ComponentSystem;

import lombok.Getter;

/**
 * Basic drawable component. Offers width and height, should be extended by all
 * drawable components.
 * 
 * @author Dragos
 *
 */
public abstract class DrawableComponent extends NotifyComponent {
	@Getter
	@JsonIgnore
	protected float width;
	@Getter
	@JsonIgnore
	protected float height;
	@Getter
	@JsonIgnore
	protected String hexColor = "ffffff";

	public DrawableComponent(ComponentSystem componentSystem) {
		super(componentSystem);
	}

	/**
	 * Set the height of this component. This also notifies the other components
	 * about it's change.
	 * 
	 * @param height
	 * @return
	 */
	public DrawableComponent setHeight(float height) {
		this.height = height;
		doNotify("size");
		return this;
	}

	/**
	 * Set the size of this component. This also notifies the other components
	 * about it's change.
	 * 
	 * @param height
	 * @return
	 */
	public DrawableComponent setSize(float width, float height) {
		this.height = height;
		this.width = width;
		doNotify("size");
		return this;
	}

	/**
	 * Set the width of this component. This also notifies the other components
	 * about it's change.
	 * 
	 * @param height
	 * @return
	 */
	public DrawableComponent setWidth(float width) {
		this.width = width;
		doNotify("size");
		return this;
	}

	public DrawableComponent setHexColor(String hexColor) {
		this.hexColor = hexColor;
		doNotify("hexColor");
		return this;
	}
}
