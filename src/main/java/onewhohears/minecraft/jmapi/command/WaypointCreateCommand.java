package onewhohears.minecraft.jmapi.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import onewhohears.minecraft.jmapi.api.ApiWaypointManager;
import onewhohears.minecraft.jmapi.util.UtilCommand;
import onewhohears.minecraft.jmapi.util.UtilParse;

public class WaypointCreateCommand extends CommandBase {

	private static final String CMD_NAME = "wpcreate";

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (sender.getEntityWorld().isRemote) {
			sendError("This command only works on the client side!", sender);
			return;
		}
		if (args.length > 8 || args.length < 5) {
			sendError("Invalid Command. Do "+getCommandUsage(sender), sender);
			return;
		}
		Integer x = null, y = null, z = null, color = null, dimension = null;
		boolean delete = false;
		String name = "";
		if (args.length >= 5) {
			name = args[1];
			x = UtilParse.decodeInt(args[2]);
			y = UtilParse.decodeInt(args[3]);
			z = UtilParse.decodeInt(args[4]);
			if (x == null || y == null || z == null) {
				sendError("The x y z params must be integers!", sender);
				return;
			}
		}
		if (args.length >= 6) {
			color = UtilParse.decodeInt(args[5]);
			if (color == null) {
				sendError("color must be an integer!", sender);
				return;
			}
		}
		if (args.length >= 7) {
			dimension = UtilParse.decodeInt(args[6]);
			if (dimension == null) {
				sendError("dimension must be an integer!", sender);
				return;
			}
		}
		if (args.length >= 8 && args[7].equals("true")) delete = true;
		if (color == null) color = 0xffff00;
		if (dimension == null) dimension = sender.getEntityWorld().provider.dimensionId;
		boolean success = false;
		if (args[0].equals("@s")) {
			success = ApiWaypointManager.getInstance().shareWaypointToPlayer(x, y, z, dimension, color, delete, name, 
					"", sender.getCommandSenderName(), false);
		} else if (args[0].equals("@a")) {
			success = ApiWaypointManager.getInstance().shareAllPlayersWaypoint(x, y, z, dimension, color, delete, name, 
					"", false);
		} else if (args[0].equals("@p")) {
			EntityPlayer player = UtilCommand.getNearestNonSenderPlayer(sender);
			if (player != null) 
				success = ApiWaypointManager.getInstance().shareWaypointToPlayer(x, y, z, dimension, color, delete, name, 
					"", player.getDisplayName(), false);
		} else if (UtilParse.hasPrefix(args[0], "@t:")) {
			success = ApiWaypointManager.getInstance().shareWaypointToTeam(x, y, z, dimension, color, delete, name, 
					"", args[0].substring("@t:".length()), false);
		} else {
			success = ApiWaypointManager.getInstance().shareWaypointToPlayer(x, y, z, dimension, color, delete, name, 
					"", args[0], false);
		}
		if (success) sendMessage("Waypoint Sent!", sender);
		else sendError("Failed to send waypoint!", sender);
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/"+CMD_NAME+" <@s/@a/@p/@t:team_name/player_name> <name> <x> <y> <z> [color] [dimension] [delete_dups]";
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1) {
			return CommandBase.getListOfStringsMatchingLastWord(args, getParam1TabComplete()); 
		}
		return null;
	}
	
	private String[] getParam1TabComplete() {
		List<String> tabs = new ArrayList<String>();
		tabs.add("@s");
		tabs.add("@a");
		tabs.add("@p");
		tabs.addAll(getUsernames());
		tabs.addAll(getTeamTabs());
		return tabs.toArray(new String[tabs.size()]);
	}
	
	private Collection<String> getUsernames() {
		return Arrays.asList(MinecraftServer.getServer().getAllUsernames());
	}
	
	private Collection<String> getTeamTabs() {
		Iterator<String> teamNames = getTeamNames().iterator();
		Collection<String> teamTabs = new ArrayList<String>();
		while (teamNames.hasNext()) teamTabs.add("@t:"+teamNames.next());
		return teamTabs;
	}
	
	@SuppressWarnings("unchecked")
	private Collection<String> getTeamNames() {
		return MinecraftServer.getServer().getEntityWorld().getScoreboard().getTeamNames();
	}
	
	@Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
    	return true;
    } 
	
	@Override
	public String getCommandName() {
		return CMD_NAME;
	}
	
	private void sendMessage(String message, ICommandSender sender) {
		ChatComponentText chat = new ChatComponentText(message);
		ChatStyle style = new ChatStyle();
		style.setColor(EnumChatFormatting.YELLOW);
		chat.setChatStyle(style);
		sender.addChatMessage(chat);
	}
	
	private void sendError(String message, ICommandSender sender) {
		ChatComponentText chat = new ChatComponentText(message);
		ChatStyle style = new ChatStyle();
		style.setColor(EnumChatFormatting.RED);
		chat.setChatStyle(style);
		sender.addChatMessage(chat);
	}

}
