package pages;

import driver.DriverManager;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class PaginaCursos extends BasePage {

    public PaginaCursos() {
        super(DriverManager.getDriver());
    }

    public void clickIntroduccionTestingLink() {

        String courseLink = "//a[@href='/introduccion-al-testing-de-software']";

        scrollToElement(courseLink);
        clickWithJS(courseLink);

        String header =
                "//h1[contains(text(),'Introducción al Testing')]";

        wait.until(ExpectedConditions.urlContains(
                "introduccion-al-testing-de-software"
        ));
    }

    public void clickElegirPlan() {
        clickSafe("//a[contains(text(),'Elegir este plan')]");

    }
}
