package com.asdflj.nech.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

@LateMixin
public class NechLatePlugin implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.nech.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        ArrayList<String> mixins = new ArrayList<>();
        if (loadedMods.contains("controlling")) {
            mixins.add("controlling.MixinSearchType");
        }

        return mixins;
    }

}
