package com.asdflj.nech.core;

import java.util.Iterator;

import net.minecraft.launchwrapper.IClassTransformer;

import com.asdflj.nech.core.transform.Transformer;
import com.asdflj.nech.core.transform.TransformerRegistry;

public class NechClassTransformer implements IClassTransformer {

    public NechClassTransformer() {}

    public byte[] transform(String s, String s1, byte[] bytes) {
        if (!NechCorePlugin.INITIALIZED) {
            return bytes;
        } else {
            Transformer t;
            for (Iterator var4 = TransformerRegistry.getTransformer(s)
                .iterator(); var4.hasNext(); bytes = t.transform(bytes)) {
                t = (Transformer) var4.next();
            }

            return bytes;
        }
    }
}
