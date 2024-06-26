package net.ltxprogrammer.changed.mixin.render;

import net.ltxprogrammer.changed.client.tfanimations.AnimationInstance;
import net.ltxprogrammer.changed.client.tfanimations.TransfurAnimations;
import net.ltxprogrammer.changed.item.SpecializedAnimations;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> extends AgeableListModel<T> implements ArmedModel, HeadedModel {
    @Unique
    protected SpecializedAnimations.AnimationHandler.EntityStateContext entityContextOf(T entity, float partialTicks) {
        return new SpecializedAnimations.AnimationHandler.EntityStateContext(entity, this.attackTime, partialTicks) {
            @Override
            public HumanoidArm getAttackArm() {
                HumanoidArm humanoidarm = entity.getMainArm();
                return entity.swingingArm == InteractionHand.MAIN_HAND ? humanoidarm : humanoidarm.getOpposite();
            }
        };
    }

    @Unique
    protected SpecializedAnimations.AnimationHandler.UpperModelContext upperModelContext() {
        HumanoidModel<T> self = (HumanoidModel<T>)(Object)this;
        return new SpecializedAnimations.AnimationHandler.UpperModelContext(self.leftArm, self.rightArm, self.body, self.head) {
            @Override
            public ModelPart getArm(HumanoidArm humanoidArm) {
                return humanoidArm == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
            }
        };
    }

    @Inject(method = "setupAttackAnimation", at = @At("HEAD"), cancellable = true)
    protected void setupAttackAnimation(T entity, float ageInTicks, CallbackInfo callback) {
        var entityContext = entityContextOf(entity, ageInTicks - entity.tickCount);
        var upperModelContext = upperModelContext();

        var mainHandItem = entity.getItemBySlot(EquipmentSlot.MAINHAND);
        var offHandItem = entity.getItemBySlot(EquipmentSlot.OFFHAND);
        if ((!entity.isUsingItem() || entity.getUsedItemHand() == InteractionHand.MAIN_HAND) &&
                !mainHandItem.isEmpty() && mainHandItem.getItem() instanceof SpecializedAnimations specialized) {
            var handler = specialized.getAnimationHandler();
            if (handler != null && handler.setupAnimation(mainHandItem, entityContext, upperModelContext, InteractionHand.MAIN_HAND)) {
                return;
            }
        }

        else if ((!entity.isUsingItem() || entity.getUsedItemHand() == InteractionHand.OFF_HAND) &&
                !offHandItem.isEmpty() && offHandItem.getItem() instanceof SpecializedAnimations specialized) {
            var handler = specialized.getAnimationHandler();
            if (handler != null && handler.setupAnimation(offHandItem, entityContext, upperModelContext, InteractionHand.OFF_HAND)) {
                return;
            }
        }
    }

    @Unique
    private final Map<T, AnimationInstance> cachedAnimationInstance = new HashMap<>();
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;copyFrom(Lnet/minecraft/client/model/geom/ModelPart;)V"))
    public void setupAnimAndForceAnimation(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float p_102870_, float p_102871_, CallbackInfo ci) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(entity), variant -> {
            if (variant.transfurProgression < 1f) {
                final var instance = cachedAnimationInstance.computeIfAbsent(entity, e -> {
                    final var anim = TransfurAnimations.getAnimationFromCause(variant.cause);
                    return anim != null ? anim.createInstance((HumanoidModel<?>)(Object)this) : null;
                });

                if (instance != null)
                    instance.animate((HumanoidModel<?>)(Object)this, variant.getTransfurProgression(ageInTicks) * variant.cause.getDuration());
            } else {
                cachedAnimationInstance.remove(entity);
            }
        });
    }
}
