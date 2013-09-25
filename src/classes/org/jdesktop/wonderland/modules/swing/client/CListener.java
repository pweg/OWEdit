/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jdesktop.wonderland.modules.swing.client;

import javax.swing.JOptionPane;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.client.cell.CellStatusChangeListener;
import org.jdesktop.wonderland.client.cell.ComponentChangeListener;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.modules.swing.client.gui.MainWindow;

/**
 *
 * @author Patrick
 */
public class CListener implements CellStatusChangeListener{
    MainWindow win = null;
    
    
    public CListener(MainWindow win2){
       win = win2;
    }

    /*public void componentChanged(Cell cell, ChangeType type, CellComponent component) {
        
        if(type.equals(ChangeType.ADDED)){
            win.addCell(cell);
        }else if(type.equals(ChangeType.REMOVED)){
            win.removeCell(cell);   
        }
    }*/

    public void cellStatusChanged(Cell cell, CellStatus status) {
        
        if(status.equals(CellStatus.DISK)){
            win.removeCell(cell);       
        }
        else if(status.equals(CellStatus.VISIBLE)){
            win.addCell(cell);
        }
    }
    
}
