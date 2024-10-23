/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;


public class DataWriter {

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * @return the fileSize
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * @param fileSize the fileSize to set
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * @return the accFile
     */
    public RandomAccessFile getAccFile() {
        return accFile;
    }

    /**
     * @param accFile the accFile to set
     */
    public void setAccFile(RandomAccessFile accFile) {
        this.accFile = accFile;
    }

    public DataWriter(File file, long fileSize) throws FileNotFoundException {
        //rw is mode read and write
        accFile = new RandomAccessFile(file, "rw");
        this.file = file;
        this.fileSize = fileSize;
    }
    
    
    private File file;
    private long fileSize;
    private RandomAccessFile accFile;
    
    public String getMaxFileSize() {
        return convertFile(fileSize);
    }
    
    public String getCurrentFileSize() throws IOException {
        return convertFile(accFile.length());
    }
    
    public double getPercentage() throws IOException {
        double percentage;
        long filePointer = accFile.length();
        percentage = filePointer * 100 / fileSize;
        return  percentage;
    }
    
    private  String convertFile(double bytes) {
        String[] fileSizeUnits = {"bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        String sizeToReturn;
        DecimalFormat df = new DecimalFormat("0.#");
        int index;
        for (index = 0; index < fileSizeUnits.length; index++) {
            if (bytes < 1024) {
                break;
            }
            bytes = bytes / 1024;
        }
        sizeToReturn = df.format(bytes) + " " + fileSizeUnits[index];
        return sizeToReturn;
    }
    
    public synchronized long writeFile(byte[] data) throws IOException {
        accFile.seek(accFile.length());
        accFile.write(data);
        return accFile.length();
    }
    
    public void close() throws IOException {
        accFile.close();
    }
}
