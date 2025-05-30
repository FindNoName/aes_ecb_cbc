package com.ando.aes;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.geom.RoundRectangle2D;
import java.nio.file.Files;
import java.nio.file.Paths;
import mdlaf.MaterialLookAndFeel;
import mdlaf.themes.MaterialLiteTheme;
import java.nio.charset.StandardCharsets; 
/**
 * AES加密解密系统的图形用户界面
 * 提供文件选择、加密解密操作和日志显示功能
 * 支持ECB和CBC两种加密模式
 */
public class UI {

    private static JTextArea logArea; // 日志显示区域
    private static JLabel statusLabel; // 状态显示标签
    private static JTextField srcFileField; // 源文件路径
    private static JTextField destFileField; // 目标文件路径
    
    public static void main(String[] args) {
        try {
            // 使用自定义主题
            MaterialLookAndFeel materialLookAndFeel = new MaterialLookAndFeel(new MaterialLiteTheme());
            UIManager.setLookAndFeel(materialLookAndFeel);
            UIManager.setLookAndFeel(new MaterialLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 创建主窗口
        JFrame frame = new JFrame("AES Encryption/Decryption System 2.0");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 700);
        frame.setLocationRelativeTo(null);
        
        // 创建圆角面板
        class RoundedPanel extends JPanel {
            private int radius = 20;
            
            public RoundedPanel(LayoutManager layout) {
                super(layout);
                setOpaque(false);
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, radius, radius));
                g2.dispose();
            }
        }

