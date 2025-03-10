package projects.ProjectAutomation;

import java.time.Duration;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;
import static org.testng.AssertJUnit.assertTrue;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

public class ProjectAutomation {

    public static void main(String[] args) {
        ProjectAutomation run = new ProjectAutomation();
        run.API();
        run.DB();
        run.teardown();
    }

    WebDriver driver = new ChromeDriver();
    String baseUrl1="https://www.youtube.com/";
    String baseUrl2="https://jsonplaceholder.typicode.com/users";

    //-------Set up FluentWait-------
    //
    Wait<WebDriver> wait = new FluentWait<>(driver)
            .withTimeout(Duration.ofSeconds(30)) // Max time to wait
            .pollingEvery(Duration.ofSeconds(1)) // How often to check the condition
            .ignoring(NoSuchElementException.class) // Ignore NoSuchElementException
            .ignoring(WebDriverException.class); // Ignore WebDriverException

    @Test
    public void API()
    {
        driver.get(baseUrl1);

        //-------Eat Cookies-------
        //
        try {
            WebElement youtubeCookies = driver.findElement(By.xpath("//button[@aria-label=\"Accept the use of cookies and other data for the purposes described\"]"));
            youtubeCookies.click();
        }
        catch (Exception e)
        {
            System.out.println("Cookies already handled");
        }


        //-------Assert the YouTube logo to make sure the page has loaded as expected-------
        //
        WebElement YT_Logo = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//yt-icon[@id='logo-icon']")));
        assertTrue(YT_Logo.isDisplayed());


        //-------Assert if the search bar is displayed-------
        //
        WebElement srcBar = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder=\"Search\"]")));
        assertTrue(wait.until(ExpectedConditions.visibilityOf(srcBar)).isDisplayed());


        //-------Search for a video and assert that it's listed in the results-------
        //
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder=\"Search\"]"))).sendKeys("dragostea din tei");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder=\"Search\"]"))).sendKeys(Keys.ENTER);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//yt-formatted-string[@class=\"style-scope ytd-video-renderer\"]")));
        assertEquals("O-Zone - Dragostea Din Tei [Official Video]", driver.findElement(By.xpath("//yt-formatted-string[@class=\"style-scope ytd-video-renderer\"]")).getText());


        //-------Open video and assert web address-------
        //
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//yt-formatted-string[@class=\"style-scope ytd-video-renderer\"]"))).click();
        String vLink = driver.getCurrentUrl();
        assertEquals("https://www.youtube.com/watch?v=YnopHCL1Jk8", vLink);


        //-------Print Status-------
        //
        System.out.println("API TEST SUCCESSFUL");
    }


    @Test
    public void DB()
    {
        driver.get(baseUrl2);

        //-------Set request & response for POST action-------
        //
        String requestBody = "{\n" +
                "    \"phone\": \"0788070333\",\n" +
                "    \"website\": \"PleaseDoNotVisit.ro\",\n" +
                "    \"company\": {\n" +
                "        \"name\":\"DND\"    \n" +
                "    }\n" +
                "}";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post(baseUrl2);


        //-------Assert status code-------
        //
        assertEquals(201, response.getStatusCode());


        //-------Assert all elements(phone/website/company name/id)-------
        //
        assertEquals("0788070333", response.jsonPath().getString("phone"));
        assertEquals("PleaseDoNotVisit.ro", response.jsonPath().getString("website"));
        assertEquals("DND", response.jsonPath().getString("company.name"));
        assertEquals("11",  response.jsonPath().getString("id"));


        //-------Print Status-------
        //
        System.out.println("DB TEST SUCCESSFUL");
    }

    @After
    public void teardown()
    {
        driver.quit();
    }
}
