package com.gemengine.component;

import java.util.Set;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.gemengine.component.base.OwnedComponent;
import com.gemengine.entity.Entity;
import com.gemengine.system.ComponentSystem;
import com.google.inject.Inject;

public class PointComponent extends OwnedComponent {
	private Matrix4 mat = new Matrix4();
	private final Quaternion quaternion = new Quaternion();
	private final Vector3 rotation = new Vector3(), position = new Vector3(), scale = new Vector3(), up = new Vector3(),
			left = new Vector3(), forward = new Vector3();

	private Matrix4 matLocal = new Matrix4();
	private final Quaternion quaternionLocal = new Quaternion();
	private final Vector3 rotationLocal = new Vector3(), positionLocal = new Vector3(), scaleLocal = new Vector3(),
			upLocal = new Vector3(), leftLocal = new Vector3(), forwardLocal = new Vector3();

	private final Vector3[] cacheVector = new Vector3[14];
	private final Quaternion[] cacheQuaternion = new Quaternion[2];
	private final Matrix4[] cacheMatrix = new Matrix4[2];

	/**
	 * Construct a new Component Point.
	 */
	@Inject
	public PointComponent(ComponentSystem componentSystem) {
		super(componentSystem);
		for (int i = 0; i < cacheVector.length; i++) {
			cacheVector[i] = new Vector3();
		}
		for (int i = 0; i < cacheQuaternion.length; i++) {
			cacheQuaternion[i] = new Quaternion();
		}
		for (int i = 0; i < cacheMatrix.length; i++) {
			cacheMatrix[i] = new Matrix4();
		}
	}

	/**
	 * Get the global forward vector of this object.
	 * 
	 * @return A unit vector representing the forward direction
	 */
	public final Vector3 getForward() {
		return cacheVector[0].set(forward);
	}

	/**
	 * Get the global left vector of this object.
	 * 
	 * @return A unit vector representing the left direction.
	 */
	public final Vector3 getLeft() {
		return cacheVector[1].set(left);
	}

	/**
	 * Get the global translation rotation scale matrix of this object.
	 * 
	 * @return The 4x4 matrix
	 */
	public final Matrix4 getMatrix() {
		return cacheMatrix[0].set(mat);
	}

	/**
	 * Get the global position of this.
	 * 
	 * @return A vector representing the position in global space.
	 */
	public final Vector3 getPosition() {
		return cacheVector[4].set(position);
	}

	/**
	 * Get the global rotation of this object.
	 * 
	 * @return A quaternion representing this objects global rotation.
	 */
	public final Quaternion getQuaternion() {
		// TODO dirty optimization
		quaternion.setEulerAngles(rotation.x, rotation.y, rotation.z);
		return cacheQuaternion[0].set(quaternion);
	}

	/**
	 * Get the relative forward vector of this object.
	 * 
	 * @return A unit vector representing the forward direction
	 */
	public final Vector3 getRelativeForward() {
		return cacheVector[7].set(forwardLocal);
	}

	/**
	 * Get the relative left vector of this object.
	 * 
	 * @return A unit vector representing the left direction.
	 */
	public final Vector3 getRelativeLeft() {
		return cacheVector[8].set(leftLocal);
	}

	/**
	 * Get the relative translation rotation scale matrix of this object.
	 * 
	 * @return The 4x4 matrix
	 */
	public final Matrix4 getRelativeMatrix() {
		return cacheMatrix[1].set(matLocal);
	}

	/**
	 * Get the relative position of this.
	 * 
	 * @return A vector representing the position in global space.
	 */
	public final Vector3 getRelativePosition() {
		return cacheVector[11].set(positionLocal);
	}

	/**
	 * Get the relative rotation of this object.
	 * 
	 * @return A quaternion representing this objects global rotation.
	 */
	public final Quaternion getRelativeQuaternion() {
		// TODO dirty optimization
		quaternionLocal.setEulerAngles(rotation.x, rotation.y, rotation.z);
		return cacheQuaternion[1].set(quaternionLocal);
	}

	/**
	 * Get the relative right vector of this object.
	 * 
	 * @return A unit vector representing the right direction.
	 */
	public final Vector3 getRelativeRight() {
		return cacheVector[9].set(leftLocal).scl(-1);
	}

	/**
	 * Get the relative rotation of this object.
	 * 
	 * @return A Vector3 where x, y and z represent the rotation on each axis in
	 *         degrees.
	 */
	public final Vector3 getRelativeRotation() {
		return cacheVector[12].set(rotationLocal);
	}

	/**
	 * Get the relative scale of this object.
	 * 
	 * @return A Vector3 where x, y and z represent the scale on each axis.
	 */
	public final Vector3 getRelativeScale() {
		return cacheVector[13].set(scaleLocal);
	}

	/**
	 * Get the relative up vector of this object.
	 * 
	 * @return A unit vector representing the up direction.
	 */
	public final Vector3 getRelativeUp() {
		return cacheVector[10].set(upLocal);
	}

	/**
	 * Get the global right vector of this object.
	 * 
	 * @return A unit vector representing the right direction.
	 */
	public final Vector3 getRight() {
		return cacheVector[2].set(left).scl(-1);
	}

