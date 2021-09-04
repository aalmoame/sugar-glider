import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.server.HttpChannel;
import spark.ModelAndView;
import spark.Request;
import spark.template.velocity.VelocityTemplateEngine;
import spark.utils.StringUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
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


        List<Candy> candyList = readFromFileStock("./server/resources/Inventory.xlsx");
        List<Candy> lowStockCandy = lowStockCandy(candyList);
        List<Candy> finalCandyList = candyList;

        List<Distributor> distributorList = readFromFileDistributors("./server/resources/Distributors.xlsx");
        List<Distributor> finalDistributorList = distributorList;

        Map<String, Double> lowestCostCandy = lowestCostDistributors(distributorList);
        Map<String, Double> finalLowestCostCandy = lowestCostCandy;

        //This is required to allow the React app to communicate with this API
        before((request, response) -> response.header("Access-Control-Allow-Origin", "http://localhost:3000"));
        after((req, res) -> {
            res.type("application/json");
        });

        get("/", (request, response) -> {
            response.type("application/json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(gson.toJsonTree(finalCandyList));
        });

        //TODO: Return JSON containing the candies for which the stock is less than 25% of it's capacity
        get("/low-stock", (request, response) -> {
            response.type("application/json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(gson.toJsonTree(lowStockCandy));
        });

        //TODO: Return JSON containing the total cost of restocking candy
        post("/restock-cost", (request, response) -> {
            Set<String> candyNames = request.queryParams();
            double totalCost = 0.0;
            for(String candies : candyNames){
                if(!request.queryParams(candies).isEmpty() && finalLowestCostCandy.containsKey(candies)){
                    totalCost += finalLowestCostCandy.get(candies) * Double.parseDouble(request.queryParams(candies));
                }
            }
            return totalCost;
        });

    }

    public static Object getCellValue(Cell cell){
        switch (cell.getCellType()){
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return cell.getNumericCellValue();
        }
        return null;
    }

    public static List<Candy> readFromFileStock(String excelFilePath) throws IOException {

        List<Candy> candyList = new ArrayList<>();

        FileInputStream inputStream = new FileInputStream(new File(excelFilePath));

        Workbook workbook = new XSSFWorkbook(inputStream);

        Sheet firstSheet = workbook.getSheetAt(0);

        Iterator<Row> iterator = firstSheet.iterator();

        if(iterator.hasNext()){
            iterator.next();
        }


        while (iterator.hasNext()){
            Row nextRow = iterator.next();

            Candy candy = new Candy();

            Iterator<Cell> cellIterator = nextRow.cellIterator();

            while(cellIterator.hasNext()){
                Cell nextCell = cellIterator.next();
                int columnIndex = nextCell.getColumnIndex();

                switch (columnIndex){
                    case 0:
                        candy.setName((String) getCellValue(nextCell));
                        break;
                    case 1:
                        candy.setStock((Double) getCellValue(nextCell));
                        break;
                    case 2:
                        candy.setCapacity((Double) getCellValue(nextCell));
                        break;
                    case 3:
                        candy.setId((Double) getCellValue(nextCell));
                        break;

                }

            }

            candyList.add(candy);
        }

        workbook.close();
        inputStream.close();

        return candyList;
    }

    public static List<Candy> lowStockCandy(List<Candy> candyList){
        List<Candy> lowStock = new ArrayList<>();
        for(Candy candy : candyList){
            if(candy.isLowStock()){
                lowStock.add(candy);
            }
        }
        return lowStock;
    }

    public static List<Distributor> readFromFileDistributors(String excelFilePath) throws IOException {

        List<Distributor> distributorList = new ArrayList<>();

        FileInputStream inputStream = new FileInputStream(new File(excelFilePath));

        Workbook workbook = new XSSFWorkbook(inputStream);

        int totalSheets = workbook.getNumberOfSheets();

        for(int i = 0; i < totalSheets; ++i){

            Sheet sheetAt = workbook.getSheetAt(i);

            Iterator<Row> iterator = sheetAt.iterator();

            if(iterator.hasNext()){
                iterator.next();
            }


            while (iterator.hasNext()){
                Row nextRow = iterator.next();

                Distributor distributor = new Distributor();

                Iterator<Cell> cellIterator = nextRow.cellIterator();

                boolean addToList = false;

                while(cellIterator.hasNext()){


                    Cell nextCell = cellIterator.next();
                    int columnIndex = nextCell.getColumnIndex();

                    if(StringUtils.isNotBlank(nextCell.toString())
                            && nextCell.getCellType()!= CellType.BLANK){

                        addToList = true;
                        switch (columnIndex){
                            case 0:
                                distributor.setCandyName(((String) getCellValue(nextCell)));
                                break;
                            case 1:
                                distributor.setId((Double) getCellValue(nextCell));
                                break;
                            case 2:
                                distributor.setCost((Double) getCellValue(nextCell));
                                break;

                        }

                    }

                }

                if(addToList){
                    distributorList.add(distributor);
                }

            }

        }


        workbook.close();
        inputStream.close();

        return distributorList;
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

}
