package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LatexBlueWolf extends AbstractLightLatexWolf implements PowderSnowWalkable {
    public LatexBlueWolf(EntityType<? extends LatexBlueWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    public Gender getGender() {
        return Gender.FEMALE;
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.LONG_MESSY.get();
    }

    @Override
    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collection.FEMALE.getStyles();
    }

    @Override
    public Color3 getHairColor(int layer) {
        return Color3.getColor("#cdcdcd");
    }

    public Color3 getDripColor() {
        return Color3.getColor("#8ad6e7");
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#8ad6e7");
    }
}
