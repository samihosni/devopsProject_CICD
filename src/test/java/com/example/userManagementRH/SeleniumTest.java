package com.example.userManagementRH;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class SeleniumTest {
    public static void main(String[] args) {
        // Set up Chrome in headless mode
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");  // Ensures the browser runs in the background
        options.addArguments("--no-sandbox");  // Sometimes necessary for Jenkins
        options.addArguments("--disable-dev-shm-usage");  // Prevents errors in some environments
        WebDriver driver = new ChromeDriver(options);

        driver.get("http://localhost:8083");  // URL of your app
        System.out.println("Page title is: " + driver.getTitle());
        driver.quit();
    }
}
