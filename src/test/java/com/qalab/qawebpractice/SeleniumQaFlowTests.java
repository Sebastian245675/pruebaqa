package com.qalab.qawebpractice;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SeleniumQaFlowTests {

	private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
	private static final boolean HEADLESS = Boolean.parseBoolean(System.getProperty("selenium.headless", "false"));
	private static final long SLOW_MS = Long.parseLong(System.getProperty("selenium.slowMs", "500"));

	@LocalServerPort
	private int port;

	private WebDriver driver;
	private WebDriverWait wait;

	@Test
	void qaCanRegisterLoginAndSeeDashboard() {
		startBrowser();

		String email = "qa." + UUID.randomUUID().toString().substring(0, 8) + "@testlab.local";
		String password = "Password123!";
		String fullName = "QA Student";

		driver.get(baseUrl() + "/register");
		type(By.id("fullName"), fullName);
		type(By.id("email"), email);
		type(By.id("password"), password);
		type(By.id("confirmPassword"), password);
		click(By.id("register-submit"));

		waitForVisible(By.id("registered-alert"));
		assertTrue(driver.getCurrentUrl().contains("/login?registered"));

		type(By.cssSelector("[data-testid='login-email']"), email);
		type(By.cssSelector("[data-testid='login-password']"), password);
		click(By.id("login-submit"));

		WebElement welcome = waitForVisible(By.id("dashboard-welcome"));
		assertTrue(driver.getCurrentUrl().contains("/dashboard"));
		assertTrue(welcome.getText().contains(fullName));
		assertEquals(email, waitForVisible(By.cssSelector("[data-testid='dashboard-email'] p")).getText());
	}

	@Test
	void qaCanRegisterLoginUpdateProfileAndAddExperience() {
		startBrowser();

		String email = "qa." + UUID.randomUUID().toString().substring(0, 8) + "@testlab.local";
		String password = "Password123!";
		String fullName = "QA Dashboard Student";

		driver.get(baseUrl() + "/register");
		type(By.id("fullName"), fullName);
		type(By.id("email"), email);
		type(By.id("password"), password);
		type(By.id("confirmPassword"), password);
		click(By.id("register-submit"));

		waitForVisible(By.id("registered-alert"));
		type(By.cssSelector("[data-testid='login-email']"), email);
		type(By.cssSelector("[data-testid='login-password']"), password);
		click(By.id("login-submit"));

		waitForVisible(By.id("dashboard-welcome"));
		type(By.cssSelector("[data-testid='profile-full-name']"), "QA Dashboard Updated");
		type(By.cssSelector("[data-testid='profile-title']"), "Automation QA Trainee");
		type(By.cssSelector("[data-testid='profile-phone']"), "+57 320 111 2222");
		type(By.cssSelector("[data-testid='profile-location']"), "Bogota, Colombia");
		type(By.cssSelector("[data-testid='profile-bio']"), "Perfil actualizado desde Selenium en navegador real.");
		click(By.cssSelector("[data-testid='profile-submit']"));

		WebElement profileAlert = waitForVisible(By.id("profile-updated-alert"));
		assertTrue(profileAlert.getText().contains("actualizada correctamente"));
		assertEquals("QA Dashboard Updated", waitForVisible(By.cssSelector("[data-testid='profile-full-name']")).getAttribute("value"));

		type(By.cssSelector("[data-testid='experience-job-title']"), "Junior QA Analyst");
		type(By.cssSelector("[data-testid='experience-company']"), "Practice Labs");
		type(By.cssSelector("[data-testid='experience-employment-type']"), "Tiempo completo");
		type(By.cssSelector("[data-testid='experience-location']"), "Remoto");
		type(By.cssSelector("[data-testid='experience-start-date']"), "01012026");
		type(By.cssSelector("[data-testid='experience-end-date']"), "30062026");
		type(By.cssSelector("[data-testid='experience-description']"), "Alta de experiencia desde Selenium para practicar QA.");
		click(By.cssSelector("[data-testid='experience-submit']"));

		WebElement experienceAlert = waitForVisible(By.id("experience-added-alert"));
		assertTrue(experienceAlert.getText().contains("agregada correctamente"));
		assertTrue(waitForVisible(By.cssSelector("[data-testid='dashboard-experiences-count']")).getText().contains("registros"));
		assertTrue(driver.getPageSource().contains("Practice Labs"));
	}

	@Test
	void qaSeesErrorMessageWhenLoginFails() {
		startBrowser();

		driver.get(baseUrl() + "/login");
		type(By.cssSelector("[data-testid='login-email']"), "qa.user@testlab.local");
		type(By.cssSelector("[data-testid='login-password']"), "Incorrecta123!");
		click(By.id("login-submit"));

		WebElement errorAlert = waitForVisible(By.id("login-error-alert"));
		assertTrue(driver.getCurrentUrl().contains("/login?error"));
		assertTrue(errorAlert.getText().contains("incorrectos"));
	}

	@Test
	void qaCanDeleteExperience() {
		startBrowser();

		String email = "qa." + UUID.randomUUID().toString().substring(0, 8) + "@testlab.local";
		String password = "Password123!";
		String fullName = "QA Delete Test";

		// 1. Registro y Login
		registerAndLogin(email, password, fullName);

		// 2. Agregar una experiencia
		type(By.id("jobTitle"), "Eliminame");
		type(By.id("company"), "Delete Co");
		type(By.id("employmentType"), "Temporal");
		type(By.id("experienceLocation"), "Remoto");
		type(By.id("startDate"), "01012026");
		type(By.id("endDate"), "01022026");
		click(By.id("experience-submit"));

		waitForVisible(By.id("experience-added-alert"));
		assertTrue(driver.getPageSource().contains("Eliminame"));

		// 3. Eliminar la experiencia
		// Buscamos el botn de eliminar que contenga el texto "Eliminar" dentro de la card
		click(By.xpath("//article[contains(., 'Eliminame')]//button[contains(text(), 'Eliminar')]"));

		waitForVisible(By.id("experience-deleted-alert"));
		assertTrue(!driver.getPageSource().contains("Eliminame"));
		assertTrue(waitForVisible(By.cssSelector("[data-testid='dashboard-experiences-count']")).getText().contains("0 registros"));
	}

	@Test
	void qaDataPersistsAfterLogoutAndLogin() {
		startBrowser();

		String email = "qa." + UUID.randomUUID().toString().substring(0, 8) + "@testlab.local";
		String password = "Password123!";
		String fullName = "QA Persistence Test";
		String newTitle = "Senior Persistence Architect";

		// 1. Registro y Login
		registerAndLogin(email, password, fullName);

		// 2. Cambiar cargo
		type(By.id("professionalTitle"), newTitle);
		click(By.id("profile-submit"));
		waitForVisible(By.id("profile-updated-alert"));

		// 3. Logout
		click(By.id("logout-button"));
		wait.until(ExpectedConditions.urlContains("/login"));
		assertTrue(driver.getCurrentUrl().contains("/login"));

		// 4. Login de nuevo
		type(By.cssSelector("[data-testid='login-email']"), email);
		type(By.cssSelector("[data-testid='login-password']"), password);
		click(By.id("login-submit"));

		// 5. Verificar que el cargo sigue ah
		waitForVisible(By.id("dashboard-welcome"));
		assertEquals(newTitle, driver.findElement(By.id("professionalTitle")).getAttribute("value"));
	}

	@Test
	void qaSeesErrorMessageWhenExperienceEndDateIsBeforeStartDate() {
		startBrowser();

		String email = "qa." + UUID.randomUUID().toString().substring(0, 8) + "@testlab.local";
		String password = "Password123!";
		String fullName = "QA Date Error Test";

		// 1. Registro y Login
		registerAndLogin(email, password, fullName);

		// 2. Intentar agregar experiencia con fechas invalidas
		type(By.id("jobTitle"), "Fecha Invalida");
		type(By.id("company"), "Error Corp");
		type(By.id("employmentType"), "Full-time");
		type(By.id("experienceLocation"), "Office");
		
		// Fecha inicio: 2026-05-10, Fecha fin: 2026-04-10 (Error)
		type(By.id("startDate"), "10052026");
		type(By.id("endDate"), "10042026");
		
		click(By.id("experience-submit"));

		// 3. Verificar mensaje de error
		WebElement errorMsg = waitForVisible(By.xpath("//p[contains(text(), 'La fecha final no puede ser anterior')]"));
		assertTrue(errorMsg.isDisplayed());
	}

	private void registerAndLogin(String email, String password, String fullName) {
		driver.get(baseUrl() + "/register");
		type(By.id("fullName"), fullName);
		type(By.id("email"), email);
		type(By.id("password"), password);
		type(By.id("confirmPassword"), password);
		click(By.id("register-submit"));

		waitForVisible(By.id("registered-alert"));
		type(By.cssSelector("[data-testid='login-email']"), email);
		type(By.cssSelector("[data-testid='login-password']"), password);
		click(By.id("login-submit"));
		waitForVisible(By.id("dashboard-welcome"));
	}

	@AfterEach
	void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}

	private void startBrowser() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--window-size=1440,960");
		options.addArguments("--disable-gpu");
		options.addArguments("--no-sandbox");
		if (HEADLESS) {
			options.addArguments("--headless=new");
		}

		driver = new ChromeDriver(options);
		wait = new WebDriverWait(driver, WAIT_TIMEOUT);
		if (!HEADLESS) {
			driver.manage().window().maximize();
		}
	}

	private String baseUrl() {
		return "http://localhost:" + port;
	}

	private void type(By locator, String value) {
		WebElement element = waitForVisible(locator);
		element.clear();
		element.sendKeys(value);
		pauseIfNeeded();
	}

	private void click(By locator) {
		wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
		pauseIfNeeded();
	}

	private WebElement waitForVisible(By locator) {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	private void pauseIfNeeded() {
		if (SLOW_MS <= 0) {
			return;
		}
		try {
			Thread.sleep(SLOW_MS);
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("La pausa del test fue interrumpida.", exception);
		}
	}
}
