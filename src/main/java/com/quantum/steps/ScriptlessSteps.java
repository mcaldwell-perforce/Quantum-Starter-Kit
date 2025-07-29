package com.quantum.steps;

import com.qmetry.qaf.automation.step.QAFTestStepProvider;
import com.qmetry.qaf.automation.step.WsStep;
import com.quantum.utils.ConsoleUtils;
import io.cucumber.java.en.When;

import static com.qmetry.qaf.automation.core.ConfigurationManager.getBundle;

@QAFTestStepProvider
public class ScriptlessSteps {

	@When("I execute scriptless test \"(.*?)\"")
	public void executeScriptlessTest(String repoLocator) throws InterruptedException {
		String startAPI = "perfecto.scriptless.start",
				statusAPI = "perfecto.scriptless.status";
		String testRepoLocator = "scriptless.test.repoLocator",
				executionId = "scriptless.test.executionId",
				testStatus = "scriptless.test.status",
				reportURL = "scriptless.test.reportURL";
		getBundle().setProperty(testRepoLocator, repoLocator);
		WsStep.userRequests(startAPI);
		WsStep.sayValueAtJsonPath(executionId, "$.executionId");
		WsStep.sayValueAtJsonPath(reportURL, "$.testGridReportUrl");
		//loop until the test is completed
		do {
			WsStep.userRequests(statusAPI);
			WsStep.sayValueAtJsonPath(testStatus, "$.completed");
			Thread.sleep(10000);
		} while(!"true".equals(getBundle().getProperty(testStatus)));
		ConsoleUtils.logWarningBlocks("PERFECTO REPORT URL: " + getBundle().getProperty(reportURL).toString().replace("[", "%5B").replace("]", "%5D"));
	}
}
