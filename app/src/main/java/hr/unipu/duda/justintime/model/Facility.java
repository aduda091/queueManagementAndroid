package hr.unipu.duda.justintime.model;


import java.util.ArrayList;
import java.util.List;

public class Facility {

    private String id;
    private String name;
    private String address;
    private String telephone;
    private String mail;
    private List<Queue> queues;


    public Facility() {
        queues = new ArrayList<>();
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public List<Queue> getQueues() {
        return queues;
    }

    public void setQueues(List<Queue> queues) {
        this.queues = queues;
    }

    public void addQueue(Queue queue) {
        queues.add(queue);
    }

    @Override
    public String toString() {
        return name;
    }
}
