package com.asdflj.nech.core.transform;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import com.asdflj.nech.NechConfig;
import com.asdflj.nech.core.NechCorePlugin;
import com.google.common.collect.HashMultimap;

public interface Transformer {

    static Optional<MethodNode> findMethod(ClassNode c, String name) {
        Optional<MethodNode> ret = c.methods.stream()
            .filter((methodNode) -> { return methodNode.name.equals(name); })
            .findFirst();
        String s = ret.isPresent() ? "," : ", not";
        NechCorePlugin.LOGGER.info("Finding method " + name + " in class " + c.name + s + " found.");
        return ret;
    }

    static Optional<MethodNode> findMethod(ClassNode c, String name, String desc) {
        Optional<MethodNode> ret = c.methods.stream()
            .filter((methodNode) -> { return methodNode.name.equals(name); })
            .filter((methodNode) -> { return methodNode.desc.equals(desc); })
            .findFirst();
        String s = ret.isPresent() ? "," : ", not";
        NechCorePlugin.LOGGER.info("Finding method " + name + desc + " in class " + c.name + s + " found.");
        return ret;
    }

    static boolean transformInvoke(MethodNode methodNode, String owner, String name, String newOwner, String newName,
        String id, boolean isInterface, int op, @Nullable String arg1, @Nullable String arg2) {
        NechCorePlugin.LOGGER.info(
            "Transforming invoke of " + owner
                + "."
                + name
                + " to "
                + newOwner
                + "."
                + newName
                + " in method "
                + methodNode.name
                + ".");
        Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
        boolean ret = false;

        while (iterator.hasNext()) {
            AbstractInsnNode node = (AbstractInsnNode) iterator.next();
            if (node instanceof MethodInsnNode
                && (node.getOpcode() == 182 || node.getOpcode() == 183 || node.getOpcode() == 184)) {
                MethodInsnNode insnNode = (MethodInsnNode) node;
                if (insnNode.owner.equals(owner) && insnNode.name.equals(name)) {
                    methodNode.instructions.set(insnNode, new MethodInsnNode(op, newOwner, newName, id, isInterface));
                    ret = true;
                }
            }

            if (node instanceof InvokeDynamicInsnNode && node.getOpcode() == 186 && arg1 != null && arg2 != null) {
                InvokeDynamicInsnNode insnNode = (InvokeDynamicInsnNode) node;
                if (insnNode.bsmArgs[1] instanceof Handle) {
                    Handle h = (Handle) insnNode.bsmArgs[1];
                    if (h.getOwner()
                        .equals(owner)
                        && h.getName()
                            .equals(name)) {
                        Object[] args = new Object[] { Type.getType(arg1), new Handle(6, newOwner, newName, id),
                            Type.getType(arg2) };
                        methodNode.instructions
                            .set(insnNode, new InvokeDynamicInsnNode(insnNode.name, insnNode.desc, insnNode.bsm, args));
                        ret = true;
                    }
                }
            }
        }

        return ret;
    }

    static void transformConstruct(MethodNode methodNode, String desc, String destNew) {
        NechCorePlugin.LOGGER
            .info("Transforming constructor of " + desc + " to " + destNew + " in method " + methodNode.name + ".");
        Iterator<AbstractInsnNode> i = methodNode.instructions.iterator();
        int cnt = 0;

        while (i.hasNext()) {
            AbstractInsnNode node = (AbstractInsnNode) i.next();
            if (node.getOpcode() == 187) {
                TypeInsnNode nodeNew = (TypeInsnNode) node;
                if (nodeNew.desc.equals(desc)) {
                    nodeNew.desc = destNew;
                    ++cnt;
                }
            } else if (node.getOpcode() == 183) {
                MethodInsnNode nodeNew = (MethodInsnNode) node;
                if (nodeNew.owner.equals(desc)) {
                    nodeNew.owner = destNew;
                }
            }
        }

        NechCorePlugin.LOGGER.info("Transformed " + cnt + " occurrences.");
    }

    static void transformHook(MethodNode methodNode, String owner, String name, String id) {
        Iterator<AbstractInsnNode> i = methodNode.instructions.iterator();

        while (i.hasNext()) {
            AbstractInsnNode node = (AbstractInsnNode) i.next();
            if (node instanceof InsnNode && node.getOpcode() == 177) {
                methodNode.instructions.insertBefore(node, new MethodInsnNode(184, owner, name, id, false));
            }
        }

    }

    boolean accepts(String var1);

    byte[] transform(byte[] var1);

    public static class MethodDecoder {

        HashMultimap<String, String> methods = HashMultimap.create();

        public MethodDecoder() {}

        public static void logError(String s) {
            NechCorePlugin.LOGGER.info("Invalid config syntax: " + s);
        }

        public void addAll(String[] names) {
            String[] var2 = names;
            int var3 = names.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                String s = var2[var4];
                String[] ss = s.split(":");
                if (ss.length == 2) {
                    this.methods.put(ss[0], ss[1]);
                } else {
                    logError(s);
                }
            }

        }

        public void removeAll(String[] names) {
            String[] var2 = names;
            int var3 = names.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                String s = var2[var4];
                String[] ss = s.split(":");
                if (ss.length == 2) {
                    this.methods.remove(ss[0], ss[1]);
                } else {
                    logError(s);
                }
            }

        }

        public Set<String> getMethodsForClass(String c) {
            return this.methods.get(c);
        }

        public boolean contains(String s) {
            return this.methods.containsKey(s);
        }
    }

    public abstract static class Configurable extends Default {

        protected MethodDecoder md = new MethodDecoder();

        public Configurable() {}

        protected abstract String[] getDefault();

        protected abstract String[] getAdditional();

        protected abstract String getName();

        protected abstract void transform(MethodNode var1);

        protected void transform(ClassNode c) {
            NechCorePlugin.LOGGER.info("Transforming class " + c.name + " for " + this.getName() + ".");
            Set<String> ms = this.md.getMethodsForClass(c.name.replace('/', '.'));
            if (!ms.isEmpty()) {
                c.methods.stream()
                    .filter((m) -> { return ms.contains(m.name); })
                    .forEach(this::transform);
            } else {
                NechCorePlugin.LOGGER.info("No function matched in class " + c.name);
            }

        }

        public boolean accepts(String name) {
            return this.md.contains(name);
        }

        public void reload() {
            MethodDecoder mdt = new MethodDecoder();
            mdt.addAll(this.getDefault());
            mdt.addAll(this.getAdditional());
            mdt.removeAll(NechConfig.transformerMethodBlackList);
            this.md = mdt;
        }
    }

    public abstract static class Default implements Transformer {

        public Default() {}

        public byte[] transform(byte[] bytes) {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(bytes);
            classReader.accept(classNode, 0);
            this.transform(classNode);
            ClassWriter classWriter = new ClassWriter(1);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }

        protected abstract void transform(ClassNode var1);
    }
}
