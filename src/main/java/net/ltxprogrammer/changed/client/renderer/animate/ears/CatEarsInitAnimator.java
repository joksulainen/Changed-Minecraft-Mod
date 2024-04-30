package net.ltxprogrammer.changed.client.renderer.animate.ears;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class CatEarsInitAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends AbstractEarsAnimator<T, M> {
    public CatEarsInitAnimator(ModelPart leftEar, ModelPart rightEar) {
        super(leftEar, rightEar);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.INIT;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float tailDrag = entity.getTailDragAmount(this.core.partialTicks);

        rightEar.zRot = Mth.lerp(core.ageLerp * 0.85f, -0.08726646F, 0.04363323F);
        rightEar.yRot = Mth.clamp(tailDrag * 0.5f, -Mth.PI / 8.0f, Mth.PI / 4.0f);
        leftEar.zRot = -Mth.lerp(core.ageLerp * 0.85f, -0.08726646F, 0.04363323F);
        leftEar.yRot = Mth.clamp(tailDrag * 0.5f, -Mth.PI / 4.0f, Mth.PI / 8.0f);
    }
}
