package com.gemengine.system.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gemengine.component.Component;
import com.gemengine.entity.Entity;
import com.gemengine.listener.EntityListener;
import com.gemengine.system.AssetSystem;
import com.gemengine.system.ComponentSystem;
import com.gemengine.system.EntitySystem;
import com.gemengine.system.base.ComponentListenerSystem;
import com.gemengine.system.base.SystemBase;
import com.gemengine.system.base.helper.ObjectMapperConfigurator;
import com.gemengine.system.helper.ListenerHelper;
import com.google.inject.Inject;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SaveSystem extends SystemBase implements EntityListener {
	public static class SaveEntity {
		public String name;
		public Component[] components;
		public String parent;

		public SaveEntity(String name, String parent) {
			this.name = name;
			this.parent = parent;
		}
	}

	private final Map<Integer, String> entityToFile;
	private final Map<String, List<Integer>> fileToEntities;
	private final String staging = "staging";
	private final AssetSystem assetSystem;
	private final ComponentSystem componentSystem;
	private final EntitySystem entitySystem;

	private final ObjectMapper objectMapper;

	@SuppressWarnings("unchecked")
	@Inject
	protected SaveSystem(ComponentSystem componentSystem, EntitySystem entitySystem, AssetSystem assetSystem) {
		super();
		entitySystem.addEntityListener(this);
		this.assetSystem = assetSystem;
		this.componentSystem = componentSystem;
		this.entitySystem = entitySystem;
		entityToFile = new HashMap<Integer, String>();
		fileToEntities = new HashMap<String, List<Integer>>();
		objectMapper = new ObjectMapper();
		ObjectMapperConfigurator.configure(objectMapper);
	}

	public String getEntityAsString(Entity ent) throws JsonProcessingException {
		Entity parent = ent.getParent();
		SaveEntity fakeEntity = new SaveEntity(ent.getName(), parent == null ? null : parent.getName());
		List<Component> components = ent.getComponents(Component.class);
		if (components != null) {
			fakeEntity.components = new Component[components.size()];
			int i = 0;
			for (Component component : components) {
				fakeEntity.components[i] = component;
				i++;
			}
		}
		return objectMapper.writeValueAsString(fakeEntity);
	}

	public String getScene(List<Integer> entities) {
		objectMapper.findAndRegisterModules();
		List<SaveEntity> scene = new ArrayList<SaveEntity>();
		for (int entity : entities) {
			Entity ent = entitySystem.get(entity);
			Entity parent = ent.getParent();
			SaveEntity fakeEntity = new SaveEntity(ent.getName(), parent == null ? null : parent.getName());
			scene.add(fakeEntity);
			List<Component> components = ent.getComponents(Component.class);
			if (components == null) {
				continue;
			}
			fakeEntity.components = new Component[components.size()];
			int i = 0;
			for (Component component : components) {
				fakeEntity.components[i] = component;
				i++;
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
		val entitiesId = getSceneAsEntityIds(sceneName);
		if (entitiesId != null) {
			removeEntities(entitiesId);
		}
		Map<String, String> parentMap = new HashMap<String, String>();
		try {
			val root = objectMapper.readTree(fileContents);
			for (JsonNode node : root) {
				String name = node.get("name").asText();
				String parent = node.get("parent").asText();
				parentMap.put(name, parent);
			}
			for (JsonNode node : root) {
				JsonNode components = node.get("components");
				String name = node.get("name").asText();
				Entity ent = entitySystem.create(name);
				for (JsonNode component : components) {
					Class<? extends Component> cls = (Class<? extends Component>) Class
							.forName(component.get(0).asText());
					Component comp = ent.createComponent(cls);
					objectMapper.readerForUpdating(comp).readValue(component.get(1));
					componentSystem.notifyFrom("load", comp);
				}
			}
			for (val parentEntry : parentMap.entrySet()) {
				Entity parent = entitySystem.get(parentEntry.getValue());
				Entity child = entitySystem.get(parentEntry.getKey());
				if (parent == null) {
					continue;
				}
				parent.addChild(child);
			}
			// root.get("");
			// objectMapper.readValue(fileContents, SaveData.class);
		} catch (Throwable t) {
			log.fatal("Save System load", t);
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
		default:
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

	private void addEntity(int id) {
		List<Integer> objects = fileToEntities.get(staging);
		if (objects == null) {
			objects = new ArrayList<Integer>();
			fileToEntities.put(staging, objects);
		}
		entityToFile.put(id, staging);
		objects.add(id);
	}

	private void removeEntities(List<Integer> entities) {
		for (int entity : entities) {
			Entity ent = entitySystem.get(entity);
			entitySystem.delete(ent);
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
