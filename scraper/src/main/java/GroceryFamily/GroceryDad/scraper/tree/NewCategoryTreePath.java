package GroceryFamily.GroceryDad.scraper.tree;

import java.util.List;

public class NewCategoryTreePath {
    final String[] codePath;

    public NewCategoryTreePath(String first) {
        this(new String[]{first});
    }

    private NewCategoryTreePath(String[] codePath) {
        this.codePath = codePath;
    }

    public String last() {
        return codePath[codePath.length - 1];
    }

    public NewCategoryTreePath add(String category) {
        String[] codePath = new String[this.codePath.length + 1];
        System.arraycopy(this.codePath, 0, codePath, 0, this.codePath.length);
        codePath[this.codePath.length] = category;
        return new NewCategoryTreePath(codePath);
    }

    public List<String> codePath() {
        return List.of(codePath);
    }
}