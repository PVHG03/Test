package com.ltm.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUploadPage extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(FileUploadPage.class.getName());  // Logger instance
    private JLabel label;
    private JButton uploadButton;
    private JTextField filePathField;
    private File selectedFile;

    public FileUploadPage() {
        // Setting up the frame
        setTitle("File Upload");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create label, text field, and button
        label = new JLabel("Select a file to upload:");
        filePathField = new JTextField(20);
        filePathField.setEditable(false);  // Make text field read-only
        uploadButton = new JButton("Upload");

        // Action listener for the upload button
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    filePathField.setText(selectedFile.getAbsolutePath());
                    LOGGER.info("File selected: " + selectedFile.getAbsolutePath());  // Log file selection
                } else {
                    LOGGER.warning("File selection was cancelled.");  // Log cancellation
                }
            }
        });

        // Button to actually send the file to the server
        JButton sendButton = new JButton("Send to Server");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null) {
                    try {
                        sendFileToServer(selectedFile);
                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, "Error occurred while uploading the file", ex);
                        JOptionPane.showMessageDialog(null, "An error occurred while uploading the file.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a file first!");
                    LOGGER.warning("No file selected for upload.");  // Log file not selected warning
                }
            }
        });

        // Add components to the layout
        setLayout(new FlowLayout());
        add(label);
        add(filePathField);
        add(uploadButton);
        add(sendButton);

        // Make the frame visible
        setVisible(true);
    }

    // Method to send the file to the server
    private void sendFileToServer(File file) throws IOException {
        String boundary = "===" + System.currentTimeMillis() + "===";
        URL url = new URL("http://localhost:8080/upload");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        LOGGER.info("Sending file to server: " + file.getName());  // Log file sending start

        try (OutputStream outputStream = connection.getOutputStream()) {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

            // Send file part
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                    .append(file.getName()).append("\"\r\n");
            writer.append("Content-Type: ").append("application/octet-stream").append("\r\n");
            writer.append("\r\n").flush();

            // Write the file content to the output stream
            try (FileInputStream inputStream = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
                LOGGER.info("File upload completed successfully: " + file.getName());  // Log successful upload
            }

            writer.append("\r\n").flush();
            writer.append("--").append(boundary).append("--\r\n").flush();

            // Check server response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(null, "File uploaded successfully!");
                LOGGER.info("Server responded with success: " + responseCode);  // Log server success response
            } else {
                JOptionPane.showMessageDialog(null, "Failed to upload file. Server responded with code: " + responseCode);
                LOGGER.warning("Server responded with failure: " + responseCode);  // Log server failure response
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error occurred while sending file to the server", ex);  // Log detailed error
            throw ex;
        } finally {
            connection.disconnect();
        }
    }

    public static void main(String[] args) {
        // Create and run the GUI on the event-dispatching thread
        SwingUtilities.invokeLater(() -> new FileUploadPage());
    }
}
