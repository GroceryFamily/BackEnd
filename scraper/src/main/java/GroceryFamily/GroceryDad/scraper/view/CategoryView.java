package GroceryFamily.GroceryDad.scraper.view;

import GroceryFamily.GroceryDad.scraper.model.Link;

import java.util.List;

public interface CategoryView {
    List<Link> childCategoryLinks();
}