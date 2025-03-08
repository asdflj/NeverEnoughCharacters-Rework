package com.asdflj.nech.core;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.asdflj.nech.core.transform.TransformerRegistry;

import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.Name;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@TransformerExclusions({ "com.asdflj.nech.core" })
@MCVersion("1.7.10")
@Name("NechCorePlugin")
public class NechCorePlugin implements IFMLLoadingPlugin, IFMLCallHook {

    public static final Logger LOGGER = LogManager.getLogger("NechCorePlugin");
    public static boolean INITIALIZED = false;

    public NechCorePlugin() {}

    public String[] getASMTransformerClass() {
        return new String[] { "com.asdflj.nech.core.NechClassTransformer" };
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return "com.asdflj.nech.core.NechCorePlugin";
    }

    public void injectData(Map<String, Object> data) {}

    public String getAccessTransformerClass() {
        return "";
    }

    public Void call() {
        TransformerRegistry.getTransformer("some.class");
        INITIALIZED = true;
        return null;
    }
}
