/*
 * Copyright (c) 2009 by Thomas Busey and Ruj Akavipat
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Experteyes nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY Thomas Busey and Ruj Akavipat ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Thomas Busey and Ruj Akavipat BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/*
 * SynchronizeJPanel.java  
 *
 * Created on September 10, 2007, 11:49 AM
 */
package eyetrackercalibrator.gui;

import eyetrackercalibrator.framemanaging.FrameManager;
import eyetrackercalibrator.framemanaging.ScreenFrameManager;
import eyetrackercalibrator.framemanaging.SynchronizationPoint;
import eyetrackercalibrator.gui.util.AnimationTimer;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * To use this Panel.  The "start" method must be explicitly called after 
 * initialization is completed.  Before disposing of this panel, "stop" method
 * must be called to stop animation timer from continue firing.
 * @author  eeglab
 */
public class SynchronizeJPanel extends javax.swing.JPanel {

    private AnimationTimer timer;
    private FrameManager eyeFrameManager = null;
    private FrameManager screenFrameManager = null;
    DefaultListModel synchPointSet = new DefaultListModel();
    public static final String SYNC_LIST_ELEMENT = "synchronization";

    /** Creates new form SynchronizeJPanel */
    public SynchronizeJPanel() {
        initComponents();

        // Set up animation timer
        timer = new AnimationTimer();
        timer.setDisplayJPanel(displayJPanel);
        timer.setEyeFrameScrollingJPanel(eyeFrameScrollingJPanel);
        timer.setScreenFrameScrollingJPanel(screenFrameScrollingJPanel);
    }

    /** For starting animation */
    public void start() {
        timer.start();
    }

    /** For stoping animation */
    public void stop() {
        timer.stop();
    }

    public int getEyeViewCurrentFrame() {
        return eyeFrameScrollingJPanel.getCurrentFrame();
    }

    public void setEyeViewCurrentFrame(int eyeViewCurrentFrame) {
        eyeFrameScrollingJPanel.setCurrentFrame(eyeViewCurrentFrame);
    }

    public int getScreenViewCurrentFrame() {
        return screenFrameScrollingJPanel.getCurrentFrame();
    }

    public void setScreenViewCurrentFrame(int screenViewCurrentFrame) {
        screenFrameScrollingJPanel.setCurrentFrame(screenViewCurrentFrame);
    }

    public FrameManager getEyeFrameManager() {
        return eyeFrameManager;
    }

    public void setEyeFrameManager(FrameManager eyeFrameManager) {
        this.eyeFrameManager = eyeFrameManager;
        // Set total frame for control
        eyeFrameScrollingJPanel.setTotalFrame(eyeFrameManager.getTotalFrames());
        timer.setEyeFrameManager(eyeFrameManager);
    }

    public FrameManager getScreenFrameManager() {
        return screenFrameManager;
    }

    public void setScreenFrameManager(ScreenFrameManager screenFrameManager) {
        this.screenFrameManager = screenFrameManager;
        // Set total frame for control
        screenFrameScrollingJPanel.setTotalFrame(screenFrameManager.getTotalFrames());
        timer.setScreenFrameManager(screenFrameManager);
    }

    /**
     * Add actionlistener to process "Synchronize" and "Back" commands
     */
    public void addActionListener(ActionListener listener) {
        synchronizeButton.addActionListener(listener);
        cancelButton.addActionListener(listener);
    }