        // 修改主面板为圆角面板
        RoundedPanel mainPanel = new RoundedPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(255, 255, 255, 240));

        // 修改顶部面板
        RoundedPanel topPanel = new RoundedPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(5, 5, 5, 5),
            BorderFactory.createTitledBorder(null, "File Selection and Key Settings",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12))
        ));
        topPanel.setBackground(new Color(245, 245, 250, 240));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 源文件选择
        JLabel srcFileLabel = new JLabel("Source File Path:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        topPanel.add(srcFileLabel, gbc);
        
        srcFileField = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        topPanel.add(srcFileField, gbc);
        
        JButton srcFileButton = new JButton("Browse...");
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        topPanel.add(srcFileButton, gbc);
        
        // 目标文件选择
        JLabel destFileLabel = new JLabel("Destination File Path:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        topPanel.add(destFileLabel, gbc);
        
        destFileField = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        topPanel.add(destFileField, gbc);
        
        JButton destFileButton = new JButton("Browse...");
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        topPanel.add(destFileButton, gbc);
        
        // 密钥输入
        JLabel keyLabel = new JLabel("Key (16 characters):");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        topPanel.add(keyLabel, gbc);
        
        JTextField keyField = new JTextField("1234567890123456", 16);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        topPanel.add(keyField, gbc);
        
        // 随机生成密钥按钮
        JButton generateKeyButton = new JButton("Generate Key");
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        topPanel.add(generateKeyButton, gbc);
        
        // 随机生成密钥按钮的事件监听器
        generateKeyButton.addActionListener(e -> {
            String generatedKey = generateRandomKey(16); // 生成16字符的随机密钥
            keyField.setText(generatedKey); // 将密钥填入输入框
            log("Random key generated: " + generatedKey);
        });
        
        // 修改中部面板
        RoundedPanel centerPanel = new RoundedPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(5, 5, 5, 5),
            BorderFactory.createTitledBorder(null, "Operations",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12))
        ));
        centerPanel.setBackground(new Color(245, 245, 250, 240));
        
        // 美化按钮样式
        class StyledButton extends JButton {
            public StyledButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setForeground(Color.BLACK); // 修改文字颜色为黑色
            setBackground(new Color(200, 200, 255, 230)); // 修改背景颜色为浅蓝色
            setBorderPainted(false);
            setFocusPainted(false);
            
            
            }
            
            @Override
            protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, 10, 10));
            super.paintComponent(g);
            g2.dispose();
            }
        }

        JButton ecbEncryptButton = new StyledButton("Encrypt (ECB)");
        JButton ecbDecryptButton = new StyledButton("Decrypt (ECB)");
        JButton cbcEncryptButton = new StyledButton("Encrypt (CBC)");
        JButton cbcDecryptButton = new StyledButton("Decrypt (CBC)");
        JButton clearButton = new StyledButton("Clear Logs");

        ecbEncryptButton.setPreferredSize(new Dimension(130, 50));
        ecbDecryptButton.setPreferredSize(new Dimension(130, 50));
        cbcEncryptButton.setPreferredSize(new Dimension(130, 50));
        cbcDecryptButton.setPreferredSize(new Dimension(130, 50));
        clearButton.setPreferredSize(new Dimension(100, 50));
        
        centerPanel.add(ecbEncryptButton);
        centerPanel.add(ecbDecryptButton);
        centerPanel.add(cbcEncryptButton);
        centerPanel.add(cbcDecryptButton);
        centerPanel.add(clearButton);
        
        // 修改底部面板
        RoundedPanel bottomPanel = new RoundedPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(5, 5, 5, 5),
            BorderFactory.createTitledBorder(null, "Processing Logs",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12))
        ));
        bottomPanel.setBackground(new Color(245, 245, 250, 240));
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setBackground(new Color(250, 250, 255));
        logArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(750, 300));
        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 美化状态栏
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setBackground(new Color(245, 245, 250, 240));
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(2, 5, 2, 5),
            BorderFactory.createLineBorder(new Color(200, 200, 200))
        ));
        statusLabel.setPreferredSize(new Dimension(800, 20));
        
        // 将面板添加到主面板
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // 将主面板添加到窗口
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(statusLabel, BorderLayout.SOUTH);
        
        // 设置事件监听器
        
        // 源文件选择按钮
        srcFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Source File");
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                srcFileField.setText(selectedFile.getAbsolutePath());
                log("Source file selected: " + selectedFile.getAbsolutePath());
            }
        });
        
        // 目标文件选择按钮
        destFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Destination File");
            if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();
                // 确保文件扩展名为.txt
                if (!filePath.toLowerCase().endsWith(".txt")) {
                    filePath += ".txt";
                }
                destFileField.setText(filePath);
                log("Destination file selected: " + filePath);
            }
        });
        
        // ECB加密按钮
        ecbEncryptButton.addActionListener(e -> {
            try {
                String srcPath = srcFileField.getText();
                String destPath = destFileField.getText();
                String key = keyField.getText();
                
                if (srcPath.isEmpty() || destPath.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please select source and destination file paths!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (key.length() != 16) {
                    JOptionPane.showMessageDialog(frame, "Key length must be 16 characters!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                setStatus("Performing ECB encryption...");
                log("Starting ECB encryption operation");
                log("Source file: " + srcPath);
                log("Destination file: " + destPath);
                log("Key: " + key);
                
                // 记录开始时间
                long startTime = System.nanoTime();
                
                // 执行加密
                EncryptFile.encryptFileECB(srcPath, destPath, key);
                
                // 记录结束时间
                long endTime = System.nanoTime();
                long duration = (endTime - startTime) / 1_000_000; // 转换为毫秒
                log("ECB encryption completed in " + duration + " ms");
                setStatus("ECB encryption completed");
                
                JOptionPane.showMessageDialog(frame, "ECB encryption successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                log("ECB encryption error: " + ex.getMessage());
                setStatus("Encryption failed");
                JOptionPane.showMessageDialog(frame, "Error during encryption: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // ECB解密按钮
        ecbDecryptButton.addActionListener(e -> {
            try {
                String srcPath = srcFileField.getText();
                String destPath = destFileField.getText();
                String key = keyField.getText();
                
                if (srcPath.isEmpty() || destPath.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please select source and destination file paths!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (key.length() != 16) {
                    JOptionPane.showMessageDialog(frame, "Key length must be 16 characters!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                setStatus("Performing ECB decryption...");
                log("Starting ECB decryption operation");
                log("Source file: " + srcPath);
                log("Destination file: " + destPath);
                log("Key: " + key);
                
                // 显示加密文件内容
                byte[] fileContent = Files.readAllBytes(Paths.get(srcPath));
                String encryptedText = new String(fileContent, "UTF-8");
                log("Encrypted file content: " + encryptedText);
                
                // 执行解密
                DecryptFile.decryptFileECB(srcPath, destPath, key);
                
                // 显示解密后内容
                byte[] decryptedContent = Files.readAllBytes(Paths.get(destPath));
                String decryptedText = new String(decryptedContent, "UTF-8");
                log("Decrypted content: " + decryptedText);
                
                log("ECB decryption completed, result saved to: " + destPath);
                setStatus("ECB decryption completed");
                
                JOptionPane.showMessageDialog(frame, "ECB decryption successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                log("ECB decryption error: " + ex.getMessage());
                setStatus("Decryption failed");
                JOptionPane.showMessageDialog(frame, "Error during decryption: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // CBC加密按钮
        cbcEncryptButton.addActionListener(e -> {
            try {
                String srcPath = srcFileField.getText();
                String destPath = destFileField.getText();
                String key = keyField.getText();
                
                if (srcPath.isEmpty() || destPath.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please select source and destination file paths!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (key.length() != 16) {
                    JOptionPane.showMessageDialog(frame, "Key length must be 16 characters!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                setStatus("Performing CBC encryption...");
                log("Starting CBC encryption operation");
                log("Source file: " + srcPath);
                log("Destination file: " + destPath);
                log("Key: " + key);
                
                // 记录开始时间
                long startTime = System.nanoTime();
                
                // 执行加密
                EncryptFile.encryptFileCBC(srcPath, destPath, key);
                
                // 记录结束时间
                long endTime = System.nanoTime();
                long duration = (endTime - startTime) / 1_000_000; // 转换为毫秒
                log("CBC encryption completed in " + duration + " ms");
                setStatus("CBC encryption completed");
                
                JOptionPane.showMessageDialog(frame, "CBC encryption successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                log("CBC encryption error: " + ex.getMessage());
                setStatus("Encryption failed");
                JOptionPane.showMessageDialog(frame, "Error during encryption: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // CBC解密按钮
        cbcDecryptButton.addActionListener(e -> {
            try {
                String srcPath = srcFileField.getText();
                String destPath = destFileField.getText();
                String key = keyField.getText();
                
                if (srcPath.isEmpty() || destPath.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please select source and destination file paths!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (key.length() != 16) {
                    JOptionPane.showMessageDialog(frame, "Key length must be 16 characters!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                setStatus("Performing CBC decryption...");
                log("Starting CBC decryption operation");
                log("Source file: " + srcPath);
                log("Destination file: " + destPath);
                log("Key: " + key);
                
                // 显示加密文件内容
                byte[] fileContent = Files.readAllBytes(Paths.get(srcPath));
                String encryptedText = new String(fileContent, "UTF-8");
                log("Encrypted file content: " + encryptedText);
                
                // 执行解密
                DecryptFile.decryptFileCBC(srcPath, destPath, key);
                
                // 显示解密后内容
                byte[] decryptedContent = Files.readAllBytes(Paths.get(destPath));
                String decryptedText = new String(decryptedContent, "UTF-8");
                log("Decrypted content: " + decryptedText);
                
                log("CBC decryption completed, result saved to: " + destPath);
                setStatus("CBC decryption completed");
                
                JOptionPane.showMessageDialog(frame, "CBC decryption successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                log("CBC decryption error: " + ex.getMessage());
                setStatus("Decryption failed");
                JOptionPane.showMessageDialog(frame, "Error during decryption: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // 清空日志按钮
        clearButton.addActionListener(e -> {
            logArea.setText("");
            setStatus("Logs cleared");
        });
        
        // 显示窗口
        frame.setVisible(true);
        log("AES Encryption/Decryption System 2.0 started");
        log("Please select source and destination files, then click the corresponding button to encrypt or decrypt.");
    }
    
    // 日志记录方法
    private static void log(String message) {
        logArea.append("[" + System.currentTimeMillis() % 10000 + "] " + message + "\n");
        // 自动滚动到最新的日志
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    // 设置状态栏
    private static void setStatus(String status) {
        statusLabel.setText(status);
    }
    
    // 添加生成随机密钥的方法
    private static String generateRandomKey(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            key.append(characters.charAt(index));
        }
        return key.toString();
    }
}
