package example;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResult;
import com.perfecto.reportium.test.result.TestResultFactory;

public class PerfectoSelenium {
	RemoteWebDriver driver;
	ReportiumClient reportiumClient;
	// Replace <<cloud name>> with your perfecto cloud name (e.g. testingcloud ) or
	// pass it as maven properties: -DcloudName=<<cloud name>>
	String cloudName = "trial";

	// Replace <<security token>> with your perfecto security token or pass it as
	// maven properties: -DsecurityToken=<<SECURITY TOKEN>> More info:
	// https://developers.perfectomobile.com/display/PD/Generate+security+tokens
	String securityToken = "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI2ZDM2NmJiNS01NDAyLTQ4MmMtYTVhOC1kODZhODk4MDYyZjIifQ.eyJpYXQiOjE3MzQxODg2NjksImp0aSI6IjBmNmJiNDc1LTk3MjMtNGY5Yy1hNzZjLWI3MDNjOWUzOTdjYSIsImlzcyI6Imh0dHBzOi8vYXV0aDMucGVyZmVjdG9tb2JpbGUuY29tL2F1dGgvcmVhbG1zL3RyaWFsLXBlcmZlY3RvbW9iaWxlLWNvbSIsImF1ZCI6Imh0dHBzOi8vYXV0aDMucGVyZmVjdG9tb2JpbGUuY29tL2F1dGgvcmVhbG1zL3RyaWFsLXBlcmZlY3RvbW9iaWxlLWNvbSIsInN1YiI6ImZmNmMxY2M4LWU3YjctNDFmOC05OTI5LWQwYWI5MWQ0MmM5YiIsInR5cCI6Ik9mZmxpbmUiLCJhenAiOiJvZmZsaW5lLXRva2VuLWdlbmVyYXRvciIsIm5vbmNlIjoiMWFiYTEwZWMtNDE3NC00NzQwLWFlMDgtYTJiODQzN2VkYzFkIiwic2Vzc2lvbl9zdGF0ZSI6IjhlMDBmZTEzLTk5OWMtNGU4Yy05Y2E2LTRhYzQxODk5MzFmZSIsInNjb3BlIjoib3BlbmlkIG9mZmxpbmVfYWNjZXNzIHByb2ZpbGUgZW1haWwiLCJzaWQiOiI4ZTAwZmUxMy05OTljLTRlOGMtOWNhNi00YWM0MTg5OTMxZmUifQ.UXxe8YG8q5CnLjyjTQxiqtP2VH58qHZ6LF7HeAWc8lc";

	@Test
	public void webTest() throws Exception {
		// Web: Make sure to Auto generate capabilities for device selection:
		// https://developers.perfectomobile.com/display/PD/Select+a+device+for+manual+testing#Selectadeviceformanualtesting-genCapGeneratecapabilities
		DesiredCapabilities capabilities = new DesiredCapabilities("", "", Platform.ANY);
		capabilities.setCapability("platformName", "Windows");
		capabilities.setCapability("platformVersion", "11");
		capabilities.setCapability("browserName", "Chrome");
		capabilities.setCapability("browserVersion", "beta");
		capabilities.setCapability("location", "US East");
		capabilities.setCapability("resolution", "1920x1080");

		// The below capability is mandatory. Please do not replace it.
		capabilities.setCapability("securityToken", PerfectoLabUtils.fetchSecurityToken(securityToken));

		driver = new RemoteWebDriver(new URL("https://" + PerfectoLabUtils.fetchCloudName(cloudName)
				+ ".perfectomobile.com/nexperience/perfectomobile/wd/hub"), capabilities);
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);

		reportiumClient = PerfectoLabUtils.setReportiumClient(driver, reportiumClient); // Creates reportiumClient
		reportiumClient.testStart("Perfecto desktop web test", new TestContext("tag2", "tag3"));
		reportiumClient.stepStart("browser navigate to perfecto"); // Starts a reportium step
		driver.get("https://www.google.com");
		Thread.sleep(Duration.ofSeconds(5));
		reportiumClient.stepEnd();
		
		reportiumClient.stepStart("Navigating to google.com");
		((JavascriptExecutor) driver).executeScript("window.open()");
		ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
		driver.switchTo().window(tabs.get(1));
		
		// Navigate to a different URL in the new tab
		driver.get("https://www.google.com");

		// Switch back to the original tab
		driver.switchTo().window(tabs.get(0));
		reportiumClient.stepEnd();
		Thread.sleep(Duration.ofSeconds(5));
		reportiumClient.stepStart("Verify title");
		String aTitle = driver.getTitle();
		System.out.println("aTitle="+aTitle);
		PerfectoLabUtils.assertTitle(aTitle, reportiumClient);
		reportiumClient.stepEnd();
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		// STOP TEST
		TestResult testResult = null;

		if (result.getStatus() == result.SUCCESS) {
			testResult = TestResultFactory.createSuccess();
		} else if (result.getStatus() == result.FAILURE) {
			testResult = TestResultFactory.createFailure(result.getThrowable());
		}
		reportiumClient.testStop(testResult);
		try {
			driver.close();
		} catch (Exception e) {
		}
		driver.quit();
		// Retrieve the URL to the DigitalZoom Report
		String reportURL = reportiumClient.getReportUrl();
		System.out.println(reportURL);
	}
}
