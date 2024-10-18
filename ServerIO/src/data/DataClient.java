/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import com.corundumstudio.socketio.SocketIOClient;

/**
 *
 * @author vdung
 */
public class DataClient {

    private SocketIOClient client;
    private String name;

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

    public Object[] toRowTable(int row) {
        return new Object[]{this, row, name};
    }
}
