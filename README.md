# ASTRANAUT - Abstract Syntax Trees Generator / Converter

![Build and test](https://github.com/cqfn/astranaut/workflows/Build%20and%20test/badge.svg)
[![Codecov](https://codecov.io/gh/cqfn/astranaut/branch/master/graph/badge.svg)](https://codecov.io/gh/cqfn/astranaut)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/cqfn/astranaut/blob/master/LICENSE.txt)
___

## Brief

**Astranaut** is a software system developed as part of an internal research initiative focused on the automation
of transformations and unification of syntactic trees. The project explores how domain-specific languages (DSLs)
can be used to formally describe both the structure of syntactic trees and the transformations applied to them.

The name combines **AST** (Abstract Syntax Tree) with the ideas of **automation** and **transformation**,
reflecting the core functionality of the project.

The system operates in two primary modes:

1. **Compilation Mode**  
   In this mode, Astranaut generates Java source code from DSL rules. The output includes:
   - Data classes representing tree node structures
   - Transformation methods for subtree rewriting
   - Auxiliary utilities to facilitate integration
     
   The resulting codebase is suitable for inclusion in external projects, and this approach bears conceptual similarity
   to tools like ANTLR or parser generators.

2. **Interpretation Mode**  
   In this mode, Astranaut directly consumes either a JSON-encoded tree or raw text. Based on the DSL definitions,
   it applies in-memory transformations and produces:
   - A transformed syntactic tree in JSON format
   - An optional visual representation of the tree structure

This dual-mode approach allows for both static integration into production systems and rapid experimentation
during DSL design and testing.

## Requirements

* Java 1.8
* Maven 3.6.3+ (to build)

## How to download and build 

You can download the latest release
[here](https://repo.maven.apache.org/maven2/org/cqfn/astranaut).

Fastest way to build the executable is to open the project in Intellij IDEA, select the "Maven" tab
and double-click on the "package" item.

To build using console, go to the folder that contains the project, and type:
```
mvn package
```

In both ways, the executable file named `generator.jar` will be in the `target` folder.

## Syntax

Astranaut operates on a custom-designed domain-specific language (DSL), which serves as the foundation for both tree
structure definitions and transformation logic. This DSL supports two core types of rules:

1. **Node Definitions**  
2. **Transformation Rules**  

The syntax has been deliberately kept minimal and readable. While simple by design, it has proven to be both expressive
and powerful in practice — striking a balance between approachability and capability.

### Statement Separation

All rules in the Astranaut DSL are separated by semicolons (`;`).

Line breaks are ignored by the parser, allowing long rules to be split across multiple lines purely for readability.

### Structure of a Rule

Each rule in the DSL consists of a **left-hand side (LHS)** and a **right-hand side (RHS)**, separated by an arrow
symbol. There are two types of arrows, each with a distinct semantic purpose:

- `<-` (left arrow) — used for **node declarations**  
  Defines a syntax tree node and its internal structure.  
  Format:
  ```
  NodeName <- NodeDescription;
  ```
- `->` (right arrow) — used for **transformation rules**  
  Describes how a subtree (or set of subtrees) should be transformed into a new structure.  
  Format:
  ```
  SourcePattern -> TargetPattern;
  ```

### Node Declarations

Node declarations in Astranaut DSL define the structural schema of tree nodes. They serve as static type definitions
for syntax tree elements and impose strict constraints on the number and types of child nodes.

**Key rules:**

- A node declaration uses the `<-` operator.
- The left-hand side is the name of the node being declared.
- The right-hand side is a comma-separated list of required child node types.
- The number and order of child nodes are fixed and must be strictly followed.

Node declarations are non-executable and declarative by nature — they do not perform any computation or transformation.
Their primary purpose is to define *what a valid node looks like*.

Each child node must be either:
- A previously declared type
- Or a type that will be declared later

Once defined, these rules guarantee:

- Nodes can only be constructed if all their children are present and match the declared types.
- The tree structure is inherently self-validating — **if a node exists, it is structurally correct** by definition.
- The root node (if present) represents a complete and valid syntax tree.
- No transformation can produce an invalid tree.

#### Example

Consider the following node declaration:

```
Addition <- Expression, Expression;
```

This defines a node named `Addition`, which must have exactly **two** children, each of type `Expression`
The declaration enforces:

- **Cardinality:** The number of children is fixed — no more, no less.
- **Type constraints:** Both children must be of type `Expression`, or of a type that inherits from `Expression`.

Any attempt to construct an `Addition` node with fewer or more children, or with children of incorrect types,
will be rejected at the structural level — before any transformations are applied.

This kind of declarative schema enables:

- Safe assumptions during transformation
- Clear, readable structure definitions
- Enforcement of correctness without additional runtime checks

### Optional Child Nodes

To support flexible tree structures, Astranaut DSL allows node declarations to include **optional** child nodes.
Optional elements are enclosed in square brackets `[...]`.

For example:

```
VariableDeclaration <- DataType, Identifier, [Expression];
```

In this declaration:

- `DataType` and `Identifier` are **required** child nodes.
- `Expression` is **optional** — it may be present or omitted.
- If present, it must conform to the declared type (`Expression` or its subtype).

Only the presence of the node is optional — its type is still strictly enforced if the node is present.

### Tags

Tags provide **named access** to specific child nodes within a node declaration. They serve as semantic labels,
helping both humans and the code generator understand the role of each child node.

The syntax is simple: a tag is written as `tagName@Type`.

#### Examples

```
Addition <- left@Expression, right@Expression;
VariableDeclaration <- dataType@DataType, name@Identifier, [initialValue@Expression];
```

In these examples:

- The `Addition` node has two required children: `left` and `right`, both of type `Expression`.
- The `VariableDeclaration` node has two required children and one optional child, each tagged with a meaningful name.

#### Purpose and Benefits

While tags are **optional**, they provide important advantages:

- **Readable structure**: Tags make the role of each child node immediately obvious.
- **Generated accessors**: When tags are present, the Astranaut code generator (currently targeting Java)
  produces named getter methods such as:
  - `getLeft()`, `getRight()` for `Addition`
  - `getInitialValue()` for `VariableDeclaration`

When no tags are specified, access to child nodes is performed by **index**. For example, in a node declared as:

```
Addition <- Expression, Expression;
```

the generated code will expose generic accessors such as `getChild(0)` and `getChild(1)`.
While this approach is fully functional, it is less expressive and more error-prone compared to named accessors.

## Contributors

* Ivan Kniazkov, @kniazkov
* Polina Volkhontseva, @pollyvolk

See our [Contributing policy](CONTRIBUTING.md).