public class Distributor {
    private String candyName;
    private double id;
    private double cost;

    public Distributor(String candyName, double id, double cost) {
        this.candyName = candyName;
        this.id = id;
        this.cost = cost;
    }

    public Distributor() {
    }

    public String getCandyName() {
        return candyName;
    }

    public void setCandyName(String candyName) {
        this.candyName = candyName;
    }

    public double getId() {
        return id;
    }

    public void setId(double id) {
        this.id = id;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Distributor{" +
                "candyName='" + candyName + '\'' +
                ", id=" + id +
                ", cost=" + cost +
                '}';
    }
}
