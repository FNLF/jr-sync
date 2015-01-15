/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package no.bfsk.jrsync.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author glenn
 */
public class JRSyncUI extends JFrame {
    private JLabel status;

    public JRSyncUI() {
        super("JRSync");
        initComponents();
    }

    private void initComponents() {
        BorderLayout layout = new BorderLayout();
        ImageIcon logo = new ImageIcon(getClass().getResource("/bfsk_logo.png"));
        JLabel main = new JLabel(logo);

        this.getContentPane().setBackground(null);
        this.getContentPane().setLayout(layout);
        this.getContentPane().add(main, BorderLayout.CENTER);

        status = new JLabel("Startar...");
        status.setBorder(new EmptyBorder(10, 10, 10, 10));
        status.setHorizontalAlignment(JLabel.CENTER);
        status.setForeground(Color.BLACK);
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(0.66f, 0.77f, 0.81f, 1.0f));
        statusPanel.add(status);
        this.getContentPane().add(statusPanel, BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int x = JOptionPane.showConfirmDialog(e.getComponent(), 
                        "Sikker på at du vil lukka JRSync?\nPC-en kan eksplodera!",
                        "Er du gal?",
                        JOptionPane.YES_NO_OPTION);
                
                if(x == 0) {
                    System.exit(0);
                }
            }
        });

        pack();
    }

    public void setMessage(String message) {
        status.setText(message);
    }

}
