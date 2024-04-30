package net.ltxprogrammer.changed.entity.beast.boss;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class Behemoth extends ChangedEntity {
    public Behemoth(EntityType<? extends Behemoth> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.xpReward = 40;
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.WHITE_LATEX;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public Color3 getDripColor() {
        return Color3.WHITE;
    }

    public TransfurVariant<?> getSelfVariant() {
        return null;
    }

    public TransfurVariant<?> getTransfurVariant() {
        return TransfurVariant.LIGHT_LATEX_WOLF.male();
    }

    public void checkDespawn() {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
        } else {
            this.noActionTime = 0;
        }
    }
}
