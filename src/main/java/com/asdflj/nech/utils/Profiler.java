package com.asdflj.nech.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Consumer;
import java.util.zip.ZipFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

import com.asdflj.nech.proxy.ClientProxy;
import com.google.gson.Gson;

public class Profiler {

    private static final JarContainer[] EMPTY_JC = new JarContainer[0];
    private static final String[] EMPTY_STR = new String[0];

    public Profiler() {}

    public static Report run() {
        File modDirectory = new File("mods");
        JarContainer[] jcs = scanDirectory(modDirectory).toArray(EMPTY_JC);
        Report r = new Report();
        r.jars = jcs;
        return r;
    }

    private static ArrayList<JarContainer> scanDirectory(File f) {
        File[] files = f.listFiles();
        ArrayList<JarContainer> jcs = new ArrayList();
        Consumer<JarContainer> callback = jcs::add;
        if (files != null) {
            File[] var4 = files;
            int var5 = files.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                File file = var4[var6];
                if (file.isFile() && file.getName()
                    .endsWith(".jar")) {
                    try {
                        ZipFile mod = new ZipFile(file);
                        Throwable var9 = null;

                        try {
                            scanJar(mod, callback);
                        } catch (Throwable var19) {
                            var9 = var19;
                            throw var19;
                        } finally {
                            if (mod != null) {
                                if (var9 != null) {
                                    try {
                                        mod.close();
                                    } catch (Throwable var18) {
                                        var9.addSuppressed(var18);
                                    }
                                } else {
                                    mod.close();
                                }
                            }

                        }
                    } catch (IOException var21) {
                        IOException e = var21;
                        e.printStackTrace();
                    }
                } else if (file.isDirectory()) {
                    jcs.addAll(scanDirectory(file));
                }
            }
        }

