package com.asdflj.nech.core;

import net.minecraft.launchwrapper.IClassTransformer;

import com.asdflj.nech.core.transform.Transformer;
import com.asdflj.nech.core.transform.TransformerRegistry;

public class LateClassTransformer implements IClassTransformer {

    public LateClassTransformer() {}

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (!NechCorePlugin.INITIALIZED || bytes == null) {
            return bytes;
        }
        String className = transformedName == null || transformedName.isEmpty() ? name : transformedName;
        for (Transformer transformer : TransformerRegistry.getLateTransformer(className)) {
            bytes = transformer.transform(bytes);
        }
        return bytes;
    }
}