    /** 
     * This method populates the synchronization panel with points expressed in
     * XML element structure
     * The root element name does not matter but the contained elements must follows
     * format described by SynchronizationPoint class.
     * <root>
     *    <synchpoint1.../>
     *    <synchpoint2.../>
     *    ...
     * </root>
     */
    public void loadSynchronizationPoints(File synchronizationFile) {
        // Clear current sync points
        this.synchPointSet.clear();

        // Load from file
        SAXBuilder builder = new SAXBuilder();
        Element root = null;
        try {
            Document doc = builder.build(synchronizationFile);
            root = doc.getRootElement();
        } catch (Exception ex) {
            // Show message saying that there is an error
            JOptionPane.showMessageDialog(this, ex,
                    "Error Loading Synchronization Points", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (root != null) {
            List list = root.getChildren(SynchronizationPoint.XMLELEMENT);
            for (Object object : list) {
                try {
                    addSyncPoint(SynchronizationPoint.fromElement((Element) object));
                } catch (InstantiationException ex) {
                    // Show message saying that there is an error
                    JOptionPane.showMessageDialog(this, ex,
                            "Error Loading Synchronization Points", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Synchronization file " +
                    synchronizationFile.getAbsolutePath() + " is empty.",
                    "Error Loading Synchronization Points", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    /**
     * This method populates the synchronization panel with points expressed in
     * XML element structure the contained elements follow
     * format described by SynchronizationPoint class.
     * <synchronization>
     *    <synchpoint1.../>
     *    <synchpoint2.../>
     *    ...
     * </synchronization>
     */
    public void saveSynchronizationPoints(File synchronizationFile) {
        Element root = new Element(SYNC_LIST_ELEMENT);

        for (Enumeration<SynchronizationPoint> e =
                (Enumeration<SynchronizationPoint>) this.synchPointSet.elements();
                e.hasMoreElements();) {
            SynchronizationPoint p = e.nextElement();
            root.addContent(p.toXMLElement());
        }

        // Write out to file as xml
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        try {
            outputter.output(new Document(root), new FileWriter(synchronizationFile));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method add a sync point to the system
     */
    public void addSyncPoint(SynchronizationPoint sp) {
        // Find a place to add according to the eye frame
        int index = 0;
        for (Enumeration<SynchronizationPoint> e =
                (Enumeration<SynchronizationPoint>) this.synchPointSet.elements();
                e.hasMoreElements();) {
            SynchronizationPoint p = e.nextElement();
            if (p.equals(sp)) {
                // Do not add
                return;
            }
            if (p.eyeFrame > sp.eyeFrame || (p.eyeFrame <= sp.eyeFrame && p.sceneFrame > sp.sceneFrame)) {
                // We found the spot just add
                this.synchPointSet.add(index, sp);
                // We are done
                return;
            } else {
                // Keep searching when eyeframe and sceneframe is still smaller
                index++;
            }
        }
        this.synchPointSet.add(index, sp);
        return;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        displayJPanel = new eyetrackercalibrator.gui.DisplayJPanel();
        SynchPointList = new javax.swing.JPanel();
        addSynchPointButton = new javax.swing.JButton();
        removeSynchPointButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        synchronizePointList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        synchronizeButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        eyeFrameScrollingJPanel = new eyetrackercalibrator.gui.FrameScrollingJPanel();
        screenFrameScrollingJPanel = new eyetrackercalibrator.gui.FrameScrollingJPanel();

        SynchPointList.setMinimumSize(new java.awt.Dimension(10, 100));
        SynchPointList.setPreferredSize(new java.awt.Dimension(10, 321));

        addSynchPointButton.setText("+");
        addSynchPointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSynchPointButtonActionPerformed(evt);
            }
        });

        removeSynchPointButton.setText("-");
        removeSynchPointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeSynchPointButtonActionPerformed(evt);
            }
        });

        synchronizePointList.setModel(synchPointSet);
        jScrollPane1.setViewportView(synchronizePointList);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Frame:(eye, scene)");

        org.jdesktop.layout.GroupLayout SynchPointListLayout = new org.jdesktop.layout.GroupLayout(SynchPointList);
        SynchPointList.setLayout(SynchPointListLayout);
        SynchPointListLayout.setHorizontalGroup(
            SynchPointListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, SynchPointListLayout.createSequentialGroup()
                .add(34, 34, 34)
                .add(addSynchPointButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(removeSynchPointButton))
            .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
        );
        SynchPointListLayout.setVerticalGroup(
            SynchPointListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, SynchPointListLayout.createSequentialGroup()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(SynchPointListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(removeSynchPointButton)
                    .add(addSynchPointButton)))
        );

        synchronizeButton.setText("Synchronize");

        cancelButton.setText("Cancel");

        jPanel1.setLayout(new java.awt.GridLayout(1, 0, 5, 0));
        jPanel1.add(eyeFrameScrollingJPanel);
        jPanel1.add(screenFrameScrollingJPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(synchronizeButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(cancelButton))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(displayJPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 714, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 714, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(SynchPointList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 189, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(displayJPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(SynchPointList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelButton)
                    .add(synchronizeButton)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addSynchPointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSynchPointButtonActionPerformed
        // Get scene and eye and add to the list
        SynchronizationPoint sp = new SynchronizationPoint(
                this.eyeFrameScrollingJPanel.getCurrentFrame(),
                this.screenFrameScrollingJPanel.getCurrentFrame());
        addSyncPoint(sp);
    }//GEN-LAST:event_addSynchPointButtonActionPerformed

    private void removeSynchPointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeSynchPointButtonActionPerformed

        Object[] objs = this.synchronizePointList.getSelectedValues();
        for (int i = 0; i < objs.length; i++) {
            this.synchPointSet.removeElement(objs[i]);
        }
    }//GEN-LAST:event_removeSynchPointButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel SynchPointList;
    private javax.swing.JButton addSynchPointButton;
    private javax.swing.JButton cancelButton;
    private eyetrackercalibrator.gui.DisplayJPanel displayJPanel;
    private eyetrackercalibrator.gui.FrameScrollingJPanel eyeFrameScrollingJPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton removeSynchPointButton;
    private eyetrackercalibrator.gui.FrameScrollingJPanel screenFrameScrollingJPanel;
    private javax.swing.JButton synchronizeButton;
    private javax.swing.JList synchronizePointList;
    // End of variables declaration//GEN-END:variables

    public void clear() {
        this.synchPointSet.clear();
    }
}
