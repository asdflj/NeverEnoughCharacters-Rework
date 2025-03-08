package com.asdflj.nech.integration.nei;

import static com.asdflj.nech.utils.Match.refresh;

import codechicken.lib.config.ConfigTagParent;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.config.OptionToggleButton;

public class BaseToggleButton extends OptionToggleButton {

    private static final ConfigTagParent tag = NEIClientConfig.global.config;

    public BaseToggleButton(String name) {
        this(name, true);
    }

    public BaseToggleButton(String name, boolean defaultValue) {
        super(name, true);
        tag.getTag(name)
            .getBooleanValue(defaultValue);
    }

    @Override
    public boolean onClick(int button) {
        refresh();
        return super.onClick(button);

    }
}
