package GroceryFamily.GroceryDad.scraper.view;

import GroceryFamily.GroceryDad.scraper.page.Link;

import java.util.List;

public interface ProductListView {
    List<Link> productPageLinks();

    List<Link> productLinks();
}