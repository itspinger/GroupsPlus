package io.pinger.groups.dependenies;

import net.byteflux.libby.Library;

public interface Dependency {

    String groupId();

    String artifactId();

    String version();

    Library toLibrary();

}
