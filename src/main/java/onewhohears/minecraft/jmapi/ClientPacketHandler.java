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
			// 0 = waypoint to all (server)
			// 1 = waypoint to a player
			// 2 = remove a player's waypoint by name
			// 3 = remove a player's waypoint by prefix
			// 4 = remove all player's waypoint by name
			// 5 = remove all player's waypoint by prefix
			switch (type) {
			case 0 : {
				int x = bbis.readInt();
				int y = bbis.readInt();
				int z = bbis.readInt();
				int dim = bbis.readInt();
				int color = bbis.readInt();
				String name = bbis.readUTF();
				String pName = bbis.readUTF();
				boolean delete = bbis.readBoolean();
				if (Minecraft.getMinecraft().thePlayer.getDisplayName().equals(pName)) break;
				Waypoint waypoint = new Waypoint(name, x, y, z, Color.YELLOW, Type.Normal, dim);
				waypoint.setColor(color);
				ChatComponentText chat = new ChatComponentText("Waypoint "+name+" shared by "+pName);
				ChatStyle style = new ChatStyle();
				style.setColor(EnumChatFormatting.AQUA);
				style.setUnderlined(true);
				style.setChatClickEvent(new WaypointChatClickEvent(null, "", waypoint, delete));
				chat.setChatStyle(style);
				Minecraft.getMinecraft().thePlayer.addChatComponentMessage(chat);
				if (autoCreate) createWayPoint(waypoint, delete);
			} case 1 : {
				String oName = bbis.readUTF();
				if (Minecraft.getMinecraft().thePlayer.getDisplayName().equals(oName)) {
					int x = bbis.readInt();
					int y = bbis.readInt();
					int z = bbis.readInt();
					int dim = bbis.readInt();
					int color = bbis.readInt();
					String name = bbis.readUTF();
					String pName = bbis.readUTF();
					boolean delete = bbis.readBoolean();
					Waypoint waypoint = new Waypoint(name, x, y, z, Color.YELLOW, Type.Normal, dim);
					waypoint.setColor(color);
					ChatComponentText chat = new ChatComponentText(pName+" shared waypoint "+name+" with you!");
					ChatStyle style = new ChatStyle();
					style.setColor(EnumChatFormatting.AQUA);
					style.setUnderlined(true);
					style.setChatClickEvent(new WaypointChatClickEvent(null, "", waypoint, delete));
					chat.setChatStyle(style);
					Minecraft.getMinecraft().thePlayer.addChatComponentMessage(chat);
					if (autoCreate) createWayPoint(waypoint, delete);
				}
			} case 2 : {
				String pName = bbis.readUTF();
				if (Minecraft.getMinecraft().thePlayer.getDisplayName().equals(pName)) {
					String name = bbis.readUTF();
					if (deleteWaypointsWithSameName(name)) sendDeleteMessage(name);
				}
			} case 3 : {
				String pName = bbis.readUTF();
				if (Minecraft.getMinecraft().thePlayer.getDisplayName().equals(pName)) {
					String prefix = bbis.readUTF();
					if (deleteWaypointsWithPrefix(prefix)) sendDeletePrefixMessage(prefix);
				}
			} case 4 : {
				String name = bbis.readUTF();
				if (deleteWaypointsWithSameName(name)) sendDeleteMessage(name);
			} case 5 : {
				String prefix = bbis.readUTF();
				if (deleteWaypointsWithPrefix(prefix)) sendDeletePrefixMessage(prefix);
			} }
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
		WaypointStore.instance().save(waypoint);
	}
	
	private boolean deleteWaypointsWithSameName(String name) {
		boolean delete = false;
		Waypoint[] waypoints = WaypointStore.instance().getAll()
				.toArray(new Waypoint[WaypointStore.instance().getAll().size()]);
		for (int i = 0; i < waypoints.length; ++i) {
			if (waypoints[i].getName().equals(name)) {
				WaypointStore.instance().remove(waypoints[i]);
				delete = true;
			}
		}
		return delete;
	}
	
	private boolean deleteWaypointsWithPrefix(String prefix) {
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
	
}
