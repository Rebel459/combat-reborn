package net.legacy.combat_reborn.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.legacy.combat_reborn.CombatReborn;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public interface ShieldInfo {
    int parryWindow = 24;
    int defaultMaxBlockDamage = 32;
    int getPercentageDamage();
    void setPercentageDamage(int value);
    default void setPercentageDamageAndSync(int value, ServerPlayer player) {
        this.setPercentageDamage(value);

        if (player != null) {
            ServerPlayNetworking.send(player, new Sync(value));
        }
    }

    // Packet payloads
    record Request() implements CustomPacketPayload {
        public static final Type<Request> TYPE = new Type<>(CombatReborn.id("request_shield_info"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Request> CODEC = StreamCodec.unit(new Request());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    record Sync(int percentageDamage) implements CustomPacketPayload {
        public static final Type<Sync> TYPE = new Type<>(CombatReborn.id("sync_shield_info"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Sync> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, Sync::percentageDamage,
                Sync::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
    ShieldInfo getInfo();
}