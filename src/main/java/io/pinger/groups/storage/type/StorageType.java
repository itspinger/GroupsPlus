package io.pinger.groups.storage.type;

public enum StorageType {

    MARIADB("mariadb"),
    MYSQL("mysql"),
    UNKNOWN("unknown");

    private final String identifier;

    StorageType(String identifier) {
        this.identifier = identifier;
    }

    public static StorageType fromIdentifier(String identifier) {
        for (final StorageType storageType : StorageType.values()) {
            if (storageType.identifier.equals(identifier)) {
                return storageType;
            }
        }
        return UNKNOWN;
    }

    public String getIdentifier() {
        return this.identifier;
    }
}
