/*
 * Users.java
 * Copyright (C) 2008 Jorge Schrauwen
 */
package be.blackdot.ahm.beans;

/**
 *
 * @author sjorge
 * @url http://www.blackdot.be
 */
public class Users {

    private int id;
    private int host;
    private String name;
    private String password;
    private String groups;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHost() {
        return host;
    }

    public void setHost(int host) {
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }
    
    @Override
    public String toString(){
        return getName();
    }
}
