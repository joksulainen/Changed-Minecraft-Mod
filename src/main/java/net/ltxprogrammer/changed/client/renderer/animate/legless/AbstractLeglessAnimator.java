package net.ltxprogrammer.changed.client.renderer.animate.legless;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

import java.util.List;

public abstract class AbstractLeglessAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends HumanoidAnimator.Animator<T, M> {
    public final ModelPart abdomen;
    public final ModelPart lowerAbdomen;
    public final ModelPart tail;
    public final List<ModelPart> tailJoints;

    public AbstractLeglessAnimator(ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail, List<ModelPart> tailJoints) {
        this.abdomen = abdomen;
        this.lowerAbdomen = lowerAbdomen;
        this.tail = tail;
        this.tailJoints = tailJoints;
    }
}
