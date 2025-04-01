package com.example.labjava.service;

import com.example.labjava.model.Product;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ExcelExport {

    private static final Logger logger = LoggerFactory.getLogger(ExcelExport.class);

    public byte[] exportProductsToExcel(List<Product> products) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Products");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Назва");
            headerRow.createCell(2).setCellValue("Ціна");
            headerRow.createCell(3).setCellValue("Наявність");
            headerRow.createCell(4).setCellValue("Посилання");

            int rowNum = 1;
            for (Product product : products) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getName());
                row.createCell(2).setCellValue(product.getPrice());
                row.createCell(3).setCellValue(product.getAvailability() ? "В наявності" : "Немає");
                row.createCell(4).setCellValue(product.getLink());
            }

            workbook.write(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            logger.error("Помилка при експорті продуктів в Excel", e);
            return null;
        }
    }
}
