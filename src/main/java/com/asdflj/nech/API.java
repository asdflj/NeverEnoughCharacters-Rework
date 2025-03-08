package com.asdflj.nech;

import com.asdflj.nech.utils.Match;

import me.towdium.pinin.PinIn;

public class API {

    public static API INSTANCE = new API();

    public PinIn getPinIn() {
        return Match.context;
    }

    public boolean contains(String s1, String s2) {
        return Match.contains(s1, s2);
    }

    public boolean contains(String s, CharSequence cs) {
        return Match.contains(s, cs);
    }

    public boolean contains(CharSequence a, CharSequence b, boolean c) {
        return Match.contains(a, b, c);
    }

    public boolean equals(String s, Object o) {
        return Match.equals(s, o);
    }

    public boolean contains(CharSequence a, CharSequence b) {
        return Match.contains(a, b);
    }

}
