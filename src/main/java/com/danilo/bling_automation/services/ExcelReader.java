package com.danilo.bling_automation.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class ExcelReader {

    public Map<String, String> readExcel(String fileName, String sku) {
        Map<String, String> productData = new HashMap<>();

        try {
            ClassPathResource resource = new ClassPathResource(fileName);
            InputStream fis = resource.getInputStream();

            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Cell skuCell = row.getCell(0); // SKU na primeira coluna
                if (skuCell.getCellType() == CellType.STRING && skuCell.getStringCellValue().equals(sku) ||
                        skuCell.getCellType() == CellType.NUMERIC && String.valueOf((int) skuCell.getNumericCellValue()).equals(sku)) {
                    productData.put("sku", getCellValue(skuCell));
                    productData.put("description", getCellValue(row.getCell(1)));
//                    productData.put("caracteristics", getCellValue(row.getCell(2)));
//                    productData.put("provider", getCellValue(row.getCell(3)));
//                    productData.put("code", getCellValue(row.getCell(4)));
                    productData.put("collection", getCellValue(row.getCell(5)));
                    productData.put("size", getCellValue(row.getCell(6)));
                    productData.put("costPrice", getCellValue(row.getCell(7)).replace("R$", "").trim());
                    productData.put("salePrice", getCellValue(row.getCell(8)).replace("R$", "").trim());
                    productData.put("stock", getCellValue(row.getCell(9)));

                    // Dividir a descrição para extrair o tipo e o banho
                    String description = productData.get("description").toLowerCase();
                    if (description.contains("ouro 18k")) {
                        productData.put("bath", "Ouro 18K");
                        productData.put("color", "Dourado");
                    } else if (description.contains("ródio branco")) {
                        productData.put("bath", "Ródio Branco");
                        productData.put("color", "Prateado");
                    }

                    if (description.contains("argola")) {
                        productData.put("type", "Brinco Argola");
                    } else if (description.contains("bracelete")) {
                        productData.put("type", "Bracelete");
                    } else if (description.contains("gota")) {
                        productData.put("type", "Brinco Gota");
                    } else if (description.contains("anel")) {
                        productData.put("type", "Anel");
                    }

                    // Adicione outros tipos conforme necessário
                    break;
                }
            }

            workbook.close();
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return productData;
    }

    private String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
