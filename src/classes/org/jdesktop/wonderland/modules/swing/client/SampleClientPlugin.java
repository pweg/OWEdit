/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jdesktop.wonderland.modules.swing.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.modules.swing.client.gui.MainWindow;


/**
 *
 * @author Patrick
 */
@Plugin
public class SampleClientPlugin extends BaseClientPlugin {
    
    private JMenuItem myMenuItem = null;
    
    @Override
    public void initialize(ServerSessionManager loginInfo) {
        
        
        myMenuItem = new JMenuItem("Editor");
        myMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               MainWindow main = new MainWindow();
               main.setVisible(true);
            }
        });
        super.initialize(loginInfo);
    }
    

    @Override
    protected void activate() {
        JmeClientMain.getFrame().addToToolsMenu(myMenuItem);
    }

    @Override
    protected void deactivate() {
        JmeClientMain.getFrame().removeFromToolsMenu(myMenuItem);
    }

    @Override
    public void cleanup() {
        myMenuItem = null;
        super.cleanup();
    }

}
