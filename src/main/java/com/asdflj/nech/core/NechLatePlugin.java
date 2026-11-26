package com.asdflj.nech.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.launchwrapper.Launch;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

@LateMixin
public class NechLatePlugin implements ILateMixinLoader {

    private static boolean lateTransformerRegistered;

    @Override
    public String getMixinConfig() {
        return "mixins.nech.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        if (!lateTransformerRegistered) {
            lateTransformerRegistered = true;
            Launch.classLoader.registerTransformer("com.asdflj.nech.core.LateClassTransformer");
        }
        ArrayList<String> mixins = new ArrayList<>();
        if (loadedMods.contains("controlling")) {
            mixins.add("controlling.MixinGuiNewKeyBindingList");
            mixins.add("controlling.MixinGuiNewKeyBindingListKeyEntry");
        }

        return mixins;
    }

}
