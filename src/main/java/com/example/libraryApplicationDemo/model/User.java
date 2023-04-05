package com.example.libraryApplicationDemo.model;

import java.util.UUID;

public class User {
    UUID id;
    String username ;
    String password ;
    String assignedNode;
    Integer port ;
    public User( String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(UUID id, String username, String password, String assignedNode, Integer port) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.assignedNode = assignedNode;
        this.port = port;
    }

    public  User (){}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAssignedNode() {
        return assignedNode;
    }

    public void setAssignedNode(String assignedNode) {
        this.assignedNode = assignedNode;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
