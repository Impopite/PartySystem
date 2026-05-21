package it.impo.partySystem.api.config.constant;

import lombok.Getter;

@Getter
public enum LangKey {

    NO_PERMISSION("message.no-permission"),
    UNKNOWN_COMMAND("message.unknown-command"),
    CONSOLE_CANT_DO("message.console-cant-do-command"),
    PLAYER_NOT_FOUND("message.player-not-found"),

    PARTY_CREATED("party.party-created"),
    PARTY_DELETED("party.party-deleted"),
    PARTY_DELETED_OTHER("party.party-deleted-other"),

    PARTY_INVITE("party.party-invite"),
    PARTY_INVITE_REQUEST("party.party-invite-request"),
    ALREADY_INVITED("party.already-invited"),
    EXPIRED_INVITE("party.expired-invite"),
    REJECT_INVITE("party.reject-invite"),
    REJECT_INVITE_LEADER("party.reject-invite-leader"),

    PARTY_JOIN("party.party-join"),
    PARTY_JOIN_TARGET("party.party-join-target"),
    PARTY_JOIN_OTHER("party.party-join-other"),
    PARTY_LEFT("party.party-left"),

    PLAYER_REMOVED("party.player-removed"),
    PLAYER_REMOVED_TARGET("party.player-removed-target"),
    PLAYER_NOT_IN_PARTY("party.player-not-in-party"),

    ALREADY_IN_PARTY("party.already-in-party"),
    PLAYER_ALREADY_IN_PARTY("party.player-already-in-party"),
    NOT_IN_PARTY("party.not-in-party"),
    NOT_PARTY_LEADER("party.not-party-leader"),
    NOT_OWNER("party.not-owner"),
    LEADER_OFFLINE("party.leader-offline"),
    PARTY_NOT_EXISTS("party.party-not-exists"),

    PREFIX("prefix");

    private final String path;

    LangKey(String path) {
        this.path = path;
    }
}
