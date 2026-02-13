package net.rebel459.combat_reborn.network;

import net.rebel459.combat_reborn.CombatReborn;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SelectQuiverSlotPacket(int slot) implements CustomPacketPayload {

    public static final Type<SelectQuiverSlotPacket> TYPE =
            new Type<>(CombatReborn.id("quiver_slot"));

    public static final StreamCodec<FriendlyByteBuf, SelectQuiverSlotPacket> CODEC =
            StreamCodec.of(
                    (buf, packet) -> buf.writeVarInt(packet.slot),
                    buf -> new SelectQuiverSlotPacket(buf.readVarInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}