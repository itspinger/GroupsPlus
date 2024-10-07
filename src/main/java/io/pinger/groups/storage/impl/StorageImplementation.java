package io.pinger.groups.storage.impl;

import io.pinger.groups.group.Group;
import io.pinger.groups.user.User;
import java.util.UUID;

public interface StorageImplementation {

    void init();

    void shutdown();

    User loadUser(UUID id) throws Exception;

    void saveUser(User user) throws Exception;

    void loadAllGroups() throws Exception;

    Group createNewGroup(String name) throws Exception;

    void saveGroup(Group group) throws Exception;

    void deleteGroup(Group group) throws Exception;

}
