package io.github.guy7cc.network;

import io.github.guy7cc.RpgwMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class RpgwMessageManager {
    public static SimpleChannel INSTANCE;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void registerMessages(String name) {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(RpgwMod.MOD_ID, name))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.registerMessage(id(), ServerboundManagePartyPacket.class, ServerboundManagePartyPacket::toBytes, ServerboundManagePartyPacket::new, ServerboundManagePartyPacket::handle);
        net.registerMessage(id(), ClientboundManagePartyPacket.class, ClientboundManagePartyPacket::toBytes, ClientboundManagePartyPacket::new, ClientboundManagePartyPacket::handle);
        net.registerMessage(id(), ClientboundSyncPlayerMpPacket.class, ClientboundSyncPlayerMpPacket::toBytes, ClientboundSyncPlayerMpPacket::new, ClientboundSyncPlayerMpPacket::handle);
    }
}
