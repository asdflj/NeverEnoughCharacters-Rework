package com.asdflj.nech.core.mixins.controlling;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

import com.asdflj.nech.API;
import com.blamejared.controlling.client.gui.GuiNewKeyBindingList;
import com.blamejared.controlling.client.gui.SearchType;

@Pseudo
@Mixin(SearchType.class)
public abstract class MixinSearchType {

    /**
     * @author asdflj
     * @reason 按键控制界面添加拼音搜索支持,由于技术上问题,无法支持高亮显示匹配的文本
     *         require vcwdfca
     *         url <a href="https://github.com/asdflj/NeverEnoughCharacters-Rework/issues/7">...</a>
     */
    @Overwrite(remap = false)
    public Predicate<GuiNewKeyBindingList.KeyEntry> getPredicate(String searchText) {
        SearchType type = (SearchType) (Object) this;
        String lowerSearchText = searchText.toLowerCase();
        if (type == SearchType.ALL) {
            return key -> {
                // 检查分类名
                String category = net.minecraft.util.StatCollector.translateToLocal(
                    key.getKeybinding()
                        .getKeyCategory())
                    .toLowerCase();
                // 检查按键描述
                String keyDesc = key.getKeyDesc()
                    .toLowerCase();
                // 检查按键显示名称
                String keyDisplay = net.minecraft.client.settings.GameSettings.getKeyDisplayString(
                    key.getKeybinding()
                        .getKeyCode())
                    .toLowerCase();

                return API.INSTANCE.contains(category, lowerSearchText)
                    || API.INSTANCE.contains(keyDesc, lowerSearchText)
                    || API.INSTANCE.contains(keyDisplay, lowerSearchText);
            };
        } else if (type == SearchType.CATEGORY_NAME) {
            return key -> API.INSTANCE.contains(
                net.minecraft.util.StatCollector.translateToLocal(
                    key.getKeybinding()
                        .getKeyCategory())
                    .toLowerCase(),
                lowerSearchText);
        } else if (type == SearchType.KEYBIND_NAME) {
            return key -> API.INSTANCE.contains(
                key.getKeyDesc()
                    .toLowerCase(),
                lowerSearchText);
        } else if (type == SearchType.KEY_NAME) {
            return key -> API.INSTANCE.contains(
                net.minecraft.client.settings.GameSettings.getKeyDisplayString(
                    key.getKeybinding()
                        .getKeyCode())
                    .toLowerCase(),
                lowerSearchText);
        } else {
            throw new IllegalStateException("Unknown SearchType");
        }
    }
}
