/*
 * This file is a part of BSL Language Server.
 *
 * Copyright © 2018-2020
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

import com.github._1c_syntax.bsl.languageserver.context.symbol.MethodSymbol;
import com.github._1c_syntax.bsl.languageserver.context.symbol.RegionSymbol;
import com.github._1c_syntax.bsl.languageserver.diagnostics.metadata.DiagnosticInfo;
import com.github._1c_syntax.bsl.languageserver.diagnostics.metadata.DiagnosticMetadata;
import com.github._1c_syntax.bsl.languageserver.diagnostics.metadata.DiagnosticParameter;
import com.github._1c_syntax.bsl.languageserver.diagnostics.metadata.DiagnosticSeverity;
import com.github._1c_syntax.bsl.languageserver.diagnostics.metadata.DiagnosticTag;
import com.github._1c_syntax.bsl.languageserver.diagnostics.metadata.DiagnosticType;
import com.github._1c_syntax.bsl.languageserver.utils.Regions;
import com.github._1c_syntax.bsl.parser.BSLParser;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Map;
import java.util.regex.Pattern;

@DiagnosticMetadata(
  type = DiagnosticType.CODE_SMELL,
  severity = DiagnosticSeverity.INFO,
  minutesToFix = 1,
  tags = {
    DiagnosticTag.STANDARD,
    DiagnosticTag.BRAINOVERLOAD,
    DiagnosticTag.BADPRACTICE
  }
)
public class PublicMethodsDescriptionDiagnostic extends AbstractVisitorDiagnostic {

  private static final Pattern API_REGION_NAME = Pattern.compile(
    "^(?:ПрограммныйИнтерфейс|Public)$",
    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
  );

  private static final boolean DEFAULT_CHECK_ALL_REGION = false;

  @DiagnosticParameter(
    type = Boolean.class,
    defaultValue = "" + DEFAULT_CHECK_ALL_REGION
  )
  private boolean checkAllRegion = DEFAULT_CHECK_ALL_REGION;

  public PublicMethodsDescriptionDiagnostic(DiagnosticInfo info) {
    super(info);
  }

  @Override
  public void configure(Map<String, Object> configuration) {
    if (configuration == null) {
      return;
    }

    checkAllRegion = (boolean) configuration.getOrDefault("checkAllRegion", DEFAULT_CHECK_ALL_REGION);
  }

  @Override
  public ParseTree visitSub(BSLParser.SubContext ctx) {

    documentContext.getMethodSymbol(ctx).ifPresent((MethodSymbol methodSymbol) -> {
      if (methodSymbol.isExport() && methodSymbol.getDescription().isEmpty()) {
        if (checkAllRegion) {
          diagnosticStorage.addDiagnostic(methodSymbol.getRange());
        } else {
          methodSymbol.getRegion().flatMap(mr -> Regions.getRootRegion(documentContext.getRegions(), mr))
            .ifPresent(rootRegion -> {
              if (isAPIRegion(rootRegion)) {
                diagnosticStorage.addDiagnostic(methodSymbol.getRange());
              }
            });
        }
      }
    });

    return ctx;
  }

  private static boolean isAPIRegion(RegionSymbol region) {
    return API_REGION_NAME.matcher(region.getName()).matches();
  }
}
