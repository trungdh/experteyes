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
 * CalibrationStoredJPanel.java
 *
 * Created on September 19, 2007, 8:17 PM
 */

package eyetrackercalibrator.gui;

import javax.swing.JLabel;

/**
 *
 * @author  SQ
 */
public class CalibrationStoredJPanel extends javax.swing.JPanel {
    JLabel[] label = new JLabel[9];
    
    /** Creates new form CalibrationStoredJPanel */
    public CalibrationStoredJPanel() {
        initComponents();
        label[0] = jLabel_1_1;
        label[1] = jLabel_1_2;
        label[2] = jLabel_1_3;
        label[3] = jLabel_2_1;
        label[4] = jLabel_2_2;
        label[5] = jLabel_2_3;
        label[6] = jLabel_3_1;
        label[7] = jLabel_3_2;
        label[8] = jLabel_3_3;        
    }
    
    /**
     *  Set label at element according to row and colume.
     *  IMPORTANT row and column start from 1 !!!
     *  return false if fail to add
     */
    public boolean setLabel(int row, int col, String value){
        if(row > 0 && row <=3 && col > 0 && col <=3){
            label[(row-1)*3+col-1].setText(value);
            return true;
        }else{
            return false;
        }
    }
    
        /**
     *  Get label text at element according to row and colume.
     *  IMPORTANT row and column start from 1 !!!
     *  return null if fail to locate
     */
    public String setLabel(int row, int col){
        if(row > 0 && row <=3 && col > 0 && col <=3){
            return label[(row-1)*3+col-1].getText();
        }else{
            return null;
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel_1_1 = new javax.swing.JLabel();
        jLabel_1_2 = new javax.swing.JLabel();
        jLabel_1_3 = new javax.swing.JLabel();
        jLabel_3_1 = new javax.swing.JLabel();
        jLabel_2_1 = new javax.swing.JLabel();
        jLabel_3_2 = new javax.swing.JLabel();
        jLabel_3_3 = new javax.swing.JLabel();
        jLabel_2_2 = new javax.swing.JLabel();
        jLabel_2_3 = new javax.swing.JLabel();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Cabration/Test Points Saved"));

        jLabel_1_1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_1_1.setText("0");
        jLabel_1_1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel_1_1.setMaximumSize(new java.awt.Dimension(15, 15));

        jLabel_1_2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_1_2.setText("0");
        jLabel_1_2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel_1_2.setMaximumSize(new java.awt.Dimension(15, 15));

        jLabel_1_3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_1_3.setText("0");
        jLabel_1_3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel_1_3.setMaximumSize(new java.awt.Dimension(15, 15));

        jLabel_3_1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_3_1.setText("0");
        jLabel_3_1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel_3_1.setMaximumSize(new java.awt.Dimension(15, 15));

        jLabel_2_1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_2_1.setText("0");
        jLabel_2_1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel_2_1.setMaximumSize(new java.awt.Dimension(15, 15));

        jLabel_3_2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_3_2.setText("0");
        jLabel_3_2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel_3_2.setMaximumSize(new java.awt.Dimension(15, 15));

        jLabel_3_3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_3_3.setText("0");
        jLabel_3_3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel_3_3.setMaximumSize(new java.awt.Dimension(15, 15));

        jLabel_2_2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_2_2.setText("0");
        jLabel_2_2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel_2_2.setMaximumSize(new java.awt.Dimension(15, 15));

        jLabel_2_3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_2_3.setText("0");
        jLabel_2_3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel_2_3.setMaximumSize(new java.awt.Dimension(15, 15));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel_1_1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 68, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel_1_2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 68, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel_1_3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 68, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel_3_1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 68, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel_2_1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 68, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(jLabel_3_2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 68, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel_3_3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 68, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(jLabel_2_2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 68, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel_2_3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 68, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel_1_2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel_1_1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel_1_3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel_2_1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel_2_2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel_2_3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(31, 31, 31)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel_3_1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel_3_2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel_3_3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel_1_1;
    private javax.swing.JLabel jLabel_1_2;
    private javax.swing.JLabel jLabel_1_3;
    private javax.swing.JLabel jLabel_2_1;
    private javax.swing.JLabel jLabel_2_2;
    private javax.swing.JLabel jLabel_2_3;
    private javax.swing.JLabel jLabel_3_1;
    private javax.swing.JLabel jLabel_3_2;
    private javax.swing.JLabel jLabel_3_3;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
    
}