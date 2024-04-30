package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class HoldEntityAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public HoldEntityAnimator(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        super(head, torso, leftArm, rightArm);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.FINAL;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (core.grabState == AdvancedHumanoidModel.GrabState.HOLD) {
            rightArm.xRot = torso.xRot + (Mth.PI * -0.4f);
            rightArm.yRot = torso.yRot + (Mth.PI * -0.3f);
            leftArm.xRot = torso.xRot + (Mth.PI * -0.525f);
            leftArm.yRot = torso.yRot + (Mth.PI * 0.3f);

            rightArm.z -= 2.5f;
            rightArm.y += 2.0f;
            leftArm.z -= 2.5f;
            leftArm.y += 2.0f;
        }

        else if (core.grabState == AdvancedHumanoidModel.GrabState.REACH) {
            rightArm.xRot = torso.xRot + (Mth.PI * -0.45f);
            rightArm.yRot = torso.yRot + (Mth.PI * 0.05f);
            leftArm.xRot = torso.xRot + (Mth.PI * -0.45f);
            leftArm.yRot = torso.yRot + (Mth.PI * -0.05f);
        }
    }
}
