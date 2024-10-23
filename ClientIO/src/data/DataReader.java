/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import io.socket.client.Ack;
import io.socket.client.Socket;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import org.json.JSONException;
import org.json.JSONObject;

public class DataReader {

    /**
     * @return the fileID
     */
    public int getFileID() {
        return fileID;
    }

    /**
     * @param fileID the fileID to set
     */
    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

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
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public DataReader(File file) throws FileNotFoundException, IOException {
        //r is mode file read only
        accFile = new RandomAccessFile(file, "r");
        this.file = file;
        this.fileSize = accFile.length();
        this.fileName = file.getName();
    }

    private int fileID;
    private File file;
    private long fileSize;
    private String fileName;
    private RandomAccessFile accFile;

    public synchronized byte[] readFile() throws IOException {
        long filePointer = accFile.getFilePointer();
        if (filePointer != fileSize) {
            int max = 2000;
            // 2000 is max send file per package
            //we spite it to send large file
            long length = filePointer + max >= fileSize ? fileSize - filePointer : max;
            byte[] data = new byte[(int) length];
            accFile.read(data);
            return data;
        } else {
            return null;
        }
    }

    public void close() throws IOException {
        accFile.close();
    }

    public String getFileSizeConverted() {
        double bytes = fileSize;
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

    public double getPercentage() throws IOException {
        double percentage;
        long filePointer = accFile.getFilePointer();
        percentage = filePointer * 100 / fileSize;
        return percentage;
    }

    public Object[] toRowTable(int no) {
        return new Object[]{this, no, fileName, getFileSizeConverted(), "Next update"};
    }

    public void startSend(Socket socket) throws JSONException {
        JSONObject data = new JSONObject();
        data.put("fileName", fileName);
        data.put("fileSize", fileSize);
        socket.emit("send_file", data, new Ack() {
            @Override
            public void call(Object... os) {
                //this call back function
                // Index 0 boolean, Index 1 FileID
                if (os.length > 0) {
                    boolean action = (boolean) os[0];
                    if (action) {
                        //fileID generate by server then return back with this function

                        fileID = (int) os[1];
                        //starting send file
                        try {
                            sendingFile(socket);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                // This call back function
            }
        });
    }

    private void sendingFile(Socket socket) throws IOException, JSONException {
        JSONObject data = new JSONObject();
        data.put("fileID", fileID);
        byte[] bytes = readFile();
        if (bytes != null) {
            data.put("data", bytes);
            data.put("finish", false);
        } else {
            data.put("finish", true);
            close();//close file
        }
        socket.emit("sending", data, new Ack() {
            @Override
            public void call(Object... os) {
                //Call back function to sending more file
                // This function meaning the server has receive file we have sent
                // So we need to send more files until we finish 
                // We response boolean true or false
                if (os.length > 0) {
                    boolean act = (boolean) os[0];
                    if (act) {
                        try {
                            // This function will recursive until act = false 
                            sendingFile(socket);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

    }
}
