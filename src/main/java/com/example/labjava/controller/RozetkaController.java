package com.example.labjava.controller;

import com.example.labjava.model.Product;
import com.example.labjava.repository.ProductRepository;
import com.example.labjava.service.ExcelExport;
import com.example.labjava.service.ExchangeRate;
import com.example.labjava.service.RozetkaParser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.List;

@Controller
public class RozetkaController {

    private final RozetkaParser rozetkaParserService;
    private final ExchangeRate exchangeRateService;
    private final ExcelExport excelExportService;
    private final ProductRepository productRepository;

    public RozetkaController(RozetkaParser rozetkaParserService, ExchangeRate exchangeRateService, ExcelExport excelExportService, ProductRepository productRepository) {
        this.rozetkaParserService = rozetkaParserService;
        this.exchangeRateService = exchangeRateService;
        this.excelExportService = excelExportService;
        this.productRepository = productRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        String exchangeRates = exchangeRateService.getExchangeRates();
        model.addAttribute("exchangeRates", exchangeRates);
        return "home";
    }

    @GetMapping("/scrapeResults")
    public String parseProducts(Model model) {
        try {
            List<Product> products = rozetkaParserService.parseProducts();
            model.addAttribute("products", products);
            model.addAttribute("message", "Парсинг пройшов успішно!");
        } catch (IOException e) {
            model.addAttribute("message", "Помилка при парсингу: " + e.getMessage());
        }
        return "scrapeResults";
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel() {
        List<Product> products = productRepository.findAll();
        byte[] bytes = excelExportService.exportProductsToExcel(products);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    @GetMapping("/currencyRates")
    public String getExchange(Model model) {
        String exchangeRates = exchangeRateService.getExchangeRates();
        model.addAttribute("exchangeRates", exchangeRates);
        return "currencyRates";
    }
}