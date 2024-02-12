package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.EyeStyle;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Cacheable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface IAbstractChangedEntity {
    @NotNull LivingEntity getEntity();
    @NotNull ChangedEntity getLatexEntity();

    @NotNull BlockPos getBlockPosition();
    @Nullable
    TransfurVariantInstance<?> getTransfurVariantInstance();
    @NotNull Level getLevel();
    @NotNull UUID getUUID();
    @NotNull TransfurMode getTransfurMode();
    @Nullable <T extends AbstractAbilityInstance> T getAbilityInstance(AbstractAbility<T> ability);
    @Nullable AbstractContainerMenu getContainerMenu();
    @NotNull CompoundTag getPersistentData();
    @Nullable List<HairStyle> getValidHairStyles();
    @NotNull HairStyle getHairStyle();

    boolean isStillLatex();
    boolean isDeadOrDying();
    boolean isCreative();
    boolean isCrouching();
    boolean isSleeping();
    boolean isInWaterOrBubble();
    boolean addItem(ItemStack item);
    float getFoodLevel();

    void setTransfurMode(TransfurMode mode);
    void displayClientMessage(Component message, boolean overlayMessage);
    void drop(ItemStack stack, boolean includeName);
    void openMenu(MenuProvider menuProvider);
    void closeContainer();
    void setHairStyle(HairStyle style);
    void setEyeStyle(EyeStyle style);
    void causeFoodExhaustion(float exhaustion);

    static IAbstractChangedEntity forPlayer(Player player) {
        Cacheable<TransfurVariantInstance<?>> instance = Cacheable.of(() -> ProcessTransfur.getPlayerTransfurVariant(player));
        Cacheable<ChangedEntity> latex = Cacheable.of(() -> instance.get().getLatexEntity());

        return new IAbstractChangedEntity() {
            @Override
            public @NotNull LivingEntity getEntity() {
                return player;
            }

            @Override
            public @NotNull ChangedEntity getLatexEntity() {
                return latex.get();
            }

            @Override
            public @NotNull BlockPos getBlockPosition() {
                return player.blockPosition();
            }

            @Override
            public @NotNull TransfurVariantInstance<?> getTransfurVariantInstance() {
                return instance.get();
            }

            @Override
            public @NotNull Level getLevel() {
                return player.level;
            }

            @Override
            public @NotNull UUID getUUID() {
                return player.getUUID();
            }

            @Override
            public @NotNull TransfurMode getTransfurMode() {
                return ProcessTransfur.getPlayerTransfurVariant(player).transfurMode;
            }

            @org.jetbrains.annotations.Nullable
            @Override
            public <T extends AbstractAbilityInstance> T getAbilityInstance(AbstractAbility<T> ability) {
                return instance.get().getAbilityInstance(ability);
            }

            @Override
            public @NotNull AbstractContainerMenu getContainerMenu() {
                return player.containerMenu;
            }

            @Override
            public @NotNull CompoundTag getPersistentData() {
                return player.getPersistentData();
            }

            @org.jetbrains.annotations.Nullable
            @Override
            public List<HairStyle> getValidHairStyles() {
                return latex.get().getValidHairStyles();
            }

            @Override
            public @NotNull HairStyle getHairStyle() {
                return latex.get().getHairStyle();
            }

            @Override
            public boolean isStillLatex() {
                return ProcessTransfur.getPlayerTransfurVariant(player) != null;
            }

            @Override
            public boolean isDeadOrDying() {
                return player.isDeadOrDying();
            }

            @Override
            public boolean isCreative() {
                return player.isCreative();
            }

            @Override
            public boolean isCrouching() {
                return player.isCrouching();
            }

            @Override
            public boolean isSleeping() {
                return player.isSleeping();
            }

            @Override
            public boolean isInWaterOrBubble() {
                return player.isInWaterOrBubble();
            }

            @Override
            public boolean addItem(ItemStack item) {
                return player.addItem(item);
            }

            @Override
            public float getFoodLevel() {
                return player.getFoodData().getFoodLevel();
            }

            @Override
            public void setTransfurMode(TransfurMode mode) {
                instance.get().transfurMode = mode;
            }

            @Override
            public void displayClientMessage(Component message, boolean overlayMessage) {
                player.displayClientMessage(message, overlayMessage);
            }

            @Override
            public void drop(ItemStack stack, boolean includeName) {
                player.drop(stack, includeName);
            }

            @Override
            public void openMenu(MenuProvider menuProvider) {
                player.openMenu(menuProvider);
            }

            @Override
            public void closeContainer() {
                player.closeContainer();
            }

            @Override
            public void setHairStyle(HairStyle style) {
                latex.get().setHairStyle(style);
            }

            @Override
            public void setEyeStyle(EyeStyle style) {
                latex.get().setEyeStyle(style);
            }

            @Override
            public void causeFoodExhaustion(float exhaustion) {
                player.causeFoodExhaustion(exhaustion);
            }
        };
    }

    static IAbstractChangedEntity forChangedEntity(ChangedEntity changedEntity) {
        return new IAbstractChangedEntity() {
            @Override
            public @NotNull LivingEntity getEntity() {
                return changedEntity;
            }

            @Override
            public @NotNull ChangedEntity getLatexEntity() {
                return changedEntity;
            }

            @Override
            public @NotNull BlockPos getBlockPosition() {
                return changedEntity.blockPosition();
            }

            @org.jetbrains.annotations.Nullable
            @Override
            public TransfurVariantInstance<?> getTransfurVariantInstance() {
                return null;
            }

            @Override
            public @NotNull Level getLevel() {
                return changedEntity.level;
            }

            @Override
            public @NotNull UUID getUUID() {
                return changedEntity.getUUID();
            }

            @Override
            public @NotNull TransfurMode getTransfurMode() {
                return changedEntity.getTransfurMode();
            }

            @org.jetbrains.annotations.Nullable
            @Override
            public <T extends AbstractAbilityInstance> T getAbilityInstance(AbstractAbility<T> ability) {
                return changedEntity.getAbilityInstance(ability);
            }

            @org.jetbrains.annotations.Nullable
            @Override
            public AbstractContainerMenu getContainerMenu() {
                return null;
            }

            @Override
            public @NotNull CompoundTag getPersistentData() {
                return changedEntity.getPersistentData();
            }

            @org.jetbrains.annotations.Nullable
            @Override
            public List<HairStyle> getValidHairStyles() {
                return changedEntity.getValidHairStyles();
            }

            @Override
            public @NotNull HairStyle getHairStyle() {
                return changedEntity.getHairStyle();
            }

            @Override
            public boolean isStillLatex() {
                return true;
            }

            @Override
            public boolean isDeadOrDying() {
                return changedEntity.isDeadOrDying();
            }

            @Override
            public boolean isCreative() {
                return false;
            }

            @Override
            public boolean isCrouching() {
                return changedEntity.isCrouching();
            }

            @Override
            public boolean isSleeping() {
                return changedEntity.isSleeping();
            }

            @Override
            public boolean isInWaterOrBubble() {
                return changedEntity.isInWaterOrBubble();
            }

            @Override
            public boolean addItem(ItemStack item) {
                return false;
            }

            @Override
            public float getFoodLevel() {
                return 20;
            }

            @Override
            public void setTransfurMode(TransfurMode mode) {
                
            }

            @Override
            public void displayClientMessage(Component message, boolean overlayMessage) {

            }

            @Override
            public void drop(ItemStack stack, boolean includeName) {
                if (stack.isEmpty()) {
                    return;
                } else {
                    if (changedEntity.level.isClientSide) {
                        changedEntity.swing(InteractionHand.MAIN_HAND);
                    }

                    double d0 = changedEntity.getEyeY() - (double)0.3F;
                    ItemEntity itementity = new ItemEntity(changedEntity.level, changedEntity.getX(), d0, changedEntity.getZ(), stack);
                    itementity.setPickUpDelay(40);
                    if (includeName) {
                        itementity.setThrower(changedEntity.getUUID());
                    }

                    float f7 = 0.3F;
                    float f8 = Mth.sin(changedEntity.getXRot() * ((float)Math.PI / 180F));
                    float f2 = Mth.cos(changedEntity.getXRot() * ((float)Math.PI / 180F));
                    float f3 = Mth.sin(changedEntity.getYRot() * ((float)Math.PI / 180F));
                    float f4 = Mth.cos(changedEntity.getYRot() * ((float)Math.PI / 180F));
                    float f5 = changedEntity.level.random.nextFloat() * ((float)Math.PI * 2F);
                    float f6 = 0.02F * changedEntity.level.random.nextFloat();
                    itementity.setDeltaMovement((double)(-f3 * f2 * 0.3F) + Math.cos((double)f5) * (double)f6, (double)(-f8 * 0.3F + 0.1F + (changedEntity.level.random.nextFloat() - changedEntity.level.random.nextFloat()) * 0.1F), (double)(f4 * f2 * 0.3F) + Math.sin((double)f5) * (double)f6);
                }
            }

            @Override
            public void openMenu(MenuProvider menuProvider) {

            }

            @Override
            public void closeContainer() {

            }

            @Override
            public void setHairStyle(HairStyle style) {
                changedEntity.setHairStyle(style);
            }

            @Override
            public void setEyeStyle(EyeStyle style) {
                changedEntity.setEyeStyle(style);
            }

            @Override
            public void causeFoodExhaustion(float exhaustion) {

            }
        };
    }
}
