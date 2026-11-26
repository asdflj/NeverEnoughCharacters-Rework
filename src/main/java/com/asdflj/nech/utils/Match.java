package com.asdflj.nech.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.asdflj.nech.NechConfig;
import com.asdflj.nech.proxy.ClientProxy;
import com.google.common.collect.ImmutableSet;

import me.towdium.pinin.DictLoader;
import me.towdium.pinin.PinIn;
import me.towdium.pinin.searchers.Searcher;
import me.towdium.pinin.searchers.TreeSearcher;
import me.towdium.pinin.utils.IndexSet;

public class Match {

    public static final PinIn context = (new PinIn(new Loader())).config()
        .accelerate(true)
        .commit();
    public static final List<ITextFunction<String, String>> textMiddleware = new LinkedList<>();
    public static final List<IInputFunction<String, Set<String>>> inputMiddleware = new LinkedList<>();
    private static final Pattern p = Pattern.compile("a");
    private static final Set<TreeSearcher<?>> searchers = Collections.newSetFromMap(new WeakHashMap<>());
    private static ImmutablePair<String, Set<String>> inputCache = null;
    private static final ThreadLocal<ImmutablePair<Integer, Integer>> LAST_HIGHLIGHT = ThreadLocal
        .withInitial(() -> ImmutablePair.of(-1, -1));
    private static final ThreadLocal<String> LAST_HIGHLIGHT_QUERY = new ThreadLocal<>();

    private static <T> TreeSearcher<T> searcher() {
        TreeSearcher<T> ret = new TreeSearcher<>(Searcher.Logic.CONTAIN, context);
        searchers.add(ret);
        return ret;
    }

    public static int rank(Object o, String s1, String s2) {
        return contains(s1, s2) ? 1 : 0;
    }

    public static boolean contains(String s, CharSequence cs) {
        for (Function<String, String> m : textMiddleware) {
            try {
                s = m.apply(s);
            } catch (Exception ignored) {}
        }
        if (cs.toString()
            .isEmpty()) {
            return pinInContains(s, cs.toString());
        }
        if (inputCache != null && inputCache.left.equals(cs.toString())) {
            return match(s, inputCache.right);
        } else {
            Set<String> list = new HashSet<>();
            for (Function<String, Set<String>> m : inputMiddleware) {
                Set<String> r = m.apply(cs.toString());
                if (r != null && !r.isEmpty()) {
                    list.addAll(r);
                }
            }
            list = list.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            if (list.isEmpty()) {
                list = ImmutableSet.of(cs.toString());
            }
            inputCache = ImmutablePair.of(cs.toString(), list);
            return match(s, list);
        }

    }

    private static boolean pinInContains(String name, String input) {
        boolean b = context.contains(name, input);
        if (NechConfig.enableVerbose) {
            ClientProxy.LOGGER.info("contains(" + name + ',' + input + ")->" + b);
        }

        return b;
    }

    private static boolean match(String name, Set<String> str) {
        if (str.isEmpty()) {
            return pinInContains(name, "");
        }
        if (str.size() == 1) {
            return pinInContains(
                name,
                str.stream()
                    .findFirst()
                    .get());
        }
        Optional<Boolean> result = str.stream()
            .map(s -> pinInContains(name, s))
            .filter(x -> !x)
            .findAny();
        if (result.isPresent()) {
            return false;
        }
        return true;
    }

    public static boolean contains(CharSequence a, CharSequence b, boolean c) {
        return c ? contains(
            a.toString()
                .toLowerCase(),
            b.toString()
                .toLowerCase())
            : contains(a, b);
    }

    public static boolean equals(String s, Object o) {
        boolean b = o instanceof String && context.matches(s, (String) o);
        if (NechConfig.enableVerbose) {
            ClientProxy.LOGGER.info("contains(" + s + ',' + o + ")->" + b);
        }

        return b;
    }

    public static boolean contains(CharSequence a, CharSequence b) {
        return contains(a.toString(), b);
    }

    public static Matcher matcher(Pattern test, CharSequence name) {
        boolean result;
        if ((test.flags() & 2) == 0 && (test.flags() & 64) == 0) {
            result = matches(name.toString(), test.toString());
        } else {
            result = matches(
                name.toString()
                    .toLowerCase(),
                test.toString()
                    .toLowerCase());
        }

        return result ? p.matcher("a") : p.matcher("");
    }

