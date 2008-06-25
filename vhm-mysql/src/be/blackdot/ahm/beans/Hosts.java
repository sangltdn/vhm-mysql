/*
 * Hosts.java
 * Copyright (C) 2008 Jorge Schrauwen
 */
package be.blackdot.ahm.beans;

import java.util.*;

/**
 *
 * @author jorge
 * @url http://www.blackdot.be
 */
public class Hosts {

    private int id;
    private String name;
    private String configuration;
    private boolean enabled;
    private boolean webdav;
    private boolean ftp;
    private boolean cgi;
    private boolean ssi;
    private boolean php;
    private Vector<Users> users = new Vector<Users>();
    private Vector<Aliases> aliases = new Vector<Aliases>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isWebdav() {
        return webdav;
    }

    public void setWebdav(boolean webdav) {
        this.webdav = webdav;
    }

    public boolean isFtp() {
        return ftp;
    }

    public void seFtp(boolean ftp) {
        this.ftp = ftp;
    }    
    
    public boolean isCgi() {
        return cgi;
    }

    public void setCgi(boolean cgi) {
        this.cgi = cgi;
    }

    public boolean isSsi() {
        return ssi;
    }

    public void setSsi(boolean ssi) {
        this.ssi = ssi;
    }

    public boolean isPhp() {
        return php;
    }

    public void setPhp(boolean php) {
        this.php = php;
    }

    public Vector<Users> getUsers() {
        return users;
    }

    public void setUsers(Vector<Users> users) {
        this.users = users;
    }

    public Vector<Aliases> getAliases() {
        return aliases;
    }

    public void setAliases(Vector<Aliases> aliases) {
        this.aliases = aliases;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    @Override
    public String toString() {
        return getName();
    }
}
