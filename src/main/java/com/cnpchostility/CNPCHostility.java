package bin.cnpchostility;

import bin.cnpchostility.network.CNPCNetwork;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CNPCHostility.MODID)
public class CNPCHostility {

    public static final String MODID = "cnpchostility";
    public static final Logger LOGGER = LogManager.getLogger();

    public CNPCHostility() {
        CNPCNetwork.register();
        LOGGER.info("CNPC恶意附属 - 莱特兰·恶意 x CustomNPCs 词条兼容已加载");
    }
}
