package com.quantum.steps;

import com.qmetry.qaf.automation.step.QAFTestStepProvider;
import com.qmetry.qaf.automation.util.DatabaseUtil;
import io.cucumber.java.en.When;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@QAFTestStepProvider
public class DatabaseSteps {

	@When("I get data from database with query \"(.*?)\"")
	public void selectFromDatabase(String query) {
		List<Map<String,Object>> rows = DatabaseUtil.getRecordAsMap(query);
		for(Map<String,Object> row : rows) {
			row.forEach((k,v)-> System.out.println(k + ":" + v));
		}
	}

	@When("I update the database with query \"(.*?)\"")
	public void updateDatabase(String query) {
		Connection con = null;
		Statement stmt = null;
		try {
			con = DatabaseUtil.getConnection();
			stmt = con.createStatement();
			stmt.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DatabaseUtil.close(stmt);
			DatabaseUtil.close(con);
		}
	}
}
