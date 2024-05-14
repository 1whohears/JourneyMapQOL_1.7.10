package onewhohears.minecraft.jmapi.util;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import journeymap.client.model.Waypoint;
import journeymap.client.model.Waypoint.Type;
import onewhohears.minecraft.jmapi.events.WaypointChatKeys;

public class UtilClientWaypoint {
	
	public static Waypoint getWaypointFromText(String bracketGroup) {
		return getWaypointFromText(bracketGroup, new AtomicBoolean());
	}
	
	@Nullable
	public static Waypoint getWaypointFromText(String bracketGroup, AtomicBoolean delete) {
		delete.set(false);
		if (bracketGroup.indexOf('[', 0) != 0 || bracketGroup.indexOf(']', 1) != bracketGroup.length()-1
				|| !bracketGroup.contains(",") || !bracketGroup.contains("x") || !bracketGroup.contains("z")) {
			//System.out.println("waypoint not formatted correctly");
			return null;
		}
		String group = bracketGroup.substring(1, bracketGroup.length()-1);
		//remove space
		group = group.replaceAll(" ", "");
		//System.out.println("Group = "+group);
		String[] args = group.split(",");
		Integer x = null, y = null, z = null, dim = null, color = null;
		String name = null;  
		for (int i = 0; i < args.length; ++i) {
			if (args[i].contains(":")) {
				String[] params = args[i].split(":");
				//System.out.println("param 1 = "+params[0]+" param 2 = "+params[1]);
				if (params[0].equals(WaypointChatKeys.getXKey())) {
					//System.out.println(WaypointChatKeys.getXKey()+" = "+params[1]);
					x = UtilParse.decodeInt(params[1]);
				} else if (params[0].equals(WaypointChatKeys.getYKey())) {
					y = UtilParse.decodeInt(params[1]);
				} else if (params[0].equals(WaypointChatKeys.getZKey())) {
					z = UtilParse.decodeInt(params[1]);
				} else if (params[0].equals(WaypointChatKeys.getDimKey())) {
					dim = UtilParse.decodeInt(params[1]);
				} else if (params[0].equals(WaypointChatKeys.getNameKey())) {
					name = params[1];
				} else if (params[0].equals(WaypointChatKeys.getColorKey())) {
					color = UtilParse.decodeInt(params[1]);
				} else if (params[0].equals(WaypointChatKeys.getDeleteKey())) {
					if (params[1].equals("true")) delete.set(true);
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
		return waypoint;
	}
	
}
