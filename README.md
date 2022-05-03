# Abstract Syntax Trees Generator / Converter

![Build and test](https://github.com/unified-ast/ast-generator/workflows/Build%20and%20test/badge.svg)
[![Codecov](https://codecov.io/gh/unified-ast/ast-generator/branch/master/graph/badge.svg)](https://codecov.io/gh/unified-ast/ast-generator)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt)
___

## Brief
This software was prepared as part of an investigation of the possibilities of transformation
and unification of syntax trees.

This application is a compiler/interpreter for a domain-specific language ("DSL") that allows
to describe syntax trees and their transformations.

In compiler mode, the application generates source code in the Java programming language,
which describes the structure of a specified syntax tree, methods for subtrees transforming,
as well as auxiliary classes. The generated source code is then used in another project for
predefined syntax tree operations.

In interpreter mode, the application transforms the syntax tree represented in JSON format according
to the specified DSL rules.
