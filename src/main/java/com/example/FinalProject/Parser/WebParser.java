package com.example.FinalProject.Parser;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.example.FinalProject.model.Product;
import com.example.FinalProject.productCheckOnThePage.NameChecking;
import com.example.FinalProject.service.GroceryInfoService;
import com.example.FinalProject.service.GroceryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
abstract class WebParser {

    protected String url;
    protected WebDriver driver;
    protected NameChecking nameChecking;
    protected List<Product> onThePage;
    protected List<String> namesFromDB;
    protected List<String> quantityFromDB;





    @Autowired
    protected GroceryInfoService groceryInfoService;

    WebParser(WebDriver driver,GroceryInfoService groceryInfoService){
        this.driver=driver;
        this.groceryInfoService=groceryInfoService;
    }
    //Setters/Getters


    public List<String> getNamesFromDB() {
        return namesFromDB=groceryInfoService.getProductName();
    }

    public List<String> getQuantityFromDB(){
        return quantityFromDB=groceryInfoService.getQuantity();
    }

    public List<Product> getOnThePage() {
        return onThePage;
    }

    public void setOnThePage(List<Product> onThePage) {
        this.onThePage = onThePage;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    //Methods
    void scrapeWebSite() {
        logIntoWebSite(getUrl());
        removeCookiePopup();
    }

    void logIntoWebSite(String url) {
        driver.get(url);
    }

    //for work for Rimi and Barbora it is good.
    // For Prisma I don't need consent to save cookie
    void removeCookiePopup() {
        WebElement element =
                driver.findElement(By
                        .id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"));


        element.click();
    }

    //get List of product on the page. Parsing for getting all information
    // about product on the searching page
    public List<String> getGroceriesInfoOnThePage(String cssSelector){

        return null;
    }
    //Searching all price per unit on the page
    public BigDecimal getUnitPrice(int numberOfElement) {
        return null;
    }






    //Sort for getting Product obj
    public List<Product> getProducts(List<String> info){

        return null;
    }
    void addToCard() {
    }


}
