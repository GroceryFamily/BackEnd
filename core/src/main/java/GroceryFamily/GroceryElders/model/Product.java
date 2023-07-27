package GroceryFamily.GroceryElders.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    @JsonProperty("title")//for barbora
    private String name;
    @JsonProperty("price")//barbora
    private BigDecimal price;

    private Measurement pricePerUnit;

    private Measurement measurement;

    private String addToCard = "Lisa ostukorvi";

    @JsonIgnore
    public Measurement getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Measurement pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Product() {
    }

    public Product(String name, Measurement pricePerUnit, BigDecimal price) {
        this.name = name;
        this.pricePerUnit = pricePerUnit;
        this.price = price;
        this.measurement = Measurement.setValueUnit(name);
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", pricePerUnit=" + pricePerUnit +
                ", measurement=" + measurement +
                ", price=" + price +


                '}';
    }

    //Getters/Setters
    public void setName(String name) {
        this.name = name.toLowerCase();
    }


    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String productName) {
        this.measurement = Measurement.setValueUnit(productName);
    }
}
