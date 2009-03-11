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
 * Main.java
 *
 * Created on September 19, 2007, 9:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package eyetrackercalibrator;

import eyetrackercalibrator.framemanaging.EyeViewFrameInfo;
import eyetrackercalibrator.framemanaging.FrameLoadingListener;
import eyetrackercalibrator.framemanaging.FrameManager;
import eyetrackercalibrator.framemanaging.InformationDatabase;
import eyetrackercalibrator.framemanaging.ScreenFrameManager;
import eyetrackercalibrator.framemanaging.ScreenViewFrameInfo;
import eyetrackercalibrator.gui.CalibrateJPanel;
import eyetrackercalibrator.gui.CleanDataJPanel;
import eyetrackercalibrator.gui.ErrorMarking;
import eyetrackercalibrator.gui.ExportMovieJFrame;
import eyetrackercalibrator.gui.NewProjectJDialog;
import eyetrackercalibrator.gui.ProjectSelectPanel;
import eyetrackercalibrator.gui.SynchronizeJPanel;
import eyetrackercalibrator.gui.TrialMarkingJPanel;
import eyetrackercalibrator.math.Computation;
import eyetrackercalibrator.math.ComputeIlluminationRangeThread;
import eyetrackercalibrator.math.EyeGazeComputing;
import eyetrackercalibrator.math.EyeGazeComputing.ComputingApproach;
import eyetrackercalibrator.trialmanaging.TrialMarker;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

/**
 * @author SQ
 */
public class Main extends javax.swing.JFrame {

    private CalibrateJPanel calibrateJPanel;
    private SynchronizeJPanel synchronizeJPanel;
    private ProjectSelectPanel projectSelectPanel;
    private CleanDataJPanel cleanDataJPanel;
    private TrialMarkingJPanel markTrialJPanel;
    private FrameManager eyeFrameManager = null;
    private ScreenFrameManager screenFrameManager = null;
    private EyeGazeComputing eyeGazeComputing = new EyeGazeComputing();
    private JMenuBar menuBar;
    private JMenu projectMenu;
    private JMenu viewSelectMenu;
    private JMenuItem new_MenuItem;
    private JMenuItem open_MenuItem;
    private JMenuItem save_MenuItem;
    private JMenuItem quit_MenuItem;
    private JRadioButtonMenuItem primaryMenuItem;
    private JRadioButtonMenuItem secondaryMenuItem;
    private JRadioButtonMenuItem linearMenuItem;
    private JMenu exportSelectMenu;
    private JMenuItem calibrationPointsExportMenuItem;
    private File projectLocation = null;
    static final public String EYE_OFFSET = "eyeoffset";
    static final public String SCREEN_OFFSET = "screenoffset";
    static final public String EYE_VIEW_DIRECTORY = "eyedirectory";
    static final public String EYE_INFO_DIRECTORY = "eyeinfodirectory";
    static final public String SCREEN_VIEW_DIRECTORY = "screendirectory";
    static final public String FULL_SCREEN_VIEW_DIRECTORY = "fullscreendirectory";
    static final public String SCREEN_INFO_DIRECTORY = "screeninfodirectory";
    static final public String EYE_LOAD_PROGRESS = "eyeloadprogress";
    static final public String SCREEN_LOAD_PROGRESS = "screenloadprogress";
    static final public String PROJECT_PROPERTY_FILE_NAME = "project.ini";
    static final public String CALIBRATION_FILE_NAME = "calibration.xml";
    static final public String ERROR_FILE_NAME = "errors.xml";
    static final public String TRIAL_FILE_NAME = "trial.xml";
    static final public String MONITOR_TRUE_WIDTH = "monitortruewidth";
    static final public String MONITOR_TRUE_HEIGHT = "monitortrueheight";
    static final public String FULL_SCREEN_WIDTH = "fullscreenwidth";
    static final public String FULL_SCREEN_HEIGHT = "fullscreenheight";
    static final public String COMMENT = "comment";
    static final public String PROPERTY_FILE = "eyetrackercalibrator.properties";
    static final public int ERROR_VALUE = -666;
    private static String DATABASE_NAME = "IlluminationDb";
    static final public String CORNERHINT_DIR = "CornerHints";
    private int DISPLAY_WIDTH = 512;
    private int DISPLAY_HEIGHT = 512;
    private InformationDatabase informationDatabase = null;
    private boolean isProjectOpen = false;

    /** Creates a new instance of Main */
    public Main() {
        initComponents();
    }

    /**
     * Close project. 
     * @return false when user cancel project closing
     */
    private boolean closeProject() {
        int n = JOptionPane.showConfirmDialog(
                this,
                "Would you like to save before exiting?",
                "Exiting program",
                JOptionPane.YES_NO_CANCEL_OPTION);
        switch (n) {
            case JOptionPane.CANCEL_OPTION:
                // Cancel closeing
                return false;
            case JOptionPane.YES_OPTION:
                // Save before exit
                save();
        }
        // Don't stop before save since we still need database during saving
        // Atop all animation
        synchronizeJPanel.stop();
        calibrateJPanel.stop();
        cleanDataJPanel.stop();
        markTrialJPanel.stop();

        // Close old project if active
        if (eyeFrameManager != null) {
            eyeFrameManager.close();
        }
        if (screenFrameManager != null) {
            screenFrameManager.close();
        }
        if (informationDatabase != null) {
            informationDatabase.close();
        }

        return true;
    }

