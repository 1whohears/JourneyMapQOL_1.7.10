package onewhohears.minecraft.jmapi;

public class ClientProxy extends CommonProxy {
	
	public void load() {
		JourneyMapApiMod.Channel.register(new ClientPacketHandler());
	}
	
}
