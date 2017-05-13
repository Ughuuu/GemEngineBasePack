package com.gemengine.system.helper;

import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class ComponentMixIn {
	@JsonIgnore
	abstract Vector3 getId();
}
