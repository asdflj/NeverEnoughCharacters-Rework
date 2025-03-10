package com.asdflj.nech.integration.nei;

import static com.asdflj.nech.integration.nei.ButtonConstants.LUA_SCRIPTS;

import codechicken.lib.config.ConfigTagParent;
import codechicken.nei.NEIClientConfig;

public class LuaToggleButton extends BaseToggleButton {

    private static final ConfigTagParent tag = NEIClientConfig.global.config;
    private final String n;

    public LuaToggleButton(String name) {
        this(name, true);
    }

    private LuaToggleButton(String name, boolean defaultValue) {
        super(LUA_SCRIPTS + "." + name, true);
        tag.getTag(name)
            .getBooleanValue(defaultValue);
        this.n = name;
    }

    @Override
    public boolean onClick(int button) {
        return super.onClick(button);
    }

    @Override
    public String getButtonText() {
        return state() ? "启用" : "关闭";
    }

    public String translateN(String s, Object... args) {
        return this.n;
    }
}
