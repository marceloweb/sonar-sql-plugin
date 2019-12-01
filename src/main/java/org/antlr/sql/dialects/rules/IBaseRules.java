package org.antlr.sql.dialects.rules;

import org.sonar.plugins.sql.models.rules.Rule;

public interface IBaseRules {

	Rule getTablesPartitionRule();

	Rule getSelectAllRule();

	Rule getInsertRule();

	Rule getOrderByRule();

	Rule getExecRule();

	Rule getSchemaRule();

	Rule getNoLockRule();

}
