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

/**
 * Service class for group-related operations.
 */
public class GroupService {
    private final DataPersistence dataPersistence;
    private final UserService userService;
    
    public GroupService(DataPersistence dataPersistence, UserService userService) {
        this.dataPersistence = dataPersistence;
        this.userService = userService;
    }
    
    /**
     * Create a new group with the given name and admin.
     * 
     * @param groupName name of the group
     * @param adminUsername username of the group admin
     * @return the created Group object
     */
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
    
    /**
     * Get a group by its ID.
     * 
     * @param groupId the group ID
     * @return the Group object
     * @throws GroupNotFoundException if the group doesn't exist
     */
    public Group getGroupById(String groupId) throws GroupNotFoundException {
        Map<String, Group> groups = dataPersistence.loadGroups();
        Group group = groups.get(groupId);
        
        if (group == null) {
            throw new GroupNotFoundException("Group with ID " + groupId + " not found.");
        }
        
        return group;
    }
    
    /**
     * Get all groups a user is a member of.
     * 
     * @param username the username to find groups for
     * @return list of groups the user is a member of
     */
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
    
    /**
     * Search for groups by partial name.
     * 
     * @param searchTerm the partial name to search for
     * @return list of matching groups
     */
    public List<Group> searchGroups(String searchTerm) {
        Map<String, Group> groups = dataPersistence.loadGroups();
        
        return groups.values().stream()
                .filter(g -> g.getGroupName().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Send a request to join a group.
     * 
     * @param groupId the ID of the group to join
     * @param username the username of the requestor
     * @return true if request was sent, false if user is already a member or has a pending request
     * @throws GroupNotFoundException if the group doesn't exist
     */
    public boolean requestToJoinGroup(String groupId, String username) throws GroupNotFoundException {
        Group group = getGroupById(groupId);
        
        // Check if user is already a member
        if (group.isMember(username)) {
            return false;
        }
        
        // Check if user already has a pending request
        if (group.hasPendingRequest(username)) {
            return false;
        }
        
        // Create and add join request
        String requestId = UUID.randomUUID().toString();
        JoinRequest request = new JoinRequest(requestId, groupId, username);
        group.addJoinRequest(request);
        
        // Save updated group
        Map<String, Group> groups = dataPersistence.loadGroups();
        groups.put(groupId, group);
        dataPersistence.saveGroups(groups);
        
        return true;
    }
    
    /**
     * Accept a join request for a group.
     * 
     * @param groupId the ID of the group
     * @param requestorUsername the username of the requestor
     * @param adminUsername the username of the admin accepting the request
     * @return true if request was accepted, false otherwise
     * @throws GroupNotFoundException if the group doesn't exist
     */
    public boolean acceptJoinRequest(String groupId, String requestorUsername, String adminUsername) 
            throws GroupNotFoundException {
        Group group = getGroupById(groupId);
        
        // Check if user is admin
        if (!group.isAdmin(adminUsername)) {
            return false;
        }
        
        // Check if request exists
        if (!group.hasPendingRequest(requestorUsername)) {
            return false;
        }
        
        // Remove the request and add user to group
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
    
    /**
     * Reject a join request for a group.
     * 
     * @param groupId the ID of the group
     * @param requestorUsername the username of the requestor
     * @param adminUsername the username of the admin rejecting the request
     * @return true if request was rejected, false otherwise
     * @throws GroupNotFoundException if the group doesn't exist
     */
    public boolean rejectJoinRequest(String groupId, String requestorUsername, String adminUsername) 
            throws GroupNotFoundException {
        Group group = getGroupById(groupId);
        
        // Check if user is admin
        if (!group.isAdmin(adminUsername)) {
            return false;
        }
        
        // Check if request exists
        if (!group.hasPendingRequest(requestorUsername)) {
            return false;
        }
        
        // Remove the request
        group.removeJoinRequest(requestorUsername);
        
        // Save updated group
        Map<String, Group> groups = dataPersistence.loadGroups();
        groups.put(groupId, group);
        dataPersistence.saveGroups(groups);
        
        return true;
    }
    
    /**
     * Remove a member from a group.
     * 
     * @param groupId the ID of the group
     * @param memberUsername the username of the member to remove
     * @param adminUsername the username of the admin removing the member
     * @return true if member was removed, false otherwise
     * @throws GroupNotFoundException if the group doesn't exist
     */
    public boolean removeMemberFromGroup(String groupId, String memberUsername, String adminUsername) 
            throws GroupNotFoundException {
        Group group = getGroupById(groupId);
        
        // Check if user is admin
        if (!group.isAdmin(adminUsername)) {
            return false;
        }
        
        // Cannot remove admin
        if (group.isAdmin(memberUsername)) {
            return false;
        }
        
        // Check if user is a member
        if (!group.isMember(memberUsername)) {
            return false;
        }
        
        // Remove member from group
        group.removeMember(memberUsername);
        
        // Save updated group
        Map<String, Group> groups = dataPersistence.loadGroups();
        groups.put(groupId, group);
        dataPersistence.saveGroups(groups);
        
        // Remove group from user's group list
        User user = userService.getUserByUsername(memberUsername);
        if (user != null) {
            user.removeGroup(groupId);
            userService.saveUser(user);
        }
        
        return true;
    }
    
    /**
     * Delete a group.
     * 
     * @param groupId the ID of the group to delete
     * @param adminUsername the username of the admin deleting the group
     * @return true if group was deleted, false otherwise
     * @throws GroupNotFoundException if the group doesn't exist
     */
    public boolean deleteGroup(String groupId, String adminUsername) throws GroupNotFoundException {
        Group group = getGroupById(groupId);
        
        // Check if user is admin
        if (!group.isAdmin(adminUsername)) {
            return false;
        }
        
        // Remove group from all members' group lists
        for (String memberUsername : group.getMembers()) {
            User member = userService.getUserByUsername(memberUsername);
            if (member != null) {
                member.removeGroup(groupId);
                userService.saveUser(member);
            }
        }
        
        // Delete group from groups map
        Map<String, Group> groups = dataPersistence.loadGroups();
        groups.remove(groupId);
        dataPersistence.saveGroups(groups);
        
        return true;
    }
    
    /**
     * Get all pending join requests for a group.
     * 
     * @param groupId the ID of the group
     * @param adminUsername the username of the admin viewing the requests
     * @return list of pending join requests
     * @throws GroupNotFoundException if the group doesn't exist
     */
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
