package GroceryFamily.GroceryDad.scraper.context.prisma;

import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

class PrismaView {
    static String productCode(String url) {
        return substringAfterLast(substringBeforeLast(url, "/"), "/");
    }
}