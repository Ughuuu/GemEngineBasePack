package com.gemengine.system.base.helper;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class PointComponentMixIn {
	@JsonIgnore
	abstract Vector3 getForward();

	@JsonIgnore
	abstract Vector3 getLeft();

	@JsonIgnore
	abstract Matrix4 getMatrix();

	@JsonIgnore
	abstract Vector3 getPosition();

	@JsonIgnore
	abstract Quaternion getQuaternion();

	@JsonIgnore
	abstract Vector3 getRelativeForward();

	@JsonIgnore
	abstract Vector3 getRelativeLeft();

	@JsonIgnore
	abstract Matrix4 getRelativeMatrix();

	@JsonProperty("position")
	abstract Vector3 getRelativePosition();
	
	@JsonProperty("position")
	abstract Vector3 setRelativePosition(Vector3 scale);

	@JsonIgnore
	abstract Quaternion getRelativeQuaternion();

	@JsonIgnore
	abstract Vector3 getRelativeRight();

	@JsonProperty("rotation")
	abstract Vector3 getRelativeRotation();
	
	@JsonProperty("rotation")
	abstract Vector3 setRelativeRotation(Vector3 scale);

	@JsonProperty("scale")
	abstract Vector3 getRelativeScale();
	
	@JsonProperty("scale")
	abstract Vector3 setRelativeScale(Vector3 scale);

	@JsonIgnore
	abstract Vector3 getRelativeUp();

	@JsonIgnore
	abstract Vector3 getRight();

	@JsonIgnore
	abstract Vector3 getRotation();

	@JsonIgnore
	abstract Vector3 getScale();

	@JsonIgnore
	abstract Vector3 getUp();
}
