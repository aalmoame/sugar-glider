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

        //set the port of the back end to localhost 8080
        port(8080);

        init();

        //This is required to allow GET and POST requests with the header 'content-type'
        options("/*",
                (request, response) -> {
                    response.header("Access-Control-Allow-Headers",
                            "content-type");

                    response.header("Access-Control-Allow-Methods",
                            "GET, POST");


                    return "OK";
                });


        //read candy list from the excel file
        List<Candy> candyList = ExcelReader.readFromFileStock("./server/resources/Inventory.xlsx");

        //only way I could figure out to manipulate the restock label was by making it a candy element
        //so here it is
        Candy restockElement = new Candy("Restock Cost", 0.0,0.0,0.0);
        candyList.add(restockElement);

        //reading in distributors from excel file
        List<Distributor> distributorList = ExcelReader.readFromFileDistributors("./server/resources/Distributors.xlsx");

        //finding the lowest cost distributors and mapping each candy to the lowest cost
        Map<String, Double> lowestCostCandy = Distributor.lowestCostDistributors(distributorList);


        //This is required to allow the React app to communicate with this API
        before((request, response) -> response.header("Access-Control-Allow-Origin", "http://localhost:3000"));


        //on every request to localhost:8080, send candy list data as JSON
        //make a request to this from the front end to obtain candy data and use in front end

        get("/", (request, response) -> {
            response.type("application/json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(gson.toJsonTree(candyList));
        });

        //TODO: Return JSON containing the candies for which the stock is less than 25% of it's capacity
        get("/low-stock", (request, response) -> {
            response.type("application/json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            //unfortunately have to create a new list in order to remove the restock element
            //could just return a sublist but potential additions of candy later would make it difficult
            List<Candy> removedRestock = new ArrayList<>(candyList);
            removedRestock.remove(restockElement);

            return gson.toJson(gson.toJsonTree(Candy.lowStockCandy(removedRestock)));
        });

        //TODO: Return JSON containing the total cost of restocking candy
        post("/restock-cost", (request, response) -> {

            //getting the input from the request
            Set<String> candyNames = request.queryParams();

            //using capacity variable as the placeholder for cost
            restockElement.setCapacity(0.0);

            //setting cost to 0
            double cost = 0.0;

            //iterating through inputted candy
            for(String candies : candyNames){

                //checking that there is input, and that this candy exists (sometimes weird input)
                if(!request.queryParams(candies).isEmpty() && lowestCostCandy.containsKey(candies)){
                    try {
                        cost += lowestCostCandy.get(candies) * Double.parseDouble(request.queryParams(candies));
                    }catch (NumberFormatException e){
                        response.redirect("http://localhost:3000");
                        e.printStackTrace();
                    }
                }
            }

            //setting the restocks candy element's capacity to the total cost to restock
            restockElement.setCapacity(Precision.round(cost, 2));

            //redirecting back to front end with this newly calculated data
            response.redirect("http://localhost:3000");

            //returning the total cost as a json
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(gson.toJsonTree((restockElement.getCapacity())));
        });


        //added this method as a way to test low stock and also because I thought it would be neat to have there
        post("/submit-order", (request, response) -> {

            //getting input
            Set<String> candyNames = request.queryParams();

            for(Candy candy : candyList){
                if(candyNames.contains(candy.getName()) && !request.queryParams(candy.getName()).isEmpty()){

                    //calculating amount to reorder
                    try{

                        double amnt = candy.getStock() + Double.parseDouble(request.queryParams(candy.getName()));

                        //making sure the users order amount isn't beyond that candy's capacity
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
            return "Order Submitted";
        });

    }

}
