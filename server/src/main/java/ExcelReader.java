import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import spark.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelReader {


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


    public static List<Distributor> readFromFileDistributors(String excelFilePath) throws IOException, FileNotFoundException {

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
}
