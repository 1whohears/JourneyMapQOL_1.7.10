package onewhohears.minecraft.jmapi.events;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import journeymap.client.model.Waypoint;
import journeymap.client.waypoint.WaypointStore;
import net.minecraft.event.ClickEvent;

public class WaypointChatClickEvent extends ClickEvent {
	
	private final Waypoint waypoint;
	private final boolean delete;
	private boolean ran;
	
	@SideOnly(Side.CLIENT)
	public WaypointChatClickEvent(Waypoint waypoint, boolean delete) {
		super(null, "");
		this.waypoint = waypoint;
		this.delete = delete;
		this.ran = false;
	}
	
	@Override
	public ClickEvent.Action getAction() {
		if (!ran) {
			createWayPoint();
			ran = true;
		}
		return null;
	}
	
	private void createWayPoint() {
		if (delete) deleteWaypointsWithSameName();
		System.out.println("waypoint click "+waypoint.toString());
		WaypointStore.instance().save(waypoint);
	}
	
	private void deleteWaypointsWithSameName() {
		Waypoint[] waypoints = WaypointStore.instance().getAll().toArray(new Waypoint[WaypointStore.instance().getAll().size()]);
		for (int i = 0; i < waypoints.length; ++i) {
			if (waypoints[i].getName().equals(waypoint.getName())) {
				WaypointStore.instance().remove(waypoints[i]);
			}
		}
	}
	
	public Waypoint getWaypoint() {
		return waypoint;
	}
	
	public boolean getDelete() {
		return delete;
	}
	
}
