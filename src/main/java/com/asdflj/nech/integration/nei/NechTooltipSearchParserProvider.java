package com.asdflj.nech.integration.nei;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.asdflj.nech.utils.Match;

import codechicken.nei.ItemList;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.SearchTokenParser;
import codechicken.nei.api.ItemFilter;
import codechicken.nei.guihook.GuiContainerManager;

public class NechTooltipSearchParserProvider implements SearchTokenParser.ISearchParserProvider {

    private static final ConcurrentHashMap<ItemStackKey, String> itemSearchNames = new ConcurrentHashMap<>();

    public NechTooltipSearchParserProvider() {}

    public ItemFilter getFilter(String searchText) {
        return (item) -> Match.contains(getSearchTooltip(item), searchText);
    }

    private static String getTooltip(ItemStack itemstack) {
        List<String> list = GuiContainerManager.itemDisplayNameMultiline(itemstack, null, true);
        StringJoiner sb = new StringJoiner("\n");
        int size = list.size();

        for (int i = 1; i < size; ++i) {
            sb.add(list.get(i));
        }

        return EnumChatFormatting.getTextWithoutFormattingCodes(sb.toString());
    }

    public static void populateSearchMap() {
        HashSet<ItemStackKey> oldItems = new HashSet(itemSearchNames.keySet());
        Iterator var1 = ItemList.items.iterator();

        while (var1.hasNext()) {
            ItemStack stack = (ItemStack) var1.next();
            oldItems.remove(new ItemStackKey(stack));
        }

        itemSearchNames.keySet()
            .removeAll(oldItems);
    }

    protected static String getSearchTooltip(ItemStack stack) {
        return itemSearchNames.computeIfAbsent(new ItemStackKey(stack), (key) -> getTooltip(key.stack));
    }

    public char getPrefix() {
        return '#';
    }

    public EnumChatFormatting getHighlightedColor() {
        return EnumChatFormatting.YELLOW;
    }

    public SearchTokenParser.SearchMode getSearchMode() {
        return SearchTokenParser.SearchMode
            .fromInt(NEIClientConfig.getIntSetting("inventory.search.tooltipSearchMode"));
    }

    private static class ItemStackKey {

        public final ItemStack stack;

        public ItemStackKey(ItemStack stack) {
            this.stack = stack;
        }

        public int hashCode() {
            if (this.stack == null) {
                return 1;
            } else {
                int hashCode = 1;
                hashCode = 31 * hashCode + this.stack.stackSize;
                hashCode = 31 * hashCode + Item.getIdFromItem(this.stack.getItem());
                hashCode = 31 * hashCode + this.stack.getItemDamage();
                hashCode = 31 * hashCode + (!this.stack.hasTagCompound() ? 0
                    : this.stack.getTagCompound()
                        .hashCode());
                return hashCode;
            }
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else {
                return !(o instanceof ItemStackKey) ? false
                    : ItemStack.areItemStacksEqual(this.stack, ((ItemStackKey) o).stack);
            }
        }
    }
}