    public static boolean matches(String s1, String s2) {
        boolean start = s2.startsWith(".*");
        boolean end = s2.endsWith(".*");
        if (start && end && s2.length() < 4) {
            end = false;
        }

        if (start || end) {
            s2 = s2.substring(start ? 2 : 0, s2.length() - (end ? 2 : 0));
        }

        return contains(s1, s2);
    }

    public static void onConfigChange() {
        refresh();
    }

    public static void refresh() {
        context.config()
            .fZh2Z(NechConfig.EnableFZh2Z)
            .fSh2S(NechConfig.EnableFSh2S)
            .fCh2C(NechConfig.EnableFCh2C)
            .fAng2An(NechConfig.EnableFAng2An)
            .fIng2In(NechConfig.EnableFIng2In)
            .fEng2En(NechConfig.EnableFEng2En)
            .fU2V(NechConfig.EnableFU2V)
            .keyboard(NechConfig.KeyboardType.get())
            .commit();
        searchers.forEach(TreeSearcher::refresh);
    }

    public static int highlightStart(String text, String query) {
        ImmutablePair<Integer, Integer> range = highlightRange(text, query);
        LAST_HIGHLIGHT_QUERY.set(query);
        if (range == null) {
            LAST_HIGHLIGHT.set(ImmutablePair.of(-1, -1));
            return -1;
        }
        LAST_HIGHLIGHT.set(range);
        return range.left;
    }

    public static int highlightLength(String searchString) {
        String query = LAST_HIGHLIGHT_QUERY.get();
        ImmutablePair<Integer, Integer> range = LAST_HIGHLIGHT.get();
        if (range.left < 0 || query == null || searchString == null || !searchString.equalsIgnoreCase(query)) {
            return searchString == null ? 0 : searchString.length();
        }
        return range.right - range.left;
    }

    public static ImmutablePair<Integer, Integer> highlightRange(String text, String query) {
        if (text == null || text.isEmpty() || query == null || query.isEmpty()) return null;
        String lowerText = text.toLowerCase(Locale.ROOT);
        String lowerQuery = query.toLowerCase(Locale.ROOT);
        int inline = lowerText.indexOf(lowerQuery);
        if (inline >= 0) return ImmutablePair.of(inline, inline + lowerQuery.length());

        int[] out = new int[2];
        for (int i = 0; i < text.length();) {
            if (matchAt(text, i, lowerQuery, 0, -1, out)) return ImmutablePair.of(out[0], out[1]);
            i += Character.charCount(text.codePointAt(i));
        }
        return null;
    }

    private static boolean matchAt(String text, int index, String query, int consumed, int first, int[] out) {
        if (consumed == query.length()) {
            out[0] = first;
            out[1] = index;
            return true;
        }
        if (index >= text.length()) return false;

        int cp = text.codePointAt(index);
        int next = index + Character.charCount(cp);
        IndexSet set = context.getChar(cp)
            .match(query, consumed, true);

        if (next >= text.length()) {
            if (set.get(query.length() - consumed)) {
                out[0] = consumed == 0 ? index : first;
                out[1] = next;
                return true;
            }
            return false;
        }
        final int current = index;
        return set
            .traverse(c -> matchAt(text, next, query, consumed + c, consumed == 0 && c > 0 ? current : first, out));
    }

    static class Loader extends DictLoader.Default {

        Loader() {}

        public void load(BiConsumer<Character, String[]> feed) {
            super.load(feed);
            feed.accept('鿏', new String[] { "mai4" });
            feed.accept('鿔', new String[] { "ge1" });
            feed.accept('鿭', new String[] { "ni3" });
            feed.accept('鿬', new String[] { "tian2" });
            feed.accept('鿫', new String[] { "ao4" });
            feed.accept('\ue900', new String[] { "lu2" });
            feed.accept('\ue901', new String[] { "du4" });
            feed.accept('\ue902', new String[] { "xi3" });
            feed.accept('\ue903', new String[] { "bo1" });
            feed.accept('\ue904', new String[] { "hei1" });
            feed.accept('\ue906', new String[] { "da2" });
            feed.accept('\ue907', new String[] { "lun2" });
            feed.accept('\ue910', new String[] { "fu1" });
            feed.accept('\ue912', new String[] { "li4" });
        }
    }
}
