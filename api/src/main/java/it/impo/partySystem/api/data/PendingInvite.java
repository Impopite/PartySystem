package it.impo.partySystem.api.data;

import java.util.UUID;

public record PendingInvite(UUID ownerUUID, long expiresAt) {

}
