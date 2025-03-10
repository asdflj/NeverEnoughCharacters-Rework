package com.asdflj.nech.proxy;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.asdflj.nech.NechCommand;
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
        ClientCommandHandler.instance.registerCommand(new NechCommand());
    }

    public static void sendToPlayer(String message, Object... args) {
        try {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(I18n.format(message, args)));
        } catch (Exception ignored) {}

    }
}
