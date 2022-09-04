package onewhohears.minecraft.jmapi;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import onewhohears.minecraft.jmapi.command.WaypointCommand;
import onewhohears.minecraft.jmapi.config.ConfigManager;
import onewhohears.minecraft.jmapi.events.WaypointChatEvent;

@Mod(modid = JourneyMapApiMod.MOD_ID, name = JourneyMapApiMod.MOD_NAME,
	version = JourneyMapApiMod.MOD_VERSION, dependencies = JourneyMapApiMod.MOD_DEPENDENCIES)
public class JourneyMapApiMod {
	
	public static final String MOD_ID = "journeymap_api_1.7.10";
	public static final String MOD_NAME = "Journey Map Api for 1.7.10";
	public static final String MOD_VERSION = "0.8.8";
	public static final String MOD_DEPENDENCIES = "journeymap";
	
    public static Logger logger;
    
    @SidedProxy(clientSide = "onewhohears.minecraft.jmapi.ClientProxy", 
    		    serverSide = "onewhohears.minecraft.jmapi.CommonProxy")
    public static CommonProxy proxy;
    
    public static FMLEventChannel Channel;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        Channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("JMA_Server");
        proxy.load();
        ConfigManager.init(event.getModConfigurationDirectory().toString()+"/");
        FMLCommonHandler.instance().bus().register(new ConfigManager());
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	if (event.getSide() == Side.CLIENT) {
    		MinecraftForge.EVENT_BUS.register(new WaypointChatEvent());
        	ClientCommandHandler.instance.registerCommand(new WaypointCommand());
    	}
    }
	
}
