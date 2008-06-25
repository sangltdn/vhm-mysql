/*
 * LoginFrame.java
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
import javax.swing.event.*;

import be.blackdot.ahm.dataaccess.*;

public class LoginFrame extends JFrame {

    public DAHosts daHosts = null;
    private Container workspace;
    private JPanel panelBottom,  panelCenter,  panelLabels,  panelTexts;
    private JLabel[] labelsConnect = new JLabel[4];
    private JTextField[] textsConnect = new JTextField[3];
    private JPasswordField textPassword;
    private JButton buttonConnect;
    private String[] strLabels = {"Host", "Database", "User", "Password"};

    /** events */
    class ConnectHandler implements ActionListener {

        private String drv = "com.mysql.jdbc.Driver";
        private String url = "jdbc:mysql://";
        private String login;
        private String password;
        private LoginFrame parent;

        public ConnectHandler(LoginFrame parent) {
            this.parent = parent;
        }

        public void actionPerformed(ActionEvent e) {
            if ((!textsConnect[0].getText().equals("")) &&
                    (!textsConnect[1].getText().equals("")) &&
                    (!textsConnect[2].getText().equals(""))) {

                login = textsConnect[2].getText();
                password = String.valueOf(textPassword.getPassword());
                url = String.format("jdbc:mysql://%s/%s", textsConnect[0].getText(), textsConnect[1].getText());

                try {
                    daHosts = (daHosts == null) ? new DAHosts(url, login, password, drv) : daHosts;
                    new MainFrame(parent);
                    setVisible(false);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            workspace,
                            "Error connecting to server!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    class WindowHandler extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            CloseFrame();
        }
    }

    /** methods */
    public void CloseFrame() {
        try {
            if (daHosts != null) {
                daHosts.close();
            }
        } catch (Exception err) {
            JOptionPane.showMessageDialog(
                    workspace,
                    "Error closing database!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            System.exit(0);
        }
    }

    /**
     * Creates a new instance of LoginFrame
     */
    public LoginFrame() throws Exception {
        //workspace
        workspace = getContentPane();
        workspace.setLayout(new BorderLayout());

        //gui-elements
        panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        panelLabels = new JPanel(new GridLayout(4, 1, 2, 2));
        panelLabels.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        for (int i = 0; i < labelsConnect.length; i++) {
            labelsConnect[i] = new JLabel(strLabels[i] + ": ");
            panelLabels.add(labelsConnect[i]);
        }

        panelTexts = new JPanel(new GridLayout(4, 1, 2, 2));
        for (int i = 0; i < textsConnect.length; i++) {
            textsConnect[i] = new JTextField(10);
            panelTexts.add(textsConnect[i]);
        }
        textPassword = new JPasswordField(10);
        panelTexts.add(textPassword);

        panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonConnect = new JButton("Connect");

        //events
        buttonConnect.addActionListener(new ConnectHandler(this));
        textPassword.addActionListener(new ConnectHandler(this));

        //get data 
        File f = new File("connection.cfg");
        if (f.exists()) {
            BufferedReader cfg = new BufferedReader(new FileReader("connection.cfg"));
            String cfgProperty;
            while ((cfgProperty = cfg.readLine()) != null) {
                String k, v;
                k = cfgProperty.substring(0, cfgProperty.indexOf('='));
                v = cfgProperty.substring(cfgProperty.indexOf('=') + 1);

                if (k.equalsIgnoreCase("host")) {
                    textsConnect[0].setText(v);
                } else if (k.equalsIgnoreCase("database")) {
                    textsConnect[1].setText(v);
                } else if (k.equalsIgnoreCase("user")) {
                    textsConnect[2].setText(v);
                } else if (k.equalsIgnoreCase("password")) {
                    textPassword.setText(v);
                }
            }
        }
        //gui-mergers
        panelCenter.add(panelLabels, BorderLayout.WEST);
        panelCenter.add(panelTexts, BorderLayout.CENTER);

        panelBottom.add(buttonConnect);

        workspace.add(panelCenter, BorderLayout.CENTER);
        workspace.add(panelBottom, BorderLayout.SOUTH);

        //JFrame
        //setLocation(200,100);
        setTitle("Virtual Host Manager");
        setSize(250, 160);
        setResizable(false);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon("images/host.png").getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowHandler());
        setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            new LoginFrame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }
