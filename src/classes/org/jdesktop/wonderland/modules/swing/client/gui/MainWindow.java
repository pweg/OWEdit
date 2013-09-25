/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jdesktop.wonderland.modules.swing.client.gui;

import com.jme.math.Vector3f;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellCache;
import org.jdesktop.wonderland.client.cell.CellEditChannelConnection;
import org.jdesktop.wonderland.client.cell.CellManager;
import org.jdesktop.wonderland.client.cell.MovableComponent;
import org.jdesktop.wonderland.client.cell.utils.CellUtils;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuActionListener;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuItemEvent;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.common.cell.CellEditConnectionType;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.messages.CellDuplicateMessage;
import org.jdesktop.wonderland.common.cell.messages.CellServerComponentMessage;
import org.jdesktop.wonderland.common.cell.messages.CellServerStateRequestMessage;
import org.jdesktop.wonderland.common.cell.messages.CellServerStateResponseMessage;
import org.jdesktop.wonderland.common.cell.messages.CellServerStateUpdateMessage;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.PositionComponentServerState;
import org.jdesktop.wonderland.common.messages.ErrorMessage;
import org.jdesktop.wonderland.common.messages.ResponseMessage;
import org.jdesktop.wonderland.modules.swing.client.CListener;
import org.jdesktop.wonderland.modules.swing.client.TListener;
//import org.jdesktop.wonderland.modules.artimport.client.jme;

/**
 *
 * @author Patrick
 */
public class MainWindow extends JFrame {
    
     private static final Logger LOGGER =
            Logger.getLogger(MainWindow.class.getName());
     
     private ArrayList<Cell> cells = null;
     private ArrayList<JTextField> fields = null;
     private ArrayList<JTextField> xf = null;
     private ArrayList<JTextField> yf = null;
     private ArrayList<JTextField> zf = null;
     private ArrayList<JPanel> panels = null;
     private Dimension d = new Dimension(250,25);
     
     private AListener listener = null;
     private  TListener tlistener = null;
     private  CListener clistener = null;
     private  RListener rlistener = null;
     private  PListener plistener = null;
     private DuplicateListener duplistener = null;
     //private ImportSessionFrame impi = null;
     private Importer importer = null;
     private AddListener addlistener = null;
     
    
    public MainWindow() {
        
       setTitle("Editor");
       setSize(1024, 768);
       setLocationRelativeTo(null);
       setDefaultCloseOperation(HIDE_ON_CLOSE); 
       
       fields = new ArrayList<JTextField>();
       xf = new ArrayList<JTextField>();
       yf = new ArrayList<JTextField>();
       zf = new ArrayList<JTextField>();
       cells = new ArrayList<Cell>();
       panels = new ArrayList<JPanel>();
       
       WonderlandSession session = LoginManager.getPrimary().getPrimarySession();
       
       CellCache cache = ClientContext.getCellCache(session);
        if (cache == null) {
            LOGGER.warning("Unable to find Cell cache for session " + session);
            return;
        }
        String s ="";
        Collection<Cell> rootCells = cache.getRootCells();
        for (Cell rootCell : rootCells) {
            //s=s+"\n"+ rootCell.getName();
            //cells.add(rootCell);
            s=iterateChilds(rootCell, s, false);
        }
        
        Container pane = this.getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        
        addlistener = new AddListener();
        
        
        listener = new AListener();
        rlistener = new RListener();
        plistener = new PListener();
        tlistener = new TListener(this);
        clistener = new CListener(this);
        duplistener = new DuplicateListener();
        
        this.setLayout(new GridLayout(0,1));
         JButton add = new JButton("Add File");
         add.addActionListener(addlistener);
         add.setName("add");
         this.add(add);
        
        CellManager.getCellManager().addCellStatusChangeListener(clistener);
        
        int i=0;
        for(Cell cell : cells){
            final JPanel pan = new JPanel();
            pan.setLayout(new GridLayout(0,8));
            JTextField textField = new JTextField(cell.getName());
            textField.setEditable(true);
            textField.setSize(200, 25);
            textField.setMaximumSize(d);
            pan.add(textField);
            fields.add(textField);
            
            CellTransform transform = cell.getLocalTransform();
            Vector3f transl = transform.getTranslation(null);
            
            JTextField textField2 = new JTextField(Float.toString(transl.x));
            textField2.setEditable(true);
            textField2.setSize(25, 25);
            textField2.setMaximumSize(d);
            pan.add(textField2);
            xf.add(textField2);
            JTextField textField3 = new JTextField(Float.toString(transl.y));
            textField3.setEditable(true);
            textField3.setSize(25, 25);
            textField3.setMaximumSize(d);
            pan.add(textField3);
            yf.add(textField3);
            JTextField textField4 = new JTextField(Float.toString(transl.z));
            textField4.setEditable(true);
            textField4.setSize(25, 25);
            textField4.setMaximumSize(d);
            pan.add(textField4);
            zf.add(textField4);
            
            JButton butt = new JButton("set");
            butt.setActionCommand(String.valueOf(i));
            butt.addActionListener(listener);
            butt.setName("butt"+i);
            pan.add(butt);
            
            
            JButton butt4 = new JButton("copy");
            butt4.setActionCommand(String.valueOf(i));
            butt4.addActionListener(duplistener);
            butt4.setName("butt4"+i);
            pan.add(butt4);
            
            JButton butt2 = new JButton("remove");
            butt2.setActionCommand(String.valueOf(i));
            butt2.addActionListener(rlistener);
            butt2.setName("butt2"+i);
            pan.add(butt2);
            
            JButton butt3 = new JButton("properties");
            butt3.setActionCommand(String.valueOf(i));
            butt3.addActionListener(plistener);
            butt3.setName("butt3"+i);
            pan.add(butt3);
            
            MovableComponent movableComponent = cell.getComponent(MovableComponent.class);
            if (movableComponent == null) {
                String className = "org.jdesktop.wonderland.server.cell." +
                        "MovableComponentMO";
                CellServerComponentMessage cscm = 
                        CellServerComponentMessage.newAddMessage(
                        cell.getCellID(), className);
                ResponseMessage response = cell.sendCellMessageAndWait(cscm);
                if (response instanceof ErrorMessage) {
                    fields.get(0).setText(response.toString());
                }
            }
            
            
                    // Listen for changes in the Cell's transform. It is ok if open() is
            // called more than once, this method call will not add duplicate
            // listeners.
            
            //müssen listener zu jeder cell dazugefügt werden?
            cell.addTransformChangeListener(tlistener);
            //cell.addStatusChangeListener(clistener);

            // Update the GUI, set local changes to true so that messages to the
            // movable component are NOT generated.
           
            
            panels.add(pan);
            this.add(pan);
            i++;
        }
        
        JTextField textfield2 = new JTextField(s);
        this.add(textfield2);
        this.pack();
        
        importer = new Importer(this);
        
        
        
    }
    
