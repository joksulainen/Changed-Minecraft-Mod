package net.ltxprogrammer.changed.client.renderer.animate.bipedal;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

public class BipedalRideAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends AbstractBipedalAnimator<T, M> {
    public BipedalRideAnimator(ModelPart leftLeg, ModelPart rightLeg) {
        super(leftLeg, rightLeg);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.RIDE;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        rightLeg.xRot = -1.4137167F;
        rightLeg.yRot = ((float)Math.PI / 10F);
        rightLeg.zRot = 0.07853982F;
        leftLeg.xRot = -1.4137167F;
        leftLeg.yRot = (-(float)Math.PI / 10F);
        leftLeg.zRot = -0.07853982F;
    }
}
