package org.antlr.sql.dialects.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.sql.dialects.Dialects;
import org.antlr.sql.dialects.mysql.MySqlParser.ComparisonOperatorContext;
import org.antlr.sql.dialects.mysql.MySqlParser.ConstantContext;
import org.antlr.sql.dialects.mysql.MySqlParser.FromClauseContext;
import org.antlr.sql.dialects.mysql.MySqlParser.InsertStatementContext;
import org.antlr.sql.dialects.mysql.MySqlParser.LikePredicateContext;
import org.antlr.sql.dialects.mysql.MySqlParser.LogicalOperatorContext;
import org.antlr.sql.dialects.mysql.MySqlParser.OrderByClauseContext;
import org.antlr.sql.dialects.mysql.MySqlParser.OrderByExpressionContext;
import org.antlr.sql.dialects.mysql.MySqlParser.PredicateExpressionContext;
import org.antlr.sql.dialects.mysql.MySqlParser.QuerySpecificationContext;
import org.antlr.sql.dialects.mysql.MySqlParser.ScalarFunctionCallContext;
import org.antlr.sql.dialects.mysql.MySqlParser.ScalarFunctionNameContext;
import org.antlr.sql.dialects.mysql.MySqlParser.SelectStarElementContext;
import org.antlr.sql.dialects.mysql.MySqlParser.UidContext;
import org.antlr.sql.dialects.mysql.MySqlParser.UidListContext;
import org.antlr.sql.dialects.mysql.MySqlParser.UnionStatementContext;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.sonar.plugins.sql.models.rules.Rule;
import org.sonar.plugins.sql.models.rules.RuleImplementation;
import org.sonar.plugins.sql.models.rules.RuleMatchType;
import org.sonar.plugins.sql.models.rules.RuleResultType;
import org.sonar.plugins.sql.models.rules.SqlRules;
import org.sonar.plugins.sql.models.rules.TextCheckType;

public enum MySQLRules {

	INSTANCE;
	BaseRules baseRules = BaseRules.INSTANCE;

	public List<SqlRules> getRules() {
		List<SqlRules> rules = new ArrayList<>();
		{
			SqlRules customRules = new SqlRules();
			customRules.setRepoKey("SQLABI");
			customRules.setRepoName("SQL Plugin ABI");
			customRules.setDialect(Dialects.MYSQL.name());
			customRules.getRule()
					.addAll(Arrays.asList(getTablesPartitionRule(), getSelectAllRule(), getInsertRule(), getOrderByRule()));
			rules.add(customRules);
		}
		return rules;
	}

	protected Rule getTablesPartitionRule() {
		Rule rule = baseRules.getTablesPartitionRule();
		RuleImplementation impl = rule.getRuleImplementation();
		impl.getNames().getTextItem().add(ScalarFunctionNameContext.class.getSimpleName());
		impl.getTextToFind().getTextItem().add("SLEEP");
		impl.setTextCheckType(TextCheckType.STRICT);
		impl.setRuleMatchType(RuleMatchType.TEXT_AND_CLASS);
		impl.setRuleResultType(RuleResultType.FAIL_IF_FOUND);
		impl.setRuleViolationMessage("SLEEP is used.");
		impl.getViolatingRulesCodeExamples().getRuleCodeExample().add("SELECT SLEEP(5);");

		return rule;
	}

	protected Rule getSelectAllRule() {
		Rule rule = baseRules.getSelectAllRule();
		RuleImplementation impl = rule.getRuleImplementation();
		impl.getNames().getTextItem().add(SelectStarElementContext.class.getSimpleName());
		impl.getTextToFind().getTextItem().add("*");
		impl.setTextCheckType(TextCheckType.STRICT);
		impl.setRuleResultType(RuleResultType.FAIL_IF_FOUND);
		impl.setRuleMatchType(RuleMatchType.CLASS_ONLY);

		return rule;
	}

	protected Rule getInsertRule() {
		Rule rule = baseRules.getInsertRule();
		RuleImplementation impl = rule.getRuleImplementation();
		RuleImplementation child2 = new RuleImplementation();
		child2.getNames().getTextItem().add(UidListContext.class.getSimpleName());
		child2.setTextCheckType(TextCheckType.DEFAULT);
		child2.setRuleResultType(RuleResultType.FAIL_IF_NOT_FOUND);
		child2.setRuleMatchType(RuleMatchType.CLASS_ONLY);
		impl.getChildrenRules().getRuleImplementation().add(child2);
		impl.getNames().getTextItem().add(InsertStatementContext.class.getSimpleName());
		impl.setRuleMatchType(RuleMatchType.CLASS_ONLY);
		impl.setRuleResultType(RuleResultType.DEFAULT);

		return rule;
	}

	protected Rule getOrderByRule() {
		Rule rule = baseRules.getOrderByRule();
		RuleImplementation impl = rule.getRuleImplementation();
		RuleImplementation child2 = new RuleImplementation();
		child2.getNames().getTextItem().add(OrderByExpressionContext.class.getSimpleName());
		child2.setTextCheckType(TextCheckType.REGEXP);
		child2.setRuleResultType(RuleResultType.FAIL_IF_FOUND);
		child2.setRuleMatchType(RuleMatchType.TEXT_AND_CLASS);
		child2.getTextToFind().getTextItem().add("[0-9]+");
		impl.getChildrenRules().getRuleImplementation().add(child2);
		impl.getNames().getTextItem().add(OrderByClauseContext.class.getSimpleName());
		impl.setRuleMatchType(RuleMatchType.CLASS_ONLY);
		impl.setRuleResultType(RuleResultType.DEFAULT);
		impl.setRuleViolationMessage("Positional reference is used instead of column name in an ORDER BY clause.");
		rule.setRuleImplementation(impl);

		return rule;
	}

}
