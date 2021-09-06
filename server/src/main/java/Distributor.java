import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Map<String, Double> lowestCostDistributors(List<Distributor> distributorList){

        Map<String, Double> lowestCost = new HashMap<>();

        for(Distributor distributor : distributorList){

            if(!lowestCost.containsKey(distributor.getCandyName())){
                lowestCost.put(distributor.getCandyName(), distributor.getCost());
            }
            else if(distributor.getCost() < lowestCost.get(distributor.getCandyName())){
                lowestCost.replace(distributor.getCandyName(), distributor.getCost());
            }

        }

        return lowestCost;

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
