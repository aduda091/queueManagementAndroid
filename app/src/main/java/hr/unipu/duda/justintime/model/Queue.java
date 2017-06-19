package hr.unipu.duda.justintime.model;


import java.util.LinkedHashMap;

public class Queue {

    private String id;
    private String name;
    private String[] usersInQueue;

    public Queue() {
    }

    public Queue(String id, String name, String[] usersInQueue) {
        this.id = id;
        this.name = name;
        this.usersInQueue = usersInQueue;
    }

    public Queue(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getUsersInQueue() {
        return usersInQueue;
    }

    public void setUsersInQueue(String[] usersInQueue) {
        this.usersInQueue = usersInQueue;
    }
}
