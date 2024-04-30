package net.ltxprogrammer.changed.client.renderer.animate.quadrupedal;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.upperbody.AbstractUpperBodyAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

public class TaurCrouchAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public TaurCrouchAnimator(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        super(head, torso, leftArm, rightArm);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.CROUCH;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        head.y = 4.2F + core.hipOffset + (12.0f - core.legLength);;
        torso.y = 3.2F + core.hipOffset + (12.0f - core.legLength);;
        leftArm.y = 5.2F + core.hipOffset + (12.0f - core.legLength);;
        rightArm.y = 5.2F + core.hipOffset + (12.0f - core.legLength);;
    }
}
