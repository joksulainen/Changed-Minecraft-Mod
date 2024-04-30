package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;

public abstract class LatexAbilityRadialScreen<T extends AbstractContainerMenu> extends AbstractRadialScreen<T> {
    public LatexAbilityRadialScreen(T menu, Inventory inventory, Component text, TransfurVariantInstance<?> variant) {
        super(menu, inventory, text, getColors(variant).setForegroundToBright().background(),
                getColors(variant).setForegroundToBright().foreground(), variant.getHost());
    }

    private static final String PATH_ORGANIC = "textures/gui/radial/organic/";
    private static final String PATH_GOO = "textures/gui/radial/goo/";
    private static final String PATH_ORGANIC_SELECTED = "textures/gui/radial/organic_selected/";
    private static final String PATH_GOO_SELECTED = "textures/gui/radial/goo_selected/";

    public abstract boolean isSelected(int section);

    protected ResourceLocation getTextureForSection(@Nullable TransfurVariant<?> variant, int section, boolean thisHovered, boolean anyHovered) {
        boolean renderSelected = thisHovered || (!anyHovered && this.isSelected(section));
        if (variant == null || variant.getEntityType().is(ChangedTags.EntityTypes.ORGANIC_LATEX))
            return Changed.modResource((renderSelected ? PATH_ORGANIC_SELECTED : PATH_ORGANIC) + section + ".png");
        else
            return Changed.modResource((renderSelected ? PATH_GOO_SELECTED : PATH_GOO) + section + ".png");
    }

    @Override
    public void renderSectionBackground(PoseStack pose, int section, double x, double y, float partialTicks, int mouseX, int mouseY, float red, float green, float blue) {
        var hovered = getSectionAt(mouseX, mouseY);
        boolean anyHovered = hovered.isPresent();
        boolean thisHovered = anyHovered && hovered.get() == section;
        RenderSystem.setShaderColor(red, green, blue, 1);
        RenderSystem.setShaderTexture(0,
                getTextureForSection(TransfurVariant.getEntityVariant((LivingEntity) Minecraft.getInstance().getCameraEntity()), section, thisHovered, anyHovered));
        blit(pose, (int)x - 32 + this.leftPos, (int)y - 32 + this.topPos, 0, 0, 64, 64, 64, 64);
    }
}
