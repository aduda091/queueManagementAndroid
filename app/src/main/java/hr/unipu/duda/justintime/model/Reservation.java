package hr.unipu.duda.justintime.model;


public class Reservation {
    private String id;
    private String user;
    private Queue queue;
    private Facility facility;
    private String time;
    private int number;

    public Reservation() {
    }

    public Reservation(String user, Queue queue, Facility facility, String time, int number) {
        this.user = user;
        this.queue = queue;
        this.facility = facility;
        this.time = time;
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
