package steps;

import io.cucumber.java.en.*;
import pages.PaginaCursos;
import pages.PaginaPrincipal;
import pages.PaginaRegistro;


public class FreeRangeSteps {

    PaginaPrincipal landingpage = new PaginaPrincipal();
    PaginaCursos cursosPage = new PaginaCursos();
    PaginaRegistro registro = new PaginaRegistro();

    @Given("I navigate to FreeRangeTesters")
    public void iNavigateToFreeRangeTesters() {
        landingpage.navigateToFreeRangeTesters();
    }

    @When("I go to {word} using the navigation bar")
    public void navigationBarUse(String section) {
        landingpage.clickOnSectionNavigationBar(section);
    }

    @And("I select Introduccion al Testing de Software")
    public void navigateToIntro() {
        cursosPage.clickIntroduccionTestingLink();}

    @When("I select Elegir Plan")
    public void iSelectElegirPlan() {
        cursosPage.clickElegirPlan();
    }

    @Then("I am redirected to the checkout page")
    public void iAmRedirectedToCheckout() {
        registro.waitUntilCheckoutIsLoaded();
    }

}
