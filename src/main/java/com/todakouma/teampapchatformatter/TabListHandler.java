package com.todakouma.teampapchatformatter;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.network.chat.TextColor;

@Mod.EventBusSubscriber(modid = "teampapchatformatter")
public class TabListHandler {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        User user = TeamPapChatFormatter.getLuckPermsAPI().getUserManager().getUser(player.getUUID());
        if (user != null) {
            updateTabForPlayer(player, user);
        }
    }

    public static void updateTabForPlayer(ServerPlayer player, User user) {
        LuckPerms api = TeamPapChatFormatter.getLuckPermsAPI();
        if (api == null) {
            System.err.println("LuckPerms API n'est pas initialisée !");
            return;
        }

        String prefix = user.getCachedData().getMetaData().getPrefix();
        prefix = prefix != null ? prefix : "";

        MutableComponent formattedPrefix = convertToFormattedComponent(prefix);

        Scoreboard scoreboard = player.getScoreboard();
        String teamName = player.getUUID().toString().substring(0, 16);
        PlayerTeam team = scoreboard.getPlayerTeam(teamName);
        if (team == null) {
            team = scoreboard.addPlayerTeam(teamName);
        }

        team.setPlayerPrefix(formattedPrefix.append(Component.literal(" ")));
        scoreboard.addPlayerToTeam(player.getScoreboardName(), team);
    }

    public static MutableComponent convertToFormattedComponent(String input) {
        MutableComponent component = Component.literal("");
        StringBuilder currentText = new StringBuilder();
        Style currentStyle = Style.EMPTY;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '&' && i + 1 < input.length()) {
                if (currentText.length() > 0) {
                    component.append(Component.literal(currentText.toString()).setStyle(currentStyle));
                    currentText.setLength(0);
                }

                char code = input.charAt(i + 1);
                currentStyle = applyStyleFromCode(currentStyle, code);
                i++;
            } else {
                currentText.append(c);
            }
        }

        if (currentText.length() > 0) {
            component.append(Component.literal(currentText.toString()).setStyle(currentStyle));
        }

        return component;
    }

    private static Style applyStyleFromCode(Style style, char code) {
        switch (code) {
            case '0': return style.withColor(TextColor.fromRgb(0x000000));
            case '1': return style.withColor(TextColor.fromRgb(0x0000AA));
            case '2': return style.withColor(TextColor.fromRgb(0x00AA00));
            case '3': return style.withColor(TextColor.fromRgb(0x00AAAA));
            case '4': return style.withColor(TextColor.fromRgb(0xAA0000));
            case '5': return style.withColor(TextColor.fromRgb(0xAA00AA));
            case '6': return style.withColor(TextColor.fromRgb(0xFFAA00));
            case '7': return style.withColor(TextColor.fromRgb(0xAAAAAA));
            case '8': return style.withColor(TextColor.fromRgb(0x555555));
            case '9': return style.withColor(TextColor.fromRgb(0x5555FF));
            case 'a': return style.withColor(TextColor.fromRgb(0x55FF55));
            case 'b': return style.withColor(TextColor.fromRgb(0x55FFFF));
            case 'c': return style.withColor(TextColor.fromRgb(0xFF5555));
            case 'd': return style.withColor(TextColor.fromRgb(0xFF55FF));
            case 'e': return style.withColor(TextColor.fromRgb(0xFFFF55));
            case 'f': return style.withColor(TextColor.fromRgb(0xFFFFFF));
            case 'k': return style.withObfuscated(true);
            case 'l': return style.withBold(true);
            case 'm': return style.withStrikethrough(true);
            case 'n': return style.withUnderlined(true);
            case 'o': return style.withItalic(true);
            case 'r': return Style.EMPTY;
            default: return style;
        }
    }
}
