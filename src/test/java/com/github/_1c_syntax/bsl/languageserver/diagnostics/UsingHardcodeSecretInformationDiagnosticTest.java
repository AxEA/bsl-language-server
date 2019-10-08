/*
 * This file is a part of BSL Language Server.
 *
 * Copyright © 2018-2019
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Gryzlov <nixel2007@gmail.com> and contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * BSL Language Server is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * BSL Language Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BSL Language Server.
 */
package com.github._1c_syntax.bsl.languageserver.diagnostics;

import com.github._1c_syntax.bsl.languageserver.providers.DiagnosticProvider;
import com.github._1c_syntax.bsl.languageserver.utils.RangeHelper;
import org.eclipse.lsp4j.Diagnostic;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UsingHardcodeSecretInformationDiagnosticTest extends AbstractDiagnosticTest<UsingHardcodeSecretInformationDiagnostic> {
	UsingHardcodeSecretInformationDiagnosticTest() {
		super(UsingHardcodeSecretInformationDiagnostic.class);
	}

	@Test
	void test() {

		// when
		List<Diagnostic> diagnostics = getDiagnostics();

		// then
		assertThat(diagnostics).hasSize(9);

		assertThat(diagnostics)
			.anyMatch(diagnostic -> diagnostic.getRange().equals(RangeHelper.newRange(8, 4, 8, 49)))
			.anyMatch(diagnostic -> diagnostic.getRange().equals(RangeHelper.newRange(12, 4, 12, 80)))
			.anyMatch(diagnostic -> diagnostic.getRange().equals(RangeHelper.newRange(16, 4, 16, 23)))
			.anyMatch(diagnostic -> diagnostic.getRange().equals(RangeHelper.newRange(17, 4, 17, 23)))
			.anyMatch(diagnostic -> diagnostic.getRange().equals(RangeHelper.newRange(27, 4, 27, 35)))
			.anyMatch(diagnostic -> diagnostic.getRange().equals(RangeHelper.newRange(32, 4, 32, 27)))
			.anyMatch(diagnostic -> diagnostic.getRange().equals(RangeHelper.newRange(33, 4, 33, 31)))
			.anyMatch(diagnostic -> diagnostic.getRange().equals(RangeHelper.newRange(44, 4, 44, 82)))
			.anyMatch(diagnostic -> diagnostic.getRange().equals(RangeHelper.newRange(45, 4, 45, 79)))
		;


	}

	@Test
	void testConfigure() {

		List<Diagnostic> diagnostics;
		Map<String, Object> configuration;

		// без изменения параметра
		// when
		configuration = DiagnosticProvider.getDefaultDiagnosticConfiguration(getDiagnosticInstance());
		getDiagnosticInstance().configure(configuration);
		diagnostics = getDiagnostics();

		// then
		assertThat(diagnostics).hasSize(9);

		// с изменением параметра searchWords
		// when
		configuration.put("searchWords", "Password");
		getDiagnosticInstance().configure(configuration);
		diagnostics = getDiagnostics();

		// then
		assertThat(diagnostics).hasSize(4);

	}

}