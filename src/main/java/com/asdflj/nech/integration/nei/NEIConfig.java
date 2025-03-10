package com.asdflj.nech.integration.nei;

import com.asdflj.nech.NeverEnoughCharacters;
import com.asdflj.nech.Tags;

import codechicken.lib.config.ConfigTagParent;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEIConfig implements IConfigureNEI {

    private static final ConfigTagParent tag = NEIClientConfig.global.config;

    public NEIConfig() {}

    public void loadConfig() {
        API.addSearchProvider(new NechSearchParserProvider());
        API.addSearchProvider(new NechTooltipSearchParserProvider());
        API.addOption(new BaseToggleButton(ButtonConstants.COMMA));
        API.addOption(new BaseToggleButton(ButtonConstants.PARENTHESES));
        API.addOption(new BaseToggleButton(ButtonConstants.VOLTAGE));
        API.addOption(new BaseToggleButton(ButtonConstants.LUA_SCRIPT));
    }

    public static boolean getConfigValue(String identifier) {
        return tag.getTag(identifier)
            .getBooleanValue(true);
    }

    public String getName() {
        return NeverEnoughCharacters.MOD_NAME;
    }

    public String getVersion() {
        return Tags.VERSION;
    }
}
