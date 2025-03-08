package com.asdflj.nech.integration.nei;

public enum Voltage {

    ulv,
    lv,
    mv,
    hv,
    ev,
    iv,
    luv,
    zpm,
    uv,
    uhv,
    uev,
    uiv,
    umv,
    uxv,
    max;

    public static String getPatternString() {
        StringBuilder s = new StringBuilder("(");
        for (int i = 0; i < Voltage.values().length; i++) {
            s.append(Voltage.values()[i].name());
            if (i < Voltage.values().length - 1) {
                s.append("|");
            }
        }
        s.append(")");
        return s.toString();
    }

}
