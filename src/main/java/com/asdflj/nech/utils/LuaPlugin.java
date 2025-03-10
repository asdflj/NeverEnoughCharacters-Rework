package com.asdflj.nech.utils;

import static codechicken.nei.NEIClientConfig.getOptionList;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaNil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.asdflj.nech.integration.nei.LuaToggleButton;
import com.asdflj.nech.proxy.ClientProxy;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.api.API;
import codechicken.nei.config.OptionList;
import cpw.mods.fml.relauncher.FMLInjectionData;

public class LuaPlugin {

    static Globals globals = JsePlatform.standardGlobals();
    public static List<LuaObject> list = new ArrayList<>();
    public static List<LuaToggleButton> btns = new ArrayList<>();

    public static List<LuaObject> readLuaFiles(String directoryPath) {
        List<LuaObject> result = new ArrayList<>();
        File directory = new File(directoryPath);
        // 检查目录是否存在
        if (!directory.exists() || !directory.isDirectory()) {
            directory.mkdir();
        }

        // 获取目录下所有.lua文件
        File[] luaFiles = directory.listFiles((dir, name) -> name.endsWith(".lua"));

        if (luaFiles == null) {
            return null;
        }

        // 遍历并读取每个.lua文件的内容
        for (File luaFile : luaFiles) {
            ClientProxy.LOGGER.info("读取文件: {}", luaFile.getName());
            Path path = Paths.get(luaFile.getAbsolutePath());
            try {
                result.add(new LuaObject(path));
            } catch (Exception ignored) {
                ClientProxy.LOGGER.info("无效文件: {}", luaFile.getName());
                ClientProxy.sendToPlayer("nech.message.lua_script.invalid_file", luaFile.getName());
            }
        }
        return result;
    }

    public static void loadLuaScript() {
        for (LuaObject luaObject : list) {
            if (luaObject.getBtn() != null) {
                OptionList optionList = getOptionList("lua_scripts", NEIClientConfig.getOptionList());
                if (optionList != null) {
                    optionList.optionList.remove(luaObject.getBtn());
                }
            }
        }
        list.clear();
        List<LuaObject> r = readLuaFiles(new File((File) FMLInjectionData.data()[6], "pinin").getPath());
        if (r != null) {
            list.addAll(r);
        }
    }

    public static OptionList getOptionList(String name, OptionList optionList) {
        if (optionList == null) {
            return null;
        }
        for (var o : optionList.optionList) {
            if (o instanceof OptionList l) {
                if (l.name.equals(name)) {
                    return l;
                } else {
                    OptionList result = getOptionList(name, l);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    public static void reloadLuaScript() {
        loadLuaScript();
        Match.refresh();
    }

    public static class LuaObject {

        private final LuaValue text_handler;
        private final LuaValue input_handler;
        private final LuaValue name;
        private LuaToggleButton btn;

        public LuaObject(Path path) {
            globals.loadfile(path.toString())
                .call();
            this.text_handler = globals.get("text_handler");
            this.input_handler = globals.get("input_handler");
            this.name = globals.get("name");
            String name = this.getName();
            if (name == null || name.isEmpty()) {
                return;
            }
            btn = new LuaToggleButton(name);
            API.addOption(btn);
        }

        public LuaToggleButton getBtn() {
            return btn;
        }

        public boolean getState() {
            if (btn == null) return true;
            return btn.state();
        }

        public String textHandler(String t) {
            if (this.text_handler instanceof LuaNil) {
                return t;
            }
            return this.text_handler.call(LuaValue.valueOf(t))
                .checkjstring();
        }

        public String getName() {
            if (this.name instanceof LuaNil) {
                return null;
            }
            return this.name.call()
                .checkjstring();
        }

        public Set<String> inputHandler(String s) {
            if (s.isEmpty() || this.input_handler instanceof LuaNil) return null;
            try {
                LuaTable table = this.input_handler.call(LuaValue.valueOf(s))
                    .checktable();
                Set<String> result = new HashSet<>();
                for (LuaValue key : table.keys()) {
                    LuaValue value = table.get(key);
                    if (!value.isnil()) {
                        result.add(value.checkjstring());
                    }
                }
                return result;
            } catch (Exception ignored) {
                return null;
            }
        }
    }

}
