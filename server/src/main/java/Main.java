import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.Precision;
import spark.TemplateEngine;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import static spark.Spark.*;

public class Main {

    public static void main(String[] args) throws IOException {

        port(8080);
        init();

        //This is required to allow GET and POST requests with the header 'content-type'
        options("/*",
                (request, response) -> {
                    response.header("Access-Control-Allow-Headers",
                            "content-type");

                    response.header("Access-Control-Allow-Methods",
                            "GET, POST, OPTIONS");


                    return "OK";
                });


        List<Candy> candyList = ExcelReader.readFromFileStock("./server/resources/Inventory.xlsx");

        Candy restockElement = new Candy("Restock Cost", 0.0,0.0,0.0);
        candyList.add(restockElement);

        List<Distributor> distributorList = ExcelReader.readFromFileDistributors("./server/resources/Distributors.xlsx");
        Map<String, Double> lowestCostCandy = Distributor.lowestCostDistributors(distributorList);


        //This is required to allow the React app to communicate with this API
        before((request, response) -> response.header("Access-Control-Allow-Origin", "http://localhost:3000"));


        get("/", (request, response) -> {
            response.type("application/json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(gson.toJsonTree(candyList));
        });

        //TODO: Return JSON containing the candies for which the stock is less than 25% of it's capacity
        get("/low-stock", (request, response) -> {
            response.type("application/json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(gson.toJsonTree(Candy.lowStockCandy(candyList)));
        });

        //TODO: Return JSON containing the total cost of restocking candy
        post("/restock-cost", (request, response) -> {
            Set<String> candyNames = request.queryParams();

            //using capacity variable as the placeholder for cost
            restockElement.setCapacity(0.0);

            double cost = 0.0;
            for(String candies : candyNames){
                if(!request.queryParams(candies).isEmpty() && lowestCostCandy.containsKey(candies)){
                    try {
                        cost += lowestCostCandy.get(candies) * Double.parseDouble(request.queryParams(candies));
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                    }
                }
            }
            restockElement.setCapacity(Precision.round(cost, 2));
            response.redirect("http://localhost:3000");
            return restockElement.getCapacity();
        });

        post("/submit-order", (request, response) -> {
            Set<String> candyNames = request.queryParams();
            for(Candy candy : candyList){
                if(candyNames.contains(candy.getName()) && !request.queryParams(candy.getName()).isEmpty()){

                    try{
                        double amnt = candy.getStock() + Double.parseDouble(request.queryParams(candy.getName()));
                        if(amnt <= candy.getCapacity()){
                            candy.setStock(amnt);
                        }
                        else{
                            return "Candy " + candy.getName() + " has a maximum capacity of " + (int) candy.getCapacity().doubleValue();
                        }

                    }catch (NumberFormatException e){
                        e.printStackTrace();
                        response.redirect("http://localhost:3000");
                    }
                }
            }
            response.redirect("http://localhost:3000");
            return null;
        });

    }

}