    private void exportCalibrationPointInfo() {
        // Ask user where to put information
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            PrintWriter out = null;
            try {
                // Start exporting
                out = new PrintWriter(chooser.getSelectedFile());
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Cannot open " +
                        chooser.getSelectedFile().getAbsolutePath() + " for writing.",
                        "File Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Dimension trueScreenDimension = projectSelectPanel.getMonitorDimension();
            Dimension viewScreenDimension = projectSelectPanel.getFullScreenDimension();

            // Print header
            out.println(
                    "Screen Frame\t" +
                    "Scene X\t" +
                    "Scene Y\t" +
                    "Top Left X\t" +
                    "Top Left Y\t" +
                    "Top Right X\t" +
                    "Top Right Y\t" +
                    "Bottom Left X\t" +
                    "Bottom Left Y\t" +
                    "Bottom Right X\t" +
                    "Bottom Right Y\t" +
                    "Screen X\t" +
                    "Screen Y");

            int totalFrame = 0;
            if (screenFrameManager != null) {
                totalFrame = screenFrameManager.getTotalFrames();
            } else {
                return;
            }

            // Scan through each frame
            for (int i = 1; i <= totalFrame; i++) {
                Point calibrationPoint =
                        calibrateJPanel.frameToCalibrationPoint(i);
                if (calibrationPoint != null) {
                    // Found calibration points so output info
                    ScreenViewFrameInfo info =
                            (ScreenViewFrameInfo) screenFrameManager.getFrameInfo(i);


                    out.print(i);

                    Point[] point = info.getMarkedPoints();
                    Point scenePos = null;
                    if (point != null) {
                        scenePos = point[0];
                    }
                    printPointHelper(scenePos, out);

                    Point p = null;
                    Point[] corners = info.getCorners();
                    if (corners != null) {
                        p = corners[ScreenViewFrameInfo.TOPLEFT];
                        printPointHelper(p, out);
                        p = corners[ScreenViewFrameInfo.TOPRIGHT];
                        printPointHelper(p, out);
                        p = corners[ScreenViewFrameInfo.BOTTOMLEFT];
                        printPointHelper(p, out);
                        p = corners[ScreenViewFrameInfo.BOTTOMRIGHT];
                        printPointHelper(p, out);

                        // Estimate true screnn position
                        Point2D pos = Computation.ComputeScreenPositionProjective(
                                trueScreenDimension,
                                scenePos,
                                corners[ScreenViewFrameInfo.TOPLEFT],
                                corners[ScreenViewFrameInfo.TOPRIGHT],
                                corners[ScreenViewFrameInfo.BOTTOMLEFT],
                                corners[ScreenViewFrameInfo.BOTTOMRIGHT]);
                        printPointHelper(pos, out);

                        out.println();
                    } else {
                        // Print error
                        for (int j = 0; j < 10; i++) {
                            out.println("\t" + ERROR_VALUE);
                        }
                    }
                }
            }
            out.close();
        }
    }

    private Point2D.Double exportEyeGazes(PrintWriter exportWriter, int i,
            Double vector, ComputingApproach approach, Dimension screenViewFullSize) {
        Double point = new Point2D.Double(ERROR_VALUE, ERROR_VALUE);

        Point2D p = this.eyeGazeComputing.computeEyeGaze(i, vector.x, vector.y, approach);
        if (p != null) {
            point.setLocation(p);
            // Check the range and mark is with ERROR_VALUE,ERROR_VALUE if out of screen
            if (point.x < 0 || point.y < 0 || point.x > screenViewFullSize.width || point.y > screenViewFullSize.height) {
                point.setLocation(ERROR_VALUE, ERROR_VALUE);
            }
        }
        exportWriter.print(point.x + "\t" + point.y + "\t");

        return point;
    }

    private void exportMovies() {
        int eyeFrame, screenFrame;

        String num = this.projectSelectPanel.getSynchronizedEyeFrame();
        if (num != null) {
            eyeFrame = Integer.parseInt(num);
        } else {
            eyeFrame = 0;
        }

        num = this.projectSelectPanel.getSynchronizedScreenFrame();
        if (num != null) {
            screenFrame = Integer.parseInt(num);
        } else {
            screenFrame = 0;
        }

        ExportMovieJFrame exportMovieJFrame = new ExportMovieJFrame(
                projectLocation, DISPLAY_WIDTH, DISPLAY_HEIGHT,
                this.eyeGazeComputing,
                eyeFrame, screenFrame,
                eyeFrameManager, screenFrameManager);
        exportMovieJFrame.setVisible(true);
    }

