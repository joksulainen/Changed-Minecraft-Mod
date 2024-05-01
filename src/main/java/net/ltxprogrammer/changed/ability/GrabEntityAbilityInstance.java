package net.ltxprogrammer.changed.ability;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.network.packet.GrabEntityPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GrabEntityAbilityInstance extends AbstractAbilityInstance {
    private boolean wasGrabbedSilent = false;
    private boolean wasGrabbedInvisible = false;
    @Nullable
    public LivingEntity grabbedEntity = null;
    @Nullable
    public ChangedEntity syncEntity = null;
    private int grabCooldown = 0;
    public boolean grabbedHasControl = false;
    public boolean suited = false;
    public float grabStrength = 0.0f;
    public float grabStrengthO = 0.0f;
    public float suitTransition = 0.0f;
    public float suitTransitionO = 0.0f;

    public static final float GRAB_STRENGTH_DECAY = 0.1f / 20;
    public static final float GRAB_STRENGTH_DECAY_SUITED = 0.01f / 20;

    public static final float GRAB_STRENGTH_DECAY_PLAYER = 0.21f;
    public static final float GRAB_STRENGTH_DECAY_PLAYER_SUITED = 0.12f;
    public static final float GRAB_STRENGTH_DECAY_PENALTY = 0.2f;

    private static final int GRAB_ESCAPE_TRUST = 5;

    public boolean shouldRenderLatex() {
        return !shouldRenderGrabbedEntity();
    }

    public boolean shouldRenderGrabbedEntity() {
        return suited && grabbedHasControl && grabbedEntity != null;
    }

    public boolean shouldAnimateArms() {
        return grabbedEntity != null && !suited;
    }

    public float getGrabStrength(float partialTicks) {
        return Mth.lerp(partialTicks, grabStrengthO, grabStrength);
    }

    public float getSuitTransitionProgress(float partialTicks) {
        return Mth.lerp(partialTicks, suitTransitionO, suitTransition) / 3.0f;
    }

    public LivingEntity getHoveredEntity(IAbstractChangedEntity entity) {
        if (!(entity.getEntity() instanceof Player player))
            return null;

        if (!UniversalDist.isLocalPlayer(player))
            return null;

        var hitResult = UniversalDist.getLocalHitResult();
        if (hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
            if (livingEntity instanceof Player targetPlayer && ProcessTransfur.isPlayerLatex(targetPlayer))
                return null;
            if (livingEntity.getType().is(ChangedTags.EntityTypes.HUMANOIDS) || livingEntity instanceof Player)
                return livingEntity;
        }
        return null;
    }

    public GrabEntityAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
        super(ability, entity);
    }

    @Override
    public AbstractAbility.UseType getUseType() {
        return AbstractAbility.UseType.HOLD;
    }

    @Override
    public void onSelected() {
        if (entity.getLevel().isClientSide)
            this.entity.displayClientMessage(new TranslatableComponent("ability.changed.grab_entity.how_to_grab", KeyReference.ABILITY.getName(entity.getLevel())), true);
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canKeepUsing() {
        return true;
    }

    public void releaseEntity() {
        if (this.syncEntity != null)
            this.syncEntity.discard();
        this.syncEntity = null;
        this.grabbedHasControl = false;
        this.grabStrength = 0.0f;
        this.suitTransition = 0.0f;

        ESCAPE_KEYS.forEach((key, value) -> {
            value.getFirst().getSecond().accept(false);
            value.getSecond().getSecond().accept(false);
        });

        if (this.grabbedEntity == null) return;

        if (this.grabbedEntity instanceof LivingEntityDataExtension ext)
            ext.setGrabbedBy(null);

        if (this.entity.getEntity() instanceof Player player && player.level.isClientSide)
            Changed.PACKET_HANDLER.sendToServer(GrabEntityPacket.release(player, this.grabbedEntity));
        if (!(entity instanceof Player)) {
            this.grabbedEntity.setInvisible(wasGrabbedInvisible);
            this.grabbedEntity.setSilent(wasGrabbedSilent);
        }
        this.grabbedEntity.noPhysics = false;
        this.entity.getEntity().noPhysics = false;
        this.grabbedEntity = null;
        this.suited = false;
        this.attackDown = false;
        this.useDown = false;
        this.grabCooldown = 40;
    }

    private void prepareSyncEntity(LivingEntity syncTo) {
        if (syncTo instanceof Player)
            return;

        this.syncEntity = (ChangedEntity) this.entity.getChangedEntity().getType().create(this.entity.getLevel());
        this.syncEntity.setId(TransfurVariant.getNextEntId());
        CompoundTag tag = new CompoundTag();
        this.entity.getChangedEntity().saveWithoutId(tag);
        this.syncEntity.load(tag);
    }

    public void suitEntity(LivingEntity entity) {
        getController().resetHoldTicks();

        ProcessTransfur.forceNearbyToRetarget(entity.level, entity);
        if (!(entity instanceof Player)) {
            wasGrabbedSilent = entity.isSilent();
            wasGrabbedInvisible = entity.isInvisible();
            entity.setSilent(true);
            entity.setInvisible(true);
            prepareSyncEntity(entity);
        }

        if (this.grabbedEntity == entity) {
            this.suited = true;
            this.grabStrength = 1.0f;
            return;
        }

        this.releaseEntity();
        this.grabbedEntity = entity;
        this.suited = true;
        this.grabStrength = 1.0f;
    }

    public void grabEntity(LivingEntity entity) {
        getController().resetHoldTicks();
        prepareSyncEntity(entity);
        if (this.grabbedEntity == entity) {
            this.suited = false;
            this.grabbedHasControl = false;
            this.grabStrength = 1.0f;
            return;
        }

        this.releaseEntity();
        this.grabbedEntity = entity;
        this.grabStrength = 1.0f;
    }

    @Override
    public void onRemove() {
        releaseEntity();
    }

    void handleInstructions(Level level) {
        if (!level.isClientSide) return;

        if (instructionTicks == 180)
            this.entity.displayClientMessage(new TranslatableComponent("ability.changed.grab_entity.how_to_release", KeyReference.ABILITY.getName(level)), true);
        else if (instructionTicks == 120)
            this.entity.displayClientMessage(new TranslatableComponent("ability.changed.grab_entity.how_to_transfur", KeyReference.ATTACK.getName(level)), true);
        else if (instructionTicks == 60)
            this.entity.displayClientMessage(new TranslatableComponent("ability.changed.grab_entity.how_to_suit", KeyReference.USE.getName(level)), true);
        if (instructionTicks > 0)
            instructionTicks--;

        if (instructionTicks == -180)
            this.entity.displayClientMessage(new TranslatableComponent("ability.changed.grab_entity.how_to_release", KeyReference.ABILITY.getName(level)), true);
        else if (instructionTicks == -120)
            this.entity.displayClientMessage(new TranslatableComponent("ability.changed.grab_entity.how_to_absorb", KeyReference.ATTACK.getName(level), KeyReference.USE.getName(level)), true);
        else if (instructionTicks == -60 && this.grabbedEntity instanceof Player) // Only show toggle when a player is grabbed
            this.entity.displayClientMessage(new TranslatableComponent("ability.changed.grab_entity.how_to_toggle_control", KeyReference.ABILITY.getName(level)), true);
        if (instructionTicks < 0)
            instructionTicks++;
    }

    int instructionTicks = 0;
    public boolean attackDown = false;
    public boolean useDown = false;

    public boolean escapeKeyForwardO = false;
    public boolean escapeKeyBackwardO = false;
    public boolean escapeKeyLeftO = false;
    public boolean escapeKeyRightO = false;
    public boolean escapeKeyForward = false;
    public boolean escapeKeyBackward = false;
    public boolean escapeKeyLeft = false;
    public boolean escapeKeyRight = false;
    public int ticksUnpressed = 0;
    public KeyReference currentEscapeKey = null;
    private final Map<KeyReference, Pair<
            Pair<Supplier<Boolean>, Consumer<Boolean>>,
            Pair<Supplier<Boolean>, Consumer<Boolean>>
            >> ESCAPE_KEYS = Util.make(new HashMap<>(), map -> {
                map.put(KeyReference.MOVE_FORWARD, Pair.of(
                        Pair.of(() -> escapeKeyForward, (v) -> escapeKeyForward = v),
                        Pair.of(() -> escapeKeyForwardO, (v) -> escapeKeyForwardO = v)
                ));
                map.put(KeyReference.MOVE_BACKWARD, Pair.of(
                        Pair.of(() -> escapeKeyBackward, (v) -> escapeKeyBackward = v),
                        Pair.of(() -> escapeKeyBackwardO, (v) -> escapeKeyBackwardO = v)
                ));
                map.put(KeyReference.MOVE_LEFT, Pair.of(
                        Pair.of(() -> escapeKeyLeft, (v) -> escapeKeyLeft = v),
                        Pair.of(() -> escapeKeyLeftO, (v) -> escapeKeyLeftO = v)
                ));
                map.put(KeyReference.MOVE_RIGHT, Pair.of(
                        Pair.of(() -> escapeKeyRight, (v) -> escapeKeyRight = v),
                        Pair.of(() -> escapeKeyRightO, (v) -> escapeKeyRightO = v)
                ));
    });

    public void handleEscape() {
        if (!(this.grabbedEntity instanceof Player player)) {
            this.grabStrength -= this.suited ? GRAB_STRENGTH_DECAY_SUITED : GRAB_STRENGTH_DECAY;
        }

        else {
            if (player == UniversalDist.getLocalPlayer()) { // Client-side code of the grabbed player
                AtomicBoolean stateChanged = new AtomicBoolean(false);

                ESCAPE_KEYS.forEach((key, value) -> {
                    boolean isDown = key.isDown(player.level);
                    boolean oldState = value.getFirst().getFirst().get();
                    if (isDown != oldState) { // Key does not match old state, send update packet
                        value.getFirst().getSecond().accept(isDown);
                        stateChanged.set(true);
                    }
                });

                if (stateChanged.getAcquire()) {
                    Changed.PACKET_HANDLER.sendToServer(new GrabEntityPacket.EscapeKeyState(player,
                            escapeKeyForward, escapeKeyBackward, escapeKeyLeft, escapeKeyRight));
                }
            }

            if (currentEscapeKey == null && !player.level.isClientSide) {
                currentEscapeKey = Util.getRandom(ESCAPE_KEYS.keySet().toArray(new KeyReference[0]), this.entity.getEntity().getRandom());
                Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new GrabEntityPacket.AnnounceEscapeKey(player, currentEscapeKey));
            }

            if (currentEscapeKey != null) {
                final var keyRef = ESCAPE_KEYS.get(currentEscapeKey);
                if (keyRef.getFirst().getFirst().get() && !keyRef.getSecond().getFirst().get()) { // Key was just pressed
                    // This is to reduce the strength of cheating (pressing the key too fast)
                    float trustStrength = Mth.clamp((float)ticksUnpressed / (float)GRAB_ESCAPE_TRUST, 0.0f, 1.0f);
                    float keyStrength = (this.suited ? GRAB_STRENGTH_DECAY_PLAYER_SUITED : GRAB_STRENGTH_DECAY_PLAYER) * trustStrength;
                    this.grabStrength -= keyStrength;
                    this.suitTransition = Mth.clamp(this.suitTransition - (keyStrength * 0.5f), 0.0f, 3.0f);
                    currentEscapeKey = null;
                    ticksUnpressed = 0;
                } else {
                    ticksUnpressed++;

                    boolean badKey = ESCAPE_KEYS.entrySet().stream().filter((entry) -> entry.getKey() != currentEscapeKey).anyMatch((entry) -> {
                        return entry.getValue().getFirst().getFirst().get() && !entry.getValue().getSecond().getFirst().get();
                    });

                    if (badKey) {
                        this.grabStrength = Mth.clamp(this.grabStrength + GRAB_STRENGTH_DECAY_PENALTY, 0.0f, 1.0f);
                        currentEscapeKey = null;
                        ticksUnpressed = 0;
                    }
                }
            }

            ESCAPE_KEYS.forEach((ref, pair) -> {
                // Copy current state to old state
                pair.getSecond().getSecond().accept(pair.getFirst().getFirst().get());
            });
        }
    }

    public void tickIdle() { // Called every tick of LatexVariantInstance, for variants that have this ability
        this.grabStrengthO = this.grabStrength;
        this.suitTransitionO = this.suitTransition;

        if (this.grabCooldown > 0)
            this.grabCooldown--;

        if (this.syncEntity != null) {
            this.syncEntity.visualTick(this.entity.getLevel());
        }

        if (this.grabbedEntity != null) {
            if (this.grabbedEntity instanceof Mob mob) {
                mob.goalSelector.getRunningGoals().forEach(WrappedGoal::stop);
                mob.targetSelector.getRunningGoals().forEach(WrappedGoal::stop);
                mob.setTarget(null);
            }

            Level level = entity.getLevel();

            handleInstructions(level);

            if (this.grabbedEntity instanceof LivingEntityDataExtension ext)
                ext.setGrabbedBy(this.entity.getEntity());

            if (!grabbedHasControl) {
                handleEscape();
                this.grabbedEntity.noPhysics = true;
                this.entity.getEntity().noPhysics = false;
                TransfurVariantInstance.syncEntityPosRotWithEntity(this.grabbedEntity, this.entity.getEntity());
            } else {
                this.grabbedEntity.noPhysics = false;
                this.entity.getEntity().noPhysics = true;
                TransfurVariantInstance.syncEntityPosRotWithEntity(this.entity.getEntity(), this.grabbedEntity);
            }

            if (suited && !(this.grabbedEntity instanceof Player)) {
                this.grabbedEntity.setAirSupply(this.entity.getEntity().getAirSupply());
                this.grabbedEntity.resetFallDistance();
            }

            if (this.entity.getEntity() instanceof Player player && (player.isDeadOrDying() || player.isRemoved() || player.isSpectator())) {
                this.releaseEntity();
                return;
            }

            if (this.grabbedEntity instanceof Player player && player.isSpectator()) {
                this.releaseEntity();
                return;
            }

            if (this.suited && this.grabbedEntity instanceof Player player && !ProcessTransfur.isPlayerLatex(player)) {
                ProcessTransfur.setPlayerTransfurVariant(player, this.entity.getSelfVariant(), TransfurCause.GRAB_REPLICATE, 1.0f, (variant) -> {
                    // This runs before the server broadcasts it to players
                    variant.checkForTemporary(this.entity);
                });
            }

            if (this.grabbedEntity instanceof Player player && ProcessTransfur.isPlayerLatex(player)) {
                var variant = ProcessTransfur.getPlayerTransfurVariant(player);
                if (!variant.isTemporaryFromSuit()) {
                    this.releaseEntity();
                    return;
                }
            }

            if (this.grabbedEntity.isDeadOrDying() || this.grabbedEntity.isRemoved() || this.grabStrength <= 0.0f) {
                this.releaseEntity();
                return;
            }

            if (UniversalDist.getLocalPlayer() == this.entity.getEntity()) {
                boolean attackKeyDown = KeyReference.ATTACK.isDown(level);
                boolean useKeyDown = KeyReference.USE.isDown(level);
                if (attackKeyDown != attackDown || useKeyDown != useDown)
                    Changed.PACKET_HANDLER.sendToServer(GrabEntityPacket.keyState(UniversalDist.getLocalPlayer(), attackKeyDown, useKeyDown));
                attackDown = attackKeyDown;
                useDown = useKeyDown;
            }

            if (attackDown && useDown && suited) {
                if (ProcessTransfur.tryAbsorption(this.grabbedEntity, this.entity, 4.0f, null)) {
                    this.releaseEntity();
                    return;
                }
            }

            if (attackDown && !suited) {
                if (ProcessTransfur.progressTransfur(this.grabbedEntity, 4.0f, entity.getChangedEntity().getTransfurVariant(),
                        TransfurContext.latexHazard(this.entity, TransfurCause.GRAB_REPLICATE)) && !this.entity.getLevel().isClientSide)
                    this.releaseEntity();
            }

            else if (useDown) {
                this.suitTransition += 0.075f;
            }

            if (this.suitTransition > 0.0f) {
                this.suitTransition = Math.max(0.0f, this.suitTransition - 0.025f);

                if (this.suitTransition > 3.0f && !suited) { // 3 seconds
                    this.suited = true;
                    this.suitTransition = 0.0f;

                    if (this.entity.getEntity() instanceof Player player && player.level.isClientSide) {
                        Changed.PACKET_HANDLER.sendToServer(GrabEntityPacket.suitGrab(player, this.grabbedEntity));
                        this.instructionTicks = -180;
                        this.grabStrength = 1.0f;
                    }
                }
            }
        }
    }

    @Override
    public void startUsing() {

    }

    @Override
    public void tick() {
        if (this.grabbedEntity != null) {
            if (this.getController().getHoldTicks() >= 40)
                this.releaseEntity();
            return;
        }

        if (this.grabCooldown > 0)
            return;

        var grabbedEntity = this.getHoveredEntity(entity);
        if (grabbedEntity != null && entity.getLevel().isClientSide && entity.getEntity() instanceof PlayerDataExtension ext) {
            if (!this.entity.getEntity().getBoundingBox().inflate(0.5, 0.0, 0.5).intersects(grabbedEntity.getBoundingBox()))
                return;

            this.grabbedEntity = grabbedEntity;
            Changed.PACKET_HANDLER.sendToServer(GrabEntityPacket.initialGrab((Player)entity.getEntity(), grabbedEntity));
            this.grabStrength = 1.0f;
            this.instructionTicks = 180;
            getController().resetHoldTicks();
            prepareSyncEntity(grabbedEntity);
        }
    }

    @Override
    public void stopUsing() {
        if (this.grabbedEntity != null && suited && this.getController().getHoldTicks() < 40) {
            if (this.grabbedEntity instanceof Player) {
                this.grabbedHasControl = !this.grabbedHasControl;
                this.grabbedEntity.noPhysics = !this.grabbedHasControl;
                this.entity.getEntity().noPhysics = this.grabbedHasControl;
            }
        }
    }

    @Override
    public void saveData(CompoundTag tag) {
        super.saveData(tag);
        if (grabbedEntity != null)
            tag.putUUID("GrabbedEntity", grabbedEntity.getUUID());
        tag.putBoolean("GrabbedHasControl", grabbedHasControl);
        tag.putBoolean("Suited", suited);
        tag.putFloat("GrabStrength", grabStrength);
        tag.putFloat("SuitTransition", suitTransition);
    }

    @Override
    public void readData(CompoundTag tag) {
        super.readData(tag);
        if (tag.contains("GrabbedEntity")) {
            final UUID entityUUID = tag.getUUID("GrabbedEntity");
            this.entity.getLevel().getEntities(this.entity.getEntity(), this.entity.getEntity().getBoundingBox().inflate(1.0)).forEach(foundEntity -> {
                if (foundEntity instanceof LivingEntity livingEntity && livingEntity.getUUID().equals(entityUUID)) {
                    this.grabbedEntity = livingEntity;
                }
            });
        }
        if (this.grabbedEntity != null) {
            this.grabbedHasControl = tag.getBoolean("GrabbedHasControl");
            this.suited = tag.getBoolean("Suited");
            this.grabStrength = tag.getFloat("GrabStrength");
            this.suitTransition = tag.getFloat("SuitTransition");
        }
    }
}
