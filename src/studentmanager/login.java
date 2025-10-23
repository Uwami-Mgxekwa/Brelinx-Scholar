package studentmanager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JOptionPane;
import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgcodecs.Imgcodecs;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;

public class login extends javax.swing.JFrame {
    
    private javax.swing.JLabel cameraView;
    private List<Mat> referenceHistograms = new ArrayList<>();
    private static final double THRESHOLD = 0.4;
    
    public login() {
        initComponents();
        edits();
        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/studentmanager/icons/BrelinxScholar.png"));
        setIconImage(icon);
        cameraView = new javax.swing.JLabel();
        cameraView.setSize(320, 240);
        cameraView.setVisible(false);
        jPanel1.add(cameraView);
        new Thread(() -> {
            loadReferenceHistograms();
            System.out.println("Reference histograms loaded: " + referenceHistograms.size());
        }).start();
        
    }
    
    private void loadReferenceHistograms() {
        try {
            InputStream xmlStream = login.class.getResourceAsStream("/studentmanager/haarcascade_frontalface_default.xml");
            if (xmlStream == null) {
                System.err.println("Cascade XML not found in JAR.");
                return;
            }

            File tempFile = File.createTempFile("cascade", ".xml");
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = xmlStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            CascadeClassifier faceDetector = new CascadeClassifier(tempFile.getAbsolutePath());

            for (int i = 1; i <= 16; i++) {
                String imageName = String.format("images/%d.jpg", i);
                try (InputStream imgStream = login.class.getResourceAsStream("/studentmanager/" + imageName)) {
                    if (imgStream == null) {
                        System.err.println("Missing image: " + imageName);
                        continue;
                    }

                    byte[] data = imgStream.readAllBytes();
                    Mat img = Imgcodecs.imdecode(new MatOfByte(data), Imgcodecs.IMREAD_COLOR);
                    if (img.empty()) continue;

                    Mat gray = new Mat();
                    Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);

                    MatOfRect faces = new MatOfRect();
                    faceDetector.detectMultiScale(gray, faces);

                    for (Rect rect : faces.toArray()) {
                        Mat face = new Mat(gray, rect);
                        Mat hist = new Mat();
                        Imgproc.calcHist(java.util.Arrays.asList(face), new MatOfInt(0), new Mat(), hist, new MatOfInt(256), new MatOfFloat(0, 256));
                        Core.normalize(hist, hist, 0, 1, Core.NORM_MINMAX);
                        referenceHistograms.add(hist);
                    }
                } catch (Exception e) {
                    System.err.println("Error loading image: " + imageName);
                    e.printStackTrace();
                }   
            }

            System.out.println("Loaded " + referenceHistograms.size() + " reference histograms.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error loading reference histograms:\n" + e.getMessage(),
                "Face Detection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(mat.width(), mat.height(), type);
        mat.get(0, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData());
        return image;
    }
    
    
    private void detectFace() {
        new Thread(() -> {
            VideoCapture camera = new VideoCapture(1);
            if (!camera.isOpened() || !camera.read(new Mat())) {
                System.out.println("Camera 0 failed. Trying camera 1...");
                camera.release();
                camera = new VideoCapture(0);
                if (!camera.isOpened() || !camera.read(new Mat())) {
                    JOptionPane.showMessageDialog(this, "No accessible camera found.");
                    return;
                }
            }

            InputStream xmlStream = login.class.getResourceAsStream("/studentmanager/haarcascade_frontalface_default.xml");
            if (xmlStream == null) {
                System.err.println("Cascade XML not found in JAR.");
                return;
            }

            File tempFile;
            try {
                tempFile = File.createTempFile("cascade", ".xml");
                tempFile.deleteOnExit();

                try (FileOutputStream out = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = xmlStream.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load cascade file.");
                return;
            }

            CascadeClassifier faceDetector = new CascadeClassifier(tempFile.getAbsolutePath());
            if (faceDetector.empty()) {
                System.err.println("Failed to load cascade classifier.");
                return;
            }

            Mat frame = new Mat();
            boolean faceFound = false;
            long startTime = System.currentTimeMillis();

            while ((System.currentTimeMillis() - startTime) < 5000) {
                camera.read(frame);
                System.out.println("Frame read: " + !frame.empty());    
                if (frame.empty()) continue;

                Mat gray = new Mat();
                Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
                Imgproc.equalizeHist(gray, gray);

                MatOfRect faces = new MatOfRect();
                faceDetector.detectMultiScale(gray, faces);

                for (Rect rect : faces.toArray()) {
                    Mat faceROI = new Mat(gray, rect);
                    Mat hist = new Mat();
                    Imgproc.calcHist(java.util.Arrays.asList(faceROI), new MatOfInt(0), new Mat(), hist, new MatOfInt(256), new MatOfFloat(0, 256));
                    Core.normalize(hist, hist, 0, 1, Core.NORM_MINMAX);

                    boolean matchFound = false;
                    for (Mat refHist : referenceHistograms) {
                        double similarity = Imgproc.compareHist(refHist, hist, Imgproc.CV_COMP_CORREL);
                        System.out.println("Similarity: " + similarity);
                        if (similarity > THRESHOLD) {
                            matchFound = true;
                            break;
                        }
                    }

                    Scalar color = matchFound ? new Scalar(0, 255, 0) : new Scalar(0, 0, 255);
                    Imgproc.rectangle(frame, rect.tl(), rect.br(), color, 2);

                    if (matchFound && !faceFound) {
                        faceFound = true;
                    }
                }

                ImageIcon image = new ImageIcon(matToBufferedImage(frame));
                cameraView.setIcon(image);

                try { Thread.sleep(30); } catch (InterruptedException e) { e.printStackTrace(); }
            }

            camera.release();

            if (faceFound) {
                JOptionPane.showMessageDialog(this, "Welcome Mr Owami! Login successful.");
                dispose();
                new FaceCheck1().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "This is not my boss, Mr Owami. Try again.");
                new error().setVisible(true);
            }
        }).start();
    }
    
    public void edits(){
        jPanel1.setBackground(new Color(48, 0, 96, 200));
        jLabel6.setOpaque(false);
        jLabel6.setBackground(new Color(255, 0, 128)); 
        jLabel6.setForeground(Color.WHITE);
        jLabel6.setFont(new Font("Segoe UI", Font.BOLD, 18));
        jLabel6.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel8.setOpaque(false);
        jLabel8.setBackground(new Color(64, 0, 128));
        jLabel8.setForeground(Color.WHITE);
        jLabel8.setFont(new Font("Segoe UI", Font.BOLD, 18));
        jLabel8.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel10.setFont(new Font("Segoe UI Black", Font.BOLD, 14));
        jLabel10.setForeground(new Color(255, 0, 128)); // Pink text
        jLabel10.setBackground(new Color(38, 0, 76));   // Dark purple background
        jLabel10.setOpaque(true);
        jLabel10.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel10.setOpaque(false); // Let custom paint handle background
        jLabel10.setBackground(new Color(48, 0, 96, 200));
        //jLabel10.setBorder(BorderFactory.createLineBorder(new Color(255, 0, 128), 2, true)); // Pink border, 2px, rounded

    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 =  new javax.swing.JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35); // 25px corner radius
                g2.dispose();
            }
        };
        jLabel2 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 =  jLabel6 = new javax.swing.JLabel("LOGIN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Let JLabel draw its text and icon
                super.paintComponent(g2);

                g2.dispose();
            }
        };
        txtPassword = new javax.swing.JPasswordField();
        jLabel8 = jLabel8 = new javax.swing.JLabel("FACE ID") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Draw the label's text and icon
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = jLabel10 = new javax.swing.JLabel("CREATE ACCOUNT") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Draw rounded border
                g2.setColor(new Color(255, 0, 128)); // Pink border
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);

                // Draw label content
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Brelinx - Login");
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/studentmanager/icons/crossSmall.png"))); // NOI18N
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 0, -1, -1));

        jPanel1.setOpaque(false);
        jPanel1.setBackground(new java.awt.Color(48, 0, 96, 200));
        jPanel1.setBackground(new java.awt.Color(48, 0, 96));

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("LOGIN");

        jLabel4.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Username :");

        jLabel5.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Password : ");

        jLabel8.setOpaque(false);
        jLabel8.setForeground(Color.WHITE);
        jLabel8.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel6.setBackground(new java.awt.Color(255, 0, 128));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("LOGIN");
        jLabel6.setOpaque(true);
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(28, 0, 56));
        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/studentmanager/icons/face.png"))); // NOI18N
        jLabel8.setText("FACE ID");
        jLabel8.setOpaque(true);
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel8MouseEntered(evt);
            }
        });

        jLabel7.setForeground(new java.awt.Color(102, 102, 102));
        jLabel7.setText("Powered by Brelinx");

        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("forgot password?");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(204, 0, 153));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("CREATE ACCOUNT");
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(94, 94, 94)
                        .addComponent(jLabel2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(102, 102, 102)
                        .addComponent(jLabel7))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(txtUsername)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtPassword)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(48, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel2)
                .addGap(29, 29, 29)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addGap(32, 32, 32)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addContainerGap())
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 20, 310, 480));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/studentmanager/images/1.png"))); // NOI18N
        jLabel3.setText("jLabel3");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 610, 520));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        // TODO add your handling code here:
        //int answer = JOptionPane.showConfirmDialog(rootPane,"Are you sure you want to leave?","Confirm Exit",JOptionPane.YES_NO_OPTION);

        //if (answer == JOptionPane.YES_OPTION) {
            System.exit(0);
        //}
    }//GEN-LAST:event_jLabel1MouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        // TODO add your handling code here:
        detectFace();
        
        
    }//GEN-LAST:event_jLabel8MouseClicked

    private void jLabel8MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel8MouseEntered

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        // TODO add your handling code here:
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        
        if(username.equalsIgnoreCase("uwami") && password.equalsIgnoreCase("2004")){
            dispose();
            new check().setVisible(true);
        }
        else{
            new error().setVisible(true);
        }
        
        
    }//GEN-LAST:event_jLabel6MouseClicked

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
        // TODO add your handling code here:
        new signUp().setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel10MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    // Get the JAR's location
                    String jarPath = login.class.getProtectionDomain()
                        .getCodeSource().getLocation().toURI().getPath();
                    File jarDir = new File(jarPath).getParentFile();

                    // Go up one level to project root
                    File projectRoot = jarDir.getParentFile();
                    File dll = new File(projectRoot, "native/opencv_java4120.dll");

                    if (dll.exists()) {
                        System.load(dll.getAbsolutePath());
                    } else {
                        JOptionPane.showMessageDialog(null,
                            "OpenCV DLL not found:\n" + dll.getAbsolutePath(),
                            "Startup Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                        "Error loading OpenCV:\n" + e.getMessage(),
                        "Startup Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                java.awt.EventQueue.invokeLater(() -> new login().setVisible(true));
}
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    public javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
