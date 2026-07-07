package com.sbproject.deokhugam.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

public class BigmFunctionContributor implements FunctionContributor {

	@Override
	public void contributeFunctions(FunctionContributions functionContributions) {
		functionContributions.getFunctionRegistry().registerPattern(
			"bigm_similar",
			"(?1 =% ?2)",
			functionContributions.getTypeConfiguration()
			                     .getBasicTypeRegistry()
			                     .resolve(StandardBasicTypes.BOOLEAN)
		);
	}
}
