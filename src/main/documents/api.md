Astranaut API
---

## About

Astranaut API provides an ability to access Astranaut as a library.

> For now API is available for Java language.

## Usage

Add Astranaut library to your project using a Maven or a Gradle dependency.

You can find project versions in [MvnRepository](https://mvnrepository.com/artifact/org.cqfn/astranaut).

With Astranaut API you can:

- [transform](#transform) - modify a tree using rules written in [DSL](https://github.com/cqfn/astranaut/blob/master/src/main/documents/bnf.md);
- [serialize](#serialize) - convert a tree to a `json` object and save in a file;
- [deserialize](#deserialize) - load a tree from a `json` file;
- [visualize](#visualize) - save a tree in a graphical format.

## Transform

To transform a tree you have to represent a tree with classes that implement the `Node` interface.

You can also use the `DraftNode` constructor with a base functionality.

To convert the tree you have to create a `txt` file with transformation rules 
and apply them to an initial tree using `TreeProcessor`.

The `TreeProcessor` has two methods:

1. `void loadRules(String filename)` - to load transformation rules from a file.
2. `Node transform(Node tree)` - to transform an input tree using loaded rules.

**Example:**

You can create a simple tree:

```mermaid
  graph Tree;
      Addition -- 0 --> IntegerLiteral_2;
      Addition -- 1 --> IntegerLiteral_3;
```

in the following way:

~~~java
final DraftNode.Constructor addition = new DraftNode.Constructor();
addition.setName("Addition");
final DraftNode.Constructor left = new DraftNode.Constructor();
left.setName("IntegerLiteral");
left.setData("2");
final DraftNode.Constructor right = new DraftNode.Constructor();
right.setName("IntegerLiteral");
right.setData("3");
addition.setChildrenList(Arrays.asList(left.createNode(), right.createNode()));
final Node tree = addition.createNode();
~~~

For this example, we will transform the tree to the only one node `IntegerLiteral` with a sum of two integers.\
To do so, create a file (here, `rules.txt`) with the rule

~~~
Addition(IntegerLiteral<"2">, IntegerLiteral<"3">) -> IntegerLiteral<"5"> ;
~~~

After that, apply the rule:

~~~java
final TreeProcessor processor = new TreeProcessor();
processor.loadRules("rules.txt");
final Node result = processor.transform(tree);
~~~

 


## Serialize

**Examples:**
~~~java
final JsonSerializer serializer = new JsonSerializer(result);
serializer.serializeToJsonFile("Data/tree.json");
~~~

or 

~~~java
final JsonSerializer serializer = new JsonSerializer(result);
final String json = serializer.serializeToJsonString();
~~~

## Deserialize

**Example:**
~~~java
final JsonDeserializer deserializer = new JsonDeserializer("Data/tree.json");
final Node tree = deserializer.deserialize();
~~~

## Visualize

**Example:**
~~~java
final TreeVisualizer visualizer = new TreeVisualizer(result);
visualizer.visualize(new File("Data/tree.png"));
~~~
