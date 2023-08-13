package GroceryFamily.GroceryDad.scraper.tree;

import GroceryFamily.GroceryDad.scraper.page.link.CategoryLink;

public class CategoryLinkTree extends Tree<String, CategoryLink> {
    public void add(CategoryLink link) {
        add(link.codePath.segments(), link);
    }
}