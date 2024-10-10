/*
 * Copyright (c) 2024 John Doe
 */

import identifiers.dsl;
import expressions.dsl;

MethodCall <- name@Identifier, [args@ExpressionList];
