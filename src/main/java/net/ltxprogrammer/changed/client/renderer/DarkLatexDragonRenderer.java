package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.DarkLatexDragonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexBlueDragonModel;
import net.ltxprogrammer.changed.entity.beast.DarkLatexDragon;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DarkLatexDragonRenderer extends LatexHumanoidRenderer<DarkLatexDragon, DarkLatexDragonModel, ArmorLatexBlueDragonModel<DarkLatexDragon>> {
    public DarkLatexDragonRenderer(EntityRendererProvider.Context context) {
        super(context, new DarkLatexDragonModel(context.bakeLayer(DarkLatexDragonModel.LAYER_LOCATION)),
                ArmorLatexBlueDragonModel::new, ArmorLatexBlueDragonModel.INNER_ARMOR, ArmorLatexBlueDragonModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(DarkLatexDragon p_114482_) {
        return new ResourceLocation("changed:textures/dark_latex_dragon.png");
    }
}