	/**
	 * Get the global rotation of this object.
	 * 
	 * @return A Vector3 where x, y and z represent the rotation on each axis in
	 *         degrees.
	 */
	public final Vector3 getRotation() {
		return cacheVector[5].set(rotation);
	}

	/**
	 * Get the global scale of this object.
	 * 
	 * @return A Vector3 where x, y and z represent the scale on each axis.
	 */
	public final Vector3 getScale() {
		return cacheVector[6].set(scale);
	}

	/**
	 * Get the global up vector of this object.
	 * 
	 * @return A unit vector representing the up direction.
	 */
	public final Vector3 getUp() {
		return cacheVector[3].set(up);
	}

	@Override
	public void onCreate() {
		reset();
		recalculateFromParent();
	}

	/**
	 * Resets this object state to have everything put to 0.
	 */
	public void reset() {
		position.setZero();
		scale.set(1, 1, 1);
		rotation.setZero();
		quaternion.idt();

		positionLocal.setZero();
		scaleLocal.set(1, 1, 1);
		rotationLocal.setZero();
		quaternionLocal.idt();
		mat.idt();
		matLocal.idt();
		recalculateFromParent();
	}

	/**
	 * Set the relative position of this object from the given vector.
	 * 
	 * @param scale
	 *            Describes the new local position of the object.
	 * @return This object for chaining.
	 */
	public PointComponent setRelativePosition(Vector3 position) {
		this.positionLocal.set(position);
		recalculateFromParent();
		return this;
	}

	/**
	 * Set the relative rotation of this object from the given quaternion.
	 * 
	 * @param rotation
	 *            Describes the new local rotation of the object.
	 * @return This object for chaining.
	 */
	public PointComponent setRelativeQuaternion(Quaternion rotation) {
		quaternionLocal.set(rotation);
		rotationLocal.set(rotation.getPitch(), rotation.getYaw(), rotation.getRoll());
		recalculateFromParent();
		return this;
	}

	/**
	 * Set the relative rotation of this object from the given vector.
	 * 
	 * @param rotation
	 *            Describes the new local rotation of the object.
	 * @return This object for chaining.
	 */
	public PointComponent setRelativeRotation(Vector3 rotation) {
		rotationLocal.set(rotation);
		quaternionLocal.setEulerAngles(rotationLocal.x, rotationLocal.y, rotationLocal.z);
		recalculateFromParent();
		return this;
	}

	/**
	 * Set the relative scale of this object from the given vector.
	 * 
	 * @param scale
	 *            Describes the new local scale of the object.
	 * @return This object for chaining.
	 */
	public PointComponent setRelativeScale(float scale) {
		this.scaleLocal.set(scale, scale, scale);
		recalculateFromParent();
		return this;
	}

	/**
	 * Set the relative scale of this object from the given vector.
	 * 
	 * @param scale
	 *            Describes the new local scale of the object.
	 * @return This object for chaining.
	 */
	public PointComponent setRelativeScale(Vector3 scale) {
		this.scaleLocal.set(scale);
		recalculateFromParent();
		return this;
	}

	@Override
	public String toString() {
		return "PointComponent [rotation=" + rotation + ", position=" + position + ", scale=" + scale + "]";
	}

	private void recalculateFromParent() {
		Entity owner = getOwner();
		matLocal.set(positionLocal.x, positionLocal.y, positionLocal.z, quaternionLocal.x, quaternionLocal.y,
				quaternionLocal.z, quaternionLocal.w, scaleLocal.x, scaleLocal.y, scaleLocal.z);
		leftLocal.set(matLocal.val[0], matLocal.val[1], matLocal.val[2]);
		upLocal.set(matLocal.val[4], matLocal.val[5], matLocal.val[6]);
		forwardLocal.set(matLocal.val[8], matLocal.val[9], matLocal.val[10]);
		if (owner.isChild()) {
			PointComponent parent = owner.getParent().getComponent(PointComponent.class);
			Matrix4 parentMat = parent.getMatrix();
			mat.set(parentMat).mul(matLocal);

			mat.getTranslation(position);
			mat.getScale(scale);
			// scale.set(parent.scale.x * scaleLocal.x, parent.scale.y *
			// scaleLocal.y, parent.scale.z * scaleLocal.z);
			quaternion.set(parent.quaternion).add(quaternionLocal);
			rotation.set(parent.rotation).add(rotationLocal);
			left.set(mat.val[0], mat.val[1], mat.val[2]);
			up.set(mat.val[4], mat.val[5], mat.val[6]);
			forward.set(mat.val[8], mat.val[9], mat.val[10]);

		} else {
			mat.set(matLocal);

			position.set(positionLocal);
			scale.set(scaleLocal);
			rotation.set(rotationLocal);
			quaternion.set(quaternionLocal);
			up.set(upLocal);
			left.set(leftLocal);
			forward.set(forwardLocal);
		}
		updateChildren();
		doNotify("update");
	}

	private void updateChildren() {
		Set<Entity> entities = getOwner().getChildren();
		for (Entity ent : entities) {
			PointComponent point = ent.getComponent(PointComponent.class);
			point.recalculateFromParent();
		}
	}
}
