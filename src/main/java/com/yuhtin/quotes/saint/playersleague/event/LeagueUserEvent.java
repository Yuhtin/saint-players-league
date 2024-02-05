package com.yuhtin.quotes.saint.playersleague.event;

import com.yuhtin.quotes.saint.playersleague.model.LeagueUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Getter
public class LeagueUserEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Nullable private final Player player;
    @NotNull private final LeagueUser user;

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
