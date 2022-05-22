package onewhohears.minecraft.jmapi;

public class CommonProxy {
	
	public void load() {
		JourneyMapApiMod.Channel.register(new ServerPacketHandler());
	}
	
}
