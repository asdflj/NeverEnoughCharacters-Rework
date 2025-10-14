package com.asdflj.nech;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import com.asdflj.nech.proxy.ClientProxy;
import com.asdflj.nech.utils.Match;

import me.towdium.pinin.Keyboard;

public class NechConfig {

    public static boolean enableVerbose = false;
    public static boolean EnableFZh2Z = true;
    public static boolean EnableFSh2S = true;
    public static boolean EnableFCh2C = true;
    public static boolean EnableFAng2An = true;
    public static boolean EnableFIng2In = true;
    public static boolean EnableFEng2En = true;
    public static boolean EnableFU2V = true;
    public static Spell KeyboardType;
    public static String[] transformerRegExpAdditionalList;
    public static String[] transformerStringAdditionalList;
    public static String[] transformerMethodBlackList;
    public static String[] defaultTransformerRegExp;
    public static String[] defaultTransformerStringList;

    public NechConfig() {}

    public static void loadConfig(File configFile) {
        Configuration config = new Configuration(configFile);
        config.load();
        config.get(
            "transformers",
            "DefaultTransformerRegExp",
            defaultTransformerRegExp,
            "Default list of methods to transform, of which uses regular expression to match.\nThis list is maintained by the mod and will have no effect if you change it.");
        config.get(
            "transformers",
            "DefaultTransformerString",
            defaultTransformerStringList,
            "Default list of methods to transform, of which uses \"String.contains\" to match.\nThis list is maintained by the mod and will have no effect if you change it.");
        config.getCategory("transformers")
            .get("DefaultTransformerRegExp")
            .set(defaultTransformerRegExp);
        config.getCategory("transformers")
            .get("DefaultTransformerString")
            .set(defaultTransformerStringList);
        transformerRegExpAdditionalList = config.get(
            "transformers",
            "AdditionalTransformerRegExpList",
            new String[0],
            "Additional list of methods to transform, of which uses regular expression to match.\nThe format is \"full.class.path$InnerClass:methodName\"")
            .getStringList();
        transformerStringAdditionalList = config.get(
            "transformers",
            "AdditionalTransformerStringList",
            new String[0],
            "Additional list of methods to transform, of which uses \"String.contains\" to match.\nThe format is \"full.class.path$InnerClass:methodName\"")
            .getStringList();
        transformerMethodBlackList = config
            .get(
                "transformers",
                "transformerMethodBlackList",
                new String[0],
                "Blacklist of methods to transform\nThe format is \"full.class.path$InnerClass:methodName\"")
            .getStringList();
        EnableFU2V = config.get("fuzzy", "EnableFU2V", true, "Set to true to enable fuzzy U <=> V")
            .getBoolean();
        EnableFZh2Z = config.get("fuzzy", "EnableFZh2Z", true, "Set to true to enable fuzzy Zh <=> Z")
            .getBoolean();
        EnableFSh2S = config.get("fuzzy", "EnableFSh2S", true, "Set to true to enable fuzzy Sh <=> S")
            .getBoolean();
        EnableFCh2C = config.get("fuzzy", "EnableFCh2C", true, "Set to true to enable fuzzy Ch <=> C")
            .getBoolean();
        EnableFAng2An = config.get("fuzzy", "EnableFAng2An", true, "Set to true to enable fuzzy Ang <=> An")
            .getBoolean();
        EnableFIng2In = config.get("fuzzy", "EnableFIng2In", true, "Set to true to enable fuzzy Ing <=> In")
            .getBoolean();
        EnableFEng2En = config.get("fuzzy", "EnableFEng2En", true, "Set to true to enable fuzzy Eng <=> En")
            .getBoolean();
        String keyboardTypeString = config
            .get(
                "general",
                "KeyboardType",
                "quanpin",
                "Set the type of the keyboard, acceptable options are: quanpin, daqian, xiaohe and ziranma.")
            .getString();

        try {
            KeyboardType = NechConfig.Spell.valueOf(keyboardTypeString.toUpperCase());
        } catch (IllegalArgumentException var4) {
            ClientProxy.LOGGER.error("Invalid keyboard type: " + keyboardTypeString);
            KeyboardType = NechConfig.Spell.QUANPIN;
        }

        if (config.hasChanged()) {
            config.save();
        }

    }

