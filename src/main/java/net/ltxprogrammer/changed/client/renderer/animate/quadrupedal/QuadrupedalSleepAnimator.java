package net.ltxprogrammer.changed.client.renderer.animate.quadrupedal;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

public class QuadrupedalSleepAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends AbstractQuadrupedalAnimator<T, M> {
    public QuadrupedalSleepAnimator(ModelPart torso, ModelPart frontLeftLeg, ModelPart frontRightLeg, ModelPart backLeftLeg, ModelPart backRightLeg) {
        super(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.SLEEP;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float adjust = (float)Math.PI * 0.5F;
        torso.xRot = -(float)Math.PI * 0.5f;
        frontRightLeg.xRot += adjust * 0.7F;
        frontLeftLeg.xRot += adjust * 0.7F;
        backRightLeg.xRot += adjust;
        backLeftLeg.xRot += adjust;
    }
}
