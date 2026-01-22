package pages;

import config.EnvironmentManager;
import driver.DriverManager;

public class PaginaPrincipal extends BasePage {


    public PaginaPrincipal() {
        super(DriverManager.getDriver());
    }

    //Metodo para navegar a la pagina
    public void navigateToFreeRangeTesters(){
        navigateTo(EnvironmentManager.get("base.url"));
    }

    public void clickOnSectionNavigationBar(String section) {
        // Reemplaza el marcador de posición en sectionLink con el nombre
        String sectionLink = "//a[normalize-space()='%s' and @href]";
        String xpathSection = String.format(sectionLink, section);
        clickSafe(xpathSection);

    }

}
