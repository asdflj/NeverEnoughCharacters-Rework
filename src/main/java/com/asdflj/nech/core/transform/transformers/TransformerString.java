package com.asdflj.nech.core.transform.transformers;

import org.objectweb.asm.tree.MethodNode;

import com.asdflj.nech.NechConfig;
import com.asdflj.nech.core.transform.Transformer;

public class TransformerString extends Transformer.Configurable {

    public TransformerString() {
        this.reload();
    }

    protected String[] getDefault() {
        return NechConfig.defaultTransformerStringList;
    }

    protected String[] getAdditional() {
        return NechConfig.transformerStringAdditionalList;
    }

    protected String getName() {
        return "string contains";
    }

    protected void transform(MethodNode n) {
        Transformer.transformInvoke(
            n,
            "java/lang/String",
            "contains",
            "com/asdflj/nech/utils/Match",
            "contains",
            "(Ljava/lang/String;Ljava/lang/CharSequence;)Z",
            false,
            184,
            "(Ljava/lang/Object;)Z",
            "(Ljava/lang/String;)Z");
    }
}
