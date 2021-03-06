package com.here.pageobjects;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

public class DocumentionPage {
	
	WebDriverWait wait ;
	HttpURLConnection httpuc = null;
    
    int respCode = 200;
	protected WebDriver driver;
	String pageTitle;
	SoftAssert softAssert;
	public void initialisation() {
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+File.separator+"Drivers" + File.separator
				+ "chromedriver.exe");
		driver=new ChromeDriver();
		wait=new WebDriverWait(driver, 60);
		driver.manage().window().maximize();
	}
	
	public String lanchTheURL(String url) {
		driver.get(url);
		pageTitle=driver.getTitle();
		System.out.println(pageTitle);
		return pageTitle;
	}
	
	
	public void validateAllTheLinksResposeCodeAndAngularJSInitializedOrNot() {
		
		List<WebElement> links= driver.findElements(By.xpath("//div[contains(@class,'documentation-link-group')]//*[@href and @target='_self']"));
		
		for(WebElement link :links) {
			String url=link.getAttribute("href");
			try {
				httpuc =(HttpURLConnection)(new URL(url).openConnection());
				httpuc.setRequestMethod("HEAD");
				httpuc.connect();
				respCode=httpuc.getResponseCode();
				if(respCode>=400) {
					System.out.println("URL : "+url);
					System.out.println("Response Code : "+respCode+"\n");
				}else {
					JavascriptExecutor js = (JavascriptExecutor)driver;
					System.out.println("URL : "+url);
					System.out.println("Response Code : "+respCode);
				    link.sendKeys(Keys.DOWN , Keys.CONTROL , Keys.ENTER);
				    ArrayList<String> tabs2 = new ArrayList<String> (driver.getWindowHandles());
				    driver.switchTo().window(tabs2.get(1));
				    Thread.sleep(20);
					Boolean falg =Boolean.valueOf((Boolean) js.executeScript("return (window.angular !== undefined) && (angular.element(document).injector() !== undefined) && (angular.element(document).injector().get('$http').pendingRequests.length === 0)"));
					if(falg==true) {
						//System.out.println("Angular JS loaded on the web page : "+url+"\n");
					}else {
						try{
							softAssert = new SoftAssert();
							softAssert.assertTrue(falg,"Angular JS is not loaded on webPage :"+url+"\n");
							softAssert.assertAll();
					        }catch(AssertionError e)
					        {
					            System.out.println("Assertion error. "+e.getMessage());
					        }
						
					}
					driver.close();
				    driver.switchTo().window(tabs2.get(0));
					
					}
				System.out.println("\n");
			}catch(Exception e) {
				
			}
		}
		
	}

}
