package dev.enjarai.doabarrelroll.physics.mixin;

import dev.enjarai.doabarrelroll.physics.DoABarrelRollPhysics;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@ModifyArg(
			method = "travel",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/entity/LivingEntity;isFallFlying()Z"
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V",
					ordinal = 0
			)
	)
	private Vec3d init(Vec3d original) {
		return DoABarrelRollPhysics.handleVelocity((LivingEntity) (Object) this, original);
	}
}