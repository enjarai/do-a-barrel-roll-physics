package dev.enjarai.doabarrelroll.physics;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import nl.enjarai.doabarrelroll.DoABarrelRollClient;
import nl.enjarai.doabarrelroll.api.RollEntity;
import nl.enjarai.doabarrelroll.math.MagicNumbers;
import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;

public class DoABarrelRollPhysics implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("do_a_barrel_roll_physics");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

	}

	public static Vec3d handleVelocity(LivingEntity entity, Vec3d original) {
		if (!DoABarrelRollClient.isFallFlying()) {
			return original;
		}

		var playerRotationMatrix = new Matrix4d();
		// These convert from the aerodynamic coordinate system to MC's one
		playerRotationMatrix.rotateX(90 * MagicNumbers.TORAD);
		playerRotationMatrix.rotateY(-90 * MagicNumbers.TORAD); // These values are an educated guess, they may have to be inverted...
		// These add the player's rotation
		playerRotationMatrix.rotateZ(((RollEntity) entity).doABarrelRoll$getRoll() * MagicNumbers.TORAD);
		playerRotationMatrix.rotateX(entity.getPitch() * MagicNumbers.TORAD);
		playerRotationMatrix.rotateY(entity.getYaw() * MagicNumbers.TORAD);

		var velocity = new Vector3d(entity.getVelocity().x, entity.getVelocity().y, entity.getVelocity().z);
		// Inverted matrix??? I think.
		var relativeVelocity = velocity.mulDirection(playerRotationMatrix.invert(new Matrix4d()), new Vector3d());

		var forward = new Vector2d(1, 0);
		var alpha = new Vector2d(relativeVelocity.x, relativeVelocity.z).angle(forward);
		var beta = new Vector2d(relativeVelocity.x, relativeVelocity.y).angle(forward);

		var relativeForceVector = MagicMath.calculateRelativeForceVector(alpha, beta, velocity.length());
		var absoluteForceVector = relativeForceVector.mulDirection(playerRotationMatrix);

		var newVelocity = MagicMath.calculateNewVelocity(absoluteForceVector, velocity);

		return new Vec3d(newVelocity.x, newVelocity.y, newVelocity.z);
	}
}