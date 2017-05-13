package com.gemengine.system.helper;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Vector3MixIn {
	@JsonIgnore
	abstract boolean isUnit();

	@JsonIgnore
	abstract boolean isZero();
}
