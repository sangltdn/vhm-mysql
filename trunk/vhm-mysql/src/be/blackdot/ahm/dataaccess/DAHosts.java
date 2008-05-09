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

    public Hosts getHost(int id) {
        //SQL-Statement
        String sql = String.format("SELECT * FROM hosts WHERE id = %d;", id);

        return getHostWithSQL(sql);
    }

    public Hosts getHost(String name) {
        //SQL-Statement
        String sql = String.format("SELECT * FROM hosts WHERE name = '%s';", name);

        return getHostWithSQL(sql);
    }

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

    public void updateHost(int id, boolean enabled,
                            boolean webdav, boolean php,
                            boolean ssi, boolean cgi) {

        //SQL-Statement
        String sql = "UPDATE hosts SET enabled = ?, webdav = ?, cgi = ?, ssi = ?, php = ? WHERE id = ?;";

        //data access
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setBoolean(1, enabled);
            stmt.setBoolean(2, webdav);
            stmt.setBoolean(3, cgi);
            stmt.setBoolean(4, ssi);
            stmt.setBoolean(5, php);
            stmt.setInt(6, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public void createHost(String naam, boolean enabled,
                            boolean webdav, boolean php,
                            boolean ssi, boolean cgi) {

        //SQL-Statement
        String sql = "INSERT INTO hosts VALUES(null, ?, ?, ?, ?, ?, ?);";

        //data access
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, naam);
            stmt.setBoolean(2, enabled);
            stmt.setBoolean(3, webdav);
            stmt.setBoolean(4, cgi);
            stmt.setBoolean(5, ssi);
            stmt.setBoolean(6, php);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public void deleteHost(int id) {

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
                stmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                } catch (SQLException e) {
                }
            }
        }

    }

    public void updateHostCfg(int id, String cfg) {
        deleteHostCfg(id);
        if (!cfg.equals("")) {
            createHostCfg(id, cfg);
        }
    }

    public void createHostCfg(int id, String cfg) {

        //SQL-Statement
        String sql = "INSERT INTO configuration VALUES(?, ?);";

        //data access
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.setString(2, cfg);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public void deleteHostCfg(int id) {

        //SQL-Statement
        String sql = "DELETE FROM configuration WHERE id = ? LIMIT 1;";

        //data access
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public void createHostAlias(int id, String alias) {

        //SQL-Statement
        String sql = "INSERT INTO aliases VALUES(?, ?);";

        //data access
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.setString(2, alias);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public void deleteHostAlias(String alias) {

        //SQL-Statement
        String sql = "DELETE FROM aliases WHERE alias = ? LIMIT 1;";

        //data access
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, alias);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public void createHostUser(int hostid, String user,
                                String password, String groups) {

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
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public void deleteHostUser(int userid) {

        //SQL-Statement
        String sql = "DELETE FROM users WHERE id = ? LIMIT 1;";

        //data access
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, userid);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
