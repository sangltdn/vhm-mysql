/*
 * MainFrame.java
 * Copyright (C) 2008 Jorge Schrauwen
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package be.blackdot.ahm;

/**
 *
 * @author sjorge
 * @url http://www.blackdot.be
 */
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import be.blackdot.ahm.beans.*;
import be.blackdot.ahm.dataaccess.*;

public class MainFrame extends JFrame {

    private LoginFrame parent;
    private Container workspace;
    private JPanel panelWest,  panelHostActions,  panelListHosts;
    private JList listHosts;
    private JButton buttonHostAdd,  buttonHostRemove;
    private JTabbedPane paneConfiguration;
    private ConfigurationPanel panelConfig = new ConfigurationPanel();
    private GeneralPanel panelGeneral = new GeneralPanel();
    private UsersPanel panelUsers = new UsersPanel();
    private AliassesPanel panelAliasses = new AliassesPanel();

    /** events */
    class WindowHandler extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            parent.CloseFrame();
        }
    }

    class HostRemoveHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            Hosts h = (Hosts) listHosts.getSelectedValue();
            if (JOptionPane.showConfirmDialog(
                    workspace,
                    "Are you sure you want to remove '" + h.getName() + "'",
                    "Remove Host",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                parent.daHosts.deleteHost(h.getId());

                //update hosts
                reloadHosts(-1);
                new HostCreateHandler().actionPerformed(null);

                //notify
                JOptionPane.showMessageDialog(
                        workspace,
                        "The host '" + h.getName() + "' has been removed.\n Please remove the files manually!",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    class HostCreateHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            panelGeneral.setHost(null);
            panelUsers.setHost(null);
            panelAliasses.setHost(null);
            panelConfig.setHost(null);
            listHosts.clearSelection();
            buttonHostAdd.setEnabled(false);
            buttonHostRemove.setEnabled(false);
            paneConfiguration.setEnabledAt(1, false);
            paneConfiguration.setEnabledAt(2, false);
            paneConfiguration.setEnabledAt(3, false);
            paneConfiguration.setSelectedIndex(0);
        }
    }

    class HostHandler implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {
            Hosts h = (Hosts) listHosts.getSelectedValue();
            panelGeneral.setHost(h);
            panelUsers.setHost(h);
            panelAliasses.setHost(h);
            panelConfig.setHost(h);
            buttonHostAdd.setEnabled(true);
            buttonHostRemove.setEnabled(true);
            paneConfiguration.setEnabledAt(1, true);
            paneConfiguration.setEnabledAt(2, true);
            paneConfiguration.setEnabledAt(3, true);
        }
    }

    /** methodes */
    private boolean hostExists(String host) {
        for (Hosts h : parent.daHosts.getHosts()) {
            if (h.getName().equalsIgnoreCase(host)) {
                return true;
            }
            for (Aliases a : h.getAliases()) {
                if (a.getAlias().equalsIgnoreCase(host)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void reloadHosts(int hostID) {
        Vector<Hosts> hUpdate = parent.daHosts.getHosts();
        listHosts.setListData(hUpdate);
        for (int i = 0; i < hUpdate.size(); i++) {
            if (hUpdate.get(i).getId() == hostID) {
                listHosts.setSelectedIndex(i);
                break;
            }
        }
    }

    private static String getApachePassword(String password) {
        try {
            return "{SHA}" + new sun.misc.BASE64Encoder().encode(
                    java.security.MessageDigest.getInstance("SHA1").digest(
                    password.getBytes()));
        } catch (Exception ex) {
            return null;
        }
    }

    class GeneralPanel extends JPanel {

        private int hostID = -1;
        private JPanel panelName,  panelOptions,  panelServers,  panelSettings,  panelAction,  panelOptionsBorder,  panelServersBorder;
        private JLabel labelName;
        private JTextField textName;
        private JButton buttonAdd;
        private String[] checkOptionText = {
            "PHP: Hypertext Preprocessor",
            "Scripts .cgi, .pl, cgi-bin",
            "Server Side Included"
        };
        private String[] checkServerText = {
            "WebDAV",
            "FTP"
        };
        private JCheckBox[] checkOptions = new JCheckBox[checkOptionText.length];
        private JCheckBox[] checkServers = new JCheckBox[checkServerText.length];
        private JCheckBox checkEnabled;

        /** events */
        class HostAddHandler implements ActionListener {

            public void actionPerformed(ActionEvent e) {
                if (textName.isEditable()) {
                    if (!hostExists(textName.getText())) {
                        parent.daHosts.createHost(textName.getText().toLowerCase(),
                                checkEnabled.isSelected(),
                                checkOptions[0].isSelected(),
                                checkOptions[1].isSelected(),
                                checkOptions[3].isSelected(),
                                checkOptions[2].isSelected());

                        //set HostID
                        for (Hosts h : parent.daHosts.getHosts()) {
                            if (h.getName().equalsIgnoreCase(textName.getText())) {
                                hostID = h.getId();
                                break;
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(
                                workspace,
                                "Host allready exists or used as alias!",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    parent.daHosts.updateHost(hostID,
                            checkEnabled.isSelected(),
                            checkOptions[0].isSelected(),
                            checkOptions[1].isSelected(),
                            checkOptions[3].isSelected(),
                            checkOptions[2].isSelected());
                }

                //update host list and select host
                reloadHosts(hostID);
            }
        }

        public GeneralPanel() {
            //panel
            setLayout(new BorderLayout());

            //gui elements
            panelName = new JPanel(new FlowLayout(FlowLayout.LEFT));
            labelName = new JLabel("Host: ");
            textName = new JTextField(34);
            
            panelSettings = new JPanel(new GridLayout(1,2,5,5));
            
            panelOptionsBorder = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panelOptionsBorder.setBorder(
                    BorderFactory.createTitledBorder(
                    BorderFactory.createEtchedBorder(),
                    " Options "));

            panelServersBorder = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panelServersBorder.setBorder(
                    BorderFactory.createTitledBorder(
                    BorderFactory.createEtchedBorder(),
                    " Servers "));

            panelOptions = new JPanel(new GridLayout(checkOptions.length, 1));
            for (int i = 0; i < checkOptionText.length; i++) {
                checkOptions[i] = new JCheckBox(checkOptionText[i]);
                panelOptions.add(checkOptions[i]);
            }

            panelServers = new JPanel(new GridLayout(checkOptions.length, 1));
            for (int i = 0; i < checkServerText.length; i++) {
                checkServers[i] = new JCheckBox(checkServerText[i]);
                checkServers[i].setSelected((i == 0) ? true : false);
                panelServers.add(checkServers[i]);
            }

            panelAction = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonAdd = new JButton("Save");
            checkEnabled = new JCheckBox("Enabled");
            checkEnabled.setSelected(true);

            //events
            buttonAdd.addActionListener(new HostAddHandler());

            //merger
            panelName.add(labelName);
            panelName.add(textName);

            panelOptionsBorder.add(panelOptions);
            panelServersBorder.add(panelServers);
            panelAction.add(checkEnabled);
            panelAction.add(buttonAdd);

            panelSettings.add(panelServersBorder);
            panelSettings.add(panelOptionsBorder);
            
            
            add(panelName, BorderLayout.NORTH);
            add(panelSettings, BorderLayout.CENTER);
            add(panelAction, BorderLayout.SOUTH);

        }

        public void setHost(Hosts h) {
            hostID = (h != null) ? h.getId() : -1;
            textName.setText((h != null) ? h.getName() : "");
            textName.setEditable((h != null) ? false : true);

            checkOptions[0].setSelected((h != null) ? h.isWebdav() : true);
            checkOptions[1].setSelected((h != null) ? h.isPhp() : false);
            checkOptions[2].setSelected((h != null) ? h.isCgi() : false);
            checkOptions[3].setSelected((h != null) ? h.isSsi() : false);
            checkEnabled.setSelected((h != null) ? h.isEnabled() : true);
        }
    }

    class UsersPanel extends JPanel {

        private int hostID = -1;
        private JPanel panelUsers,  panelEdit;
        private JPanel panelName,  panelPass,  panelGroups,  panelAction;
        private JLabel labelName,  labelPass;
        private JTextField textName;
        private JButton buttonRemove,  buttonAdd,  buttonSave;
        private JPasswordField textPass;
        private JList listUsers;
        private JScrollPane scrollUsers;
        private String[] checkGroupStrings = {"Users", "Admin", "WebDAV", "FTP"};
        private JCheckBox[] checkGroups = new JCheckBox[checkGroupStrings.length];

        /** events */
        class UserRemoveHandler implements ActionListener {

            public void actionPerformed(ActionEvent e) {
                Users u = (Users) listUsers.getSelectedValue();
                if (JOptionPane.showConfirmDialog(
                        workspace,
                        "Are you sure you want to remove '" + u.getName() + "'",
                        "Remove User",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    parent.daHosts.deleteHostUser(u.getId());

                    //update hosts
                    reloadHosts(hostID);
                    paneConfiguration.setSelectedIndex(1);
                }
            }
        }

        class UserAddHandler implements ActionListener {

            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Add")) {
                    listUsers.clearSelection(); //UserHandler will return null
                } else {
                    if (!textName.getText().equals("") && !(textPass.getPassword().length == 0)) {
                        String pass = getApachePassword(new String(textPass.getPassword()));
                        String groups = "";

                        groups = (checkGroups[0].isSelected()) ? groups + "users," : groups;
                        groups = (checkGroups[1].isSelected()) ? groups + "admin," : groups;
                        groups = (checkGroups[2].isSelected()) ? groups + "dav," : groups;
                        groups = (checkGroups[3].isSelected()) ? groups + "ftp," : groups;
                        if (!groups.equals("")) {
                            //remove trailing ,
                            groups = (groups.charAt(groups.length() - 1) == ',') ? groups.substring(0, groups.length() - 1) : groups;

                            //create user
                            parent.daHosts.createHostUser(hostID,
                                    textName.getText(),
                                    pass,
                                    groups);

                            //update hosts
                            reloadHosts(hostID);
                            paneConfiguration.setSelectedIndex(1);
                        } else {
                            JOptionPane.showMessageDialog(
                                    workspace,
                                    "Please select atleast one group!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(
                                workspace,
                                "Please select fill in username and password!",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }

        class UserHandler implements ListSelectionListener {

            public void valueChanged(ListSelectionEvent e) {
                Users u = (Users) listUsers.getSelectedValue();
                if (u != null) {
                    textName.setText(u.getName());
                    textName.setEnabled(false);
                    textPass.setText("");
                    textPass.setEnabled(false);
                    buttonAdd.setText("Add");

                    checkGroups[0].setSelected(
                            (u.getGroups().indexOf("users") > -1) ? true : false);
                    checkGroups[1].setSelected(
                            (u.getGroups().indexOf("admin") > -1) ? true : false);
                    checkGroups[2].setSelected(
                            (u.getGroups().indexOf("dav") > -1) ? true : false);
                    checkGroups[3].setSelected(
                            (u.getGroups().indexOf("ftp") > -1) ? true : false);

                    buttonAdd.setEnabled(true);
                    buttonRemove.setEnabled(true);
                } else {
                    textName.setText("");
                    textName.setEnabled(true);
                    textPass.setText("");
                    textPass.setEnabled(true);
                    buttonAdd.setText("Save");
                    for (int i = 0; i < checkGroups.length; i++) {
                        checkGroups[i].setSelected((i == 0) ? true : false);
                    }
                    buttonAdd.setEnabled(true);
                    buttonRemove.setEnabled(false);
                }
            }
        }

        public UsersPanel() {
            //panel
            setLayout(new BorderLayout());

            //gui elements
            panelUsers = new JPanel(new GridLayout(1, 1));
            panelUsers.setPreferredSize(new Dimension(120, 100)); //force width

            listUsers = new JList();

            scrollUsers = new JScrollPane(listUsers,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            panelEdit = new JPanel(null);

            panelName = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            labelName = new JLabel("Username: ");
            textName = new JTextField(20);

            panelPass = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            labelPass = new JLabel("Password: ");
            textPass = new JPasswordField(20);

            panelGroups = new JPanel(new GridLayout(1, checkGroupStrings.length));
            panelGroups.setBorder(
                    BorderFactory.createTitledBorder(
                    BorderFactory.createEtchedBorder(),
                    " Groups "));
            for (int i = 0; i < checkGroupStrings.length; i++) {
                checkGroups[i] = new JCheckBox(checkGroupStrings[i]);
                checkGroups[i].setSelected((i == 0) ? true : false);
                panelGroups.add(checkGroups[i]);
            }

            panelAction = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonAdd = new JButton("Save");
            buttonRemove = new JButton("Remove");
            buttonRemove.setEnabled(false);

            //events
            listUsers.addListSelectionListener(new UserHandler());
            buttonAdd.addActionListener(new UserAddHandler());
            buttonRemove.addActionListener(new UserRemoveHandler());

            //merger
            panelUsers.add(scrollUsers);

            panelName.add(labelName, BorderLayout.WEST);
            panelName.add(textName, BorderLayout.CENTER);
            panelName.setBounds(0, 0, 305, 30);
            panelEdit.add(panelName);

            panelPass.add(labelPass, BorderLayout.WEST);
            panelPass.add(textPass, BorderLayout.CENTER);
            panelPass.setBounds(0, 25, 305, 30);
            panelEdit.add(panelPass);

            panelGroups.setBounds(0, 51, 305, 50);
            panelEdit.add(panelGroups);

            panelAction.add(buttonAdd);
            panelAction.add(buttonRemove);
            panelAction.setBounds(0, 100, 305, 35);
            panelEdit.add(panelAction);

            add(panelUsers, BorderLayout.WEST);
            add(panelEdit, BorderLayout.CENTER);
        }

        public void setHost(Hosts h) {
            hostID = (h != null) ? h.getId() : -1;
            listUsers.clearSelection();
            listUsers.setListData((h != null) ? h.getUsers() : new Vector());
            buttonAdd.setEnabled(true);
            buttonRemove.setEnabled(false);
            textName.setText("");
            textPass.setText("");
        }
    }

    class AliassesPanel extends JPanel {

        private int hostID = -1;
        private JPanel panelAlias,  panelEdit;
        private JPanel panelName,  panelAction;
        private JLabel labelName;
        private JTextField textName;
        private JButton buttonRemove,  buttonAdd;
        private JList listAlias;
        private JScrollPane scrollAlias;

        /** events */
        class AliasRemoveHandler implements ActionListener {

            public void actionPerformed(ActionEvent e) {
                Aliases a = (Aliases) listAlias.getSelectedValue();
                if (JOptionPane.showConfirmDialog(
                        workspace,
                        "Are you sure you want to remove '" + a.getAlias() + "'",
                        "Remove Alias",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    parent.daHosts.deleteHostAlias(a.getAlias());

                    //update hosts
                    reloadHosts(hostID);
                    paneConfiguration.setSelectedIndex(2);
                }
            }
        }

        class AliasAddHandler implements ActionListener {

            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Add")) {
                    listAlias.clearSelection(); //UserHandler will return null
                } else {
                    if (!textName.getText().equals("")) {
                        if (!hostExists(textName.getText())) {
                            parent.daHosts.createHostAlias(
                                    hostID,
                                    textName.getText().toLowerCase());
                            //update hosts
                            reloadHosts(hostID);
                            paneConfiguration.setSelectedIndex(2);
                        } else {
                            JOptionPane.showMessageDialog(
                                    workspace,
                                    "Host allready exists or used as alias!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }

        class AliasHandler implements ListSelectionListener {

            public void valueChanged(ListSelectionEvent e) {
                Aliases a = (Aliases) listAlias.getSelectedValue();
                if (a != null) {
                    textName.setText(a.getAlias());
                    textName.setEnabled(false);
                    buttonAdd.setText("Add");
                    buttonAdd.setEnabled(true);
                    buttonRemove.setEnabled(true);
                } else {
                    textName.setText("");
                    textName.setEnabled(true);

                    buttonAdd.setText("Save");
                    buttonAdd.setEnabled(true);
                    buttonRemove.setEnabled(false);
                }
            }
        }

        public AliassesPanel() {
            //panel
            setLayout(new BorderLayout());

            //gui elements
            panelAlias = new JPanel(new GridLayout(1, 1));
            panelAlias.setPreferredSize(new Dimension(120, 100)); //force width

            listAlias = new JList();

            scrollAlias = new JScrollPane(listAlias,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            panelEdit = new JPanel(null);

            panelName = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            labelName = new JLabel("Alias: ");
            textName = new JTextField(20);

            panelAction = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonAdd = new JButton("Save");
            buttonRemove = new JButton("Remove");
            buttonRemove.setEnabled(false);

            //events
            listAlias.addListSelectionListener(new AliasHandler());
            buttonAdd.addActionListener(new AliasAddHandler());
            buttonRemove.addActionListener(new AliasRemoveHandler());

            //merger
            panelAlias.add(scrollAlias);

            panelName.add(labelName, BorderLayout.WEST);
            panelName.add(textName, BorderLayout.CENTER);
            panelName.setBounds(0, 0, 305, 30);
            panelEdit.add(panelName);

            panelAction.add(buttonAdd);
            panelAction.add(buttonRemove);
            panelAction.setBounds(0, 25, 305, 35);
            panelEdit.add(panelAction);

            add(panelAlias, BorderLayout.WEST);
            add(panelEdit, BorderLayout.CENTER);
        }

        public void setHost(Hosts h) {
            hostID = (h != null) ? h.getId() : -1;
            listAlias.clearSelection();
            listAlias.setListData((h != null) ? h.getAliases() : new Vector());
            buttonAdd.setEnabled(true);
            buttonRemove.setEnabled(false);
            textName.setText("");
        }
    }

    class ConfigurationPanel extends JPanel {

        private int hostID = -1;
        private JLabel labelInfo;
        private JTextArea textCfg;
        private JScrollPane scrollCfg;
        private JPanel panelAction;
        private JButton buttonSave;

        /** events */
        class CfgSaveHandler implements ActionListener {

            public void actionPerformed(ActionEvent e) {
                parent.daHosts.updateHostCfg(hostID, textCfg.getText());

                //update hosts
                reloadHosts(hostID);
                paneConfiguration.setSelectedIndex(3);
            }
        }

        public ConfigurationPanel() {
            //panel
            setLayout(new BorderLayout());

            //gui elements
            labelInfo = new JLabel("Specialized configuration directives", SwingConstants.CENTER);

            textCfg = new JTextArea();
            textCfg.setFont(new Font("monospaced", Font.PLAIN, 12));

            scrollCfg = new JScrollPane(textCfg,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            panelAction = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonSave = new JButton("Save");

            //events
            buttonSave.addActionListener(new CfgSaveHandler());

            //merger
            panelAction.add(buttonSave);

            add(labelInfo, BorderLayout.NORTH);
            add(scrollCfg, BorderLayout.CENTER);
            add(panelAction, BorderLayout.SOUTH);
        }

        public void setHost(Hosts h) {
            hostID = (h != null) ? h.getId() : -1;
            textCfg.setText((h != null) ? h.getConfiguration() : "");
            textCfg.select(0, 0);
        }
    }

    /**
     * Creates a new instance of MainFrame
     */
    public MainFrame(LoginFrame parent) throws Exception {
        //parent
        this.parent = parent;

        //workspace
        workspace = getContentPane();
        workspace.setLayout(new BorderLayout());

        //gui-elements
        panelWest = new JPanel(new BorderLayout(5, 5));
        panelWest.setBorder(
                BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                " Virtual Hosts "));
        listHosts = new JList(parent.daHosts.getHosts());
        panelListHosts = new JPanel(new GridLayout(1, 1));
        panelListHosts.setPreferredSize(new Dimension(150, 100)); //force width

        panelHostActions = new JPanel(new GridLayout(2, 1, 5, 5));
        buttonHostAdd = new JButton("Add");
        buttonHostAdd.setEnabled(false);
        buttonHostRemove = new JButton("Remove");
        buttonHostRemove.setEnabled(false);

        paneConfiguration = new JTabbedPane();
        paneConfiguration.addTab("General", panelGeneral);
        paneConfiguration.addTab("Users", panelUsers);
        paneConfiguration.setEnabledAt(1, false);
        paneConfiguration.addTab("Aliasses", panelAliasses);
        paneConfiguration.setEnabledAt(2, false);
        paneConfiguration.addTab("Configuration", panelConfig);
        paneConfiguration.setEnabledAt(3, false);

        //events
        buttonHostRemove.addActionListener(new HostRemoveHandler());
        buttonHostAdd.addActionListener(new HostCreateHandler());
        listHosts.addListSelectionListener(new HostHandler());

        //gui-mergers
        panelHostActions.add(buttonHostAdd);
        panelHostActions.add(buttonHostRemove);

        panelListHosts.add(new JScrollPane(listHosts,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        panelWest.add(panelListHosts, BorderLayout.CENTER);
        panelWest.add(panelHostActions, BorderLayout.SOUTH);

        workspace.add(panelWest, BorderLayout.WEST);
        workspace.add(paneConfiguration, BorderLayout.CENTER);

        //JFrame
        //setLocation(200,100);
        setTitle("Virtual Host Manager");
        setSize(600, 350);
        setResizable(false);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon("images/host.png").getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowHandler());
        setVisible(true);
    }
}
