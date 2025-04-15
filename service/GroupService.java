package com.chatapp.service;

import com.chatapp.exception.GroupNotFoundException;
import com.chatapp.model.Group;
import com.chatapp.model.JoinRequest;
import com.chatapp.model.User;
import com.chatapp.util.DataPersistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GroupService {
    private final DataPersistence dataPersistence;
    private final UserService userService;
    
    public GroupService(DataPersistence dataPersistence, UserService userService) {
        this.dataPersistence = dataPersistence;
        this.userService = userService;
    }

    public Group createGroup(String groupName, String adminUsername) {
        String groupId = UUID.randomUUID().toString();
        Group group = new Group(groupId, groupName, adminUsername);
        
        // Save group
        Map<String, Group> groups = dataPersistence.loadGroups();
        groups.put(groupId, group);
        dataPersistence.saveGroups(groups);
        
        // Add group to user's group list
        User admin = userService.getUserByUsername(adminUsername);
        if (admin != null) {
            admin.addGroup(groupId);
            userService.saveUser(admin);
        }
        
        return group;
    }

    public Group getGroupById(String groupId) throws GroupNotFoundException {
        Map<String, Group> groups = dataPersistence.loadGroups();
        Group group = groups.get(groupId);
        
        if (group == null) {
            throw new GroupNotFoundException("Group with ID " + groupId + " not found.");
        }
        
        return group;
    }

    public List<Group> getUserGroups(String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return new ArrayList<>();
        }
        
        List<String> groupIds = user.getGroupIds();
        Map<String, Group> allGroups = dataPersistence.loadGroups();
        
        return groupIds.stream()
                .filter(allGroups::containsKey)
                .map(allGroups::get)
                .collect(Collectors.toList());
    }

    public List<Group> searchGroups(String searchTerm) {
        Map<String, Group> groups = dataPersistence.loadGroups();
        
        return groups.values().stream()
                .filter(g -> g.getGroupName().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }

    public boolean requestToJoinGroup(String groupId, String username) throws GroupNotFoundException {
        Group group = getGroupById(groupId);
        
        // Check if user is already a member
        if (group.isMember(username)) {
            return false;
        }

        if (group.hasPendingRequest(username)) {
            return false;
        }

        String requestId = UUID.randomUUID().toString();
        JoinRequest request = new JoinRequest(requestId, groupId, username);
        group.addJoinRequest(request);

        Map<String, Group> groups = dataPersistence.loadGroups();
        groups.put(groupId, group);
        dataPersistence.saveGroups(groups);
        
        return true;
    }
    

    public boolean acceptJoinRequest(String groupId, String requestorUsername, String adminUsername) 
            throws GroupNotFoundException {
        Group group = getGroupById(groupId);

        if (!group.isAdmin(adminUsername)) {
            return false;
        }

        if (!group.hasPendingRequest(requestorUsername)) {
            return false;
        }

        group.removeJoinRequest(requestorUsername);
        group.addMember(requestorUsername);
        
        // Save updated group
        Map<String, Group> groups = dataPersistence.loadGroups();
        groups.put(groupId, group);
        dataPersistence.saveGroups(groups);
        
        // Add group to user's group list
        User user = userService.getUserByUsername(requestorUsername);
        if (user != null) {
            user.addGroup(groupId);
            userService.saveUser(user);
        }
        
        return true;
    }

    public boolean rejectJoinRequest(String groupId, String requestorUsername, String adminUsername) 
            throws GroupNotFoundException {
        Group group = getGroupById(groupId);

        if (!group.isAdmin(adminUsername)) {
            return false;
        }

        if (!group.hasPendingRequest(requestorUsername)) {
            return false;
        }

        group.removeJoinRequest(requestorUsername);

        Map<String, Group> groups = dataPersistence.loadGroups();
        groups.put(groupId, group);
        dataPersistence.saveGroups(groups);
        
        return true;
    }

    public boolean removeMemberFromGroup(String groupId, String memberUsername, String adminUsername) 
            throws GroupNotFoundException {
        Group group = getGroupById(groupId);

        if (!group.isAdmin(adminUsername)) {
            return false;
        }

        if (group.isAdmin(memberUsername)) {
            return false;
        }

        if (!group.isMember(memberUsername)) {
            return false;
        }

        group.removeMember(memberUsername);

        Map<String, Group> groups = dataPersistence.loadGroups();
        groups.put(groupId, group);
        dataPersistence.saveGroups(groups);

        User user = userService.getUserByUsername(memberUsername);
        if (user != null) {
            user.removeGroup(groupId);
            userService.saveUser(user);
        }
        
        return true;
    }

    public boolean deleteGroup(String groupId, String adminUsername) throws GroupNotFoundException {
        Group group = getGroupById(groupId);

        if (!group.isAdmin(adminUsername)) {
            return false;
        }

        for (String memberUsername : group.getMembers()) {
            User member = userService.getUserByUsername(memberUsername);
            if (member != null) {
                member.removeGroup(groupId);
                userService.saveUser(member);
            }
        }

        Map<String, Group> groups = dataPersistence.loadGroups();
        groups.remove(groupId);
        dataPersistence.saveGroups(groups);
        
        return true;
    }
    

    public List<JoinRequest> getPendingJoinRequests(String groupId, String adminUsername) 
            throws GroupNotFoundException {
        Group group = getGroupById(groupId);
        
        // Check if user is admin
        if (!group.isAdmin(adminUsername)) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(group.getPendingRequests());
    }
}
