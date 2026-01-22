package driver;

import org.openqa.selenium.WebDriver;

public class DriverManager {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void setDriver(WebDriver driverInstance) {
        driver.set(driverInstance);
    }

    public static void quitDriver() {
        WebDriver currentDriver = driver.get();
        if (currentDriver != null) {
            currentDriver.quit();
            driver.remove();
        }
    }
}






//NotasLocal (ver navegador): Ejecutar gradle test -Dheadless=false
//       CI (sin ver navegador): Ejecutar gradle test -Dheadless=true
//comando para correr en ambiente dev: > ./gradlew test -Denv=dev
//comando para correr en ambiente dev: > ./gradlew test -Denv=qa
//comando ejecutar test pero limpiando antes: ./gradlew clean test
// ./gradlew allureServe -genera el reporte inmediato
// ./gradlew clean test -Dheadless=false
// ./gradlew clean test "-Dcucumber.filter.tags=@Courses" -Dheadless=false.