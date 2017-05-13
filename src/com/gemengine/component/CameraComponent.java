package com.gemengine.component;

import com.badlogic.gdx.Gdx;
import com.gemengine.component.base.NotifyComponent;
import com.gemengine.system.ComponentSystem;
import com.google.inject.Inject;

import lombok.Getter;

public class CameraComponent extends NotifyComponent {
	public static enum ViewportType {
		Stretch, Fit, Fill, Screen, Extend
	}

	@Getter
	private boolean resizeable = true;
	@Getter
	private float fov = -1;
	@Getter
	private int width = 0;
	@Getter
	private int height = 0;
	@Getter
	private ViewportType viewportType = ViewportType.Screen;

	@Inject
	public CameraComponent(ComponentSystem componentSystem) {
		super(componentSystem);
	}

	public void makeOrthographicCamera() {
		this.width = Gdx.graphics.getWidth();
		this.height = Gdx.graphics.getHeight();
		this.fov = -1;
		doNotify("create");
	}

	public void makeOrthographicCamera(int width, int height) {
		this.width = width;
		this.height = height;
		this.fov = -1;
		doNotify("create");
	}

	public void makePerspectiveCamera() {
		this.width = Gdx.graphics.getWidth();
		this.height = Gdx.graphics.getHeight();
		this.fov = 90f;
		doNotify("create");
	}

	public void makePerspectiveCamera(int fov, int width, int height) {
		this.width = width;
		this.height = height;
		this.fov = fov;
		doNotify("create");
	}

	@Override
	public <T extends Component> void onNotify(String arg0, T arg1) {
		if (!(arg1 instanceof PointComponent)) {
			return;
		}
		doNotify("point");
	}

	public CameraComponent setHeight(int height) {
		this.height = height;
		doNotify("size");
		return this;
	}

	public CameraComponent setResizeable(boolean resizeable) {
		this.resizeable = resizeable;
		doNotify("resizeable");
		return this;
	}

	public CameraComponent setSize(int width, int height) {
		this.width = width;
		this.height = height;
		doNotify("size");
		return this;
	}

	public CameraComponent setViewportType(ViewportType type) {
		this.viewportType = type;
		doNotify("viewportType");
		return this;
	}

	public CameraComponent setWidth(int width) {
		this.width = width;
		doNotify("size");
		return this;
	}

	@Override
	public String toString() {
		return "CameraComponent [fov=" + fov + ", width=" + width + ", height=" + height + ", viewportType="
				+ viewportType + "]";
	}
}
