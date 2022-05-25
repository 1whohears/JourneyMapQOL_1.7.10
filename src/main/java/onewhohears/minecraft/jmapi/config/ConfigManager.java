package onewhohears.minecraft.jmapi.config;

import java.io.File;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;
import onewhohears.minecraft.jmapi.JourneyMapApiMod;

public class ConfigManager {
	
	public static Configuration config;
	
	public static final String WAYPOINT_GENERAL = "Waypoint General";
	public static boolean disableAutoClick;
	
	public static void init(String configDir) {
		if (config == null) {
			File path = new File(configDir + JourneyMapApiMod.MOD_ID + ".cfg");
			config = new Configuration(path);
			loadConfig();
		}
	}
	
	private static void loadConfig() {
		// general
		disableAutoClick = config.getBoolean("Disable Auto Click", WAYPOINT_GENERAL, false, 
				"If enabled formatted text and other waypoint mesages won't automatically generate a waypoint.");
		if (config.hasChanged()) config.save();
	}
	
	@SubscribeEvent
	public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.modID.equalsIgnoreCase(JourneyMapApiMod.MOD_ID)) loadConfig();
	}
	
}
