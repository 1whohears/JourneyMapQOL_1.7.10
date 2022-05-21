package onewhohears.minecraft.jmapi;

import java.awt.Color;
import java.io.IOException;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBufInputStream;
import journeymap.client.model.Waypoint;
import journeymap.client.model.Waypoint.Type;
import journeymap.client.waypoint.WaypointStore;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import onewhohears.minecraft.jmapi.events.WaypointChatClickEvent;
import onewhohears.minecraft.jmapi.events.WaypointChatKeys;

@SideOnly(Side.CLIENT)
public class ClientPacketHandler extends ServerPacketHandler {
	
	@SubscribeEvent
	public void onClientPacket(ClientCustomPacketEvent event) {
		if (event.side() != Side.CLIENT) return;
		boolean autoCreate = !Minecraft.getMinecraft().thePlayer.getEntityData().getBoolean(WaypointChatKeys.getNoAutoKey());
		ByteBufInputStream bbis = new ByteBufInputStream(event.packet.payload());
		try {
			int type = bbis.readInt();
			//System.out.println("Recieved Packet in Client of type "+type);
			// 0 = waypoint to all (server)
			// 1 = waypoint to a player
			// 2 = remove a player's waypoint by name
			// 3 = remove a player's waypoint by prefix
			// 4 = remove all player's waypoint by name
			// 5 = remove all player's waypoint by prefix
			boolean delete, showMessage;
			int x, y, z, dim, color;
			String name, pName, oName, prefix;
			Waypoint waypoint;
			ChatComponentText chat; 
			ChatStyle style;
			switch (type) {
			case 0 : 
				x = bbis.readInt();
				y = bbis.readInt();
				z = bbis.readInt();
				dim = bbis.readInt();
				color = bbis.readInt();
				name = bbis.readUTF();
				pName = bbis.readUTF();
				//System.out.println("Waypoint name = "+name+" from "+pName);
				delete = bbis.readBoolean();
				if (Minecraft.getMinecraft().thePlayer.getDisplayName().equals(pName)) break;
				waypoint = new Waypoint(name, x, y, z, Color.YELLOW, Type.Normal, dim);
				waypoint.setColor(color);
				chat = new ChatComponentText("Waypoint "+name+" shared by "+pName);
				style = new ChatStyle();
				style.setColor(EnumChatFormatting.AQUA);
				style.setUnderlined(true);
				style.setChatClickEvent(new WaypointChatClickEvent(null, "", waypoint, delete));
				chat.setChatStyle(style);
				Minecraft.getMinecraft().thePlayer.addChatComponentMessage(chat);
				if (autoCreate) createWayPoint(waypoint, delete);
				break;
			case 1 : 
				oName = bbis.readUTF();
				if (Minecraft.getMinecraft().thePlayer.getDisplayName().equals(oName)) {
					x = bbis.readInt();
					y = bbis.readInt();
					z = bbis.readInt();
					dim = bbis.readInt();
					color = bbis.readInt();
					name = bbis.readUTF();
					pName = bbis.readUTF();
					//System.out.println("Waypoint name = "+name+" from "+pName);
					delete = bbis.readBoolean();
					waypoint = new Waypoint(name, x, y, z, Color.YELLOW, Type.Normal, dim);
					waypoint.setColor(color);
					chat = new ChatComponentText(pName+" shared waypoint "+name+" with you!");
					style = new ChatStyle();
					style.setColor(EnumChatFormatting.AQUA);
					style.setUnderlined(true);
					style.setChatClickEvent(new WaypointChatClickEvent(null, "", waypoint, delete));
					chat.setChatStyle(style);
					Minecraft.getMinecraft().thePlayer.addChatComponentMessage(chat);
					if (autoCreate) createWayPoint(waypoint, delete);
				}
				break;
			case 2 :
				pName = bbis.readUTF();
				if (Minecraft.getMinecraft().thePlayer.getDisplayName().equals(pName)) {
					name = bbis.readUTF();
					showMessage = bbis.readBoolean();
					if (deleteWaypointsWithSameName(name) && showMessage) sendDeleteMessage(name);
				}
				break;
			case 3 :
				pName = bbis.readUTF();
				if (Minecraft.getMinecraft().thePlayer.getDisplayName().equals(pName)) {
					prefix = bbis.readUTF();
					showMessage = bbis.readBoolean();
					if (deleteWaypointsWithPrefix(prefix) && showMessage) sendDeletePrefixMessage(prefix);
				}
				break;
			case 4 :
				name = bbis.readUTF();
				showMessage = bbis.readBoolean();
				if (deleteWaypointsWithSameName(name) && showMessage) sendDeleteMessage(name);
				break;
			case 5 :
				prefix = bbis.readUTF();
				showMessage = bbis.readBoolean();
				if (deleteWaypointsWithPrefix(prefix) && showMessage) sendDeletePrefixMessage(prefix);
				break;
			}
			bbis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendDeleteMessage(String waypointName) {
		ChatComponentText chat = new ChatComponentText("Your Waypoints named "+waypointName+" have been deleted!");
		ChatStyle style = new ChatStyle();
		style.setColor(EnumChatFormatting.YELLOW);
		style.setUnderlined(true);
		chat.setChatStyle(style);
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(chat);
	}
	
	private void sendDeletePrefixMessage(String prefixName) {
		ChatComponentText chat = new ChatComponentText("Your Waypoints with prefix "+prefixName+" have been deleted!");
		ChatStyle style = new ChatStyle();
		style.setColor(EnumChatFormatting.YELLOW);
		style.setUnderlined(true);
		chat.setChatStyle(style);
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(chat);
	}
	
	private void createWayPoint(Waypoint waypoint, boolean delete) {
		if (delete) deleteWaypointsWithSameName(waypoint.getName());
		//System.out.println("Creating waypoint in client packet handler "+waypoint.getName());
		WaypointStore.instance().save(waypoint);
	}
	
	private boolean deleteWaypointsWithSameName(String name) {
		//System.out.println("Deleting waypoints named "+name);
		if (name == null || name.equals("")) return false;
		boolean delete = false;
		Waypoint[] waypoints = WaypointStore.instance().getAll().toArray(new Waypoint[WaypointStore.instance().getAll().size()]);
		for (int i = 0; i < waypoints.length; ++i) {
			if (waypoints[i].getName().equals(name)) {
				WaypointStore.instance().remove(waypoints[i]);
				delete = true;
				//System.out.println("Deleting "+waypoints[i].getName());
			}
		}
		return delete;
	}
	
	private boolean deleteWaypointsWithPrefix(String prefix) {
		//System.out.println("Deleting waypoints with prefix "+prefix);
		if (prefix == null || prefix.equals("")) return false;
		boolean delete = false;
		Waypoint[] waypoints = WaypointStore.instance().getAll().toArray(new Waypoint[WaypointStore.instance().getAll().size()]);
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
	
}
