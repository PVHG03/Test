/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import com.corundumstudio.socketio.SocketIOClient;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author vdung
 */
public class DataClient {

    private SocketIOClient client;
    private String name;
    //Key interger is fileID
    //Use hash to store multi transfer
    private final HashMap<Integer, DataWriter> list = new HashMap<>();

    /**
     * @return the client
     */
    public SocketIOClient getClient() {
        return client;
    }

    /**
     * @param client the client to set
     */
    public void setClient(SocketIOClient client) {
        this.client = client;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public DataClient(SocketIOClient client, String name) {
        this.client = client;
        this.name = name;
    }

    public DataClient() {
    }
    
    public void addWrite(DataWriter data, int fileID) {
        list.put(fileID, data);
    }
    
    public void writeFile(byte[] data, int fileID) throws IOException {
        list.get(fileID).writeFile(data);
    }

    public Object[] toRowTable(int row) {
        return new Object[]{this, row, name};
    }
}
