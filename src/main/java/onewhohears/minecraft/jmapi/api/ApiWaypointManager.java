package onewhohears.minecraft.jmapi.api;

import java.io.IOException;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import journeymap.client.model.Waypoint;
import onewhohears.minecraft.jmapi.JourneyMapApiMod;

public class ApiWaypointManager {
	
	public static ApiWaypointManager instance;
	
	public ApiWaypointManager() {
		instance = this;
	}
	
	/**
	 * send a waypoint to all players
	 * @param waypoint
	 * @param dimension
	 * @param senderName
	 * @return if it was sent successfully
	 */
	public boolean shareAllPlayersWaypoint(Waypoint waypoint, int dimension, String senderName) {
		FMLProxyPacket packet = createWaypointAllPlayersPacket(waypoint.getX(), waypoint.getY(), waypoint.getZ(), 
				waypoint.getColor(), waypoint.getName(), dimension, false, senderName);
		if (packet == null) return false;
		JourneyMapApiMod.Channel.sendToServer(packet);
		return true;
	}
	
	/**
	 * send a waypoint to all players
	 * @param waypoint
	 * @param dimension
	 * @param delete remove all other waypoints with the same name
	 * @param senderName
	 * @return if it was sent successfully
	 */
	public boolean shareAllPlayersWaypoint(Waypoint waypoint, int dimension, boolean delete, String senderName) {
		FMLProxyPacket packet = createWaypointAllPlayersPacket(waypoint.getX(), waypoint.getY(), waypoint.getZ(), 
				waypoint.getColor(), waypoint.getName(), dimension, false, senderName);
		if (packet == null) return false;
		JourneyMapApiMod.Channel.sendToServer(packet);
		return true;
	}
	
	/**
	 * send a waypoint to all players
	 * @param x
	 * @param y
	 * @param z
	 * @param dimension
	 * @param color
	 * @param waypointName
	 * @param senderName
	 * @return if it was sent successfully
	 */
	public boolean shareAllPlayersWaypoint(int x, int y, int z, int dimension, int color, String waypointName, String senderName) {
		FMLProxyPacket packet = createWaypointAllPlayersPacket(x, y, z, color, waypointName, dimension, false, senderName);
		if (packet == null) return false;
		JourneyMapApiMod.Channel.sendToServer(packet);
		return true;
	}
	
	/**
	 * send a waypoint to all players
	 * @param x
	 * @param y
	 * @param z
	 * @param dimension
	 * @param color
	 * @param delete remove all other waypoints with the same name
	 * @param waypointName
	 * @param senderName
	 * @return if it was sent successfully
	 */
	public boolean shareAllPlayersWaypoint(int x, int y, int z, int dimension, int color, boolean delete, String waypointName, String senderName) {
		FMLProxyPacket packet = createWaypointAllPlayersPacket(x, y, z, color, waypointName, dimension, delete, senderName);
		if (packet == null) return false;
		JourneyMapApiMod.Channel.sendToServer(packet);
		return true;
	}
	
	/**
	 * send a waypoint to a certain player
	 * @param waypoint
	 * @param dimension
	 * @param senderName
	 * @param recieverName
	 * @return if it was sent successfully
	 */
	public boolean shareWaypointToPlayer(Waypoint waypoint, int dimension, String senderName, String recieverName) {
		FMLProxyPacket packet = createWaypointToPlayerPacket(waypoint.getX(), waypoint.getY(), waypoint.getZ(), 
				waypoint.getColor(), waypoint.getName(), dimension, false, senderName, recieverName);
		if (packet == null) return false;
		JourneyMapApiMod.Channel.sendToServer(packet);
		return true;
	}
	
	/**
	 * send a waypoint to a certain player
	 * @param waypoint
	 * @param dimension
	 * @param delete remove all other waypoints with the same name
	 * @param senderName
	 * @param recieverName
	 * @return if it was sent successfully
	 */
	public boolean shareWaypointToPlayer(Waypoint waypoint, int dimension, boolean delete, String senderName, String recieverName) {
		FMLProxyPacket packet = createWaypointToPlayerPacket(waypoint.getX(), waypoint.getY(), waypoint.getZ(), 
				waypoint.getColor(), waypoint.getName(), dimension, delete, senderName, recieverName);
		if (packet == null) return false;
		JourneyMapApiMod.Channel.sendToServer(packet);
		return true;
	}
	
	/**
	 * send a waypoint to a certain player
	 * @param x
	 * @param y
	 * @param z
	 * @param dimension
	 * @param color
	 * @param waypointName
	 * @param senderName
	 * @param recieverName
	 * @return if it was sent successfully
	 */
	public boolean shareWaypointToPlayer(int x, int y, int z, int dimension, int color, String waypointName, String senderName, String recieverName) {
		FMLProxyPacket packet = createWaypointToPlayerPacket(x, y, z, color, waypointName, dimension, false, senderName, recieverName);
		if (packet == null) return false;
		JourneyMapApiMod.Channel.sendToServer(packet);
		return true;
	}
	
	/**
	 * send a waypoint to a certain player
	 * @param x
	 * @param y
	 * @param z
	 * @param dimension
	 * @param color
	 * @param delete remove all other waypoints with the same name
	 * @param waypointName
	 * @param senderName
	 * @param recieverName
	 * @return if it was sent successfully
	 */
	public boolean shareWaypointToPlayer(int x, int y, int z, int dimension, int color, boolean delete, String waypointName, String senderName, String recieverName) {
		FMLProxyPacket packet = createWaypointToPlayerPacket(x, y, z, color, waypointName, dimension, delete, senderName, recieverName);
		if (packet == null) return false;
		JourneyMapApiMod.Channel.sendToServer(packet);
		return true;
	}
	
