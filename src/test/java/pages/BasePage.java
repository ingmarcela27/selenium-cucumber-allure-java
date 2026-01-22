package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    protected void navigateTo(String url) {
        driver.get(url);
    }

    protected void clickWithJS(String locator) {
        WebElement element = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath(locator))
        );

        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", element);
    }

    protected void scrollToElement(String xpath) {
        WebElement element = wait.until( ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)) );
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    protected By getBy(String locator) {
        return By.xpath(locator);
    }

    protected void clickSafe(String xpath) {
        scrollToElement(xpath);
        wait.until(ExpectedConditions.elementToBeClickable(getBy(xpath)));
        clickWithJS(xpath);
    }

}