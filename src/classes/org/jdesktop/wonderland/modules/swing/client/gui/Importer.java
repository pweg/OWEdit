/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jdesktop.wonderland.modules.swing.client.gui;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.ZBufferState;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBException;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.NewFrameCondition;
import org.jdesktop.mtgame.ProcessorArmingCollection;
import org.jdesktop.mtgame.ProcessorComponent;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.RenderManager;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.CellEditChannelConnection;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.client.jme.artimport.DeployedModel;
import org.jdesktop.wonderland.client.jme.artimport.ImportSettings;
import org.jdesktop.wonderland.client.jme.artimport.ImportedModel;
import org.jdesktop.wonderland.client.jme.artimport.LoaderManager;
import org.jdesktop.wonderland.client.jme.artimport.ModelLoader;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.modules.ModuleUtils;
import org.jdesktop.wonderland.common.FileUtils;
import org.jdesktop.wonderland.common.cell.CellEditConnectionType;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.messages.CellCreateMessage;
import org.jdesktop.wonderland.common.login.AuthenticationInfo;
import org.jdesktop.wonderland.common.modules.ModuleInfo;
import org.jdesktop.wonderland.common.modules.ModuleList;
import org.jdesktop.wonderland.common.modules.ModuleUploader;
import org.jdesktop.wonderland.common.modules.utils.ModuleJarWriter;
import org.jdesktop.wonderland.modules.swing.client.TListener;

/**
 * ModelImporterFrame
 * @author Patrick
 */
public class Importer  extends javax.swing.JFrame{
    
    MainWindow main = null;
    
     private  Listener listener = null;
     
    private ImportSettings importSettings = null;
    
    
    private JButton butt = null;
    private JTextField btext = null;
     
    private JButton okButt = null;
    private JButton cancelButt = null;
    private JTextField name = null;
    
    private ImportedModel importedModel = null;
     
    private javax.swing.JPanel loadingDialogPanel;
    
    private TransformProcessorComponent transformProcessor = null;
    
    private JTextField x = null;
    private JTextField y = null;
    private JTextField z =null;
    private javax.swing.JComboBox targetServerSelector;
    private Node rootBG;
    
        private Node modelBG;
    
    Importer(MainWindow m){
        
        loadingDialogPanel = new javax.swing.JPanel();
        loadingDialogPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        loadingDialogPanel.setMinimumSize(new java.awt.Dimension(215, 100));
        loadingDialogPanel.setLayout(new java.awt.GridBagLayout());
        
        this.setLayout(new GridLayout(0,1));
        final JPanel pan = new JPanel();
        pan.setLayout(new GridLayout(0,2));
        name = new JTextField();
         name.setSize(250, 25);
        pan.add(name);
        
        main = m;
        setTitle("Importer");
        listener = new Listener();
        
        butt = new JButton("chose file");
        butt.addActionListener(listener);
        butt.setName("fileselector");
        pan.add(butt);
        this.add(pan);
        
        btext = new JTextField("");
        btext.setEditable(false);
        btext.setSize(250, 25);
        this.add(btext);
        
        final JPanel pan3 = new JPanel();
        pan3.setLayout(new GridLayout(0,2));
        okButt = new JButton("OK");
        okButt.addActionListener(listener);
        pan3.add(okButt);
        
        cancelButt = new JButton("Cancel");
        cancelButt.addActionListener(listener);
        pan3.add(cancelButt);
        
        
        final JPanel pan4 = new JPanel();
        pan4.setLayout(new GridLayout(0,2));
        x = new JTextField("x");
        y = new JTextField("y");
        z = new JTextField("z");
         x.setSize(25, 25);
          y.setSize(25, 25);
           z.setSize(25, 25);
        pan4.add(x);
        pan4.add(y);
        pan4.add(z);
        this.add(pan4);
        
        targetServerSelector = new javax.swing.JComboBox();
        Collection<ServerSessionManager> servers = LoginManager.getAll();
        for (ServerSessionManager server : servers) {
            targetServerSelector.addItem(server);
        }
        this.add(targetServerSelector);
        
        this.setSize(500, 400);
        this.add(pan3);
        pack();
    }
    
