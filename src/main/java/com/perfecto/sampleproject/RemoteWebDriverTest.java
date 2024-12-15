package com.perfecto.sampleproject;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.Platform;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

public class RemoteWebDriverTest {

	@Test
	public void runRemote() throws Exception {
		String gridUrl = "http://192.168.0.102:4444";
		DesiredCapabilities capabilities = new DesiredCapabilities();
	
		capabilities.setCapability(CapabilityType.BROWSER_NAME, "fi-refox");
		capabilities.setCapability(CapabilityType.PLATFORM_NAME, Platform.WINDOWS);

		URL c = new URL(gridUrl);

		RemoteWebDriver driver = new RemoteWebDriver(c, capabilities);

		List<String> urls = generateHardcodedUrls();
		for (int i = 0; i < urls.size(); i++) {
			String url = urls.get(i);
			try {
				driver.get(url);
				Thread.sleep(Duration.ofSeconds(5));
				System.out.println("URL: " + url);
			} catch (Exception e) {
				System.out.println("Failed to take screenshot for: " + url + " - " + e.getMessage());
			}
		}

		driver.quit();
	}

	private static List<String> generateHardcodedUrls() {
		List<String> urls = new ArrayList<>();
		urls.add("https://www.hulu.com");
		urls.add("https://www.crunchyroll.com");
		urls.add("https://www.vudu.com");
		urls.add("https://www.shudder.com");
		return urls;
	}

}
