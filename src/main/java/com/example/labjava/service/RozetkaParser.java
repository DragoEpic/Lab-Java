package com.example.labjava.service;

import com.example.labjava.model.Product;
import com.example.labjava.repository.ProductRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RozetkaParser {

    private final ProductRepository productRepository;

    public RozetkaParser(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> parseProducts() throws IOException {
        List<Product> products = new ArrayList<>();
        String url = "https://rozetka.com.ua/ua/video_cards/c80087/";
        Document doc = Jsoup.connect(url).get();

        Elements productElements = doc.select("div.goods-tile");

        for (Element element : productElements) {
            boolean available = !element.select("div.goods-tile__availability--out-of-stock").hasText();
            if (!available) {
                continue;
            }

            Product product = new Product();
            String name = element.select("span.goods-tile__title").text();
            String priceStr = element.select("span.goods-tile__price-value").text().replaceAll("[^\\d.]", "");
            Double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                price = 0.0;
            }
            String link = element.select("a").attr("href");

            product.setName(name);
            product.setPrice(price);
            product.setAvailability(available);
            product.setLink(link);

            products.add(product);
        }
        productRepository.saveAll(products);
        return products;
    }
}