	/**
	 * remove all waypoints with this name from all players
	 * @param waypointName
	 * @param showMessage notify players in chat that their waypoint was deleted
	 * @return if it was sent successfully
	 */
	public boolean removeAllWaypoints(String waypointName, boolean showMessage) {
		FMLProxyPacket packet = createRemoveWaypointPacket("", waypointName, true, showMessage);
		if (packet == null) return false;
		JourneyMapApiMod.Channel.sendToServer(packet);
		return true;
	}
	
	/**
	 * delete all of this player's waypoints with this name
	 * @param playerName
	 * @param waypointName
	 * @param showMessage notify players in chat that their waypoint was deleted
	 * @return if it was sent successfully
	 */
	public boolean removePlayerWaypoint(String playerName, String waypointName, boolean showMessage) {
		FMLProxyPacket packet = createRemoveWaypointPacket(playerName, waypointName, false, showMessage);
		if (packet == null) return false;
		JourneyMapApiMod.Channel.sendToServer(packet);
		return true;
	}
	
	/**
	 * remove all waypoints with this prefix from all players
	 * @param prefixName
	 * @param showMessage notify players in chat that their waypoint was deleted
	 * @return if it was sent successfully
	 */
	public boolean removeAllWaypointsByPrefix(String prefixName, boolean showMessage) {
		FMLProxyPacket packet = createRemoveWaypointPrefixPacket("", prefixName, true, showMessage);
		if (packet == null) return false;
		JourneyMapApiMod.Channel.sendToServer(packet);
		return true;
	}
	
	/**
	 * delete all of this player's waypoints that's name start with the prefix
	 * @param playerName
	 * @param prefixName
	 * @param showMessage notify players in chat that their waypoint was deleted
	 * @return if it was sent successfully
	 */
	public boolean removePlayerWaypointByPrefix(String playerName, String prefixName, boolean showMessage) {
		FMLProxyPacket packet = createRemoveWaypointPrefixPacket(playerName, prefixName, false, showMessage);
		if (packet == null) return false;
		JourneyMapApiMod.Channel.sendToServer(packet);
		return true;
	}
	
	private FMLProxyPacket createRemoveWaypointPacket(String playerName, String waypointName, boolean allPlayers, boolean showMessage) {
		ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer());
		FMLProxyPacket thePacket = null;
		try {
			if (!allPlayers) {
				bbos.writeInt(2); // type
				bbos.writeUTF(playerName); // player name
			} else {
				bbos.writeInt(4); // type
			}
			bbos.writeUTF(waypointName); // prefix name
			bbos.writeBoolean(showMessage); // show message
			thePacket = new FMLProxyPacket(bbos.buffer(), "JMA_Server");
			bbos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return thePacket;
	}
	
	private FMLProxyPacket createRemoveWaypointPrefixPacket(String playerName, String prefixName, boolean allPlayers, boolean showMessage) {
		ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer());
		FMLProxyPacket thePacket = null;
		try {
			if (!allPlayers) {
				bbos.writeInt(3); // type
				bbos.writeUTF(playerName); // player name
			} else {
				bbos.writeInt(5); // type
			}
			bbos.writeUTF(prefixName); // prefix name
			bbos.writeBoolean(showMessage); // show message
			thePacket = new FMLProxyPacket(bbos.buffer(), "JMA_Server");
			bbos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return thePacket;
	}
	
	private FMLProxyPacket createWaypointAllPlayersPacket(int x, int y, int z, int color, String name, int dim, boolean delete, String playerName) {
		if (name == null) return null;
		ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer());
		FMLProxyPacket thePacket = null;
		//System.out.println("Waypoint Packet to All Players "+waypoint.getName());
		try {
			bbos.writeInt(0); //type
			bbos.writeInt(x); //x
			bbos.writeInt(y); //y
			bbos.writeInt(z); //z
			bbos.writeInt(dim); //dimension
			bbos.writeInt(color); //color
			bbos.writeUTF(name); //name
			bbos.writeUTF(playerName); // player name
			bbos.writeBoolean(delete); // delete
			thePacket = new FMLProxyPacket(bbos.buffer(), "JMA_Server");
			bbos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return thePacket;
	}
	
	private FMLProxyPacket createWaypointToPlayerPacket(int x, int y, int z, int color, String name, int dim, boolean delete, String pName, String otherName) {
		if (name == null) return null;
		ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer());
		FMLProxyPacket thePacket = null;
		try {
			bbos.writeInt(1); //type
			bbos.writeUTF(otherName); // other player name
			bbos.writeInt(x); //x
			bbos.writeInt(y); //y
			bbos.writeInt(z); //z
			bbos.writeInt(dim); //dimension
			bbos.writeInt(color); //color
			bbos.writeUTF(name); //name
			bbos.writeUTF(pName); // player name
			bbos.writeBoolean(delete); // delete
			thePacket = new FMLProxyPacket(bbos.buffer(), "JMA_Server");
			bbos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return thePacket;
	}
	
	public String waypointToFormattedString(Waypoint waypoint, int dimension, boolean delete) {
		return createFormattedString(waypoint.getName(), waypoint.getX(), waypoint.getY(), waypoint.getZ(), dimension, waypoint.getColor(), delete);
	}
	
	public String createFormattedString(String waypointName, int x, int y, int z, int dimension, int color, boolean delete) {
		String w = "[name:"+waypointName
		+ ",x:"+x+",y:"+y+",z:"+z
		+ ",color:"+color
		+ ",dim:"+dimension;
		if (delete) w += ",delete:true";
		else w += ",delete:false";
		w += "]";
		return w;
	}
}
