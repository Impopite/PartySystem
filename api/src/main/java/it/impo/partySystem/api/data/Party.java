package it.impo.partySystem.api.data;

import java.util.Map;
import java.util.UUID;

public record Party(int id, UUID ownerUUID, Map<UUID, String> members) {
}
