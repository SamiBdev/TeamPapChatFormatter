package com.todakouma.teampapchatformatter;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ChatEventHandler {

    @SubscribeEvent
    public static void onChatMessage(ServerChatEvent event) {
        LuckPerms api = TeamPapChatFormatter.getLuckPermsAPI();
        if (api == null) {
            System.err.println("LuckPerms API n'est pas initialisée !");
            return;
        }

        User user = api.getUserManager().getUser(event.getPlayer().getUUID());
        if (user != null) {
            String prefix = user.getCachedData().getMetaData().getPrefix();
            prefix = prefix != null ? prefix : "";

            MutableComponent formattedPrefix = convertToFormattedComponent(prefix);

            String originalMessage = event.getMessage().getString();
            String playerName = event.getPlayer().getName().getString();

            MutableComponent newMessage = Component.literal("")
                    .append(formattedPrefix)
                    .append(Component.literal(" " + playerName).withStyle(style -> style.withColor(TextColor.fromRgb(0xFFFFFF)).withBold(true)))
                    .append(Component.literal(": " + originalMessage).withStyle(style -> style.withColor(TextColor.fromRgb(0xAAAAAA))));

            event.setCanceled(true);

            ServerPlayer player = event.getPlayer();
            MinecraftServer server = player.getServer();
            if (server != null) {
                server.getPlayerList().getPlayers().forEach(p -> p.sendSystemMessage(newMessage));
            }
        } else {
            System.out.println("Impossible de récupérer les données de l'utilisateur LuckPerms.");
        }
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