        return jcs;
    }

    private static void scanJar(ZipFile f, Consumer<JarContainer> cbkJar) {
        ArrayList<String> methodsString = new ArrayList();
        ArrayList<String> methodsRegExp = new ArrayList();
        ArrayList<String> methodsSuffix = new ArrayList();
        ArrayList<String> methodsStrsKt = new ArrayList();
        JarContainer ret = new JarContainer();
        f.stream()
            .forEach((entry) -> {
                if (entry.getName()
                    .endsWith(".class")) {
                    try {
                        InputStream isx = f.getInputStream(entry);
                        Throwable var8 = null;

                        try {
                            long size = entry.getSize() + 4L;
                            if (size > 2147483647L) {
                                ClientProxy.LOGGER.info(
                                    "Class file " + entry.getName()
                                        + " in jar file "
                                        + f.getName()
                                        + " is too large, skip.");
                            } else {
                                scanClass(
                                    isx,
                                    methodsString::add,
                                    methodsRegExp::add,
                                    methodsSuffix::add,
                                    methodsStrsKt::add);
                            }
                        } catch (Throwable var40) {
                            var8 = var40;
                            throw var40;
                        } finally {
                            if (isx != null) {
                                if (var8 != null) {
                                    try {
                                        isx.close();
                                    } catch (Throwable var37) {
                                        var8.addSuppressed(var37);
                                    }
                                } else {
                                    isx.close();
                                }
                            }

                        }
                    } catch (IOException var44) {
                        ClientProxy.LOGGER
                            .info("Fail to read file " + entry.getName() + " in jar file " + f.getName() + ", skip.");
                    }
                } else if (entry.getName()
                    .equals("mcmod.info")) {
                        Gson gson = new Gson();

                        try {
                            InputStream is = f.getInputStream(entry);
                            Throwable var47 = null;

                            try {
                                try {
                                    ret.mods = (ModContainer[]) gson
                                        .fromJson(new InputStreamReader(is), ModContainer[].class);
                                } catch (Exception var38) {
                                    ret.mods = new ModContainer[] {
                                        (ModContainer) gson.fromJson(new InputStreamReader(is), ModContainer.class) };
                                }
                            } catch (Throwable var39) {
                                var47 = var39;
                                throw var39;
                            } finally {
                                if (is != null) {
                                    if (var47 != null) {
                                        try {
                                            is.close();
                                        } catch (Throwable var36) {
                                            var47.addSuppressed(var36);
                                        }
                                    } else {
                                        is.close();
                                    }
                                }

                            }
                        } catch (Exception var42) {
                            ClientProxy.LOGGER.info("Fail to read mod info in jar file " + f.getName() + ", skip.");
                        }
                    }

            });
        if (!methodsString.isEmpty() || !methodsRegExp.isEmpty()
            || !methodsSuffix.isEmpty()
            || !methodsStrsKt.isEmpty()) {
            ret.methodsString = methodsString.toArray(EMPTY_STR);
            ret.methodsRegExp = methodsRegExp.toArray(EMPTY_STR);
            ret.methodsSuffix = methodsSuffix.toArray(EMPTY_STR);
            ret.methodsStrsKt = methodsStrsKt.toArray(EMPTY_STR);
            cbkJar.accept(ret);
        }

    }

    private static void scanClass(InputStream is, Consumer<String> string, Consumer<String> regexp,
        Consumer<String> suffix, Consumer<String> strskt) throws IOException {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(is);

        try {
            classReader.accept(classNode, 0);
        } catch (Exception var8) {
            Exception e = var8;
            if (classNode.name == null) {
                throw new IOException(e);
            }

            ClientProxy.LOGGER.info("File decoding of class " + classNode.name + " failed. Try to continue.");
        }

        classNode.methods.forEach((methodNode) -> {
            Iterator<AbstractInsnNode> it = methodNode.instructions.iterator();

            while (it.hasNext()) {
                AbstractInsnNode node = (AbstractInsnNode) it.next();
                if (node instanceof MethodInsnNode) {
                    MethodInsnNode mNode = (MethodInsnNode) node;
                    if (mNode.getOpcode() == 182 && mNode.owner.equals("java/lang/String")
                        && mNode.name.equals("contains")
                        && mNode.desc.equals("(Ljava/lang/CharSequence;)Z")) {
                        string
                            .accept((classNode.name + ":" + methodNode.name + ":" + methodNode.desc).replace('/', '.'));
                        break;
                    }

                    if (mNode.getOpcode() == 182 && mNode.owner.equals("java/lang/String")
                        && mNode.name.equals("matches")
                        && mNode.desc.equals("(Ljava/lang/String;)Z")) {
                        regexp
                            .accept((classNode.name + ":" + methodNode.name + ":" + methodNode.desc).replace('/', '.'));
                        break;
                    }

                    if (mNode.getOpcode() == 182 && mNode.owner.equals("java/util/regex/Pattern")
                        && mNode.name.equals("matcher")
                        && mNode.desc.equals("(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;")) {
                        regexp
                            .accept((classNode.name + ":" + methodNode.name + ":" + methodNode.desc).replace('/', '.'));
                        break;
                    }

                    if (mNode.getOpcode() == 184 && mNode.owner.equals("kotlin/text/StringsKt")
                        && mNode.name.equals("contains")
                        && mNode.desc.equals("(Ljava/lang/CharSequence;Ljava/lang/CharSequence;Z)Z")) {
                        strskt
                            .accept((classNode.name + ":" + methodNode.name + ":" + methodNode.desc).replace('/', '.'));
                        break;
                    }
                } else if (node instanceof TypeInsnNode) {
                    TypeInsnNode tNode = (TypeInsnNode) node;
                    if (tNode.getOpcode() == 187
                        && (tNode.desc.equals("net/minecraft/client/util/SuffixArray") || tNode.desc.equals("cgx"))
                        || tNode.desc.equals("cgz")) {
                        suffix
                            .accept((classNode.name + ":" + methodNode.name + ":" + methodNode.desc).replace('/', '.'));
                        break;
                    }
                }
            }

        });
    }

    private static class ModContainer {

        String modid;
        String name;
        String version;
        String mcversion;
        String url;
        String[] authorList;

        private ModContainer() {}
    }

    private static class JarContainer {

        ModContainer[] mods;
        String[] methodsString;
        String[] methodsRegExp;
        String[] methodsSuffix;
        String[] methodsStrsKt;

        private JarContainer() {}
    }

    public static class Report {

        String version = "@VERSION@";
        String mcversion = "1.7.10";
        String date;
        JarContainer[] jars;

        public Report() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            this.date = sdf.format(new Date());
        }
    }
}
