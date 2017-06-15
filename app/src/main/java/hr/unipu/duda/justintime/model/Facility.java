package hr.unipu.duda.justintime.model;



public class Facility {

    private String id;
    private String name;
    private String address;
    private String telephone;
    private String mail;
    private String[] queues;

    public Facility(String id, String name, String address, String telephone, String mail, String[] queues) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.telephone = telephone;
        this.mail = mail;
        this.queues = queues;
    }

    public Facility(String id, String name, String address, String telephone, String mail) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.telephone = telephone;
        this.mail = mail;
    }

    public Facility() {}


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

    public String[] getQueues() {
        return queues;
    }

    public void setQueues(String[] queues) {
        this.queues = queues;
    }

    @Override
    public String toString() {
        return name;
    }
}
