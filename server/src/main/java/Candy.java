
public class Candy{

    private String name;
    private Double stock;
    private Double capacity;
    private Double id;

    public Candy() {
    }

    public Candy(String name, Double stock, Double capacity, Double id) {
        this.name = name;
        this.stock = stock;
        this.capacity = capacity;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getStock() {
        return stock;
    }

    public void setStock(Double stock) {
        this.stock = stock;
    }

    public Double getCapacity() {
        return capacity;
    }

    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }

    public Double getId() {
        return id;
    }

    public void setId(Double id) {
        this.id = id;
    }

    public boolean isLowStock(){
        return stock < (capacity * 0.25);
    }
}
