package onewhohears.minecraft.jmapi.events;

import java.awt.Color;
import java.util.ArrayList;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import journeymap.client.model.Waypoint;
import journeymap.client.model.Waypoint.Type;
import journeymap.client.waypoint.WaypointStore;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import onewhohears.minecraft.jmapi.config.ConfigManager;

@SideOnly(Side.CLIENT)
public class WaypointChatEvent {
	
	private boolean autoCreate;
	
	@SubscribeEvent
	public void chatReceived(ClientChatReceivedEvent event) {
		//System.out.println("Chat Recieved = "+event.message.getFormattedText());
		//autoCreate = !Minecraft.getMinecraft().thePlayer.getEntityData().getBoolean(WaypointChatKeys.getNoAutoKey());
		autoCreate = !ConfigManager.disableAutoClick;
		ChatStyle style = event.message.getChatStyle();
		if (style != null && style.getChatClickEvent() != null) {
			if (style.getChatClickEvent() instanceof WaypointChatClickEvent) {
				WaypointChatClickEvent click = (WaypointChatClickEvent) style.getChatClickEvent();
				//System.out.println("Recieved chat has a click event with waypoint named "+click.getWaypoint().getName());
				if (autoCreate) createWayPoint(click.getWaypoint(), click.getDelete());
				return;
			}
		}
		String text = event.message.getUnformattedText();
		int index = text.indexOf('>')+1;
		String name = text.substring(0, index);
		text = text.substring(index);
		String[] groups = getGroupsOfSquareBrackets(text);
		boolean cancel = false;
		for (int i = 0; i < groups.length; ++i) if (processGroup(groups[i])) {
			cancel = true;
			text = text.replaceFirst(groups[i], "");
		}
		if (cancel) { 
			if (isBlank(text)) event.setCanceled(true);
			else event.message = new ChatComponentText(name+text);
		}
	}
	
	private boolean isBlank(String s) {
		for (int i = 0; i < s.length(); ++i) {
			if (s.charAt(i) != ' ' && s.charAt(i) != '[' && s.charAt(i) != ']') return false;
		}
		return true;
	}
	
	private String[] getGroupsOfSquareBrackets(String text) {
		ArrayList<String> groups = new ArrayList<String>();
		while (true) {
			int index1 = text.indexOf("["), index2 = text.indexOf("]");
			if (index1 != -1 && index2 > index1) {
				groups.add(text.substring(index1+1, index2));
				text = text.substring(index2+1);
			} else {
				break;
			}
		}
		return groups.toArray(new String[groups.size()]);
	}
	
	private boolean processGroup(String group) {
		//remove space
		group = group.replaceAll(" ", "");
		//check for comma and x and z
		if (!group.contains(",") || !group.contains("x") || !group.contains("z")) {
			//System.out.println("The group wasn't formatted right");
			return false; 
		}
		//System.out.println("Group = "+group);
		String[] args = group.split(",");
		Integer x = null, y = null, z = null, dim = null, color = null;
		String name = null;  
		boolean delete = false;
		for (int i = 0; i < args.length; ++i) {
			if (args[i].contains(":")) {
				String[] params = args[i].split(":");
				//System.out.println("param 1 = "+params[0]+" param 2 = "+params[1]);
				if (params[0].equals(WaypointChatKeys.getXKey())) {
					//System.out.println(WaypointChatKeys.getXKey()+" = "+params[1]);
					x = getIntFromString(params[1]);
				} else if (params[0].equals(WaypointChatKeys.getYKey())) {
					y = getIntFromString(params[1]);
				} else if (params[0].equals(WaypointChatKeys.getZKey())) {
					z = getIntFromString(params[1]);
				} else if (params[0].equals(WaypointChatKeys.getDimKey())) {
					dim = getIntFromString(params[1]);
				} else if (params[0].equals(WaypointChatKeys.getNameKey())) {
					name = params[1];
				} else if (params[0].equals(WaypointChatKeys.getColorKey())) {
					color = getIntFromString(params[1]);
				} else if (params[0].equals(WaypointChatKeys.getDeleteKey())) {
					if (params[1].equals("true")) delete = true;
				}
			}
		}
		Waypoint waypoint = null;
		if (x != null && z != null) {
			if (y == null) y = 60;
			if (dim == null) dim = 0;
			if (name == null) name = String.format("%s, %s", x, z);
			waypoint = new Waypoint(name, x, y, z, Color.YELLOW, Type.Normal, dim);
			if (color != null) waypoint.setColor(color);
			else waypoint.setRandomColor();
		}
		if (waypoint != null) {
			ChatComponentText chat = new ChatComponentText("["+group+"]");
			ChatStyle style = new ChatStyle();
			style.setColor(EnumChatFormatting.AQUA);
			style.setUnderlined(true);
			style.setChatClickEvent(new WaypointChatClickEvent(null, "", waypoint, delete));
			chat.setChatStyle(style);
			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(chat);
			if (autoCreate) createWayPoint(waypoint, delete);
			return true;
		}
		return false;
	}
	
	private Integer getIntFromString(String s) {
		try {
			Integer i = Integer.decode(s);
			return i;
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	private void createWayPoint(Waypoint waypoint, boolean delete) {
		if (delete) deleteWaypointsWithSameName(waypoint);
		//System.out.println("Creating waypoint in chat recieved event: "+waypoint.getName());
		WaypointStore.instance().save(waypoint);
	}
	
	private void deleteWaypointsWithSameName(Waypoint waypoint) {
		//System.out.println("Deleting waypoints named "+waypoint.getName());
		Waypoint[] waypoints = WaypointStore.instance().getAll().toArray(new Waypoint[WaypointStore.instance().getAll().size()]);
		for (int i = 0; i < waypoints.length; ++i) {
			if (waypoints[i].getName().equals(waypoint.getName())) {
				WaypointStore.instance().remove(waypoints[i]);
			}
		}
	}
	
}
