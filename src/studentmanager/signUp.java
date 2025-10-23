package studentmanager;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;


public class signUp extends javax.swing.JFrame {


    public signUp() {
        initComponents();
        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/studentmanager/icons/BrelinxScholar.png"));
        setIconImage(icon);
        focuse1();
        
        
    }
    
    public void focuse1(){
        //first name
        if (firstName.getText().equals("First name")) {
            firstName.setText("");
            firstName.setForeground(Color.BLACK);
        }
        if (firstName.getText().trim().isEmpty()) {
            firstName.setForeground(Color.GRAY);
            firstName.setText("First name");
        }
        //last name
        if (lastName.getText().equals("Last name")) {
            lastName.setText("");
            lastName.setForeground(Color.BLACK);
        }
        if (lastName.getText().trim().isEmpty()) {
            lastName.setForeground(Color.GRAY);
            lastName.setText("Last name");
        }
        
        //courses
        if (course.getText().equals("Course")) {
            course.setText("");
            course.setForeground(Color.BLACK);
        }
        if (course.getText().trim().isEmpty()) {
            course.setForeground(Color.GRAY);
            course.setText("Course");
        }
        //email addresses
        if (email.getText().equals("Email Address")) {
            email.setText("");
            email.setForeground(Color.BLACK);
        }
        if (email.getText().trim().isEmpty()) {
            email.setForeground(Color.GRAY);
            email.setText("Email Address");
        }
        //password
        if (password.getText().equals("Password")) {
            password.setText("");
            password.setForeground(Color.BLACK);
        }
        if (password.getText().trim().isEmpty()) {
            password.setForeground(Color.GRAY);
            password.setText("Password");
        }
    }
    
    public void signUpUser() {
        String url = "jdbc:mysql://localhost:3306/courses";
        String dbUser = "root";
        String dbPassword = "";

        String fName = firstName.getText().trim();
        String lName = lastName.getText().trim();
        String courseName = course.getText().trim();
        String emailAddress = email.getText().trim();
        String pass = password.getText().trim();

        if (fName.isEmpty() || fName.equals("First name") ||
            lName.isEmpty() || lName.equals("Last name") ||
            courseName.isEmpty() || courseName.equals("Course") ||
            emailAddress.isEmpty() || emailAddress.equals("Email Address") ||
            pass.isEmpty() || pass.equals("Password")) {

            JOptionPane.showMessageDialog(null, "Please fill in all fields before signing up.");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);

            String checkQuery = "SELECT * FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, emailAddress);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Email already registered. Try logging in.");
            } else {
                String insertQuery = "INSERT INTO users (first_name, last_name, course, email, password) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, fName);
                insertStmt.setString(2, lName);
                insertStmt.setString(3, courseName);
                insertStmt.setString(4, emailAddress);
                insertStmt.setString(5, pass);

                int rowsInserted = insertStmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(null, "Account created successfully!");
                    
                } else {
                    JOptionPane.showMessageDialog(null, "Signup failed. Please try again.");
                }

                insertStmt.close();
            }

            rs.close();
            checkStmt.close();
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lastName = new javax.swing.JTextField();
        firstName = new javax.swing.JTextField();
        course = new javax.swing.JTextField();
        password = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        email = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Differently !");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 250, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 0, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Create your ");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 140, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI Black", 0, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Account");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 190, -1, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Manage your students");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 230, -1, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/studentmanager/images/newSign.png"))); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 330, 450));

        jLabel6.setFont(new java.awt.Font("Segoe UI Black", 0, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Sign Up");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 10, -1, -1));

        lastName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                lastNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                lastNameFocusLost(evt);
            }
        });
        jPanel1.add(lastName, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 120, 270, 30));

        firstName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                firstNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                firstNameFocusLost(evt);
            }
        });
        jPanel1.add(firstName, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 70, 270, 30));

        course.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                courseFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                courseFocusLost(evt);
            }
        });
        jPanel1.add(course, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 170, 270, 30));

        password.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                passwordFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                passwordFocusLost(evt);
            }
        });
        jPanel1.add(password, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 270, 270, 30));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/studentmanager/icons/closeSign.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, -1, -1));

        jCheckBox1.setForeground(new java.awt.Color(0, 0, 0));
        jCheckBox1.setText("Accept Term & Conditions");
        jPanel1.add(jCheckBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 310, -1, -1));

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setForeground(new java.awt.Color(0, 0, 0));
        jButton1.setText("Login");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 400, 270, -1));

        jButton2.setBackground(new java.awt.Color(0, 0, 0));
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Join us");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 360, 270, -1));

        email.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                emailFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                emailFocusLost(evt);
            }
        });
        jPanel1.add(email, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 220, 270, 30));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 670, 450));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void firstNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_firstNameFocusGained
        // TODO add your handling code here:
        if (firstName.getText().equals("First name")) {
            firstName.setText("");
            firstName.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_firstNameFocusGained

    private void firstNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_firstNameFocusLost
        // TODO add your handling code here:
        if (firstName.getText().trim().isEmpty()) {
            firstName.setForeground(Color.GRAY);
            firstName.setText("First name");
        }
        
    }//GEN-LAST:event_firstNameFocusLost

    private void lastNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lastNameFocusLost
        // TODO add your handling code here:
        if (lastName.getText().trim().isEmpty()) {
            lastName.setForeground(Color.GRAY);
            lastName.setText("Last name");
        }

    }//GEN-LAST:event_lastNameFocusLost

    private void lastNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lastNameFocusGained
        // TODO add your handling code here:
        if (lastName.getText().equals("Last name")) {
            lastName.setText("");
            lastName.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_lastNameFocusGained

    private void courseFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_courseFocusLost
        // TODO add your handling code here:
        if (course.getText().trim().isEmpty()) {
            course.setForeground(Color.GRAY);
            course.setText("Course");
        }
    }//GEN-LAST:event_courseFocusLost

    private void courseFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_courseFocusGained
        // TODO add your handling code here:
        if (course.getText().equals("Course")) {
            course.setText("");
            course.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_courseFocusGained

    private void passwordFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passwordFocusGained
        // TODO add your handling code here:
        if (password.getText().equals("Password")) {
            password.setText("");
            password.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_passwordFocusGained

    private void passwordFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passwordFocusLost
        // TODO add your handling code here:
        if (password.getText().trim().isEmpty()) {
            password.setForeground(Color.GRAY);
            password.setText("Password");
        }
    }//GEN-LAST:event_passwordFocusLost

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jLabel7MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        new login().setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void emailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_emailFocusGained
        // TODO add your handling code here:
        if (email.getText().equals("Email Address")) {
            email.setText("");
            email.setForeground(Color.BLACK);
        }
        
    }//GEN-LAST:event_emailFocusGained

    private void emailFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_emailFocusLost
        // TODO add your handling code here:
        if (email.getText().trim().isEmpty()) {
            email.setForeground(Color.GRAY);
            email.setText("Email Address");
        }
    }//GEN-LAST:event_emailFocusLost

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        signUpUser();
    }//GEN-LAST:event_jButton2ActionPerformed

    
    
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
            java.util.logging.Logger.getLogger(signUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(signUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(signUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(signUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new signUp().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField course;
    private javax.swing.JTextField email;
    private javax.swing.JTextField firstName;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField lastName;
    private javax.swing.JTextField password;
    // End of variables declaration//GEN-END:variables
}
