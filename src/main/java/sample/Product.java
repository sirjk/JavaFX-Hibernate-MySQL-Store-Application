package sample;

public class Product {
    int id;
    String name;
    int available;

    public Product(){
        this.id = -1;
        this.name = "";
        this.available = -1;
    }

    public Product(int id, String name, int available){
        this.id = id;
        this.name = name;
        this.available = available;
    }

    public void increaseAvailable(int quantity){
        this.available += quantity;
    }

    //co w sytuacji gdy odejmiemy wiecej niz jest?
    //available ponizej zera
    public void decreaseAvailable(int quantity){
        this.available -= quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }
}
