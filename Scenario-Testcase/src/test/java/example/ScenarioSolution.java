package example;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ScenarioSolution {
    private static WebDriver chromeDriver;
    private static WebDriver safariDriver;
    private WebDriver edgeDriver;

    private final String EXPECTED_LATEST_CATEGORY_SCENARIO1 = "コンピュータ・IT";
    private final String EXPECTED_LATEST_CATEGORY_SCENARIO2 = "javascript, コンピュータ・IT";


    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        // For Other browsers
        WebDriverManager.safaridriver().setup();
        WebDriverManager.edgedriver().setup();
    }

    public void createHistory() {
        chromeDriver.get("https://jp.mercari.com/");
        WebDriverWait wait = new WebDriverWait(chromeDriver, Duration.ofSeconds(10));


        WebElement searchBarEl = chromeDriver.findElement(By.cssSelector(".sc-de99d471-3.foftCJ"));
        searchBarEl.click();

        WebElement searchByCategoryEl = chromeDriver.findElement(By.xpath("//*[text()='" + "カテゴリーからさがす" + "']"));
        searchByCategoryEl.click();

        WebElement firstCategoryEl = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@href, '/categories?category_id=7')]")));
        firstCategoryEl.click();

        WebElement firstCategoryAllEl = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@href, '/search?category_id=7')]")));
        firstCategoryAllEl.click();
        wait.until(ExpectedConditions.urlToBe("https://jp.mercari.com/search?search_condition_id=1cx0zHGNpZB03"));

    }

    @Test
    public void MercariChromeScenario1Test() {
        chromeDriver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(chromeDriver, Duration.ofSeconds(5));

        // Use this method for creating initial history
        createHistory();

        try {
            // Go to Book category from the search bar
            WebElement searchBarEl = chromeDriver.findElement(By.cssSelector(".sc-de99d471-3.foftCJ"));
            searchBarEl.click();

            WebElement searchByCategoryEl = chromeDriver.findElement(By.xpath("//*[text()='" + "カテゴリーからさがす" + "']"));
            searchByCategoryEl.click();

            WebElement bookCategoryEl = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@href, '/categories?category_id=5')]")));
            bookCategoryEl.click();

            WebElement bookAllEl = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@href, '/search?category_id=5')]")));
            bookAllEl.click();

            // Select subcategory to be Books in the left menu
            WebElement subCategoryEl = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".merInputNode.select__da4764db.medium__da4764db.placeholder__da4764db")));
            Select subCategoryDropdown = new Select(subCategoryEl);
            subCategoryDropdown.selectByValue("72");

            // Click the checkbox for Computer/IT
            WebElement computerITCategoryEl = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[value='674']")));
            computerITCategoryEl.click();
            wait.until(ExpectedConditions.attributeToBe(computerITCategoryEl, "aria-checked", "true"));

            // Check if the Books & Computer IT is selected
            ((JavascriptExecutor) chromeDriver).executeScript("window.scrollTo(0, 0);");
            WebElement h1Element = chromeDriver.findElement(By.cssSelector(".merHeading.page__a7d91561.sc-1a095b48-3.jLqVfG mer-spacing-b-4"));
            Assertions.assertTrue(h1Element.getText().contains("本 コンピュータ・IT"), "Category is not set to Books");
            Assertions.assertEquals(computerITCategoryEl.getAttribute("aria-checked"), "true", "Computer and IT is not checked");

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void MercariChromeScenario2Test() {
        chromeDriver.get("https://jp.mercari.com/");
        WebDriverWait wait = new WebDriverWait(chromeDriver, Duration.ofSeconds(5));


        Assertions.assertEquals(getHistoryCount(), 2);

        // Validate the content of the latest category
        WebElement latestHistoryEl = getLatestCategory();
        Assertions.assertEquals(latestHistoryEl.getText(), EXPECTED_LATEST_CATEGORY_SCENARIO1);

        // Check the latest category has all condition in the sidebar
        latestHistoryEl.click();
        WebElement h1Element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".heading__a7d91561.page__a7d91561")));
        Assertions.assertTrue(h1Element.getText().contains("本 コンピュータ・IT"), "Category is not Books");
        WebElement subCategoryEl =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("formGroup__d482e825")));
        List<WebElement> subCategoryChild = subCategoryEl.findElements(By.xpath(".//input[@type='checkbox']"));

        // Validate only the computer/IT has the checkbox checked
        WebElement targetEl = null;
        int targetCount = 0;
        for (WebElement child : subCategoryChild) {
            if ("true".equals(child.getAttribute("aria-checked"))) {
                targetEl = child;
                targetCount++;
            }
        }
        Assertions.assertEquals(targetCount, 1,"Checked category is not only Computer/IT");
        Assertions.assertEquals(targetEl.getAttribute("value"), "674");


        // Search for 'javascript'
        WebElement searchBarEl = chromeDriver.findElement(By.cssSelector(".sc-de99d471-3.foftCJ"));
        searchBarEl.click();
        searchBarEl.sendKeys("javascript");
        searchBarEl.submit();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".heading__a7d91561.page__a7d91561")));

        // return to main page
        chromeDriver.get("https://jp.mercari.com/");


        // Validate History after 'javascript' search
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("textContent__261d11db")));
        // Validate history count
        Assertions.assertEquals(3, getHistoryCount(), "History count is not 3");
        latestHistoryEl = getLatestCategory();
        // Validate the last category searched
        Assertions.assertEquals(latestHistoryEl.getText(), EXPECTED_LATEST_CATEGORY_SCENARIO2);

    }

    // method to get the history count in search
    public int getHistoryCount() {
        WebDriverWait wait = new WebDriverWait(chromeDriver, Duration.ofSeconds(10));

        WebElement searchBarEl = chromeDriver.findElement(By.cssSelector(".sc-de99d471-3.foftCJ"));
        searchBarEl.click();

        WebElement historySection = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".merList.border__17a1e07b.separator__17a1e07b")));
        List<WebElement> categoryHistoryList = historySection.findElements(By.xpath("./div"));

        return categoryHistoryList.size();
    }

    // method to grab element of latest category from history
    public WebElement getLatestCategory() {
        WebDriverWait wait = new WebDriverWait(chromeDriver, Duration.ofSeconds(5));

        WebElement historySection = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".merList.border__17a1e07b.separator__17a1e07b")));
        WebElement latestHistoryEl  = historySection.findElement(By.xpath("./div[1]"));

        return latestHistoryEl;
    }

    @AfterAll
    public static void cleanup() {
        chromeDriver.close();
    }
}