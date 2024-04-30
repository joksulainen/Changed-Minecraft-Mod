package net.ltxprogrammer.changed.client.renderer.animate.armsets;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

public class ArmSetTwoCrouchAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends HumanoidAnimator.Animator<T, M> {
    public final ModelPart leftArm;
    public final ModelPart rightArm;
    public final ModelPart leftArm2;
    public final ModelPart rightArm2;

    public ArmSetTwoCrouchAnimator(ModelPart leftArm, ModelPart rightArm, ModelPart leftArm2, ModelPart rightArm2) {
        this.leftArm = leftArm;
        this.rightArm = rightArm;
        this.leftArm2 = leftArm2;
        this.rightArm2 = rightArm2;
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.CROUCH;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        leftArm.y += 4.0f;
        rightArm.y += 4.0F;
    }
}
