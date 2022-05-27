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
			//System.out.println("Recieved Packet in Server of type "+type);
			// 0 = waypoint to all
			// 1 = waypoint to a player
			// 2 = remove a player's waypoint by name
			// 3 = remove a player's waypoint by prefix
			// 4 = remove all player's waypoint by name
			// 5 = remove all player's waypoint by prefix
			// 6 = share waypoint to team
			switch (type) {
				case 0 : 
					MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(event.packet);
					break;
				case 1 : 
					MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(event.packet);
					break;
				case 2:
					MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(event.packet);
					break;
				case 3:
					MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(event.packet);
					break;
				case 4:
					MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(event.packet);
					break;
				case 5:
					MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(event.packet);
					break;
				case 6:
					MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(event.packet);
					break;
			}
			bbis.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
}
