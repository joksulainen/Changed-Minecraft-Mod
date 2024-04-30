package net.ltxprogrammer.changed.client.renderer.animate.quadrupedal;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

public class QuadrupedalRideAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends AbstractQuadrupedalAnimator<T, M> {
    public QuadrupedalRideAnimator(ModelPart torso, ModelPart frontLeftLeg, ModelPart frontRightLeg, ModelPart backLeftLeg, ModelPart backRightLeg) {
        super(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.RIDE;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        frontRightLeg.xRot = -1.4137167F;
        frontRightLeg.yRot = ((float)Math.PI / 10F);
        frontRightLeg.zRot = 0.07853982F;
        frontLeftLeg.xRot = -1.4137167F;
        frontLeftLeg.yRot = (-(float)Math.PI / 10F);
        frontLeftLeg.zRot = -0.07853982F;
    }
}
