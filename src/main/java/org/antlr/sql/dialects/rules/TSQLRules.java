package org.antlr.sql.dialects.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.sql.dialects.Dialects;
import org.antlr.sql.dialects.psql.PostgreSQLParser.Func_nameContext;
import org.antlr.sql.dialects.tsql.TSqlParser.AsteriskContext;
import org.antlr.sql.dialects.tsql.TSqlParser.Column_name_listContext;
import org.antlr.sql.dialects.tsql.TSqlParser.Comparison_operatorContext;
import org.antlr.sql.dialects.tsql.TSqlParser.ConstantContext;
import org.antlr.sql.dialects.tsql.TSqlParser.Create_indexContext;
import org.antlr.sql.dialects.tsql.TSqlParser.Execute_statementContext;
import org.antlr.sql.dialects.tsql.TSqlParser.Full_column_nameContext;
import org.antlr.sql.dialects.tsql.TSqlParser.Function_callContext;
import org.antlr.sql.dialects.tsql.TSqlParser.IdContext;
import org.antlr.sql.dialects.tsql.TSqlParser.Insert_statementContext;
import org.antlr.sql.dialects.tsql.TSqlParser.Order_by_clauseContext;
import org.antlr.sql.dialects.tsql.TSqlParser.Order_by_expressionContext;
import org.antlr.sql.dialects.tsql.TSqlParser.PredicateContext;
import org.antlr.sql.dialects.tsql.TSqlParser.Primitive_expressionContext;
import org.antlr.sql.dialects.tsql.TSqlParser.SCALAR_FUNCTIONContext;
import org.antlr.sql.dialects.tsql.TSqlParser.Search_conditionContext;
import org.antlr.sql.dialects.tsql.TSqlParser.Search_condition_notContext;
import org.antlr.sql.dialects.tsql.TSqlParser.Select_list_elemContext;
import org.antlr.sql.dialects.tsql.TSqlParser.Sql_unionContext;
import org.antlr.sql.dialects.tsql.TSqlParser.SubqueryContext;
import org.antlr.sql.dialects.tsql.TSqlParser.Table_constraintContext;
import org.antlr.sql.dialects.tsql.TSqlParser.Table_hintContext;
import org.antlr.sql.dialects.tsql.TSqlParser.Waitfor_statementContext;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.sonar.plugins.sql.models.rules.Rule;
import org.sonar.plugins.sql.models.rules.RuleDistanceIndexMatchType;
import org.sonar.plugins.sql.models.rules.RuleImplementation;
import org.sonar.plugins.sql.models.rules.RuleMatchType;
import org.sonar.plugins.sql.models.rules.RuleResultType;
import org.sonar.plugins.sql.models.rules.SqlRules;
import org.sonar.plugins.sql.models.rules.TextCheckType;

public enum TSQLRules {

	INSTANCE;
	BaseRules baseRules = BaseRules.INSTANCE;

	public List<SqlRules> getRules() {
		List<SqlRules> rules = new ArrayList<>();
		{
			SqlRules customRules = new SqlRules();
			customRules.setRepoKey("SQLCC");
			customRules.setRepoName("SQL Plugin checks");
			customRules.setDialect(Dialects.TSQL.name());
			customRules.getRule()
					.addAll(Arrays.asList(getTablesPartitionRule(), getTablesNameRule(), getDatabaseNameRule(),
							getExecRule(), getNoLockRule()));
			rules.add(customRules);
		}
		return rules;
	}

	protected Rule getTablesPartitionRule() {
		Rule rule = baseRules.getTablesPartitionRule();
		RuleImplementation impl = rule.getRuleImplementation();
		impl.getNames().getTextItem().add(Waitfor_statementContext.class.getSimpleName());

		impl.setRuleMatchType(RuleMatchType.CLASS_ONLY);
		impl.setRuleResultType(RuleResultType.FAIL_IF_FOUND);
		impl.setRuleViolationMessage("WAITFOR is used.");
		impl.getViolatingRulesCodeExamples().getRuleCodeExample().add("WAITFOR '10:00:00';");

		return rule;
	}

