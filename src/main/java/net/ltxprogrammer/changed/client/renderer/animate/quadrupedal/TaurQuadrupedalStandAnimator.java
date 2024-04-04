package net.ltxprogrammer.changed.client.renderer.animate.quadrupedal;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class TaurQuadrupedalStandAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractQuadrupedalAnimator<T, M> {
    public final ModelPart frontLeftLegLower, frontLeftFoot;
    public final ModelPart frontRightLegLower, frontRightFoot;
    public final ModelPart backLeftLegLower, backLeftFoot, backLeftPad;
    public final ModelPart backRightLegLower, backRightFoot, backRightPad;

    public TaurQuadrupedalStandAnimator(ModelPart torso, ModelPart frontLeftLeg, ModelPart frontLeftLegLower, ModelPart frontLeftFoot,
                                        ModelPart frontRightLeg, ModelPart frontRightLegLower, ModelPart frontRightFoot,
                                        ModelPart backLeftLeg, ModelPart backLeftLegLower, ModelPart backLeftFoot, ModelPart backLeftPad,
                                        ModelPart backRightLeg, ModelPart backRightLegLower, ModelPart backRightFoot, ModelPart backRightPad) {
        super(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg);
        this.frontLeftLegLower = frontLeftLegLower;
        this.frontLeftFoot = frontLeftFoot;
        this.frontRightLegLower = frontRightLegLower;
        this.frontRightFoot = frontRightFoot;
        this.backLeftLegLower = backLeftLegLower;
        this.backLeftFoot = backLeftFoot;
        this.backLeftPad = backLeftPad;
        this.backRightLegLower = backRightLegLower;
        this.backRightFoot = backRightFoot;
        this.backRightPad = backRightPad;
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.STAND;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float offset = -Mth.lerp(core.ageLerp, 0.0f, Mth.lerp(limbSwingAmount, 1.0f, 0.25f));
        frontRightLeg.y = offset + 1.0f;
        frontLeftLeg.y = offset + 1.0f;
        backRightLeg.y = offset + 1.0f;
        backLeftLeg.y = offset + 1.0f;
    }
}