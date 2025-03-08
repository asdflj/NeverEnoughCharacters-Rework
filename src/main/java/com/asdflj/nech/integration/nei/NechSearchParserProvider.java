package com.asdflj.nech.integration.nei;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import net.minecraft.client.resources.Language;
import net.minecraft.util.EnumChatFormatting;

import com.asdflj.nech.utils.Match;

import codechicken.nei.SearchTokenParser;
import codechicken.nei.SearchTokenParser.SearchMode;
import codechicken.nei.api.ItemFilter;

public class NechSearchParserProvider implements SearchTokenParser.ISearchParserProvider {

    public NechSearchParserProvider() {}

    public ItemFilter getFilter(String searchText) {
        return (item) -> Match.contains(
            item.getDisplayName()
                .toLowerCase(Locale.ROOT),
            searchText);
    }

    @SuppressWarnings("unchecked")
    public List<Language> getMatchingLanguages() {
        return SearchTokenParser.ISearchParserProvider.getAllLanguages()
            .stream()
            .filter(
                lang -> lang.getLanguageCode()
                    .startsWith("zh_"))
            .collect(Collectors.toList());
    }

    public char getPrefix() {
        return '\u0000';
    }

    public EnumChatFormatting getHighlightedColor() {
        return EnumChatFormatting.RESET;
    }

    public SearchTokenParser.SearchMode getSearchMode() {
        return SearchMode.ALWAYS;
    }
}
