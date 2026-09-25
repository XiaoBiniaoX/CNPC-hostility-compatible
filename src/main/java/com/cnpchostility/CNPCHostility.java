package bin.cnpchostility;

import bin.cnpchostility.client.tab.CnpcPlayerTabs;
import bin.cnpchostility.network.CNPCNetwork;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CNPCHostility.MODID)
public class CNPCHostility {

    public static final String MODID = "cnpchostility";
    public static final String FALLEN_TRAITS_MODID = "fallen_traits";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final boolean FALLEN_TRAITS_LOADED = ModList.get().isLoaded(FALLEN_TRAITS_MODID);

    public CNPCHostility() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::clientSetup);
        LOGGER.info("CNPC恶意附属 - 莱特兰·恶意 x CustomNPCs 词条兼容已加载");
        if (FALLEN_TRAITS_LOADED) {
            LOGGER.info("Fallen Traits detected; registered fallen_traits entries are available through the L2Hostility trait registry");
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(CNPCNetwork::register);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(CnpcPlayerTabs::register);
    }
}
