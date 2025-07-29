@WebServices
Feature: Demo for Web Service example

	@scriptlessrun
	Scenario: Run Scriptless Test
		When I execute scriptless test "${perfecto.scripless.testpath}"

	@triggerblaze
	Scenario: Trigger Blaze Test
		When I trigger a blaze runscope test with trigger token "${blaze.runscope.triggertoken}"

	@generatefile
	Scenario: Download CSV
		When I download a CSV from Blaze as "./src/main/resources/data/pet_store.csv"

	@selectdatabase
	Scenario: Select Data from Database
		When I get data from database with query "SELECT * FROM users;"

	@updatedatabase
	Scenario: Update Database with Query
		When I get data from database with query "SELECT * FROM users;"
		When I update the database with query "UPDATE users SET name = 'Jim Doe' WHERE name = 'John Doe'"
		When I get data from database with query "SELECT * FROM users;"
		When I update the database with query "UPDATE users SET name = 'John Doe' WHERE name = 'Jim Doe'"
		When I get data from database with query "SELECT * FROM users;"