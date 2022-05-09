package onewhohears.minecraft.jmapi.events;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import journeymap.client.model.Waypoint;
import journeymap.client.waypoint.WaypointStore;
import net.minecraft.event.ClickEvent;

public class WaypointChatClickEvent extends ClickEvent {
	
	private Waypoint waypoint;
	private boolean ran;
	private boolean delete;
	
	@SideOnly(Side.CLIENT)
	public WaypointChatClickEvent(Action p_i45156_1_, String p_i45156_2_, Waypoint waypoint, boolean delete) {
		super(p_i45156_1_, p_i45156_2_);
		this.waypoint = waypoint;
		ran = false;
		this.delete = delete;
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
