package com.gemengine.system;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gemengine.component.CameraComponent;
import com.gemengine.component.Component;
import com.gemengine.component.PointComponent;
import com.gemengine.entity.Entity;
import com.gemengine.system.base.ComponentListenerSystem;
import com.gemengine.system.helper.ListenerHelper;
import com.google.inject.Inject;

import lombok.val;

public class CameraSystem extends ComponentListenerSystem {
	private final ComponentSystem componentSystem;
	private Map<Integer, Viewport> viewports;
	private final CameraTrackerSystem cameraTrackerSystem;

	@Inject
	protected CameraSystem(ComponentSystem componentSystem, EntitySystem entitySystem,
			CameraTrackerSystem cameraTrackerSystem) {
		super(componentSystem, ListenerHelper.createConfiguration(CameraComponent.class), true, 6);
		this.cameraTrackerSystem = cameraTrackerSystem;
		this.componentSystem = componentSystem;
		viewports = new HashMap<Integer, Viewport>();
	}

	public Camera getCamera(CameraComponent cameraComponent) {
		Viewport viewport = getViewport(cameraComponent);
		if (viewport == null) {
			return null;
		}
		return viewport.getCamera();
	}

	public Viewport getViewport(CameraComponent cameraComponent) {
		return viewports.get(cameraComponent.getId());

	}

	public CameraComponent getWatchingCamera(Entity ent) {
		return cameraTrackerSystem.getTrackedComponentUp(ent);
	}

	@Override
	public <T extends Component> void onChange(ComponentChangeType changeType, T component) {
		switch (changeType) {
		case ADD:
			break;
		case DELETE:
			viewports.remove(component.getId());
			break;
		default:
			break;
		}
	}

	@Override
	public <T extends Component> void onNotify(String event, T notifier) {
		if (!notifier.isEnable()) {
			return;
		}
		CameraComponent cameraComponent = (CameraComponent) notifier;
		switch (event) {
		case "create":
			createCamera(cameraComponent);
		case "viewportType":
			Viewport viewport = createViewport(cameraComponent, getViewport(cameraComponent).getCamera());
			changeViewport(cameraComponent);
			viewports.put(cameraComponent.getId(), viewport);
			break;
		case "point":
			setPoint(cameraComponent, cameraComponent.getOwner().getComponent(PointComponent.class));
			break;
		default:
			changeViewport(cameraComponent);
			break;
		}
	}

	@Override
	public void onResize(int width, int height) {
		for (val entry : viewports.entrySet()) {
			CameraComponent camera = componentSystem.get(componentSystem.getOwner(entry.getKey()), entry.getKey());
			camera.setSize(width, height);
			if (camera.isResizeable()) {
				System.out.println(camera);
				entry.getValue().update(width, height, false);
			}
			setPoint(camera, camera.getOwner().getComponent(PointComponent.class));
		}
	}

	private void changeCamera(CameraComponent cameraComponent, Camera camera) {
		int id = cameraComponent.getId();
		Viewport viewport = createViewport(cameraComponent, camera);
		changeViewport(cameraComponent);
		viewports.put(id, viewport);
	}

	private void changeViewport(CameraComponent cameraComponent) {
		int id = cameraComponent.getId();
		Viewport viewport = viewports.get(id);
		if (viewport == null) {
			return;
		}
		int width = cameraComponent.getWidth();
		int height = cameraComponent.getHeight();
		viewport.setScreenX(width);
		viewport.setScreenY(height);
		viewport.update(width, height, false);
	}

	private void createCamera(CameraComponent cameraComponent) {
		Camera camera;
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		float fov = cameraComponent.getFov();
		if (fov < 0) {
			camera = new OrthographicCamera(width, height);
		} else {
			camera = new PerspectiveCamera(fov, width, height);
		}
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		camera.update();
		changeCamera(cameraComponent, camera);
	}

	private Viewport createViewport(CameraComponent cameraComponent, Camera camera) {
		Viewport viewport = null;
		float width = cameraComponent.getWidth();
		float height = cameraComponent.getHeight();
		switch (cameraComponent.getViewportType()) {
		case Stretch:
			viewport = new StretchViewport(width, height, camera);
			break;
		case Fit:
			viewport = new FitViewport(width, height, camera);
			break;
		case Fill:
			viewport = new FillViewport(width, height, camera);
			break;
		case Screen:
			viewport = new ScreenViewport(camera);
			break;
		case Extend:
			viewport = new ExtendViewport(width, height, camera);
			break;
		}
		return viewport;
	}

	private void setPoint(CameraComponent cameraComponent, PointComponent pos) {
		Camera camera = getCamera(cameraComponent);
		Vector3 v3 = pos.getPosition();
		v3.x += cameraComponent.getWidth() / 2;
		v3.y += cameraComponent.getHeight() / 2;
		camera.position.set(v3);
		camera.update();
	}
}
