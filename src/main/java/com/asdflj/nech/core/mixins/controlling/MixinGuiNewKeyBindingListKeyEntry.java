package com.asdflj.nech.core.mixins.controlling;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.asdflj.nech.utils.Match;
import com.blamejared.controlling.client.gui.GuiNewKeyBindingList;

@Mixin(GuiNewKeyBindingList.KeyEntry.class)
public abstract class MixinGuiNewKeyBindingListKeyEntry {

    @Redirect(
        method = "drawEntry",
        at = @At(value = "INVOKE", target = "Ljava/lang/String;indexOf(Ljava/lang/String;)I"))
    private int nech$highlightStart(String text, String query) {
        return Match.highlightStart(text, query);
    }

    @Redirect(method = "drawEntry", at = @At(value = "INVOKE", target = "Ljava/lang/String;length()I"))
    private int nech$highlightLength(String searchString) {
        return Match.highlightLength(searchString);
    }
}
