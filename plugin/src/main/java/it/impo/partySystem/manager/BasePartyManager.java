package it.impo.partySystem.manager;

import it.impo.partySystem.PartySystem;
import it.impo.partySystem.api.config.constant.LangKey;
import it.impo.partySystem.api.data.Party;
import it.impo.partySystem.api.data.PendingInvite;
import it.impo.partySystem.api.manager.PartyManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static net.kyori.adventure.text.Component.text;

public class BasePartyManager extends PartyManager {

    private final PartySystem plugin;
    private final Map<Integer, Party> partyCache = new HashMap<>();
    private final Map<UUID, PendingInvite> pendingInvites = new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    private static final long INVITE_EXPIRE_MS = 30_000L;

    public BasePartyManager(PartySystem plugin) {
        this.plugin = plugin;

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            long now = System.currentTimeMillis();
            pendingInvites.entrySet().removeIf(e -> now > e.getValue().expiresAt());
        }, 20L * 30, 20L * 30);
    }

    @Override
    public void createParty(Player owner) {
        if (playerIsInParty(owner)) {
            owner.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.ALREADY_IN_PARTY));
            return;
        }

        int id = idCounter.getAndIncrement();
        Party party = new Party(id, owner.getUniqueId(), new HashMap<>());

        plugin.getPartyTable().saveParty(owner.getUniqueId(), new HashMap<>()).thenAccept(res -> {
            if (!res) {
                owner.sendMessage(text("§cᴇʀʀᴏʀ\n §7Error on party creation. Contact a staff member."));
                return;
            }
            partyCache.put(id, party);
            owner.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.PARTY_CREATED));
        });
    }

    @Override
    public void removeParty(Player owner) {
        Party party = getPartyByOwner(owner.getUniqueId());

        if (party == null) {
            owner.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.NOT_PARTY_LEADER));
            return;
        }

        plugin.getPartyTable().removeParty(party.id()).thenAccept(success -> {
            if (!success) {
                owner.sendMessage(text("§cᴇʀʀᴏʀ\n §7Error on party removal. Contact a staff member."));
                return;
            }

            party.members().keySet().forEach(uuid -> {
                Player member = Bukkit.getPlayer(uuid);
                if (member != null) member.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.PARTY_DELETED_OTHER)
                        .replaceText(config -> config .matchLiteral("{player}").replacement(owner.getName())));
            });

            partyCache.remove(party.id());
            owner.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.PARTY_DELETED));
        });
    }

    @Override
    public void addPlayerInParty(Player owner, Player target) {
        Party party = getPartyByOwner(owner.getUniqueId());

        if (party == null) {
            owner.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.NOT_PARTY_LEADER));
            return;
        }

        if (playerIsInParty(target)) {
            owner.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.PLAYER_ALREADY_IN_PARTY));
            return;
        }

        if (pendingInvites.containsKey(target.getUniqueId())) {
            owner.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.ALREADY_INVITED));
            return;
        }

        pendingInvites.put(target.getUniqueId(), new PendingInvite(owner.getUniqueId(), System.currentTimeMillis() + INVITE_EXPIRE_MS));
        owner.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.PARTY_INVITE)
                .replaceText(config -> config .matchLiteral("{player}").replacement(target.getName())));

        Component message = plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.PARTY_INVITE_REQUEST)
                .replaceText(config -> config .matchLiteral("{player}").replacement(owner.getName()))
                .append(
                        Component.text("§a[✓] ")
                                .clickEvent(ClickEvent.callback(audience -> {
                                    if (!(audience instanceof Player p)) return;

                                    PendingInvite invite = pendingInvites.remove(p.getUniqueId());
                                    if (invite == null || System.currentTimeMillis() > invite.expiresAt()) {
                                        p.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.EXPIRED_INVITE));
                                        return;
                                    }

                                    Player ownerPlayer = Bukkit.getPlayer(invite.ownerUUID());
                                    if (ownerPlayer == null) {
                                        p.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.LEADER_OFFLINE));
                                        return;
                                    }

                                    Party targetParty = getPartyByOwner(invite.ownerUUID());
                                    if (targetParty == null) {
                                        p.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.PARTY_NOT_EXISTS));
                                        return;
                                    }

                                    targetParty.members().put(p.getUniqueId(), p.getName());

                                    plugin.getPartyTable().saveParty(targetParty.ownerUUID(), targetParty.members());

                                    p.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.PARTY_JOIN)
                                            .replaceText(config -> config .matchLiteral("{player}").replacement(ownerPlayer.getName())));
                                    ownerPlayer.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.PARTY_JOIN_OTHER)
                                            .replaceText(config -> config .matchLiteral("{player}").replacement(p.getName())));
                                }))
                )
                .append(
                        Component.text("§c[✗]")
                                .clickEvent(ClickEvent.callback(audience -> {
                                    if (!(audience instanceof Player p)) return;

                                    PendingInvite invite = pendingInvites.remove(p.getUniqueId());
                                    if (invite != null) {
                                        Player ownerPlayer = Bukkit.getPlayer(invite.ownerUUID());
                                        if (ownerPlayer != null)
                                            ownerPlayer.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.REJECT_INVITE_LEADER)
                                                    .replaceText(config -> config .matchLiteral("{player}").replacement(p.getName())));
                                    }
                                    p.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.REJECT_INVITE));
                                }))
                );

        target.sendMessage(message);
    }

    @Override
    public void removePlayerInParty(Player owner, Player target) {
        Party party = getPartyByOwner(owner.getUniqueId());

        if (party == null) {
            owner.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.NOT_PARTY_LEADER));
            return;
        }

        if (!party.members().containsKey(target.getUniqueId())) {
            owner.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.PLAYER_NOT_IN_PARTY));
            return;
        }

        party.members().remove(target.getUniqueId());
        plugin.getPartyTable().saveParty(party.ownerUUID(), party.members());

        owner.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.PLAYER_REMOVED)
                .replaceText(config -> config .matchLiteral("{player}").replacement(target.getName())));
        target.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.PLAYER_REMOVED_TARGET)
                .replaceText(config -> config .matchLiteral("{player}").replacement(owner.getName())));
    }

    @Override
    public void partyInfo(Player player) {
        Party party = getPartyByMember(player.getUniqueId());

        if (party == null) {
            player.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.NOT_IN_PARTY));
            return;
        }

        String ownerName = Bukkit.getOfflinePlayer(party.ownerUUID()).getName();

        player.sendMessage(text("§bᴘᴀʀᴛʏ ɪɴꜰᴏ"));
        player.sendMessage(text("§4Leader: §f" + (ownerName != null ? ownerName : "Unknown")));
        player.sendMessage(text("§cMembers: §7" + (party.members().isEmpty() ? "Empty" : "")));

        party.members().forEach((uuid, name) ->
                player.sendMessage(text("§7- " + name))
        );
    }

    @Override
    public void partyLeave(Player player) {
        Party party = getPartyByMember(player.getUniqueId());
        if (party == null) {
            player.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.NOT_IN_PARTY));
            return;
        }

        Player leader = Bukkit.getPlayer(party.ownerUUID());
        if(leader == null) {
            player.sendMessage(text("§cᴇʀʀᴏʀ\n\n §7Contact a staff member."));
            return;
        }

        if(leader.equals(player)) {
            removeParty(leader);
            return;
        }

        party.members().remove(player.getUniqueId());
        plugin.getPartyTable().saveParty(party.ownerUUID(), party.members());

        leader.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.PARTY_LEFT_LEADER)
                .replaceText(config -> config.matchLiteral("{player}").replacement(player.getName())));
        player.sendMessage(plugin.getConfigLoader().getLangLoader().getString(LangKey.PREFIX, LangKey.PARTY_LEFT)
                .replaceText(config -> config.matchLiteral("{player}").replacement(leader.getName())));
    }

    @Override
    public boolean playerIsInParty(Player player) {
        return partyCache.values().stream()
                .anyMatch(party ->
                        party.ownerUUID().equals(player.getUniqueId()) ||
                                party.members().containsKey(player.getUniqueId())
                );
    }

    private Party getPartyByOwner(UUID ownerUUID) {
        return partyCache.values().stream()
                .filter(p -> p.ownerUUID().equals(ownerUUID))
                .findFirst().orElse(null);
    }

    private Party getPartyByMember(UUID memberUUID) {
        return partyCache.values().stream()
                .filter(p -> p.ownerUUID().equals(memberUUID) ||
                        p.members().containsKey(memberUUID))
                .findFirst().orElse(null);
    }

    @Override
    public Map<Integer, Party> getPartyCache() {
        return Collections.unmodifiableMap(partyCache);
    }
}