	protected Rule getTablesNameRule() {
		Rule rule = baseRules.getTablesNameRule();
		RuleImplementation impl = rule.getRuleImplementation();

		impl.getNames().getTextItem().add(Select_list_elemContext.class.getSimpleName());

		impl.setRuleMatchType(RuleMatchType.CLASS_ONLY);

		RuleImplementation child = new RuleImplementation();

		child.getNames().getTextItem().add(AsteriskContext.class.getSimpleName());
		child.getTextToFind().getTextItem().add("*");

		child.setTextCheckType(TextCheckType.CONTAINS);
		child.setRuleResultType(RuleResultType.SKIP_IF_NOT_FOUND);
		child.setRuleMatchType(RuleMatchType.TEXT_AND_CLASS);

		RuleImplementation child2 = new RuleImplementation();

		child2.getNames().getTextItem().add(TerminalNodeImpl.class.getSimpleName());
		child2.getTextToFind().getTextItem().add("*");

		child2.setTextCheckType(TextCheckType.STRICT);
		child2.setRuleResultType(RuleResultType.FAIL_IF_FOUND);
		child2.setRuleMatchType(RuleMatchType.TEXT_AND_CLASS);
		child.getChildrenRules().getRuleImplementation().add(child2);

		impl.getChildrenRules().getRuleImplementation().add(child);

		// AsteriskContext
		return rule;
	}

	protected Rule getDatabaseNameRule() {
		Rule rule = baseRules.getDatabaseNameRule();
		RuleImplementation impl = rule.getRuleImplementation();

		RuleImplementation child2 = new RuleImplementation();
		child2.getNames().getTextItem().add(ConstantContext.class.getSimpleName());
		child2.setTextCheckType(TextCheckType.DEFAULT);
		child2.setRuleResultType(RuleResultType.FAIL_IF_FOUND);
		child2.setRuleMatchType(RuleMatchType.CLASS_ONLY);

		impl.getChildrenRules().getRuleImplementation().add(child2);
		impl.getNames().getTextItem().add(Order_by_clauseContext.class.getSimpleName());

		impl.setRuleMatchType(RuleMatchType.CLASS_ONLY);
		impl.setRuleResultType(RuleResultType.DEFAULT);
		impl.setRuleViolationMessage("Positional reference is used instead of column name in an ORDER BY clause.");

		rule.setRuleImplementation(impl);
		return rule;
	}

	protected Rule getExecRule() {
		Rule rule = baseRules.getExecRule();
		RuleImplementation impl = rule.getRuleImplementation();

		RuleImplementation child2 = new RuleImplementation();
		child2.getNames().getTextItem().add(Primitive_expressionContext.class.getSimpleName());
		child2.setTextCheckType(TextCheckType.DEFAULT);
		child2.setRuleResultType(RuleResultType.FAIL_IF_FOUND);
		child2.setRuleMatchType(RuleMatchType.CLASS_ONLY);

		RuleImplementation skipSubRule = new RuleImplementation();
		skipSubRule.getNames().getTextItem().add(Func_nameContext.class.getSimpleName());
		skipSubRule.setTextCheckType(TextCheckType.DEFAULT);
		skipSubRule.setRuleResultType(RuleResultType.SKIP_IF_FOUND);
		skipSubRule.setRuleMatchType(RuleMatchType.CLASS_ONLY);

		impl.getChildrenRules().getRuleImplementation().add(child2);
		impl.getChildrenRules().getRuleImplementation().add(skipSubRule);
		impl.getNames().getTextItem().add(Execute_statementContext.class.getSimpleName());
		impl.setRuleMatchType(RuleMatchType.CLASS_ONLY);
		impl.setRuleResultType(RuleResultType.DEFAULT);

		return rule;
	}

	protected Rule getNoLockRule() {
		Rule rule = baseRules.getNoLockRule();
		RuleImplementation impl = rule.getRuleImplementation();

		impl.getTextToFind().getTextItem().add("NOLOCK");
		impl.setTextCheckType(TextCheckType.CONTAINS);
		impl.getNames().getTextItem().add(Table_hintContext.class.getSimpleName());
		impl.setRuleMatchType(RuleMatchType.TEXT_AND_CLASS);
		impl.setRuleResultType(RuleResultType.FAIL_IF_FOUND);

		return rule;
	}

}
