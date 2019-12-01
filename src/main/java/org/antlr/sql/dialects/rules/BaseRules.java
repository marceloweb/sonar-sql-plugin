package org.antlr.sql.dialects.rules;

import org.antlr.sql.dialects.tsql.TSqlParser.Declare_statementContext;
import org.antlr.sql.dialects.tsql.TSqlParser.Primitive_expressionContext;
import org.antlr.sql.dialects.tsql.TSqlParser.Set_statementContext;
import org.sonar.plugins.sql.models.rules.Rule;
import org.sonar.plugins.sql.models.rules.RuleImplementation;
import org.sonar.plugins.sql.models.rules.RuleMatchType;
import org.sonar.plugins.sql.models.rules.RuleMode;
import org.sonar.plugins.sql.models.rules.RuleResultType;
import org.sonar.plugins.sql.models.rules.TextCheckType;

public enum BaseRules implements IBaseRules {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.antlr.sql.dialects.rules.IBaseRules#getTablesPartitionRule()
	 */
	@Override
	public Rule getTablesPartitionRule() {
		Rule rule = new Rule();
		rule.setKey("C001");
		rule.setInternalKey("C001");
		rule.setName("THE TABLES PARTITION PATTERN was used");
		rule.setDescription("THE TABLES PARTITION PATTERN was used");
		rule.setTag("performance");
		rule.setSeverity("MINOR");
		rule.setRemediationFunction("LINEAR");
		rule.setDebtRemediationFunctionCoefficient("2min");
		rule.getRuleImplementation().setRuleViolationMessage("THE TABLES PARTITION PATTERN was used.");

		return rule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.antlr.sql.dialects.rules.IBaseRules#getTablesNameRule()
	 */
	@Override
	public Rule getTablesNameRule() {
		Rule rule = new Rule();
		rule.setKey("C002");
		rule.setInternalKey("C002");
		rule.setName("THE TABLES NAME PATTERN was used");
		rule.setDescription("THE TABLES NAME PATTERN was used");
		rule.setTag("best-practise");
		rule.setSeverity("MINOR");
		rule.setRemediationFunction("LINEAR");
		rule.setDebtRemediationFunctionCoefficient("2min");
		RuleImplementation impl = new RuleImplementation();
		impl.getViolatingRulesCodeExamples().getRuleCodeExample().add("CREATE TABLE Test_Br;");
		impl.getCompliantRulesCodeExamples().getRuleCodeExample().add("CREATE TABLE test;");
		impl.setRuleViolationMessage("CREATE TABLE Test_Br was found.");
		rule.setRuleImplementation(impl);

		return rule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.antlr.sql.dialects.rules.IBaseRules#getOrderByRule()
	 */
	@Override
	public Rule getDatabaseNameRule() {
		Rule rule = new Rule();
		rule.setKey("C003");
		rule.setInternalKey("C003");
		rule.setName("THE DATABASE NAME PATTERN was used");
		rule.setDescription("THE DATABASE NAME PATTERN was used");
		rule.setTag("best-practise");
		rule.setSeverity("MINOR");
		rule.setRemediationFunction("LINEAR");
		rule.setDebtRemediationFunctionCoefficient("2min");
		RuleImplementation impl = new RuleImplementation();
		impl.setRuleViolationMessage("CREATE DATABASE Example_1 was found.");
		impl.getViolatingRulesCodeExamples().getRuleCodeExample().add("CREATE DATABASE Example_1;");
		impl.getCompliantRulesCodeExamples().getRuleCodeExample().add("CREATE DATABASE example;");
		rule.setRuleImplementation(impl);

		return rule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.antlr.sql.dialects.rules.IBaseRules#getExecRule()
	 */
	@Override
	public Rule getExecRule() {
		Rule rule = new Rule();
		rule.setKey("C005");
		rule.setInternalKey("C005");
		rule.setName("THE HIVE EXECUTABLE FILE PATTERN was used");
		rule.setDescription(".");
		rule.setDescription("THE HIVE EXECUTABLE FILE PATTERN was used");
		rule.setTag("best-practise");
		rule.setSeverity("MINOR");
		rule.setRemediationFunction("LINEAR");
		rule.setDebtRemediationFunctionCoefficient("2min");
		RuleImplementation impl = new RuleImplementation();
		impl.getViolatingRulesCodeExamples().getRuleCodeExample().add("EXEC ('SELECT 1');");
		impl.getViolatingRulesCodeExamples().getRuleCodeExample().add("EXEC (@sQueryText);");
		impl.setRuleViolationMessage(
				"EXECUTE/EXEC for dynamic query is used. It is better to use sp_executesql for dynamic queries.");
		impl.getCompliantRulesCodeExamples().getRuleCodeExample().add("EXECUTE sp_executesql N'select 1';");
		impl.getCompliantRulesCodeExamples().getRuleCodeExample().add("exec sys.sp_test  @test = 'Publisher';");
		rule.setRuleImplementation(impl);

		return rule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.antlr.sql.dialects.rules.IBaseRules#getSchemaRule()
	 */
	@Override
	public Rule getSchemaRule() {
		Rule rule = new Rule();
		rule.setKey("C006");
		rule.setInternalKey("C006");
		rule.setName("THE FOLDER NAMES PATTERN was used");
		rule.setDescription("THE FOLDER NAMES PATTERN was used");
		RuleImplementation impl = new RuleImplementation();
		impl.setRuleViolationMessage("Always use schema-qualified object names");
		impl.getViolatingRulesCodeExamples().getRuleCodeExample().add("SELECT * from test order by 1;");
		rule.setRuleImplementation(impl);
		
		return rule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.antlr.sql.dialects.rules.IBaseRules#getNoLockRule()
	 */
	@Override
	public Rule getNoLockRule() {
		Rule rule = new Rule();
		rule.setKey("C007");
		rule.setInternalKey("C007");
		rule.setName("THE TABLE COLUMNS PATTERN was used");
		rule.setDescription("THE TABLE COLUMNS PATTERN was used");
		rule.setTag("reliability");
		rule.setSeverity("MINOR");
		rule.setRemediationFunction("LINEAR");
		rule.setDebtRemediationFunctionCoefficient("2min");
		RuleImplementation impl = new RuleImplementation();
		impl.setRuleViolationMessage("NOLOCK hint is used.");
		impl.getViolatingRulesCodeExamples().getRuleCodeExample()
				.add("SELECT name, surname from dbo.test WITH (NOLOCK);");
		impl.getCompliantRulesCodeExamples().getRuleCodeExample().add("SELECT name, surname from dbo.test;");
		rule.setSource("http://sqlmag.com/t-sql/t-sql-best-practices-part-1");
		rule.setRuleImplementation(impl);

		return rule;
	}

}
