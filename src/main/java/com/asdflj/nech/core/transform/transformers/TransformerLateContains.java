package com.asdflj.nech.core.transform.transformers;

import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.asdflj.nech.NechConfig;
import com.asdflj.nech.core.NechCorePlugin;
import com.asdflj.nech.core.transform.Transformer;

public class TransformerLateContains extends Transformer.Configurable {

    public TransformerLateContains() {
        this.reload();
    }

    @Override
    protected String[] getDefault() {
        return NechConfig.defaultTransformerLateContainsList;
    }

    @Override
    protected String[] getAdditional() {
        return NechConfig.transformerLateContainsAdditionalList;
    }

    @Override
    protected String getName() {
        return "late string contains";
    }

    @Override
    protected void transform(ClassNode c) {
        NechCorePlugin.LOGGER.info("Transforming class " + c.name + " for " + this.getName() + ".");
        Set<String> ms = this.md.getMethodsForClass(c.name.replace('/', '.'));
        if (!ms.isEmpty()) {
            c.methods.stream()
                .filter(
                    m -> ms.stream()
                        .anyMatch(p -> matchMethod(p, m.name)))
                .forEach(this::transform);
        } else {
            NechCorePlugin.LOGGER.info("No function matched in class " + c.name);
        }
    }

    private static boolean matchMethod(String pattern, String methodName) {
        if (pattern.equals(methodName)) return true;
        boolean head = pattern.startsWith("*");
        boolean tail = pattern.endsWith("*");
        if (head && tail) return methodName.contains(pattern.substring(1, pattern.length() - 1));
        if (head) return methodName.endsWith(pattern.substring(1));
        if (tail) return methodName.startsWith(pattern.substring(0, pattern.length() - 1));
        return false;
    }

    @Override
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