    public static void setKeyboard(Spell keyboard) {
        KeyboardType = keyboard;
        Match.onConfigChange();
    }

    static {
        KeyboardType = NechConfig.Spell.QUANPIN;
        transformerRegExpAdditionalList = new String[0];
        transformerStringAdditionalList = new String[0];
        transformerMethodBlackList = new String[0];
        defaultTransformerRegExp = new String[] { "appeng.client.me.ItemRepo:updateView",
            "net.p455w0rd.wirelesscraftingterminal.client.me.ItemRepo:updateView",
            "com.glodblock.github.client.me.FluidRepo:updateView",
            "com.glodblock.github.client.me.EssentiaRepo:updateView",
            "com.glodblock.github.client.me.FluidRepo:addEntriesToView",
            "com.sinthoras.visualprospecting.integration.model.layers.UndergroundFluidLayerManager:computeSearch",
            "com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching:lambda$recalculateSearch$0"
        };
        defaultTransformerStringList = new String[] { "extracells.gui.GuiFluidTerminal:updateFluids",
            "extracells.gui.GuiFluidStorage:updateFluids",
            "witchinggadgets.client.ThaumonomiconIndexSearcher:buildEntryList",
            "net.glease.tc4tweak.modules.researchBrowser.ThaumonomiconIndexSearcher:buildEntryList",
            "appeng.client.gui.implementations.GuiInterfaceTerminal:refreshList",
            "appeng.client.gui.implementations.GuiInterfaceTerminal:itemStackMatchesSearchTerm",
            "appeng.client.gui.implementations.GuiInterfaceTerminal:interfaceSectionMatchesSearchTerm",
            "appeng.client.gui.implementations.GuiInterfaceTerminal$InterfaceTerminalList:update",
            "appeng.client.gui.implementations.GuiCraftConfirm:updateSearchGoToList",
            "appeng.client.gui.implementations.GuiCraftingCPU:updateSearchGoToList",
            "com.glodblock.github.client.gui.GuiInterfaceTerminalWireless:refreshList",
            "com.glodblock.github.client.gui.GuiInterfaceTerminalWireless:itemStackMatchesSearchTerm",
            "com.glodblock.github.client.gui.GuiInterfaceWireless$InterfaceWirelessList:updateList",
            "com.glodblock.github.client.gui.GuiInterfaceWireless:itemStackMatchesSearchTerm",
            "com.glodblock.github.client.gui.GuiLevelTerminal$LevelTerminalList:update",
            "com.glodblock.github.client.gui.GuiLevelTerminal:itemStackMatchesSearchTerm",
            "com.glodblock.github.client.gui.GuiInterfaceWireless$InterfaceWirelessList:update",
            "appeng.client.gui.widgets.GuiCraftingTree:updateSearchGoToList",
            "vswe.stevesfactory.components.ComponentMenuLiquid:updateSearch",
            "vswe.stevesfactory.components.ComponentMenuItem:updateSearch",
            "betterquesting.api2.client.gui.panels.lists.CanvasQuestSearch:queryMatches",
            "me.towdium.jecalculation.utils.Utilities$I18n:contains",
            "logisticspipes.gui.orderer.GuiOrderer:isSearched", "logisticspipes.gui.orderer.GuiRequestTable:isSearched",
            "mrtjp.projectred.transportation.GuiRequester$$anonfun$stringMatch$1$1:apply",
            "gregtech.crossmod.navigator.PowerfailLayerManager:onSearch"
        };
    }

    public static enum Spell {

        QUANPIN(Keyboard.QUANPIN),
        DAQIAN(Keyboard.DAQIAN),
        XIAOHE(Keyboard.XIAOHE),
        ZIRANMA(Keyboard.ZIRANMA),
        SOUGOU(Keyboard.SOUGOU),
        GUOBIAO(Keyboard.GUOBIAO),
        MICROSOFT(Keyboard.MICROSOFT),
        PINYINPP(Keyboard.PINYINPP),
        ZIGUANG(Keyboard.ZIGUANG);

        public final Keyboard keyboard;

        private Spell(Keyboard keyboard) {
            this.keyboard = keyboard;
        }

        public Keyboard get() {
            return this.keyboard;
        }
    }
}
