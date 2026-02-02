package pages;

import driver.DriverManager;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class PaginaRegistro extends BasePage {

    public PaginaRegistro() {
        super(DriverManager.getDriver());
    }

    //public void waitUntilCheckoutIsLoaded() {wait.until(ExpectedConditions.urlContains("payment"));}
   public void waitUntilCheckoutIsLoaded() { wait.until(ExpectedConditions.urlContains("checkout"));}
}
