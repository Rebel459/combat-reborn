package net.rebel459.combat_reborn.network;

import net.rebel459.combat_reborn.CombatReborn;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SelectQuiverItemPacket(int slotId, int selectedSlot) implements CustomPacketPayload {

    public static final Type<SelectQuiverItemPacket> TYPE =
            new Type<>(CombatReborn.id("quiver_item"));

    public static final StreamCodec<FriendlyByteBuf, SelectQuiverItemPacket> CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeVarInt(packet.slotId);
                        buf.writeVarInt(packet.selectedSlot);
                    },
                    buf -> new SelectQuiverItemPacket(
                            buf.readVarInt(),
                            buf.readVarInt()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public Packet<?> toVanillaPacket() {
        return new ServerboundCustomPayloadPacket(this);
    }
}