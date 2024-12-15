package com.perfecto.sampleproject;
import java.net.URL;

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

public class RemoteWebDriverTest {

	@Test
	public void runRemote() throws Exception {
		String gridUrl = "http://192.168.0.102:4444";
		DesiredCapabilities capabilities = new DesiredCapabilities();

		capabilities.setCapability(CapabilityType.BROWSER_NAME, "firefox");
		capabilities.setCapability(CapabilityType.PLATFORM_NAME, Platform.WINDOWS);

		RemoteWebDriver driver = new RemoteWebDriver(new URL(gridUrl), capabilities);

		driver.get("https://www.google.com");

		driver.quit();
	}

}
