package onewhohears.minecraft.jmapi;

import java.io.IOException;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBufInputStream;
import net.minecraft.server.MinecraftServer;

public class ServerPacketHandler {
	
	@SubscribeEvent
	public void onServerPacket(ServerCustomPacketEvent event) {
		if (event.side() != Side.SERVER) return;
		ByteBufInputStream bbis = new ByteBufInputStream(event.packet.payload());
		try {
			int type = bbis.readInt();
			// 0 = waypoint to all
			// 1 = waypoint to a player
			switch (type) {
				case 0 : {
					MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(event.packet);
					break;
				} case 1 : {
					MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(event.packet);
					break;
				}
			}
			bbis.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
}
