package com.quantum.steps;

import com.google.common.collect.ImmutableMap;
import com.qmetry.qaf.automation.step.QAFTestStepProvider;
import com.qmetry.qaf.automation.ui.WebDriverTestBase;
import com.qmetry.qaf.automation.util.Validator;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.hamcrest.Matchers;
import org.openqa.selenium.remote.RemoteWebDriver;

@QAFTestStepProvider
public class PerfectoAISteps {

	@When("^AI (.*?)$")
	public void aiAction(String prompt) {
		RemoteWebDriver driver = new WebDriverTestBase().getDriver();
		String result = driver.executeScript("perfecto:ai:user-action", ImmutableMap.of("action", prompt)).toString();
		Validator.assertThat("User Action failed: " + prompt, result, Matchers.equalTo("true"));
	}

	@Then("^AI Validates (.*?)$")
	public void aiVerify(String prompt) {
		RemoteWebDriver driver = new WebDriverTestBase().getDriver();
		String result = driver.executeScript("perfecto:ai:validation", ImmutableMap.of("validation", prompt)).toString();
		Validator.assertThat("Validation failed: " + prompt, result, Matchers.equalTo("true"));
	}
}
