package org.antlr.sql.dialects.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.sql.dialects.Dialects;
import org.antlr.sql.dialects.mysql.MySqlParser.InsertStatementContext;
import org.antlr.sql.dialects.mysql.MySqlParser.UidListContext;
import org.antlr.sql.dialects.psql.PostgreSQLParser.Combine_clauseContext;
import org.antlr.sql.dialects.psql.PostgreSQLParser.ExprContext;
import org.antlr.sql.dialects.psql.PostgreSQLParser.Func_callContext;
import org.antlr.sql.dialects.psql.PostgreSQLParser.IdentifierContext;
import org.antlr.sql.dialects.psql.PostgreSQLParser.Order_by_clauseContext;
import org.antlr.sql.dialects.psql.PostgreSQLParser.Order_by_itemContext;
import org.antlr.sql.dialects.psql.PostgreSQLParser.PredicateContext;
import org.antlr.sql.dialects.psql.PostgreSQLParser.Select_stmtContext;
import org.antlr.sql.dialects.psql.PostgreSQLParser.Where_clauseContext;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.sonar.plugins.sql.models.rules.Rule;
import org.sonar.plugins.sql.models.rules.RuleDistanceIndexMatchType;
import org.sonar.plugins.sql.models.rules.RuleImplementation;
import org.sonar.plugins.sql.models.rules.RuleMatchType;
import org.sonar.plugins.sql.models.rules.RuleResultType;
import org.sonar.plugins.sql.models.rules.SqlRules;
import org.sonar.plugins.sql.models.rules.TextCheckType;

public enum PSSQLRules {

	INSTANCE;
	BaseRules baseRules = BaseRules.INSTANCE;

	public List<SqlRules> getRules() {
		List<SqlRules> rules = new ArrayList<>();
		{
			SqlRules customRules = new SqlRules();
			customRules.setRepoKey("SQLCC");
			customRules.setRepoName("SQL Plugin checks");
			customRules.setDialect(Dialects.PSSQL.name());
			customRules.getRule()
					.addAll(Arrays.asList(getTablesPartitionRule(),
							getTablesNameRule(),  /* getInsertRule(), */ getDatabaseNameRule()));
			rules.add(customRules);
		}
		return rules;
	}

	protected Rule getTablesPartitionRule() {
		Rule rule = baseRules.getTablesPartitionRule();
		RuleImplementation impl = rule.getRuleImplementation();
		impl.getNames().getTextItem().add(Func_callContext.class.getSimpleName());
		impl.getTextToFind().getTextItem().add("PG_SLEEP");
		impl.setTextCheckType(TextCheckType.CONTAINS);
		impl.setRuleMatchType(RuleMatchType.TEXT_AND_CLASS);
		impl.setRuleResultType(RuleResultType.FAIL_IF_FOUND);
		impl.setRuleViolationMessage("PG_SLEEP is used.");
		impl.getViolatingRulesCodeExamples().getRuleCodeExample().add("SELECT PG_SLEEP(5);");

		return rule;
	}

	protected Rule getTablesNameRule() {
		Rule rule = baseRules.getTablesNameRule();
		RuleImplementation impl = rule.getRuleImplementation();

		impl.getNames().getTextItem().add(ExprContext.class.getSimpleName());
		impl.setRuleMatchType(RuleMatchType.CLASS_ONLY);

		{
			RuleImplementation child = new RuleImplementation();
			child.getNames().getTextItem().add(TerminalNodeImpl.class.getSimpleName());
			child.getTextToFind().getTextItem().add("*");
			child.setRuleMatchType(RuleMatchType.TEXT_AND_CLASS);
			child.setRuleResultType(RuleResultType.FAIL_IF_FOUND);
			child.setDistance(1);
			child.setDistanceCheckType(RuleDistanceIndexMatchType.EQUALS);
			impl.getChildrenRules().getRuleImplementation().add(child);
		}

		return rule;
	}

	protected Rule getDatabaseNameRule() {
		Rule rule = baseRules.getDatabaseNameRule();
		RuleImplementation impl = rule.getRuleImplementation();

		RuleImplementation child2 = new RuleImplementation();
		child2.getNames().getTextItem().add(Order_by_itemContext.class.getSimpleName());
		child2.setTextCheckType(TextCheckType.REGEXP);
		child2.setRuleResultType(RuleResultType.FAIL_IF_FOUND);
		child2.setRuleMatchType(RuleMatchType.TEXT_AND_CLASS);
		child2.getTextToFind().getTextItem().add("[0-9]+");

		impl.getChildrenRules().getRuleImplementation().add(child2);
		impl.getNames().getTextItem().add(Order_by_clauseContext.class.getSimpleName());

		impl.setRuleMatchType(RuleMatchType.CLASS_ONLY);
		impl.setRuleResultType(RuleResultType.DEFAULT);
		impl.setRuleViolationMessage("Positional reference is used instead of column name in an ORDER BY clause.");

		rule.setRuleImplementation(impl);
		return rule;
	}


}
