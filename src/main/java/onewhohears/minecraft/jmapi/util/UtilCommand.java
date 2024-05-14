package onewhohears.minecraft.jmapi.util;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;

public class UtilCommand {
	
	@SuppressWarnings("unchecked")
	public static EntityPlayer getNearestNonSenderPlayer(ICommandSender sender) {
		ChunkCoordinates cc = sender.getPlayerCoordinates();
		List<EntityPlayer> players = sender.getEntityWorld().playerEntities;
		EntityPlayer nearestPlayer = null;
		double nearestDist = -1;
		for (int i = 0; i < players.size(); ++i) {
			if (players.get(i).equals(sender)) continue;
			double dist = players.get(i).getDistanceSq(cc.posX, cc.posY, cc.posZ);
			if (nearestDist == -1 || dist < nearestDist) {
				nearestPlayer = players.get(i);
				nearestDist = dist;
			}
		}
		return nearestPlayer;
	}
	
}
