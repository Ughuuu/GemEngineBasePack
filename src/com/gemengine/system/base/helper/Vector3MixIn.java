package com.gemengine.system.base.helper;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Vector3MixIn {
	@JsonIgnore
	abstract boolean isUnit();

	@JsonIgnore
	abstract boolean isZero();
}
