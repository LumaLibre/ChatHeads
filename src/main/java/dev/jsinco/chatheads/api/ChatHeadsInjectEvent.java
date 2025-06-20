package dev.jsinco.chatheads.api;

import dev.jsinco.chatheads.Handler;
import dev.jsinco.chatheads.obj.CachedPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class ChatHeadsInjectEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Player receiver;
    private Component avatar;
    private boolean reverseOrientation;
    private boolean cancelled;

    public ChatHeadsInjectEvent(@Nullable Player player, @NotNull Player receiver, @NotNull Component avatar, boolean reverseOrientation) {
        this.player = player;
        this.receiver = receiver;
        this.avatar = avatar;
        this.reverseOrientation = reverseOrientation;
        this.cancelled = false;
    }

    public @Nullable Player getPlayer() {
        return player;
    }

    public @NotNull Player getReceiver() {
        return receiver;
    }

    public void setAvatar(@NotNull Component avatar) {
        this.avatar = avatar;
    }

    public @NotNull Component getAvatar() {
        return avatar;
    }

    public void setReverseOrientation(boolean reverseOrientation) {
        this.reverseOrientation = reverseOrientation;
    }

    public boolean isReverseOrientation() {
        return reverseOrientation;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
