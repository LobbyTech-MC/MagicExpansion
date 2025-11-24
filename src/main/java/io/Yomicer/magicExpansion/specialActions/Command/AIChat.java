package io.Yomicer.magicExpansion.specialActions.Command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import io.Yomicer.magicExpansion.utils.aiManager.AIManager;



public class AIChat implements CommandExecutor,TabCompleter {

    private final AIManager aiManager;

    public AIChat(AIManager aiManager) {
        this.aiManager = aiManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("只有玩家可以使用此命令。");
            return true;
        }

        Player player = (Player) sender;
        // ✅ 1. 检查基础权限 mxai.use
        if (!player.hasPermission("mxai.use")) {
            player.sendMessage("你没有使用AI命令的权限！");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("chaton")) {
            aiManager.enableAI(player);
            player.sendMessage("§a✅ 已进入AI对话模式（私聊）");
        } else if (sub.equals("chatoff")) {
            aiManager.disableAI(player);
            player.sendMessage("§b❌ 已退出AI对话模式");
        } else if (sub.equals("public") && args.length > 1) {
            // /mxai public 你好
            if (aiManager.getPublicMode()) {
                String msg = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                aiManager.askAIPublic(player, msg);
            }else{
                Bukkit.broadcastMessage("玩家 " + player.getName() + " 想问AI：\"" + String.join(" ", Arrays.copyOfRange(args, 1, args.length)) + "\"");
                player.sendMessage("❌ 全服AI模式未开启，请联系管理员使用 /mxai publicmode on 开启");
            }
        }else if (sub.equals("publicmode")) {
            // ✅ 检查OP权限
            if (!player.hasPermission("mxai.op") && !player.isOp()) {
                player.sendMessage("§c你没有权限使用此命令！");
                return true;
            }

            if (args.length == 1) {
                // 查看状态
                boolean isPublic = aiManager.getPublicMode();
                player.sendMessage("§6【AI管理】全服模式: " + (isPublic ? "§a开启" : "§c关闭"));
                return true;
            }

            String mode = args[1].toLowerCase();
            if (mode.equals("on")) {
                aiManager.setPublicMode(true);
                Bukkit.broadcastMessage("📢 §a全服AI聊天已开启！所有人可使用 §6/mxai public §a参与对话。");
            } else if (mode.equals("off")) {
                aiManager.setPublicMode(false);
                Bukkit.broadcastMessage("📢 §c全服AI聊天已关闭，恢复为私聊模式。");
            } else {
                player.sendMessage("§c用法: /mxai publicmode <on|off>");
            }

        } else {
            sendHelp(player);
        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage("§6========== §bAI 聊天助手 §6==========");
        player.sendMessage("§7/mxai chaton §f- 开启AI对话");
        player.sendMessage("§7/mxai chatoff §f- 关闭AI对话");
        player.sendMessage("§7/mxai public §a对话内容 §f- 与全服AI对话");
        if (player.hasPermission("mxai.op") || player.isOp()) {
            player.sendMessage("§c/mxai publicmode <on|off> §7- §c[OP] 控制全服AI模式");
        }
        player.sendMessage("§6==================================");
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("chaton", "chatoff", "public", "publicmode");
        }
        return Collections.emptyList();
    }


}
