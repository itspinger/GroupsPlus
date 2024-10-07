package io.pinger.groups.storage.impl;

import io.pinger.groups.group.Group;

public interface StorageImplementation {

    void init();

    void shutdown();

    void loadAllGroups() throws Exception;

    Group createNewGroup(String name) throws Exception;

    void saveGroup(Group group) throws Exception;

    void deleteGroup(Group group) throws Exception;

}