    public void updateGUI(Cell cell) {
        
        int i =0;
        for(Cell c : cells){
            if(cell.equals(c))
                break;
            i++;
        }
       
        CellTransform cellTransform = cell.getLocalTransform();
        Vector3f translation = cellTransform.getTranslation(null);
        
        xf.get(i).setText(Float.toString(translation.x));
        yf.get(i).setText(Float.toString(translation.y));
        zf.get(i).setText(Float.toString(translation.z));
        
    }
    
    class AListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String com = e.getActionCommand();
            
            //fields.get(0).setText(com);
            
            try{
             int i = Integer.parseInt(com);
             
             
             updateName(i);
             updateTranslation(i);
             
            }catch(Exception x){
                
            }
        }
    }
    
    class AddListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            importer.setVisible(true);
        }
    }
    
    class RListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String com = e.getActionCommand();
            
            fields.get(0).setText(com);
            
            try{
             int i = Integer.parseInt(com);
             
             CellUtils.deleteCell(cells.get(i));
             
            }catch(Exception x){
                
            }
        }
    }
    
    class PListener implements ActionListener {
        
        
        public void actionPerformed(ActionEvent e) {
            String com = e.getActionCommand();
            
            
            
            try{
             int i = Integer.parseInt(com);
             
             
             JOptionPane.showMessageDialog(rootPane, "Not implemented. Cannot import CellPropertiesFrame whatsoever.",
                    "Implementatinerror", JOptionPane.ERROR_MESSAGE);
             
            }catch(Exception x){
                
            }
        }
    }
    
    
    private void updateName(int button){
        
        Cell cell = cells.get(button);
        
        String name = fields.get(button).getText();
        CellServerState cellServerState =fetchCellServerState(cell);
        ((CellServerState) cellServerState).setName(name);
        
        
        
        // Form a new CellUpdateServerState message with the appropriate
        // information and send it
        CellServerStateUpdateMessage msg = new CellServerStateUpdateMessage(
                cell.getCellID(),
                cellServerState,
                null);
        ResponseMessage response = cell.sendCellMessageAndWait(msg);
        if (response instanceof ErrorMessage) {
            // XXX Probably should get a success/failed here!
            ErrorMessage em = (ErrorMessage) response;
            LOGGER.log(Level.WARNING, "Error applugin values: " +
                    em.getErrorMessage(), em.getErrorCause());

            JOptionPane.showMessageDialog(this, em.getErrorMessage(),
                    "Error applying values", JOptionPane.ERROR_MESSAGE);
        }
        
        
        //Scheinbar gibs keinen Listener, der überprüft ob der Name geändert wurde
        //im objekteditor wird nur immer refreshed, wenn wieder fenster wieder visible ist.
        //irgendwie stateupdatemessages abfangen? Johanna fragen.
        //String name2 = cell.getName();
        //fields.get(button).setText(name2);
        
    }
    
        private CellServerState fetchCellServerState(Cell cell) {
        // Fetch the setup object from the Cell object. We send a message on
        // the cell channel, so we must fetch that first.
        ResponseMessage response = cell.sendCellMessageAndWait(
                new CellServerStateRequestMessage(cell.getCellID()));
        if (response == null) {
            return null;
        }

        // We need to remove the position component first as a special case
        // since we do not want to update it after the cell is created.
        CellServerStateResponseMessage cssrm = (CellServerStateResponseMessage) response;
        CellServerState state = cssrm.getCellServerState();
        if (state != null) {
            state.removeComponentServerState(PositionComponentServerState.class);
        }
        return state;
    }
    
    
    private void updateTranslation(int button) {
        //fields.get(0).setText(button + "ralala");
        float x = Float.parseFloat(xf.get(button).getText());
        float y = Float.parseFloat(yf.get(button).getText());
        float z = Float.parseFloat(zf.get(button).getText());
        
        
        //fields.get(0).setText(button + "ralala:" + x +" " + y +" " +z);

        Vector3f translation = new Vector3f(x, y, z);
        Cell cell = cells.get(button);
        MovableComponent movableComponent = cell.getComponent(MovableComponent.class);
        
        /*if(movableComponent == null){
            if (movableComponent == null) {
                String className = "org.jdesktop.wonderland.server.cell." +
                        "MovableComponentMO";
                CellServerComponentMessage cscm = 
                        CellServerComponentMessage.newAddMessage(
                        cell.getCellID(), className);
                cell.sendCellMessageAndWait(cscm);
            }
        }*/
        
        if (movableComponent != null) {
            
            //fields.get(0).setText("press");
            CellTransform cellTransform = cell.getLocalTransform();
            cellTransform.setTranslation(translation);
            movableComponent.localMoveRequest(cellTransform);
        }
    }
    
        /**
     * Asks the server for the server state of the cell; returns null upon
     * error
     */
    /*private CellServerState fetchCellServerState(Cell cell) {
        // Fetch the setup object from the Cell object. We send a message on
        // the cell channel, so we must fetch that first.
        ResponseMessage response = cell.sendCellMessageAndWait(
                new CellServerStateRequestMessage(cell.getCellID()));
        if (response == null) {
            return null;
        }

        // We need to remove the position component first as a special case
        // since we do not want to update it after the cell is created.
        CellServerStateResponseMessage cssrm = (CellServerStateResponseMessage) response;
        CellServerState state = cssrm.getCellServerState();
        if (state != null) {
            state.removeComponentServerState(PositionComponentServerState.class);
        }
        return state;
    }*/
    
    public void addCell(Cell cell){
        
        for(Cell c : cells){
            if(cell.equals(c)){
                return;
            }
        }
        
        final JPanel pan = new JPanel();
            pan.setLayout(new GridLayout(0,7));
            JTextField textField = new JTextField(cell.getName());
            textField.setEditable(true);
            textField.setSize(200, 25);
            textField.setMaximumSize(d);
            pan.add(textField);
            fields.add(textField);
            
            CellTransform transform = cell.getLocalTransform();
            Vector3f transl = transform.getTranslation(null);
            
            JTextField textField2 = new JTextField(Float.toString(transl.x));
            textField2.setEditable(true);
            textField2.setSize(25, 25);
            textField2.setMaximumSize(d);
            pan.add(textField2);
            xf.add(textField2);
            JTextField textField3 = new JTextField(Float.toString(transl.y));
            textField3.setEditable(true);
            textField3.setSize(25, 25);
            textField3.setMaximumSize(d);
            pan.add(textField3);
            yf.add(textField3);
            JTextField textField4 = new JTextField(Float.toString(transl.z));
            textField4.setEditable(true);
            textField4.setSize(25, 25);
            textField4.setMaximumSize(d);
            pan.add(textField4);
            zf.add(textField4);
            
            JButton butt = new JButton("set");
            butt.setActionCommand(String.valueOf(cells.size()));
            butt.addActionListener(listener);
            butt.setName("butt"+cells.size());
            pan.add(butt);
            
            
            JButton butt4 = new JButton("copy");
            butt4.setActionCommand(String.valueOf(cells.size()));
            butt4.addActionListener(duplistener);
            butt4.setName("butt4"+cells.size());
            pan.add(butt4);
            
            
            
            JButton butt2 = new JButton("remove");
            butt2.setActionCommand(String.valueOf(cells.size()));
            butt2.addActionListener(rlistener);
            butt2.setName("butt2"+cells.size());
            pan.add(butt2);
            
            
            JButton butt3 = new JButton("properties");
            butt3.setActionCommand(String.valueOf(cells.size()));
            butt3.addActionListener(plistener);
            butt3.setName("butt3"+cells.size());
            pan.add(butt3);
            
            cells.add(cell);
            panels.add(pan);
            
            
                    // Listen for changes in the Cell's transform. It is ok if open() is
            // called more than once, this method call will not add duplicate
            // listeners.
            
            //müssen listener zu jeder cell dazugefügt werden?
            cell.addTransformChangeListener(tlistener);
            //cell.addStatusChangeListener(clistener);

            // Update the GUI, set local changes to true so that messages to the
            // movable component are NOT generated.
            
            MovableComponent movableComponent = cell.getComponent(MovableComponent.class);
            if (movableComponent == null) {
                String className = "org.jdesktop.wonderland.server.cell." +
                        "MovableComponentMO";
                CellServerComponentMessage cscm = 
                        CellServerComponentMessage.newAddMessage(
                        cell.getCellID(), className);
                ResponseMessage response = cell.sendCellMessageAndWait(cscm);
                if (response instanceof ErrorMessage) {
                    JOptionPane.showMessageDialog(this, response.toString(),
                    "Error applying values", JOptionPane.ERROR_MESSAGE);
                }
            }
           
            
            this.add(pan);
            this.pack();
            this.repaint();
    }
    
    public void removeCell(Cell cell){
        
        int i =0;
        for(Cell c : cells){
            if(cell.equals(c)){
                cells.remove(i);
                break;
            }
            i++;
        }
        
        xf.remove(i);
        yf.remove(i);
        zf.remove(i);
        fields.remove(i);
        this.remove(panels.get(i));
        panels.remove(i);
            this.pack();
            this.repaint();
        
        
    }
    
    private String iterateChilds(Cell cell, String s, boolean b){
        
        Collection<Cell> childs = cell.getChildren();
        String name = "";
        if(b == true)
            name = "CHILD__";
        s= s+"\n "+name+cell.getName();
        cells.add(cell);
   
        for(Cell child : childs)
        {
            iterateChilds(child,s,true);
        }
        return s;
        
    }
    
    private class DuplicateListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
             String com = e.getActionCommand();
            
            
            
            try{
                int i = Integer.parseInt(com);

              
               // Create a new name for the cell, based upon the old name.
               Cell cell = cells.get(i);
               String cellName = "Copy_Of_"+cell.getName();
               cellName = MessageFormat.format(cellName, cell.getName());

               // If we want to delete, send a message to the server as such
               WonderlandSession session =
                       LoginManager.getPrimary().getPrimarySession();
               CellEditChannelConnection connection = 
                       (CellEditChannelConnection) session.getConnection(
                       CellEditConnectionType.CLIENT_TYPE);
               CellDuplicateMessage msg =
                       new CellDuplicateMessage(cell.getCellID(), cellName);
               connection.send(msg);
            }catch(Exception ex){
            }

            // Really should receive an OK/Error response from the server!
        }
    }
    
    
    //Für configfiles?
    /*
    private File lastModelDir = null;
    
    private File getLastModelFile() {
        File configDir = ClientContext.getUserDirectory("config");
        return new File(configDir, "last_model_dir");
    }*/
    
    /**
     * Write the defaults for this UI
    
    void writeDefaultsConfig() {
        try {
            File lastModelFile = getLastModelFile();
            DataOutputStream out = new DataOutputStream(
                    new FileOutputStream(lastModelFile));
            out.writeBoolean(lastModelDir != null);
            if (lastModelDir != null) {
                out.writeUTF(lastModelDir.getAbsolutePath());
            }
            out.writeBoolean(compiledDir != null);
            if (compiledDir != null) {
                out.writeUTF(compiledDir.getAbsolutePath());
            }
            out.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

    } */
    
      // Load the config file which contains the directory from which we last
        // loaded a model.
    /*try {
            File lastModelFile = getLastModelFile();
            if (lastModelFile.exists()) {
                DataInputStream in = new DataInputStream(
                        new FileInputStream(lastModelFile));
                String str;
                if (in.readBoolean()) {
                    str = in.readUTF();
                    lastModelDir = new File(str);
                } else {
                    lastModelDir = null;
                }

                if (in.readBoolean()) {
                    str = in.readUTF();
                    compiledDir = new File(str);
                } else {
                    compiledDir = null;
                }
                in.close();
            }
        } catch (Exception ex) {
            lastModelDir = null;
            LOGGER.log(Level.INFO, null, ex);
        }*/
    
}
