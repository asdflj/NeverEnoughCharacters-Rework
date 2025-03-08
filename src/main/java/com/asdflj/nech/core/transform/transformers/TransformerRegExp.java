package com.asdflj.nech.core.transform.transformers;

import org.objectweb.asm.tree.MethodNode;

import com.asdflj.nech.NechConfig;
import com.asdflj.nech.core.transform.Transformer;

public class TransformerRegExp extends Transformer.Configurable {

    public TransformerRegExp() {
        this.reload();
    }

    protected String[] getDefault() {
        return NechConfig.defaultTransformerRegExp;
    }

    protected String[] getAdditional() {
        return NechConfig.transformerRegExpAdditionalList;
    }

    protected String getName() {
        return "regular expression";
    }

    protected void transform(MethodNode n) {
        Transformer.transformInvoke(
            n,
            "java/util/regex/Pattern",
            "matcher",
            "com/asdflj/nech/utils/Match",
            "matcher",
            "(Ljava/util/regex/Pattern;Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;",
            false,
            184,
            (String) null,
            (String) null);
        Transformer.transformInvoke(
            n,
            "java/lang/String",
            "matches",
            "com/asdflj/nech/utils/Match",
            "matches",
            "(Ljava/lang/String;Ljava/lang/CharSequence;)Z",
            false,
            184,
            "(Ljava/lang/Object;)Z",
            "(Ljava/lang/String;)Z");
    }
}
