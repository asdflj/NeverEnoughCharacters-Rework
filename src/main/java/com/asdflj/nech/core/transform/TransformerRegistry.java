package com.asdflj.nech.core.transform;

import java.util.ArrayList;
import java.util.Iterator;

import com.asdflj.nech.core.transform.transformers.TransformerRegExp;
import com.asdflj.nech.core.transform.transformers.TransformerString;

public class TransformerRegistry {

    public static ArrayList<Transformer.Configurable> configurables = new ArrayList();
    public static ArrayList<Transformer> transformers = new ArrayList();

    public TransformerRegistry() {}

    public static ArrayList<Transformer> getTransformer(String name) {
        ArrayList<Transformer> ret = new ArrayList();
        Iterator var2 = transformers.iterator();

        while (var2.hasNext()) {
            Transformer t = (Transformer) var2.next();
            if (t.accepts(name)) {
                ret.add(t);
            }
        }

        return ret;
    }

    static {
        configurables.add(new TransformerString());
        configurables.add(new TransformerRegExp());
        transformers.addAll(configurables);
    }
}
