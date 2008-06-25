/*
 * DAHosts.java
 * Copyright (C) 2008 Jorge Schrauwen
 */
package be.blackdot.ahm.dataaccess;

/**
 *
 * @author sjorge
 * @url http://www.blackdot.be
 */
import java.util.*;
import java.sql.*;
import be.blackdot.ahm.beans.*;

public class DAHosts {

    private Connection connection = null;

    /**
     * Creates a new instance of DAHosts
     */
    public DAHosts(String url, String login, String password, String driver)
            throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        connection = DriverManager.getConnection(url, login, password);
    }

    /**
     * Retrieve the additional configuration code
     * 
     * @param host id of host
     * @return configuration code as string
     */
    private String getConfiguration(int host) {
        //SQL-Statement
        String sql = String.format("SELECT configuration FROM configuration WHERE id = %d;", host);

        //data access
        Statement stmt = null;
        ResultSet rs = null;
        String cfg = "";	//initialiseren want anders compilerfout!
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            rs.first();
            cfg = rs.getString("configuration");
        } catch (SQLException ex) {
            cfg = "";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
            }
        }
        return cfg;
    }

    /**
     * Retrieve vector with users for host
     * 
     * @param host id of host
     * @return vector of Users
     */
    private Vector<Users> getUsers(int host) {
        //SQL-Statement
        String sql = String.format("SELECT * FROM users WHERE host = %d;", host);

        //data access
        Statement stmt = null;
        ResultSet rs = null;
        Users u;
        Vector<Users> users = new Vector<Users>();
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                u = new Users();
                u.setId(rs.getInt("id"));
                u.setHost(rs.getInt("host"));
                u.setName(rs.getString("name"));
                u.setPassword(rs.getString("password"));
                u.setGroups(rs.getString("groups"));
                users.add(u);
            }
        } catch (SQLException ex) {
            users = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
            }
        }
        return users;
    }

    /**
     * Retrieve vector with aliasses for host
     * 
     * @param host id of host
     * @return vector of Aliasses
     */
    private Vector<Aliases> getAliases(int host) {
        //SQL-Statement
        String sql = String.format("SELECT * FROM aliases WHERE id = %d;", host);

        //data access
        Statement stmt = null;
        ResultSet rs = null;
        Aliases a;
        Vector<Aliases> aliases = new Vector<Aliases>();
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                a = new Aliases();
                a.setId(rs.getInt("id"));
                a.setAlias(rs.getString("alias"));
                aliases.add(a);
            }
        } catch (SQLException ex) {
            aliases = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
            }
        }
        return aliases;
    }

    /**
     * Get Hosts object based on sql
     * 
     * @param sql sql to select host from
     * @return Hosts object
     */
    private Hosts getHostWithSQL(String sql) {
        //data access
        Statement stmt = null;
        ResultSet rs = null;
        Hosts h = null;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            rs.first();

            h = new Hosts();
            h.setId(rs.getInt("id"));
            h.setName(rs.getString("name"));
            h.setConfiguration(getConfiguration(h.getId()));
            h.setEnabled(rs.getBoolean("enabled"));
            h.setWebdav(rs.getBoolean("webdav"));
            h.setFtp(rs.getBoolean("ftp"));
            h.setCgi(rs.getBoolean("cgi"));
            h.setSsi(rs.getBoolean("ssi"));
            h.setPhp(rs.getBoolean("php"));
            h.setUsers(getUsers(h.getId()));
            h.setAliases(getAliases(h.getId()));
        } catch (SQLException ex) {
            h = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
            }
        }
        return h;
    }

    /**
     * Retrieve vector with all hosts
     * 
     * @return vector of Hosts
     */
    public Vector<Hosts> getHosts() {
        //SQL-Statement
        String sql = "SELECT * FROM hosts;";

        Statement stmt = null;
        ResultSet rs = null;
        Hosts h;
        Vector<Hosts> resultaat = new Vector<Hosts>();
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                h = new Hosts();
                h.setId(rs.getInt("id"));
                h.setName(rs.getString("name"));
                h.setConfiguration(getConfiguration(h.getId()));
                h.setEnabled(rs.getBoolean("enabled"));
                h.setWebdav(rs.getBoolean("webdav"));
                h.setFtp(rs.getBoolean("ftp"));
                h.setCgi(rs.getBoolean("cgi"));
                h.setSsi(rs.getBoolean("ssi"));
                h.setPhp(rs.getBoolean("php"));
                h.setUsers(getUsers(h.getId()));
                h.setAliases(getAliases(h.getId()));
                resultaat.add(h);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
            }
        }
        return resultaat;
    }

    /**
     * Retrieve host by id
     * 
     * @param host id of host
     * @return Hosts object for host
     */
    public Hosts getHost(int id) {
        //SQL-Statement
        String sql = String.format("SELECT * FROM hosts WHERE id = %d;", id);

        return getHostWithSQL(sql);
    }

    /**
     * Retrieve host by name
     * 
     * @param name FQDN for host
     * @return Hosts object for host
     */
    public Hosts getHost(String name) {
        //SQL-Statement
        String sql = String.format("SELECT * FROM hosts WHERE name = '%s';", name);

        return getHostWithSQL(sql);
    }

    /**
     * Returns host count
     * 
     * @return total number of hosts
     */
    public int getHostCount() {
        //SQL-Statement
        String sql = "SELECT count(id) FROM hosts;";

        //data access
        Statement stmt = null;
        ResultSet rs = null;
        int count = 0;	//initialiseren want anders compilerfout!
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            rs.first();
            count = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
            }
        }
        return count;
    }

    /**
     * Update a host
     * 
     * @param id host id of host to update
     * @param enabled true to enable host
     * @param webdav true to activate webdav
     * @param ftp true to activate ftp
     * @param php allow php to be used
     * @param ssi allow ssi to be used
     * @param cgi allow cgi/perl to be used
     * @return true if successful
     */
    public boolean updateHost(int id, boolean enabled,
                              boolean webdav, boolean ftp, boolean php,
                              boolean ssi, boolean cgi) {

        //locals
        boolean r = false;

        //SQL-Statement
        String sql = "UPDATE hosts SET enabled = ?, webdav = ?, ftp = ?, cgi = ?, ssi = ?, php = ? WHERE id = ?;";

        //data access
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setBoolean(1, enabled);
            stmt.setBoolean(2, webdav);
            stmt.setBoolean(3, ftp);
            stmt.setBoolean(4, cgi);
            stmt.setBoolean(5, ssi);
            stmt.setBoolean(6, php);
            stmt.setInt(7, id);
            r = (stmt.executeUpdate() >= 1);

            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return r;
        }
    }

    /**
     * Create a host
     * 
     * @param name FQDN for host
     * @param enabled true to enable host
     * @param webdav true to activate webdav
     * @param ftp true to activate ftp
     * @param php allow php to be used
     * @param ssi allow ssi to be used
     * @param cgi allow cgi/perl to be used
     * @return true if successful
     */
    public boolean createHost(String naam, boolean enabled,
                              boolean webdav, boolean ftp, boolean php,
                              boolean ssi, boolean cgi) {

        //locals
        boolean r = false;

        //SQL-Statement
        String sql = "INSERT INTO hosts VALUES(null, ?, ?, ?, ?, ?, ?, ?);";

        //data access
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, naam);
            stmt.setBoolean(2, enabled);
            stmt.setBoolean(3, webdav);
            stmt.setBoolean(4, ftp);
            stmt.setBoolean(5, cgi);
            stmt.setBoolean(6, ssi);
            stmt.setBoolean(7, php);
            r = (stmt.executeUpdate() >= 1);

            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return r;
        }
    }

    /**
     * Delete host by id
     * 
     * @param id host id
     * @return true if successful
     */
    public boolean deleteHost(int id) {
        //locals
        boolean r = false;

        //SQL-Statement
        String[] sql = {
            "DELETE FROM hosts WHERE id = ? LIMIT 1;",
            "DELETE FROM aliases WHERE id = ?;",
            "DELETE FROM configuration WHERE id = ? LIMIT 1;",
            "DELETE FROM users WHERE host = ?;",
        };

        //data access
        PreparedStatement stmt = null;
        for (int i = 0; i < sql.length; i++) {
            try {
                stmt = connection.prepareStatement(sql[i]);
                stmt.setInt(1, id);
                r = (stmt.executeUpdate() >= 1);

                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return r;
    }

    /**
     * Update additional configuration for host
     * 
     * @param id host id
     * @param cfg additional configuration
     * @return true if successful
     */
    public boolean updateHostCfg(int id, String cfg) {
        boolean r = deleteHostCfg(id);
        if (!cfg.equals("") && r) {
            r = createHostCfg(id, cfg);
        }

        return r;
    }

    /**
     * Create additional configuration for host
     * 
     * @param id host id
     * @param cfg additional configuration
     * @return true if successful
     */
    public boolean createHostCfg(int id, String cfg) {
        //locals
        boolean r = false;

        //SQL-Statement
        String sql = "INSERT INTO configuration VALUES(?, ?);";

        //data access
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.setString(2, cfg);
            r = (stmt.executeUpdate() >= 1);

            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return r;
        }
    }

    /**
     * Delete additional configuration for host
     * 
     * @param id host id
     * @return true if successful
     */
    public boolean deleteHostCfg(int id) {
        //locals
        boolean r = false;

        //SQL-Statement
        String sql = "DELETE FROM configuration WHERE id = ? LIMIT 1;";

        //data access
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            r = (stmt.executeUpdate() >= 1);

            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return r;
        }
    }

    /**
     * Create alias for host
     * 
     * @param id host id
     * @param alias FQDN of alias
     * @return true if successful
     */
    public boolean createHostAlias(int id, String alias) {
        //locals
        boolean r = false;

        //SQL-Statement
        String sql = "INSERT INTO aliases VALUES(?, ?);";

        //data access
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.setString(2, alias);
            r = (stmt.executeUpdate() >= 1);

            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return r;
        }
    }

    /**
     * Delete alias
     * 
     * @param alias FQDN of alias
     * @return true if successful
     */
    public boolean deleteHostAlias(String alias) {
        //locals
        boolean r = false;

        //SQL-Statement
        String sql = "DELETE FROM aliases WHERE alias = ? LIMIT 1;";

        //data access
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, alias);
            r = (stmt.executeUpdate() >= 1);

            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return r;
        }
    }

    /**
     * Create user for host
     * 
     * @param hostid host id
     * @param user username
     * @param password password
     * @param groups list of groups seperated by ,
     * @return true if successful
     */
    public boolean createHostUser(int hostid, String user,
                                  String password, String groups) {
        //locals
        boolean r = false;

        //SQL-Statement
        String sql = "INSERT INTO users VALUES(null, ?, ?, ?, ?);";

        //data access
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, hostid);
            stmt.setString(2, user);
            stmt.setString(3, password);
            stmt.setString(4, groups);
            r = (stmt.executeUpdate() >= 1);


            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return r;
        }
    }

    /**
     * Update user
     * 
     * @param userid user id
     * @param password password
     * @param groups list of groups seperated by , 
     * @return true if successful
     */
    public boolean updateHostUser(int userid, String password, String groups) {
        //locals
        boolean r = false;

        //SQL-Statement
        String sql = "UPDATE users SET password = ?, groups = ? WHERE id = ?;";

        //data access
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, password);
            stmt.setString(2, groups);
            stmt.setInt(3, userid);
            r = (stmt.executeUpdate() >= 1);

            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return r;
        }
    }

    /**
     * Delete user by user id
     * 
     * @param userid unique id of user
     * @return true if successful
     */
    public boolean deleteHostUser(int userid) {
        //locals
        boolean r = false;

        //SQL-Statement
        String sql = "DELETE FROM users WHERE id = ? LIMIT 1;";

        //data access
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, userid);
            r = (stmt.executeUpdate() >= 1);

            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return r;
        }
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
