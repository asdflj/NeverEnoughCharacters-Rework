package com.asdflj.nech.proxy;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.asdflj.nech.NechConfig;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.FMLInjectionData;

public class ClientProxy extends CommonProxy {

    public static final Logger LOGGER = LogManager.getLogger("Never Enough Characters");

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        NechConfig
            .loadConfig(new File(new File((File) FMLInjectionData.data()[6], "config"), "NeverEnoughCharacters.cfg"));
    }
}
