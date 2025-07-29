package com.quantum.steps;

import com.qmetry.qaf.automation.step.QAFTestStepProvider;
import com.qmetry.qaf.automation.step.WsStep;
import com.qmetry.qaf.automation.util.CSVUtil;
import com.quantum.utils.ConsoleUtils;
import com.sun.jersey.api.client.ClientResponse;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.util.Map;

import static com.qmetry.qaf.automation.core.ConfigurationManager.getBundle;

@QAFTestStepProvider
public class BlazeWebServiceSteps {

	@When("I trigger a blaze runscope test with trigger token \"(.*?)\"")
	public void triggerRunscopeTest(String triggerToken) throws InterruptedException {
		getBundle().setProperty("blaze.runscope.triggerToken", triggerToken);
		WsStep.userRequests("blaze.runscope.trigger");
		WsStep.sayValueAtJsonPath("blaze.runscope.testId", "$.data.runs[0].test_id");
		WsStep.sayValueAtJsonPath("blaze.runscope.testRunId", "$.data.runs[0].test_run_id");
		WsStep.sayValueAtJsonPath("blaze.runscope.bucketId", "$.data.runs[0].bucket_key");
		do {
			WsStep.userRequests("blaze.runscope.testrun");
			WsStep.sayValueAtJsonPath("blaze.runscope.testrun.status", "$.data.result");
			Thread.sleep(1000);
		} while(!"working".equals(getBundle().getProperty("blaze.runscope.testrun.status")));
	}

	@When("I download a CSV from Blaze as \"(.*?)\"")
	public void downloadCSV(String filePath) throws IOException {
		ClientResponse response = WsStep.userRequests("blaze.tdm.generatefile");
		WsStep.downloadFileFromResponse(filePath, response);
		Map<String,String> data = (Map<String, String>) CSVUtil.getCSVDataAsMap(filePath).get(0)[0];
		data.forEach((k,v) -> ConsoleUtils.logWarningBlocks(k+":"+v));
	}
}
