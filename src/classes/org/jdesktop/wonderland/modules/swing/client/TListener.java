/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jdesktop.wonderland.modules.swing.client;

import java.util.ArrayList;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.TransformChangeListener;
import org.jdesktop.wonderland.modules.swing.client.gui.MainWindow;

/**
 *
 * @author Patrick
 */
public class TListener implements TransformChangeListener{

    MainWindow win = null;
    
    public TListener(MainWindow win2){
       win = win2;
    }
    
    public void transformChanged(Cell cell, ChangeSource source) {
        win.updateGUI(cell);
    }
    
}
