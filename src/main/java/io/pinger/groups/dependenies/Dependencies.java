package io.pinger.groups.dependenies;

import net.byteflux.libby.Library;

public enum Dependencies implements Dependency {

    MARIA_DB(
        "org.mariadb.jdbc",
        "mariadb-java-client",
        "3.4.1"
    );

    private final String groupId;
    private final String artifactId;
    private final String version;

    Dependencies(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    @Override
    public String groupId() {
        return this.groupId;
    }

    @Override
    public String artifactId() {
        return this.artifactId;
    }

    @Override
    public String version() {
        return this.version;
    }

    @Override
    public Library toLibrary() {
        return Library.builder()
            .groupId(this.groupId)
            .artifactId(this.artifactId)
            .version(this.version)
            .build();
    }
}