    class Listener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if(e.getSource().equals(butt)){
                importModel();
                importedModel.setWonderlandName(name.getText());
                
            }else if (e.getSource().equals(okButt)){
                
                float x1 = Float.parseFloat(x.getText());
                float y1 = Float.parseFloat(y.getText());
                float z1 = Float.parseFloat(z.getText());
                
                
                
                Vector3f trans = new Vector3f(x1, y1, z1);
                importedModel.setTranslation(trans);
                WorldManager wm = ClientContextJME.getWorldManager();
                rootBG.setLocalTranslation(trans);
                wm.addToUpdateList(rootBG);
                
                /*if (transformProcessor != null) {
                    Vector3f bla = importedModel.getOrientation();
                    Matrix3f rotation = calcRotationMatrix(
                        (float) 0,
                        (float) 0,
                        (float) 0);
                    
               /* JOptionPane.showMessageDialog(main,
                        "Bla?"+trans.toString() + " " +bla.toString());
                        transformProcessor.setTransform(
                                rotation, trans);
                    }*/
                    
                 deployToServerBActionPerformed();
                 setVisible(false);
            }else if (e.getSource().equals(cancelButt)){
                WorldManager wm = ClientContextJME.getWorldManager();
                wm.removeEntity(importedModel.getEntity());
                
                setVisible(false);
            }
        }
    }
    
    private void importModel(){
        okButt.setEnabled(false);
        cancelButt.setEnabled(false);
        
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
              LoaderManager.getLoaderManager().getLoaderExtensions());
        chooser.setFileFilter(filter);
        if (btext.getText() != null && btext.getText().equals("")) {
            
                    chooser.setCurrentDirectory(new File(btext.getText()).getParentFile());
        }
        
        int returnVal = chooser.showOpenDialog(Importer.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                btext.setText(chooser.getSelectedFile().toString());
               importModel(chooser.getSelectedFile());
            } catch (FileNotFoundException ex) {
                System.out.println("Bla");
            } catch (IOException ioe) {
                System.out.println("Bla");

            }
                   
                    
        }
    }
    
    void importModel(final File file)
            throws IOException {

        importSettings = new ImportSettings(file.toURI().toURL());
        asyncLoadModel(importSettings, file);
            

    }
    
   synchronized void asyncLoadModel(final ImportSettings settings, final File file) {
        final JDialog loadingDialog = new JDialog(this);
        loadingDialog.setLayout(new BorderLayout());
        loadingDialog.add(loadingDialogPanel, BorderLayout.CENTER);
        loadingDialog.pack();
        loadingDialog.setSize(200, 100);
        loadingDialog.setVisible(true);
        loadingDialog.setAlwaysOnTop(true);

        try{
        ImportedModel loadedModel = loadModel(settings);
        loadComplete(loadedModel, file);
                    
        } catch (IOException ex) {
                   return;
       } finally {
                    loadingDialog.setVisible(false);
                    loadingDialog.dispose();
       }
    }
                
        public void loadComplete(ImportedModel impmod, File origFile) {
                importedModel = impmod;
                Entity entity = importedModel.getEntity();

                entity.addComponent(LoadCompleteProcessor.class,
                        new LoadCompleteProcessor(importedModel));

                String dir = origFile.getAbsolutePath();
                dir = dir.substring(0, dir.lastIndexOf(File.separatorChar));
                dir = dir.substring(dir.lastIndexOf(File.separatorChar) + 1);

                String filename = origFile.getAbsolutePath();
                filename = filename.substring(
                        filename.lastIndexOf(File.separatorChar) + 1);
                filename = filename.substring(0, filename.lastIndexOf('.'));
                name.setText(filename);
                okButt.setEnabled(true);
                cancelButt.setEnabled(true);

            }
    
    
    ImportedModel loadModel(ImportSettings settings) throws IOException {
        rootBG = new Node();

        URL url = settings.getModelURL();

        Node modelBG = null;

        ModelLoader modelLoader =
                LoaderManager.getLoaderManager().getLoader(url);


        if (modelLoader == null) {
            String urlString = url.toExternalForm();
            String fileExtension = FileUtils.getFileExtension(urlString);
            return null;
        }

        ImportedModel loadedModel = modelLoader.importModel(settings);
        modelBG = loadedModel.getModelBG();

        rootBG.attachChild(modelBG);

        WorldManager wm = ClientContextJME.getWorldManager();

        RenderManager renderManager = wm.getRenderManager();
        //ZBufferState buf = (ZBufferState) renderManager.createRendererState(
        //        RenderState.RS_ZBUFFER);
        //buf.setEnabled(true);
        //buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);

        //MaterialState matState =
        //        (MaterialState) renderManager.createRendererState(
         //       RenderState.RS_MATERIAL);
//        matState.setDiffuse(color);
        //rootBG.setRenderState(matState);
        //rootBG.setRenderState(buf);

        Entity entity = new Entity(loadedModel.getWonderlandName());
        RenderComponent scene = renderManager.createRenderComponent(rootBG);
        entity.addComponent(RenderComponent.class, scene);

        scene.setLightingEnabled(loadedModel.getImportSettings().isLightingEnabled());
        transformProcessor = new TransformProcessorComponent(wm, modelBG, rootBG);
        entity.addComponent(TransformProcessorComponent.class,
                transformProcessor);

        wm.addEntity(entity);
        loadedModel.setEntity(entity);

//        findTextures(modelBG);

        return loadedModel;
    }
    
    private void deployToServerBActionPerformed() {                                                

        String moduleName = name.getText();
        final ArrayList<DeployedModel> deploymentInfo = new ArrayList();
        WorldManager wm = ClientContextJME.getWorldManager();
        final ServerSessionManager targetServer =
                (ServerSessionManager) targetServerSelector.getSelectedItem();

        // Check we are not about to overwrite an existing module
        String url = targetServer.getServerURL();
        ModuleList moduleList = ModuleUtils.fetchModuleList(url);
        ModuleInfo[] modules = moduleList.getModuleInfos();
        if (modules != null) {
            boolean conflict = false;
            for (int i = 0; i < modules.length && !conflict; i++) {
                if (moduleName.equals(modules[i].getName())) {
                    conflict = true;
                }
            }

            if (conflict) {
                int ret = JOptionPane.showConfirmDialog(this,
                        "conflicticus. Overwrite?",
                        "Conflict",
                        JOptionPane.YES_NO_OPTION);
                if (ret == JOptionPane.NO_OPTION) {
                    return;
                }
            }
        }

        final File moduleJar = createModuleJar(deploymentInfo, null);

        // Now deploy to server
        Thread t = new Thread() {
            private JDialog uploadingDialog;
            @Override
            public void run() {
                try {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {

                            public void run() {
                                uploadingDialog = new JDialog(main);
                                    uploadingDialog.setLayout(new BorderLayout());
                                    uploadingDialog.add(loadingDialogPanel, BorderLayout.CENTER);
                                    uploadingDialog.pack();
                                    uploadingDialog.setSize(200, 100);
                                    uploadingDialog.setVisible(true);
                                    uploadingDialog.setAlwaysOnTop(true);
                            }
                        });
                    } catch (InterruptedException ex) {
                        
                    } catch (InvocationTargetException ex) {
                        
                    }

                    ModuleUploader uploader = new ModuleUploader(new URL(targetServer.getServerURL()));

                    // if the authentication type is NONE, don't authenticate,
                    // since the upload will only accept modules from an
                    // administrator.
                    // XXX TODO: we should fix this so that upload writes to
                    // the content repository, so that non-admin users can
                    // upload art when authenication is turned on XXX
                    if (targetServer.getDetails().getAuthInfo().getType() !=
                            AuthenticationInfo.Type.NONE)
                    {
                        uploader.setAuthURL(targetServer.getCredentialManager().getAuthenticationURL());
                    }

                    uploader.upload(moduleJar);
                } catch (MalformedURLException ex) {
                   
                    return;
                } catch (IOException e) {
                    
                    return;
                } catch (Throwable t) {
                    
                    return;
                }

                // Now create the cells for the new content
                WonderlandSession session =
                        LoginManager.getPrimary().getPrimarySession();
                CellEditChannelConnection connection =
                        (CellEditChannelConnection) session.getConnection(
                        CellEditConnectionType.CLIENT_TYPE);
                for (DeployedModel info : deploymentInfo) {
                    CellID parentCellID = null;
                    CellCreateMessage msg = new CellCreateMessage(
                            parentCellID, info.getCellServerState());
                    connection.send(msg);
                }

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        uploadingDialog.setVisible(false);
                        uploadingDialog.dispose();
                    }
                });
            }
        };
        t.start();

        // Remove entities, once we create the cells on the server we
        // will be sent the client cells
            wm.removeEntity(importedModel.getEntity());
        

        importedModel = null;
    }
    
    private File createModuleJar(
            ArrayList<DeployedModel> deploymentInfo, File targetDir) {

        File moduleJar = null;
        String moduleName = name.getText();

        try {
            File tmpDir = File.createTempFile("wlart", null);
            if (tmpDir.isDirectory()) {
                FileUtils.deleteDirContents(tmpDir);
            } else {
                tmpDir.delete();
            }
            tmpDir.mkdir();
            tmpDir = new File(tmpDir, name.getText());

           
                try {
                    importedModel.setDeploymentBaseURL("wla://"+moduleName+"/");
                    deploymentInfo.add(importedModel.getModelLoader().deployToModule(
                            tmpDir, importedModel));
                } catch (IOException ex) {
                }
            

            ModuleJarWriter mjw = new ModuleJarWriter();
            File[] dirs = tmpDir.listFiles();
            if (dirs != null) {
                for (File f : dirs) {
                    if (f.isDirectory()) {
                        mjw.addDirectory(f);
                    }
                }
            }
            ModuleInfo mi =
                    new ModuleInfo(moduleName, 1, 0, 0, "This is description");
            mjw.setModuleInfo(mi);
            try {
                if (targetDir == null) {
                    targetDir = tmpDir.getParentFile();
                }
                moduleJar = new File(targetDir, moduleName + ".jar");
                mjw.writeToJar(moduleJar);
            } catch (IOException ex) {
            } catch (JAXBException ex) {
            }

            if (moduleJar == null) {
                JOptionPane.showMessageDialog(this,
                        "Module jar errroror",
                        "some error",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } catch (IOException ex) {
            return null;
        }

        return moduleJar;

}  
    
    
    /**
     * Case independent filename extension filter
     */
    class FileNameExtensionFilter extends FileFilter {

        private HashSet<String> extensions;
        private String description;

        public FileNameExtensionFilter(String ext) {
            extensions = new HashSet();
            extensions.add(ext);
            description = new String(ext);
        }

        public FileNameExtensionFilter(String[] ext) {
            extensions = new HashSet();
            StringBuffer desc = new StringBuffer();
            for (String e : ext) {
                extensions.add(e);
                desc.append(e + ", ");
            }
            description = desc.toString();
        }

        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                return true;
            }
            String e = pathname.getName();
            e = e.substring(e.lastIndexOf('.') + 1);
            if (extensions.contains(e.toLowerCase())) {
                return true;
            }

            return false;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }
    class LoadCompleteProcessor extends ProcessorComponent {

        private ImportedModel importedModel;

        public LoadCompleteProcessor(ImportedModel importedModel) {
            this.importedModel = importedModel;

        }

        @Override
        public void compute(ProcessorArmingCollection arg0) {
            processBounds(importedModel.getModelBG());

            //populateTextureList(importedModel.getModelBG());

            importedModel.getEntity().removeComponent(
                    LoadCompleteProcessor.class);
            setArmingCondition(null);
        }

        @Override
        public void commit(ProcessorArmingCollection arg0) {
        }

        @Override
        public void initialize() {
            setArmingCondition(new NewFrameCondition(this));
        }
    }
    
    private void processBounds(Node bg) {
//        System.err.println("Model Node "+bg);

        if (bg == null) {
            return;
        }

        BoundingVolume bounds = bg.getWorldBound();

        if (bounds == null) {
            bounds = calcBounds(bg);
        }

        // Remove the rotation from the bounds because it will be reapplied by
        // the cell
//        Quaternion rot = bg.getWorldRotation();
//        rot.inverseLocal();
//        bounds = bounds.transform(rot, new Vector3f(), new Vector3f(1,1,1), bounds);
//
//        System.err.println("ROTATED "+bounds);
//        System.err.println(rot.toAngleAxis(null));

        if (bounds instanceof BoundingSphere) {
            BoundingSphere sphere = (BoundingSphere) bounds;
            Vector3f center = new Vector3f();
            sphere.getCenter(center);
            x.setText(Double.toString(center.x));
            y.setText(Double.toString(center.y));
            z.setText(Double.toString(center.z));
            //boundsSizeXTF.setText(Double.toString(sphere.getRadius()));
            //boundsSizeYTF.setText("N/A Sphere");
            //boundsSizeZTF.setText("N/A Sphere");
        } else if (bounds instanceof BoundingBox) {
            BoundingBox box = (BoundingBox) bounds;
            Vector3f center = new Vector3f();
            box.getCenter();
            x.setText(Double.toString(center.x));
            y.setText(Double.toString(center.y));
            z.setText(Double.toString(center.z));

            //boundsSizeXTF.setText(Float.toString(box.xExtent));
            //boundsSizeYTF.setText(Float.toString(box.yExtent));
            //boundsSizeZTF.setText(Float.toString(box.zExtent));
        }
    }
    BoundingVolume calcBounds(Spatial n) {
        BoundingVolume bounds = null;

        if (n instanceof Geometry) {
            bounds = new BoundingBox();
            bounds.computeFromPoints(((Geometry) n).getVertexBuffer());

            bounds.transform(
                    n.getLocalRotation(),
                    n.getLocalTranslation(),
                    n.getLocalScale());
        }

        if (n instanceof Node && ((Node) n).getQuantity() > 0) {
            for (Spatial child : ((Node) n).getChildren()) {
                BoundingVolume childB = calcBounds(child);
                if (bounds == null) {
                    bounds = childB;
                } else {
                    bounds.mergeLocal(childB);
                }
            }
        }

        if (bounds != null) {
            bounds.transform(
                    n.getLocalRotation(),
                    n.getLocalTranslation(),
                    n.getLocalScale(),
                    bounds);
        }
//        Vector3f axis = new Vector3f();
//        float angle = n.getLocalRotation().toAngleAxis(axis);
//        System.err.println("Applying transform "+n.getLocalTranslation()+"  "+angle+"  "+axis);
//        System.err.println("BOunds "+bounds);

        return bounds;
    }
    
    public static Matrix3f calcRotationMatrix(float x, float y, float z) {
        Matrix3f m3f = new Matrix3f();
        m3f.loadIdentity();
        m3f.fromAngleAxis(x, new Vector3f(1f, 0f, 0f));
        Matrix3f rotY = new Matrix3f();
        rotY.loadIdentity();
        rotY.fromAngleAxis(y, new Vector3f(0f, 1f, 0f));
        Matrix3f rotZ = new Matrix3f();
        rotZ.loadIdentity();
        rotZ.fromAngleAxis(z, new Vector3f(0f, 0f, 1f));

        m3f.multLocal(rotY);
        m3f.multLocal(rotZ);

        return m3f;
    }
}
