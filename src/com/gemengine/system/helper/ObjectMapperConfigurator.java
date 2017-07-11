package com.gemengine.system.helper;

import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gemengine.component.Component;
import com.gemengine.component.base.OwnedComponent;
import com.gemengine.component.PointComponent;

public class ObjectMapperConfigurator {
	public static void configure(ObjectMapper objectMapper) {
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.setVisibility(PropertyAccessor.ALL, Visibility.NON_PRIVATE);
		objectMapper.enableDefaultTyping();
		objectMapper.addMixIn(Component.class, ComponentMixIn.class);
		objectMapper.addMixIn(PointComponent.class, PointComponentMixIn.class);
		objectMapper.addMixIn(Component.class, ComponentMixIn.class);
		objectMapper.addMixIn(Vector3.class, Vector3MixIn.class);
	}
}
