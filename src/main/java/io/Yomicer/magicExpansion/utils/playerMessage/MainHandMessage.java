package io.Yomicer.magicExpansion.utils.playerMessage;

import org.bukkit.entity.Player;

import io.Yomicer.magicExpansion.utils.ColorGradient;

public class MainHandMessage {
    public static void sendMainHandMessage(Player p){
        p.sendMessage(ColorGradient.getGradientNameVer2("请放在主手使用！"));
    }
}