    private void initComponents() {
        /** SEt windows behavior */
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                formWindowClosed();
            }
        });

        /* Set menu bar */
        menuBar = new javax.swing.JMenuBar();
        projectMenu = new javax.swing.JMenu("Project");
        new_MenuItem = new JMenuItem("New");
        open_MenuItem = new JMenuItem("Open");
        save_MenuItem = new JMenuItem("Save");
        quit_MenuItem = new JMenuItem("Quit");

        viewSelectMenu = new JMenu("View");
        primaryMenuItem = new JRadioButtonMenuItem("Primary");
        primaryMenuItem.setSelected(true);
        secondaryMenuItem = new JRadioButtonMenuItem("Secondary");
        linearMenuItem = new JRadioButtonMenuItem("Linear Interpolation");
        ButtonGroup viewGroup = new ButtonGroup();
        viewGroup.add(primaryMenuItem);
        viewGroup.add(secondaryMenuItem);
        viewGroup.add(linearMenuItem);

        exportSelectMenu = new JMenu("Export");
        calibrationPointsExportMenuItem = new JMenuItem("Calibration Points");

        new_MenuItem.setMnemonic(KeyEvent.VK_N);
        open_MenuItem.setMnemonic(KeyEvent.VK_O);
        save_MenuItem.setMnemonic(KeyEvent.VK_S);
        quit_MenuItem.setMnemonic(KeyEvent.VK_Q);

        ActionListener menuListener = new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuActionPerformed(evt);
            }
        };

        new_MenuItem.addActionListener(menuListener);
        open_MenuItem.addActionListener(menuListener);
        save_MenuItem.addActionListener(menuListener);
        quit_MenuItem.addActionListener(menuListener);

        menuListener = new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewMenuActionPerformed(evt);
            }
        };

        primaryMenuItem.addActionListener(menuListener);
        secondaryMenuItem.addActionListener(menuListener);
        linearMenuItem.addActionListener(menuListener);

        menuListener = new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportMenuActionPerformed(evt);
            }
        };

        calibrationPointsExportMenuItem.addActionListener(menuListener);

        projectMenu.add(new_MenuItem);
        projectMenu.add(open_MenuItem);
        projectMenu.add(save_MenuItem);
        projectMenu.add(quit_MenuItem);

        viewSelectMenu.add(primaryMenuItem);
        viewSelectMenu.add(secondaryMenuItem);
        viewSelectMenu.add(linearMenuItem);

        exportSelectMenu.add(calibrationPointsExportMenuItem);

        menuBar.add(projectMenu);
        menuBar.add(viewSelectMenu);
        menuBar.add(exportSelectMenu);
        setJMenuBar(menuBar);

        /* Interaction panel */
        calibrateJPanel = new CalibrateJPanel();
        synchronizeJPanel = new SynchronizeJPanel();
        projectSelectPanel = new ProjectSelectPanel();
        cleanDataJPanel = new CleanDataJPanel();
        markTrialJPanel = new TrialMarkingJPanel();

        calibrateJPanel.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calibrateJPanelActionPerformed(evt);
            }
        });
        synchronizeJPanel.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                synchronizeJPanelActionPerformed(evt);
            }
        });
        projectSelectPanel.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectSelectPanelActionPerformed(evt);
            }
        });
        cleanDataJPanel.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanDataJPanelActionPerformed(evt);
            }
        });
        markTrialJPanel.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                markTrialJPanelActionPerformed(evt);
            }
        });

        // Give all an eye gaze computation
        calibrateJPanel.setEyeGazeComputing(this.eyeGazeComputing);
        cleanDataJPanel.setEyeGazeComputing(this.eyeGazeComputing);
        markTrialJPanel.setEyeGazeComputing(this.eyeGazeComputing);

        projectSelectPanel.setEnabled(false);
        add(projectSelectPanel);
        pack();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    /* Handle when "Back" are pressed in calibration panel*/
    public void calibrateJPanelActionPerformed(java.awt.event.ActionEvent evt) {
        if ("Back".equals(evt.getActionCommand())) {
            // Stop animation
            calibrateJPanel.stop();

            // Go back to projectSelectPanel
            remove(calibrateJPanel);
            add(projectSelectPanel);
            pack();
        }
    }

    /** 
     * Handle when "Synchronize" and "Cancel" are pressed in synchronize panel
     * @param evt 
     */
    public void synchronizeJPanelActionPerformed(java.awt.event.ActionEvent evt) {
        boolean switchBack = false;
        if ("Synchronize".equals(evt.getActionCommand())) {
            // Read in synchronize data and set all offset in all panel
            setOffset(
                    synchronizeJPanel.getEyeViewCurrentFrame(),
                    synchronizeJPanel.getScreenViewCurrentFrame());

            switchBack = true;
        } else if ("Cancel".equals(evt.getActionCommand())) {
            switchBack = true;
        }
        if (switchBack) {
            // Stop animation
            synchronizeJPanel.stop();
            // Go back to projectSelectPanel
            remove(synchronizeJPanel);
            add(projectSelectPanel);
            pack();
        }
    }

    /** Handle when "Back" are pressed in clean data panel */
    public void cleanDataJPanelActionPerformed(java.awt.event.ActionEvent evt) {
        if ("Back".equals(evt.getActionCommand())) {
            // Stop animation
            cleanDataJPanel.stop();

            // Go back to projectSelectPanel
            remove(cleanDataJPanel);
            add(projectSelectPanel);
            pack();
        }
    }

    /** Handle when "Back" are pressed in mark trials panel */
    public void markTrialJPanelActionPerformed(java.awt.event.ActionEvent evt) {
        if ("Back".equals(evt.getActionCommand())) {
            // Stop animation
            markTrialJPanel.stop();

            // Go back to projectSelectPanel
            remove(markTrialJPanel);
            add(projectSelectPanel);
            pack();
        }
    }

    private void printPointHelper(Point2D p, PrintWriter out) {
        // Show corners
        if (p != null) {
            out.print("\t" + p.getX() + "\t" + p.getY());
        } else {
            out.print("\t" + ERROR_VALUE + "\t" + ERROR_VALUE);
        }
    }

    private void setOffset(
            int synchronizedEyeFrame,
            int synChronizedScreenFrame) {
        /** Information to keep */
        int eyeViewOffSet = 0;
        int screenViewOffSet = 0;

        // St offset value in relative fasion.
        if (synChronizedScreenFrame > synchronizedEyeFrame) {
            eyeViewOffSet = 0;
            screenViewOffSet = synChronizedScreenFrame - synchronizedEyeFrame;
        } else {
            screenViewOffSet = 0;
            eyeViewOffSet = synchronizedEyeFrame - synChronizedScreenFrame;
        }
        // Set display in project select what we chose
        projectSelectPanel.setSynchronizeFrames(synchronizedEyeFrame, synChronizedScreenFrame);

        // Set calibration offset
        calibrateJPanel.setOffSet(eyeViewOffSet, screenViewOffSet);

        // Set clean data offset
        cleanDataJPanel.setOffSet(eyeViewOffSet, screenViewOffSet);

        // Set mark trial offset
        markTrialJPanel.setOffSet(eyeViewOffSet, screenViewOffSet);
    }

    /**
     * Handle when "Load Eye Image", "Reload Eye Information",
     * "Load Screen Image", "Reload Screen Information",
     * "Synchronize", "Calibrate", "Clean data" and "Mark trials" buttons
     * are pressed.
     */
    public void projectSelectPanelActionPerformed(java.awt.event.ActionEvent evt) {
        screenFrameManager.setFrameDirectory(projectSelectPanel.getScreenFrameDirectory());
        eyeFrameManager.setFrameDirectory(projectSelectPanel.getEyeFrameDirectory());

        if ("Synchronize".equals(evt.getActionCommand())) {
            // Set up synchronized panel
            synchronizeJPanel.setEyeViewCurrentFrame(1);
            synchronizeJPanel.setScreenViewCurrentFrame(1);

            // need this to set the stat for frame playing (total frame and what not)
            synchronizeJPanel.setEyeFrameManager(eyeFrameManager);
            synchronizeJPanel.setScreenFrameManager(screenFrameManager);

            // Go to synchronized panel
            remove(projectSelectPanel);
            add(synchronizeJPanel);
            pack();

            // Start playing synchronize panel
            synchronizeJPanel.start();

        } else if ("Load Eye Images".equals(evt.getActionCommand())) {
            projectSelectPanel.setEyeLoadButtonsEnable(false);

            final Thread eyeThread = new Thread(new Runnable() {

                public void run() {
                    eyeFrameManager.loadFrames(
                            projectSelectPanel.getEyeFrameDirectory(),
                            projectSelectPanel.getEyeInfoDirectory());
                }
            });
            eyeThread.start();
            // Create a thred to wait and reenable the button
            Thread waitThread = new Thread(new Runnable() {

                public void run() {
                    try {
                        eyeThread.join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    projectSelectPanel.setEyeLoadButtonsEnable(true);
                }
            });
            waitThread.start();

        } else if ("Load Screen Images".equals(evt.getActionCommand())) {
            projectSelectPanel.setScreenLoadButtonsEnable(false);

            final Thread screenThread = new Thread(new Runnable() {

                public void run() {
                    screenFrameManager.loadFrames(
                            projectSelectPanel.getScreenFrameDirectory(),
                            projectSelectPanel.getScreenInfoDirectory());
                }
            });
            screenThread.start();
            // Create a thred to wait and reenable the button
            Thread waitThread = new Thread(new Runnable() {

                public void run() {
                    try {
                        screenThread.join();
                        updateFullScreenDimansion();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    projectSelectPanel.setScreenLoadButtonsEnable(true);
                }
            });
            waitThread.start();

        } else if ("Reload Eye Information".equals(evt.getActionCommand())) {
            projectSelectPanel.setEyeLoadButtonsEnable(false);
            final Thread eyeThread = new Thread(new Runnable() {

                public void run() {
                    eyeFrameManager.loadFrameInfo(
                            projectSelectPanel.getEyeFrameDirectory(),
                            projectSelectPanel.getEyeInfoDirectory());
                }
            });
            eyeThread.start();

            // Create a thred to wait and reenable the button
            Thread waitThread = new Thread(new Runnable() {

                public void run() {
                    try {
                        eyeThread.join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    projectSelectPanel.setEyeLoadButtonsEnable(true);
                }
            });
            waitThread.start();
        } else if ("Reload Screen Information".equals(evt.getActionCommand())) {
            projectSelectPanel.setScreenLoadButtonsEnable(false);

            final Thread screenThread = new Thread(new Runnable() {

                public void run() {
                    screenFrameManager.loadFrameInfo(
                            projectSelectPanel.getScreenFrameDirectory(),
                            projectSelectPanel.getScreenInfoDirectory());
                }
            });
            screenThread.start();
            // Create a thred to wait and reenable the button
            Thread waitThread = new Thread(new Runnable() {

                public void run() {
                    try {
                        screenThread.join();
                        updateFullScreenDimansion();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    projectSelectPanel.setScreenLoadButtonsEnable(true);
                }
            });
            waitThread.start();

        } else if ("Compute Screen Illumination".equals(evt.getActionCommand())) {
            projectSelectPanel.setScreenLoadButtonsEnable(false);

            final FrameLoadingListener frameLoadingListener =
                    projectSelectPanel.getScreenFrameLoadingListener();
            final int totalFrame = screenFrameManager.getTotalFrames();
            final ComputeIlluminationRangeThread computeIlluminationRangeThread =
                    new ComputeIlluminationRangeThread(screenFrameManager,
                    informationDatabase, 1, totalFrame,
                    new PropertyChangeListener() {

                        public void propertyChange(PropertyChangeEvent evt) {
                            Integer totalLoaded = (Integer) evt.getNewValue();
                            frameLoadingListener.update(totalLoaded + " of " + totalFrame,
                                    totalLoaded, totalFrame);
                        }
                    });
            computeIlluminationRangeThread.start();

            // Create a thred to wait and reenable the button
            Thread waitThread = new Thread(new Runnable() {

                public void run() {
                    try {
                        computeIlluminationRangeThread.join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    projectSelectPanel.setScreenLoadButtonsEnable(true);
                }
            });
            waitThread.start();

        } else {
            // Screen scaling must be recomputed before any of the following operations
            screenFrameManager.setScreenInfoScalefactor(computeScalingFactor());

            if ("Calibrate".equals(evt.getActionCommand())) {
                // need this to set the stat for frame playing (total frame and what not)
                calibrateJPanel.setEyeFrameManager(eyeFrameManager);
                calibrateJPanel.setScreenFrameManager(screenFrameManager);
                calibrateJPanel.setProjectRoot(projectLocation);
                calibrateJPanel.setFullScreenFrameDirectory(
                        projectSelectPanel.getFullScreenFrameDirectory());
                calibrateJPanel.setFullScreenDim(projectSelectPanel.getFullScreenDimension());
                calibrateJPanel.setGazeScaleFactor(
                        screenFrameManager.getScreenInfoScalefactor());
                calibrateJPanel.setEyeScreenInfoDirectory(
                        projectSelectPanel.getEyeInfoDirectory());

                remove(projectSelectPanel);
                add(calibrateJPanel);
                pack();

                calibrateJPanel.start();

            } else if ("Clean Data".equals(evt.getActionCommand())) {

                // Set calibrate panel offset
                cleanDataJPanel.setEyeGazeScaleFactor(
                        screenFrameManager.getScreenInfoScalefactor());

                // need this to set the stat for frame playing (total frame and what not)
                cleanDataJPanel.setEyeFrameManager(eyeFrameManager);
                cleanDataJPanel.setScreenFrameManager(screenFrameManager);
                cleanDataJPanel.setFullScreenFrameDirectory(
                        projectSelectPanel.getFullScreenFrameDirectory());
                cleanDataJPanel.setCornerHintDir(new File(projectLocation, CORNERHINT_DIR));
                cleanDataJPanel.setScreenInfoDir(projectSelectPanel.getScreenInfoDirectory());

                remove(projectSelectPanel);
                add(cleanDataJPanel);
                pack();

                cleanDataJPanel.start();
            } else if ("Mark Trials".equals(evt.getActionCommand())) {
//            @todo remove
                // Set calibrate panel offset
                markTrialJPanel.setEyeGazeScaleFactor(
                        screenFrameManager.getScreenInfoScalefactor());

                // need this to set the stat for frame playing (total frame and what not)
                markTrialJPanel.setEyeFrameManager(eyeFrameManager);
                markTrialJPanel.setScreenFrameManager(screenFrameManager);
                markTrialJPanel.setProjectRoot(projectLocation);
                markTrialJPanel.setFullScreenFrameDirectory(
                        projectSelectPanel.getFullScreenFrameDirectory());

                remove(projectSelectPanel);
                add(markTrialJPanel);
                pack();

                markTrialJPanel.start();
            } else if ("Export Data".equals(evt.getActionCommand())) {
                exportData();
            } else if ("Export Movies".equals(evt.getActionCommand())) {
                exportMovies();
            }
        }
    }

    private void menuActionPerformed(java.awt.event.ActionEvent evt) {
        JMenuItem item = (JMenuItem) evt.getSource();
        if ("New".equals(item.getText())) {
            performNewMenuAction();
        } else if ("Open".equals(item.getText())) {
            performOpenMenuAction();//pack();
        } else if ("Save".equals(item.getText())) {
            save();
        } else if ("Quit".equals(item.getText())) {
            formWindowClosed();
        }
    }

    private void viewMenuActionPerformed(ActionEvent evt) {
        JMenuItem item = (JMenuItem) evt.getSource();
        if (primaryMenuItem.getText().equals(item.getText())) {
            this.eyeGazeComputing.setComputingApproach(
                    EyeGazeComputing.ComputingApproach.PRIMARY);
        } else if (secondaryMenuItem.getText().equals(item.getText())) {
            this.eyeGazeComputing.setComputingApproach(
                    EyeGazeComputing.ComputingApproach.SECONDARY);
        } else if (linearMenuItem.getText().equals(item.getText())) {
            this.eyeGazeComputing.setComputingApproach(
                    EyeGazeComputing.ComputingApproach.LINEAR);
        }
    }

    private void exportMenuActionPerformed(ActionEvent evt) {
        JMenuItem item = (JMenuItem) evt.getSource();
        if (calibrationPointsExportMenuItem.getText().equals(item.getText())) {
            // Export menu action
            exportCalibrationPointInfo();
        }
    }

    /** Handel new project menu action */
    private void performNewMenuAction() {
        NewProjectJDialog dialog = new NewProjectJDialog(this, "Create new project", true);
        dialog.addActionListener(new ProjectCreateActionListener(dialog));
        dialog.setVisible(true);
    }

    /**Class to help with handling dialog*/
    private class ProjectCreateActionListener implements ActionListener {

        NewProjectJDialog dialog;

        ProjectCreateActionListener(NewProjectJDialog d) {
            dialog = d;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getActionCommand().equals("Create")) {
                // Check if the directory already exists
                File location = new File(dialog.getProjectLocation());
                if (!location.exists()) {
                    location.mkdirs();
                }
                // Reset offset
                setOffset(0, 0);

                // Load in information
                openProject(location);
            } else {
                // Does nothing
            }
            dialog.setVisible(false);
            dialog.dispose();
            // Remove dialog
            dialog = null;
        }
    }

    /** Handel open project menu action */
    private void performOpenMenuAction() {
        // Choose new directory to put the project
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            boolean isNotCancel = true;

            // Stop everything from old project
            if (isProjectOpen) {
                isNotCancel = closeProject();
            }

            if (isNotCancel) {
                // Switch to project select view in disable mode
                projectSelectPanel.setEnabled(false);
                remove(projectSelectPanel);
                remove(synchronizeJPanel);
                remove(calibrateJPanel);
                remove(cleanDataJPanel);
                remove(markTrialJPanel);
                add(projectSelectPanel);
                pack();
                // Switch to selected project
                openProject(fileChooser.getSelectedFile());
            }
        }
    }

    /** Open a project. */
    private void openProject(File projectLocation) {
        this.isProjectOpen = true;

        // Set project location
        this.projectLocation = projectLocation;

        // Load project properties if exists
        Properties p = new Properties();
        try {
            p.loadFromXML(new FileInputStream(new File(projectLocation, PROJECT_PROPERTY_FILE_NAME)));
        } catch (FileNotFoundException ex) {
            //ex.printStackTrace();
            System.err.println("Cannot load project property file.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        setOffset(Integer.parseInt(p.getProperty(EYE_OFFSET, "0")), Integer.parseInt(p.getProperty(SCREEN_OFFSET, "0")));

        projectSelectPanel.setEyeFrameDirectory(p.getProperty(EYE_VIEW_DIRECTORY));
        projectSelectPanel.setEyeInfoDirectory(p.getProperty(EYE_INFO_DIRECTORY));
        projectSelectPanel.setScreenFrameDirectory(p.getProperty(SCREEN_VIEW_DIRECTORY));
        projectSelectPanel.setFullScreenFrameDirectory(p.getProperty(FULL_SCREEN_VIEW_DIRECTORY));
        projectSelectPanel.setScreenInfoDirectory(p.getProperty(SCREEN_INFO_DIRECTORY));
        projectSelectPanel.setMonitorDimension(p.getProperty(MONITOR_TRUE_WIDTH, ""), p.getProperty(MONITOR_TRUE_HEIGHT, ""));
        projectSelectPanel.setFullScreenDimension(p.getProperty(FULL_SCREEN_WIDTH, ""), p.getProperty(FULL_SCREEN_HEIGHT, ""));
        projectSelectPanel.setComment(p.getProperty(COMMENT, ""));

        // Trying to determine full screen dimension
        try {
            eyeFrameManager = new FrameManager(projectLocation.getAbsolutePath() + File.separator + "EyeViewCacheDB", 512, 512, new EyeViewFrameInfo());
            // Open a project
            screenFrameManager = new ScreenFrameManager(projectLocation.getAbsolutePath() + File.separator + "ScreenViewCacheDB", 512, 512, new ScreenViewFrameInfo());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error open project", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Set up scaling factor of the screen info
        updateFullScreenDimansion();

        // Set up scaling factor of the screen info
        Dimension d = projectSelectPanel.getFullScreenDimension();

        // Assign frame manager to all panel
        synchronizeJPanel.setEyeFrameManager(eyeFrameManager);
        synchronizeJPanel.setScreenFrameManager(screenFrameManager);
        calibrateJPanel.setEyeFrameManager(eyeFrameManager);
        calibrateJPanel.setScreenFrameManager(screenFrameManager);
        cleanDataJPanel.setEyeFrameManager(eyeFrameManager);
        cleanDataJPanel.setScreenFrameManager(screenFrameManager);
        markTrialJPanel.setEyeFrameManager(eyeFrameManager);
        markTrialJPanel.setScreenFrameManager(screenFrameManager);

        // Load calibration file if exists
        File calibrationFile = new File(projectLocation, CALIBRATION_FILE_NAME);
        if (calibrationFile.exists()) {
            calibrateJPanel.loadCalibrationPoints(calibrationFile);
        } else {
            calibrateJPanel.clearCalibrationInfo();
        }
        // Set up calibration full screen dia
        calibrateJPanel.setFullScreenDim(d);


        // Set up eye gaze coeff
        this.eyeGazeComputing.setPrimaryEyeCoeff(calibrateJPanel.getEyeGazeCoefficient(0));
        this.eyeGazeComputing.setSecondaryEyeCoeff(calibrateJPanel.getEyeGazeCoefficient(1));

        // Load error file if exists
        File errorFile = new File(projectLocation, ERROR_FILE_NAME);
        if (errorFile.exists()) {
            cleanDataJPanel.loadErrors(errorFile);
        } else {
            cleanDataJPanel.clear();
        }

        // Load trial file if exists
        File trialMarkFile = new File(projectLocation, TRIAL_FILE_NAME);
        if (trialMarkFile.exists()) {
            markTrialJPanel.loadTrialMarks(trialMarkFile);
        } else {
            markTrialJPanel.clear();
        }

        // Link frame manager to update progress to project panel
        eyeFrameManager.setLoadingListener(projectSelectPanel.getEyeFrameLoadingListener());
        screenFrameManager.setLoadingListener(projectSelectPanel.getScreenFrameLoadingListener());

        // SEt project title
        this.setTitle(projectLocation.getName());

        // Open illumination database
        try {
            File databaseLocation = new File(projectLocation, DATABASE_NAME);
            this.informationDatabase = new InformationDatabase(databaseLocation.getAbsolutePath());
            // Give illumination database to mark trial panel
            markTrialJPanel.setInformationDatabase(informationDatabase);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        projectSelectPanel.setEnabled(true);

        pack();
    }

    /**
     * Compute scaling factor
     */
    private double computeScalingFactor() {

        // Get fullscreen dimension
        Dimension d = projectSelectPanel.getFullScreenDimension();
        // Get the first image file
        BufferedImage image = screenFrameManager.getFrame(1);

        // Only do anything when there is somthing to compute
        if (image == null || d == null) {
            return 1d;
        } else {
            return ((double)image.getWidth()) / ((double) d.width);
        }
    }

    private void formWindowClosed() {
        boolean isNotCanceled = true;
        if (isProjectOpen) {
            isNotCanceled = closeProject();
        }
        if (isNotCanceled) {
            System.exit(0);
        }
    }

    private void save() {
        // Get new screen dimension
        Dimension d = projectSelectPanel.getMonitorDimension();
        calibrateJPanel.setFullScreenDim(d);

        // Save calibration points
        calibrateJPanel.saveCalibrationPoints(new File(projectLocation, CALIBRATION_FILE_NAME));

        // Save error information
        cleanDataJPanel.saveErrors(new File(projectLocation, ERROR_FILE_NAME));

        // Save trial mark information
        markTrialJPanel.saveTrialMarks(new File(projectLocation, TRIAL_FILE_NAME));

        // Save project property
        Properties p = new Properties();
        p.setProperty(EYE_OFFSET,
                projectSelectPanel.getSynchronizedEyeFrame());
        p.setProperty(SCREEN_OFFSET,
                projectSelectPanel.getSynchronizedScreenFrame());
        p.setProperty(EYE_VIEW_DIRECTORY, projectSelectPanel.getEyeFrameDirectory());
        p.setProperty(EYE_INFO_DIRECTORY, projectSelectPanel.getEyeInfoDirectory());
        p.setProperty(SCREEN_VIEW_DIRECTORY, projectSelectPanel.getScreenFrameDirectory());
        p.setProperty(FULL_SCREEN_VIEW_DIRECTORY, projectSelectPanel.getFullScreenFrameDirectory());
        p.setProperty(SCREEN_INFO_DIRECTORY, projectSelectPanel.getScreenInfoDirectory());
        p.setProperty(COMMENT, projectSelectPanel.getComment());
        if (d != null) {
            p.setProperty(MONITOR_TRUE_HEIGHT, String.valueOf(d.height));
            p.setProperty(MONITOR_TRUE_WIDTH, String.valueOf(d.width));
        }
        d = projectSelectPanel.getFullScreenDimension();
        if (d != null) {
            p.setProperty(FULL_SCREEN_HEIGHT, String.valueOf(d.height));
            p.setProperty(FULL_SCREEN_WIDTH, String.valueOf(d.width));
        }
        try {
            p.storeToXML(new FileOutputStream(new File(this.projectLocation,
                    PROJECT_PROPERTY_FILE_NAME)), null);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Exporting information to files
     */
    private void exportData() {
        // Gather information 

        // Get offset
        int eyeSynchFrame = 0;
        String s = projectSelectPanel.getSynchronizedEyeFrame();
        if (s != null) {
            eyeSynchFrame = Integer.parseInt(s);
        }
        int screenSynchFrame = 0;
        s = projectSelectPanel.getSynchronizedScreenFrame();
        if (s != null) {
            screenSynchFrame = Integer.parseInt(s);
        }
        int eyeToScreen = -eyeSynchFrame + screenSynchFrame;

        // Get screen dimansion
        Dimension realMonitorDimension = projectSelectPanel.getMonitorDimension();
        Dimension screenViewFullSize = projectSelectPanel.getFullScreenDimension();

        // Check for eye calibration vector
        double[][] gazeCoefficient = calibrateJPanel.getEyeGazeCoefficient(0);
        if (gazeCoefficient == null) {
            // Show error that there is no gaze coefficient
            JOptionPane.showMessageDialog(null,
                    "<html>Eye gaze has not been calibrated</html>",
                    "Error getting eye gaze coefficients",
                    JOptionPane.ERROR_MESSAGE);
        // Does nothing more and waiting to return
        } else {
            PrintWriter exportWriter = null;

            // Get the file to save raw info
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setDialogTitle("Data export file");
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    exportWriter = new PrintWriter(fileChooser.getSelectedFile());
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }

                // Print header
                exportWriter.println(
                        "Screen Frame\t" +
                        "Pupil x\t" +
                        "Pupil y\t" +
                        "Pupil fit top left x\t" +
                        "Pupil fit topleft y\t" +
                        "Pupil fit bottom right x\t" +
                        "Pupil fit bottom right y\t" +
                        "Cornia reflect x\t" +
                        "Cornia reflect y\t" +
                        "Raw primary x\t" + // This is a gaze coor on the screen view
                        "Raw primary y\t" +
                        "Raw secondary x\t" + // This is a gaze coor on the screen view
                        "Raw secondary y\t" +
                        "Raw linear interpolated x\t" + // This is a gaze coor on the screen view
                        "Raw linear interpolated y\t" +
                        "Screen primary x\t" +
                        "Screen primary y\t" +
                        "Screen secondary x\t" +
                        "Screen secondary y\t" +
                        "Screen linear interpolated x\t" +
                        "Screen linear interpolated y\t" +
                        "Similarity of topleft\t" +
                        "Similarity of topright\t" +
                        "Similarity of bottomleft\t" +
                        "Similarity of bottomright\t" +
                        "Error\t" +
                        "Trial file name\t" +
                        "Trial number");

                // Variables
                Point2D fixation = new Point2D.Double(ERROR_VALUE, ERROR_VALUE);

                // Get a trial set
                TrialMarker[] trials = this.markTrialJPanel.getTrials();
                int trialNumber = 0;
                String trialName = null;
                int calibrationNumber = 0;
                String calibrationName = null;


                // Get a error set
                Iterator<ErrorMarking> errorIter = cleanDataJPanel.getCompressedErrorMarkingSet().iterator();
                ErrorMarking error = null;
                if (errorIter.hasNext()) {
                    error = errorIter.next();
                }

                for (int i = 1; i <= eyeFrameManager.getTotalFrames(); i++) {
                    // Get the current trial number
                    if (trialNumber < trials.length) {
                        if (i > trials[trialNumber].stopEyeFrame) {
                            // Move to the next trial
                            trialNumber++;
                        }

                        if (trialNumber < trials.length &&
                                i >= trials[trialNumber].startEyeFrame) {
                            // Set new trial name
                            trialName = trials[trialNumber].label;
                        } else {
                            // Clear the name
                            trialName = null;
                        }
                    }

                    /** If not a trial check if this is a calibration */
                    if (trialName == null) {
                        Point calibrationPoint =
                                calibrateJPanel.frameToCalibrationPoint(i + eyeToScreen);
                        if (calibrationPoint != null) {
                            calibrationName = "C_" + calibrationPoint.x + "_" +
                                    calibrationPoint.y;
                            calibrationNumber = -(calibrationPoint.x +
                                    (calibrationPoint.y - 1) *
                                    CalibrateJPanel.TOTAL_CALIBRATION_X);
                        } else {
                            calibrationName = null;
                        }
                    }

                    // Get eyeInfo
                    EyeViewFrameInfo eyeInfo =
                            (EyeViewFrameInfo) eyeFrameManager.getFrameInfo(i);

                    // Skip a frame when there is no eye information
                    if (eyeInfo != null) {
                        double[] pupilFit = {ERROR_VALUE, ERROR_VALUE, ERROR_VALUE, ERROR_VALUE};
                        if (eyeInfo.getCorniaFit() != null) {
                            pupilFit = eyeInfo.getCorniaFit();
                        }

                        // Write Screen Frame, Pupil(x,y)
                        exportWriter.print((i + eyeToScreen) + "\t" +
                                eyeInfo.getCorniaX() + "\t" +
                                eyeInfo.getCorniaY() + "\t");

                        // Write Pupil fit topleft x,y bottom right (x,y)
                        for (int j = 0; j < pupilFit.length; j++) {
                            exportWriter.print(pupilFit[j] + "\t");
                        }

                        // Write Cornia reflect (x,y), Gaze on screen view (x,y)
                        exportWriter.print(eyeInfo.getReflectX() + "\t" +
                                eyeInfo.getReflectX() + "\t");

                        // Compute eye vector
                        Point2D.Double vector = Computation.computeEyeVector(
                                eyeInfo.getCorniaX(), eyeInfo.getCorniaY(),
                                eyeInfo.getReflectX(), eyeInfo.getReflectY());

                        EyeGazeComputing.ComputingApproach approach =
                                EyeGazeComputing.ComputingApproach.PRIMARY;

                        // Compute eye gaze
                        Point2D.Double point[] = new Point2D.Double[3];
                        point[0] = exportEyeGazes(exportWriter, i, vector,
                                EyeGazeComputing.ComputingApproach.PRIMARY,
                                screenViewFullSize);
                        point[1] = exportEyeGazes(exportWriter, i, vector,
                                EyeGazeComputing.ComputingApproach.SECONDARY,
                                screenViewFullSize);
                        point[2] = exportEyeGazes(exportWriter, i, vector,
                                EyeGazeComputing.ComputingApproach.LINEAR,
                                screenViewFullSize);

                        // Get screen info
                        ScreenViewFrameInfo screenInfo =
                                (ScreenViewFrameInfo) screenFrameManager.getFrameInfo(i + eyeToScreen);

                        // Set default value for not available
                        fixation.setLocation(ERROR_VALUE, ERROR_VALUE);

                        if (screenInfo != null) {
                            Point2D[] corners = screenInfo.getCorners();

                            if (corners != null && corners[0] != null &&
                                    corners[1] != null && corners[2] != null &&
                                    corners[3] != null) {
                                for (int j = 0; j < point.length; j++) {
                                    // Compute fixation
                                    fixation = Computation.ComputeScreenPositionProjective(
                                            realMonitorDimension, point[j],
                                            corners[ScreenViewFrameInfo.TOPLEFT],
                                            corners[ScreenViewFrameInfo.TOPRIGHT],
                                            corners[ScreenViewFrameInfo.BOTTOMLEFT],
                                            corners[ScreenViewFrameInfo.BOTTOMRIGHT]);

                                    if (fixation == null) {
                                        fixation = new Point2D.Double(ERROR_VALUE, ERROR_VALUE);
                                    }
                                    if ((point[j].x < 0 && point[j].y < 0) ||
                                            fixation.getX() < 0 || fixation.getY() < 0 ||
                                            fixation.getX() > realMonitorDimension.width ||
                                            fixation.getY() > realMonitorDimension.height) {
                                        fixation.setLocation(ERROR_VALUE, ERROR_VALUE);
                                    }

                                    exportWriter.print(
                                            fixation.getX() + "\t" + fixation.getY() + "\t");
                                }

                                double[] similarities = screenInfo.similarities;
                                // Write Gaze on monitor (x,y), Similarity (topleft, topright, bottomleft, bottomright)
                                exportWriter.print(
                                        similarities[ScreenViewFrameInfo.TOPLEFT] + "\t" +
                                        similarities[ScreenViewFrameInfo.TOPRIGHT] + "\t" +
                                        similarities[ScreenViewFrameInfo.BOTTOMLEFT] + "\t" +
                                        similarities[ScreenViewFrameInfo.BOTTOMRIGHT] + "\t");
                            } else {
                                // Just put blank
                                exportWriter.print(
                                        ERROR_VALUE + "\t" + ERROR_VALUE + "\t" + ERROR_VALUE + "\t" + ERROR_VALUE + "\t" + ERROR_VALUE + "\t" + ERROR_VALUE + "\t" + ERROR_VALUE + "\t" + ERROR_VALUE + "\t" + ERROR_VALUE + "\t" + ERROR_VALUE + "\t");
                            }
                        } else {
                            // Just put blank
                            exportWriter.print(
                                    ERROR_VALUE + "\t" + ERROR_VALUE + "\t" + ERROR_VALUE + "\t" + ERROR_VALUE + "\t" + ERROR_VALUE + "\t" + ERROR_VALUE + "\t" + ERROR_VALUE + "\t" + ERROR_VALUE + "\t" + ERROR_VALUE + "\t" + ERROR_VALUE + "\t");
                        }


                        // Processing error code
                        if (error != null && error.startEyeFrame <= i) {
                            // Check if it's in range
                            if (i <= error.stopEyeFrame) {
                                // Print error code
                                exportWriter.print(error.getErrorCode() + "\t");
                            } else {
                                // Mismatch. Try finding the next one
                                while (errorIter.hasNext() &&
                                        error.stopEyeFrame < i) {
                                    error = errorIter.next();
                                }
                                // Check if we found one
                                if (error.startEyeFrame <= i) {
                                    if (i <= error.stopEyeFrame) {
                                        // Found it so print
                                        exportWriter.print(error.getErrorCode() + "\t");
                                    } else {
                                        // Nothing is found and we are out of error so stop searching
                                        error = null;
                                        exportWriter.print("0\t");
                                    }
                                } else {
                                    exportWriter.print("0\t");
                                }
                            }
                        } else {
                            exportWriter.print("0\t");
                        }

                        if (trialName != null) {
                            exportWriter.println(trials[trialNumber].label + "\t" + trialNumber);
                        } else {
                            if (calibrationName != null) {
                                exportWriter.println(calibrationName + "\t" + calibrationNumber);
                            } else {
                                exportWriter.println("-\t" + ERROR_VALUE);
                            }
                        }
                    }
                }
                if (exportWriter != null) {
                    exportWriter.close();
                }
            }
        }
    }

    private void updateFullScreenDimansion() {
        // Trying to determine full screen dimension
        String filename = screenFrameManager.getFrameFileName(1);

        if (filename != null &&
                projectSelectPanel.getFullScreenFrameDirectory() != null) {
            // Use screen screen dir if full screen does not exists
            String fullScreenDir = projectSelectPanel.getFullScreenFrameDirectory();
            if (fullScreenDir.length() < 1) {
                fullScreenDir = projectSelectPanel.getScreenFrameDirectory();
            }

            File fullScreenFile = new File(fullScreenDir, filename);
            if (fullScreenFile.exists()) {
                BufferedImage image = ImageTools.loadImage(fullScreenFile);
                projectSelectPanel.setFullScreenDimension(
                        String.valueOf(image.getWidth()),
                        String.valueOf(image.getHeight()));
            }
        }
    }
}
