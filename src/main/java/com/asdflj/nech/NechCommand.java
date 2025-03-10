package com.asdflj.nech;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

import com.asdflj.nech.proxy.ClientProxy;
import com.asdflj.nech.utils.LuaPlugin;
import com.asdflj.nech.utils.Profiler;
import com.google.gson.GsonBuilder;

public class NechCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "nech";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 1 && "reload".equals(args[0])) {
            ClientProxy.sendToPlayer("nech.message.lua_script.reload");
            LuaPlugin.reloadLuaScript();
            ClientProxy.sendToPlayer("nech.message.lua_script.done");
        } else if (args.length == 1 && "profile".equals(args[0])) {
            Thread t = new Thread(() -> {
                sender.addChatMessage(new ChatComponentText(I18n.format("chat.start")));
                Profiler.Report r = Profiler.run();

                try {
                    FileOutputStream fos = new FileOutputStream("logs/necharacters-profiler.txt");
                    Throwable var3 = null;

                    try {
                        OutputStreamWriter osw = new OutputStreamWriter(fos);
                        osw.write(
                            (new GsonBuilder()).setPrettyPrinting()
                                .create()
                                .toJson(r));
                        osw.flush();
                        sender.addChatMessage(new ChatComponentText(I18n.format("chat.saved")));
                    } catch (Throwable var13) {
                        var3 = var13;
                        throw var13;
                    } finally {
                        if (fos != null) {
                            if (var3 != null) {
                                try {
                                    fos.close();
                                } catch (Throwable var12) {
                                    var3.addSuppressed(var12);
                                }
                            } else {
                                fos.close();
                            }
                        }

                    }
                } catch (IOException var15) {
                    sender.addChatMessage(new ChatComponentText(I18n.format("chat.save_error", new Object[0])));
                }

            });
            t.setPriority(1);
            t.start();
        } else if (args.length == 2 && "verbose".equals(args[0])) {
            switch (args[1].toLowerCase()) {
                case "true":
                    NechConfig.enableVerbose = true;
                    break;
                case "false":
                    NechConfig.enableVerbose = false;
                    break;
                default:
                    sender.addChatMessage(new ChatComponentTranslation("command.unknown", new Object[0]));
            }
        } else if (args.length == 2 && "keyboard".equals(args[0])) {
            switch (args[1].toLowerCase()) {
                case "quanpin":
                    NechConfig.setKeyboard(NechConfig.Spell.QUANPIN);
                    break;
                case "daqian":
                    NechConfig.setKeyboard(NechConfig.Spell.DAQIAN);
                    break;
                case "xiaohe":
                    NechConfig.setKeyboard(NechConfig.Spell.XIAOHE);
                    break;
                case "ziranma":
                    NechConfig.setKeyboard(NechConfig.Spell.ZIRANMA);
                    break;
                case "sougou":
                    NechConfig.setKeyboard(NechConfig.Spell.SOUGOU);
                    break;
                case "guobiao":
                    NechConfig.setKeyboard(NechConfig.Spell.GUOBIAO);
                    break;
                case "microsoft":
                    NechConfig.setKeyboard(NechConfig.Spell.MICROSOFT);
                    break;
                case "pinyinjiajia":
                    NechConfig.setKeyboard(NechConfig.Spell.PINYINPP);
                    break;
                case "ziguang":
                    NechConfig.setKeyboard(NechConfig.Spell.ZIGUANG);
                    break;
                default:
                    sender.addChatMessage(new ChatComponentTranslation("command.unknown"));
            }
        } else {
            sender.addChatMessage(new ChatComponentTranslation("command.unknown"));
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "profile", "verbose", "keyboard", "reload");
        } else if (args.length == 2 && "verbose".equals(args[0])) {
            return getListOfStringsMatchingLastWord(args, "true", "false");
        } else {
            return args.length == 2 && "keyboard".equals(args[0]) ? getListOfStringsMatchingLastWord(
                args,
                "quanpin",
                "daqian",
                "xiaohe",
                "ziranma",
                "sougou",
                "guobiao",
                "microsoft",
                "pinyinjiajia",
                "ziguang") : null;
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 1;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
