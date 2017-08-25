package hr.unipu.duda.justintime.model;


import java.util.LinkedHashMap;

public class Queue {

    private String id;
    private String name;
    private int priority = 0;
    private Facility facility;

    public Queue() {
        //priority = (int) (Math.random()*10)+1;
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

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
    @Override
    public String toString() {
        return name;
    }
}
