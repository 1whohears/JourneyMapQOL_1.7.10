package onewhohears.minecraft.jmapi.command;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import journeymap.client.model.Waypoint;
import journeymap.client.waypoint.WaypointStore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import onewhohears.minecraft.jmapi.api.ApiWaypointManager;
import onewhohears.minecraft.jmapi.config.ConfigManager;

@SideOnly(Side.CLIENT)
public class WaypointCommand extends CommandBase {
	
	private String cmdName = "waypoint";
	
	@Override
	public String getCommandName() {
		return cmdName;
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/"+cmdName+" cleardeath/remove/share/disableautoclick";
	}
	
	@Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
    	return true;
    } 
	
	@SuppressWarnings("rawtypes")
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1) {
			return CommandBase.getListOfStringsMatchingLastWord(args, new String[] 
					 {"cleardeath","share","shareteam","remove","removeprefix","disableautoclick"}); 
		} else if (args.length == 2) {
			if (args[0].equals("remove") || args[0].equals("share")) {
				return CommandBase.getListOfStringsMatchingLastWord(args, getWaypointNames()); 
			} else if (args[0].equals("disableautoclick")) {
				return CommandBase.getListOfStringsMatchingLastWord(args, new String[] {"true", "false"}); 
			}
		} else if (args.length >= 3 && (args[0].equals("share") || args[0].equals("remove")) && args[1].charAt(0) == '"') {
			int l = args.length;
			return CommandBase.getListOfStringsMatchingLastWord(args, getWaypointNameOptions(args[l-2], l-3, args[0].equals("share")));
		} else if (args.length == 3) {
			if (args[0].equals("share")) {
				return CommandBase.getListOfStringsMatchingLastWord(args, getUsernames()); 
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private String[] getUsernames() {
		if (Minecraft.getMinecraft().getNetHandler() == null) return new String[0];
		if (Minecraft.getMinecraft().getNetHandler().playerInfoList == null) return new String[0];
		List<GuiPlayerInfo> playerInfo = Minecraft.getMinecraft().getNetHandler().playerInfoList;
		String[] names = new String[playerInfo.size()];
		for (int i = 0; i < names.length; ++i) names[i] = playerInfo.get(i).name;
		return names;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		// TODO share to team members
		if (args[0].equals("cleardeath")) {
			if (args.length == 1) {
				Waypoint[] waypoints = WaypointStore.instance().getAll()
						.toArray(new Waypoint[WaypointStore.instance().getAll().size()]);
				int d = 0;
				for (int i = 0; i < waypoints.length; ++i) {
					if (waypoints[i].isDeathPoint()) {
						WaypointStore.instance().remove(waypoints[i]);
						++d;
					}
				}
				sendMessage(d+" Deathpoints were Removed!");
			} else if (args.length == 2) {
				int num = -1;
				try { num = Integer.parseInt(args[1]); } 
				catch (NumberFormatException e) {
					sendError(args[1]+" is not a number.");
					return;
				}
				Waypoint[] waypoints = WaypointStore.instance().getAll()
						.toArray(new Waypoint[WaypointStore.instance().getAll().size()]);
				ArrayList<Waypoint> deathPoints = getSortedDeathPointsByTime(waypoints);
				int d = 0;
				for (int i = 0; i < deathPoints.size()-num; ++i) {
					WaypointStore.instance().remove(deathPoints.get(i));
					++d;
				}
				sendMessage(d+" Deathpoints were Removed!");
			} else sendError("Invalid Command");
		} else if (args[0].equals("remove") && args.length >= 2) {
			String name = args[1];
			int l = args.length;
			if (l > 2) {
				if (args[1].charAt(0) == '"' && args[l-1].charAt(args[l-1].length()-1) == '"') {
					for (int i = 2; i < l; ++i) name += " " + args[i];
				} else {
					sendError("Error: Removing Waypoints with spaces in their names require quotes!");
					return;
				}
			}
			int d = deleteWaypointsWithSameName(name);
			if (d > 0) sendMessage(d+" Waypoints named "+removeQuotes(name)+" were removed!");
			else sendMessage("There wasn't any Waypoints named "+args[1]+" to be removed!");
		} else if (args[0].equals("removeprefix") && args.length == 2) {
			if (deleteWaypointsWithPrefix(args[1])) sendMessage("Removed waypoints with prefix "+args[1]);
			else sendMessage("There wasn't any Waypoints with prefix "+args[1]+" to be removed!");
		} else if (args[0].equals("disableautoclick") && args.length == 2) {
			if (args[1].equals("true")) {
				ConfigManager.disableAutoClick = true;
				sendMessage("Waypoints in chat will NOT be clicked on!");
			} else if (args[1].equals("false")) {
				ConfigManager.disableAutoClick = false;
				sendMessage("Waypoints in chat will automatically be clicked on!");
			} else sendError("You must input true or false!");
		} else if (args[0].equals("share") || args[0].equals("shareteam")) {
			int dimension = Minecraft.getMinecraft().thePlayer.dimension;
			String displayName = Minecraft.getMinecraft().thePlayer.getDisplayName();
			int l = args.length;
			if (l == 2) {
				Waypoint waypoint = getWaypointByName(args[1]);
				if (ApiWaypointManager.instance.shareAllPlayersWaypoint(waypoint, dimension, displayName, true)) {
					sendMessage("Waypoint Sent!");
				} else sendError("Failed to find Waypoint");
			} else if (l >= 3) {
				String name = args[1], playerName = null;
				if (l == 3 && args[1].charAt(0) != '"') {
					name = args[1]; playerName = args[2];
				} else if (args[1].charAt(0) == '"') {
					if (args[l-1].charAt(args[l-1].length()-1) == '"') {
						for (int i = 2; i < l; ++i) name += " " + args[i];
					} else if (args[1].charAt(0) == '"' && args[l-2].charAt(args[l-2].length()-1) == '"') {
						for (int i = 2; i < l-1; ++i) name += " " + args[i];
						playerName = args[l-1];
					} else {
						sendError("Error: Sharing Waypoints with spaces in their names require quotes!");
						return;
					}
				} else {
					sendError("Error: Sharing Waypoints with spaces in their names require quotes!");
					return;
				}
				Waypoint waypoint = getWaypointByName(name);
				if (playerName == null 
						&& ApiWaypointManager.instance.shareAllPlayersWaypoint(waypoint, dimension, displayName, true)) {
					sendMessage("Waypoint Sent!");
				} else if (playerName != null && args[0].equals("share") 
						&& ApiWaypointManager.instance.shareWaypointToPlayer(waypoint, dimension, displayName, playerName, true)) {
					sendMessage("Waypoint Sent!");
				} else if (playerName == null && args[0].equals("shareteam")
						&& ApiWaypointManager.instance.shareWaypointToTeam(waypoint, dimension, false, playerName, true)) {
					sendMessage("Waypoint Sent!");
				} else sendError("Failed to find Waypoint");
			} else sendError("Invalid Command");
		} else sendError("Invalid Command");
	}
	
	private String pattern = "h:m:s a M/d/yy";
	private DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern) ;
	
	private ArrayList<Waypoint> getSortedDeathPointsByTime(Waypoint[] waypoints) {
		ArrayList<Waypoint> deaths = new ArrayList<Waypoint>();
		ArrayList<LocalDateTime> times = new ArrayList<LocalDateTime>();
		for (int i = 0; i < waypoints.length; ++i) {
			if (waypoints[i].isDeathPoint()) {
				String time = waypoints[i].getName().substring(6);
				try {
					LocalDateTime d = LocalDateTime.parse(time, format);
					times.add(d);
					deaths.add(waypoints[i]);
				} catch (DateTimeParseException e) {
					System.out.println("The name of death point \""+time+"\" is not formatted correctly");
				}
			}
		}
		int n = deaths.size();
		for (int i = 0; i < n-1; i++) {
			for (int j = 0; j < n-i-1; j++) {
				if (times.get(j).isAfter(times.get(j+1))) {
					LocalDateTime t1 = times.get(j);
					times.set(j, times.get(j+1));
					times.set(j+1, t1);
					Waypoint t2 = deaths.get(j);
					deaths.set(j, deaths.get(j+1));
					deaths.set(j+1, t2);
				}
			}
		}
		return deaths;
	}
	
	private int deleteWaypointsWithSameName(String name) {
		if (name == null || name.equals("")) return 0;
		name = removeQuotes(name);
		Waypoint[] waypoints = WaypointStore.instance().getAll()
				.toArray(new Waypoint[WaypointStore.instance().getAll().size()]);
		int deleted = 0;
		for (int i = 0; i < waypoints.length; ++i) {
			if (waypoints[i].getName().equals(name)) {
				WaypointStore.instance().remove(waypoints[i]);
				++deleted;
			}
		}
		return deleted;
	}
	
	private boolean deleteWaypointsWithPrefix(String prefix) {
		if (prefix == null || prefix.equals("")) return false;
		boolean delete = false;
		Waypoint[] waypoints = WaypointStore.instance().getAll()
				.toArray(new Waypoint[WaypointStore.instance().getAll().size()]);
		for (int i = 0; i < waypoints.length; ++i) {
			if (waypoints[i].getName().length() >= prefix.length()) {
				if (waypoints[i].getName().subSequence(0, prefix.length()).equals(prefix)) {
					WaypointStore.instance().remove(waypoints[i]);
					delete = true;
				}
			}
		}
		return delete;
	}
	
	private String removeQuotes(String name) {
		if (name.charAt(0) == '"' && name.charAt(name.length()-1) == '"') {
			return name.substring(1, name.length()-1);
		}
		return name;
	}
	
	private String[] getWaypointNames() {
		Waypoint[] waypoints = WaypointStore.instance().getAll()
				.toArray(new Waypoint[WaypointStore.instance().getAll().size()]);
		ArrayList<String> n = new ArrayList<String>();
		for (int i = 0; i < waypoints.length; ++i) {
			if (!waypoints[i].isDeathPoint() && !n.contains(waypoints[i].getName())) n.add(waypoints[i].getName());
		}
		String[] names = n.toArray(new String[n.size()]);
		for (int i = 0; i < names.length; ++i) if (names[i].contains(" ")) names[i] = '"'+names[i].substring(0, names[i].indexOf(" "));
		return names;
	}
	
	private String[] getWaypointNameOptions(String namePart, int index, boolean includePlayers) {
		if (includePlayers && namePart.charAt(namePart.length()-1) == '"') return getUsernames();
		Waypoint[] waypoints = WaypointStore.instance().getAll()
				.toArray(new Waypoint[WaypointStore.instance().getAll().size()]);
		ArrayList<String> n = new ArrayList<String>();
		for (int i = 0; i < waypoints.length; ++i) {
			if (!n.contains(waypoints[i].getName())) n.add(waypoints[i].getName());
		}
		ArrayList<String> n2 = new ArrayList<String>();
		for (int i = 0; i < n.size(); ++i) {
			String[] parts = n.get(i).split(" ");
			if (parts.length > index+1) {
				String p = null;
				if (index == 0 && ('"'+parts[index]).equals(namePart)) p = parts[index+1];
				else if (parts[index].equals(namePart)) p = parts[index+1];
				if (p != null) {
					if (index+1 == parts.length-1) p += '"';
					n2.add(p);
				}
			}
		}
		String[] names = n2.toArray(new String[n2.size()]);
		return names;
	}
	
	private Waypoint getWaypointByName(String name) {
		name = removeQuotes(name);
		Waypoint[] waypoints = WaypointStore.instance().getAll()
				.toArray(new Waypoint[WaypointStore.instance().getAll().size()]);
		for (int i = 0; i < waypoints.length; ++i) {
			if (waypoints[i].getName().equals(name)) {
				return waypoints[i];
			}
		}
		return null;
	}
	
	private void sendMessage(String message) {
		ChatComponentText chat = new ChatComponentText(message);
		ChatStyle style = new ChatStyle();
		style.setColor(EnumChatFormatting.YELLOW);
		chat.setChatStyle(style);
		Minecraft.getMinecraft().thePlayer.addChatMessage(chat);
	}
	
	private void sendError(String message) {
		ChatComponentText chat = new ChatComponentText(message);
		ChatStyle style = new ChatStyle();
		style.setColor(EnumChatFormatting.RED);
		chat.setChatStyle(style);
		Minecraft.getMinecraft().thePlayer.addChatMessage(chat);
	}
	
}
