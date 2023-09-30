package com.daniel.amazonTracker.scheduler;

import com.daniel.amazonTracker.model.Product;
import com.daniel.amazonTracker.repository.ProductRepository;
import com.twilio.rest.api.v2010.account.Message;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import com.twilio.Twilio;


public class ProductScraperJob extends Thread {

    private Product product;

    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");

    public ProductRepository productRepository;

    @Autowired
    public ProductScraperJob(Product product, ProductRepository productRepository) {
        this.product = product;
        this.productRepository = productRepository;
    }

    public void run() {
        Document doc;
        try {
            doc = Jsoup.connect(this.product.url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();

            Elements priceSpan = doc.select(".a-price-whole");
            Elements productTitleElem = doc.select("#productTitle");

            String productTitle = productTitleElem.first().text();
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            int currentPrice = 0;
            try {
                currentPrice = format.parse(priceSpan.first().text()).intValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }


            if (product.prices.size() == 365) {
                product.prices.remove(0);
            }

            Integer flag = 0;

            for(Integer value: product.prices) {
                if (value <= currentPrice) {
                    flag = 1;
                    break;
                }
            }

            product.prices.add(currentPrice);
            this.productRepository.save(product);

            if (flag == 0) {

                Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

                for (String user: product.usersTracking) {
                    Message.creator(
                            new com.twilio.type.PhoneNumber(user),
                            new com.twilio.type.PhoneNumber("<twilio_phone_number_assigned>"),
                            ("Price for your tracked product " + product.name + " has dropped to - " + currentPrice)
                    ).create();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }
