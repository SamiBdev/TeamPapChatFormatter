package com.todakouma.teampapchatformatter;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.user.User;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.UUID;

@Mod("teampapchatformatter")
public class TeamPapChatFormatter {
    private static LuckPerms luckPermsAPI;

    public TeamPapChatFormatter() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        try {
            luckPermsAPI = LuckPermsProvider.get();
            System.out.println("LuckPerms API initialisée avec succès !");

            EventBus eventBus = luckPermsAPI.getEventBus();
            eventBus.subscribe(UserDataRecalculateEvent.class, this::onUserDataRecalculate);
        } catch (IllegalStateException e) {
            System.err.println("Impossible d'obtenir LuckPerms API. Vérifiez que LuckPerms est installé et actif !");
        }
    }

    private void onUserDataRecalculate(UserDataRecalculateEvent event) {
        User user = event.getUser();
        ServerPlayer player = findPlayerByUUID(user.getUniqueId());
        if (player != null) {
            TabListHandler.updateTabForPlayer(player, user); // Passez le joueur et l'utilisateur LuckPerms
        }
    }

    private ServerPlayer findPlayerByUUID(UUID uuid) {
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            if (player.getUUID().equals(uuid)) {
                return player;
            }
        }
        return null;
    }

    public static LuckPerms getLuckPermsAPI() {
        return luckPermsAPI;
    }
}
