package com.gemengine.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gemengine.component.Component;
import com.gemengine.entity.Entity;
import com.gemengine.listener.EntityListener;
import com.gemengine.system.base.ComponentListenerSystem;
import com.gemengine.system.helper.ListenerHelper;
import com.gemengine.system.helper.ObjectMapperConfigurator;
import com.google.inject.Inject;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SaveSystem extends ComponentListenerSystem implements EntityListener {
	public static class SaveEntity {
		public String name;
		public Component[] components;
		public String parent;

		public SaveEntity(String name, String parent) {
			this.name = name;
			this.parent = parent;
		}
	}
	private final Map<Integer, List<Integer>> entityToComponents;
	private final Map<Integer, String> entityToFile;
	private final Map<String, List<Integer>> fileToEntities;
	private final String staging = "staging";
	private final AssetSystem assetSystem;
	private final ComponentSystem componentSystem;
	private final EntitySystem entitySystem;

	private final ObjectMapper objectMapper;

	@Inject
	protected SaveSystem(ComponentSystem componentSystem, EntitySystem entitySystem, AssetSystem assetSystem) {
		super(componentSystem, ListenerHelper.createConfiguration(Component.class), true, 10);
		entitySystem.addEntityListener(this);
		this.assetSystem = assetSystem;
		this.componentSystem = componentSystem;
		this.entitySystem = entitySystem;
		entityToComponents = new HashMap<Integer, List<Integer>>();
		entityToFile = new HashMap<Integer, String>();
		fileToEntities = new HashMap<String, List<Integer>>();
		objectMapper = new ObjectMapper();
		ObjectMapperConfigurator.configure(objectMapper);
	}

	public String getScene(List<Integer> entities) {
		objectMapper.findAndRegisterModules();
		List<SaveEntity> scene = new ArrayList<SaveEntity>();
		for (int entity : entities) {
			Entity ent = entitySystem.get(entity);
			Entity parent = ent.getParent();
			SaveEntity fakeEntity = new SaveEntity(ent.getName(), parent == null ? null : parent.getName());
			scene.add(fakeEntity);
			List<Integer> components = entityToComponents.get(entity);
			if (components == null) {
				continue;
			}
			fakeEntity.components = new Component[components.size()];
			for (int i = 0; i < components.size(); i++) {
				int componentId = components.get(i);
				Component component = componentSystem.get(ent, componentId);
				fakeEntity.components[i] = component;
			}
		}
		String data = "";
		try {
			SaveEntity[] saveEntity = scene.toArray(new SaveEntity[0]);
			data = objectMapper.writeValueAsString(saveEntity);
		} catch (Throwable t) {
			log.fatal("Save System save error", t);
		}
		return data;
	}

	public List<Integer> getSceneAsEntityIds(String scene) {
		List<Integer> entities = fileToEntities.get(scene);
		return entities;
	}

	public void load(String sceneName) {
		String fileContents = assetSystem.getAsset(sceneName);
		removeEntities(getSceneAsEntityIds(sceneName));
		Map<String, String> parentMap = new HashMap<String, String>();
		try {
			val root = objectMapper.readTree(fileContents);
			for (JsonNode node : root) {
				String name = node.get("name").asText();
				String parent = node.get("parent").asText();
				parentMap.put(name, parent);
			}
			// root.get("");
			// objectMapper.readValue(fileContents, SaveData.class);
		} catch (Throwable t) {
			log.fatal("Save System load", t);
		}
	}

	@Override
	public <T extends Component> void onChange(ComponentChangeType change, T arg1) {
		switch (change) {
		case ADD:
			addComponent(componentSystem.getOwner(arg1.getId()).getId(), arg1.getId());
			break;
		case DELETE:
			removeComponent(componentSystem.getOwner(arg1.getId()).getId(), arg1.getId());
			break;
		}
	}

	@Override
	public void onChange(EntityChangeType change, Entity arg1, Entity arg2) {
		switch (change) {
		case ADD:
			addEntity(arg1.getId());
			break;
		case DELETE:
			removeEntity(arg1.getId());
			break;
		}
	}

	public void save(String name) {
		List<Integer> entities = fileToEntities.remove(staging);
		if (entities == null) {
			entities = new ArrayList<Integer>();
		}
		List<Integer> currentEntities = fileToEntities.get(name);
		if (currentEntities != null) {
			entities.addAll(currentEntities);
		}
		fileToEntities.put(name, entities);
		FileHandle file = Gdx.files.local(name);
		file.writeString(getScene(entities), false);
	}

	private void addComponent(int parent, int id) {
		List<Integer> objects = entityToComponents.get(parent);
		if (objects == null) {
			objects = new ArrayList<Integer>();
			entityToComponents.put(parent, objects);
		}
		objects.add(id);
	}

	private void addEntity(int id) {
		List<Integer> objects = fileToEntities.get(staging);
		if (objects == null) {
			objects = new ArrayList<Integer>();
			fileToEntities.put(staging, objects);
		}
		entityToFile.put(id, staging);
		objects.add(id);
	}

	private void removeComponent(int parent, Integer id) {
		List<Integer> objects = entityToComponents.get(parent);
		if (objects == null) {
			return;
		}
		objects.remove(id);
	}

	private void removeEntities(List<Integer> entities) {
		for (int entity : entities) {
			Entity ent = entitySystem.get(entity);
			entitySystem.delete(ent);
			List<Integer> components = new ArrayList<>(entityToComponents.get(entity));
			for (int component : components) {
				Component comp = componentSystem.get(ent, component);
				componentSystem.remove(ent, comp.getId());
			}
		}
	}

	private void removeEntity(Integer id) {
		List<Integer> objects = fileToEntities.get(staging);
		if (objects == null) {
			objects = new ArrayList<Integer>();
			fileToEntities.put(staging, objects);
		}
		entityToFile.remove(id);
		objects.remove(id);
		if (objects.isEmpty()) {
			fileToEntities.remove(staging);
		}
	}
}
