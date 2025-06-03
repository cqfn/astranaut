# Example Of Generated Code

```java
/**
 * Node of the 'Addition' type.
 * @since 1.13
 */
public final class Addition implements Expression {
    /**
     * Name of the type.
     */
    public static final String NAME = "Addition";

    /**
     * Type of the node.
     */
    public static final Type TYPE = new AdditionType();

    /**
     * Fragment of source code that is associated with the node.
     */
    private Fragment fragment;

    /**
     * Child node with 'left' tag.
     */
    private Expression left;

    /**
     * Child node with 'right' tag.
     */
    private Expression right;

    /**
     * List of child nodes.
     */
    private List<Node> children;

    /**
     * Constructor.
     */
    private Addition() {
    }

    @Override
    public Fragment getFragment() {
        return this.fragment;
    }

    @Override
    public Type getType() {
        return Addition.TYPE;
    }

    @Override
    public String getData() {
        return "";
    }

    @Override
    public int getChildCount() {
        return this.children.size();
    }

    @Override
    public Node getChild(final int index) {
        return this.children.get(index);
    }

    /**
     * Returns child node with 'left' tag.
     * @return Child node (can't be {@code null})
     */
    public Expression getLeft() {
        return this.left;
    }

    /**
     * Returns child node with 'right' tag.
     * @return Child node (can't be {@code null})
     */
    public Expression getRight() {
        return this.right;
    }

    @Override
    public List<Node> getChildrenList() {
        return this.children;
    }

    /**
     * Type implementation describing 'Addition' nodes.
     * @since 1.13
     */
    private static final class AdditionType implements Type {
        /**
         * The 'Expression' type name.
         */
        private static final String TYPE_EXPRESSION = "Expression";

        /**
         * List of child node descriptors.
         */
        private static final List<ChildDescriptor> CHILD_TYPES =
            ChildDescriptor.create()
                .required(AdditionType.TYPE_EXPRESSION)
                .required(AdditionType.TYPE_EXPRESSION)
                .build();

        /**
         * Node hierarchy.
         */
        private static final List<String> HIERARCHY =
            Arrays.asList(Addition.NAME, AdditionType.TYPE_EXPRESSION);

        @Override
        public List<ChildDescriptor> getChildTypes() {
            return AdditionType.CHILD_TYPES;
        }

        @Override
        public String getName() {
            return Addition.NAME;
        }

        @Override
        public List<String> getHierarchy() {
            return AdditionType.HIERARCHY;
        }

        @Override
        public Map<String, String> getProperties() {
            return CommonFactory.PROPERTIES;
        }

        @Override
        public Builder createBuilder() {
            return new Addition.Constructor();
        }
    }

    /**
     * Constructor (builder) that creates nodes of the 'Addition' type.
     * @since 1.13
     */
    public static final class Constructor implements Builder {
        /**
         * Fragment of source code that is associated with the node.
         */
        private Fragment fragment;

        /**
         * Child node with 'left' tag.
         */
        private Expression left;

        /**
         * Child node with 'right' tag.
         */
        private Expression right;

        /**
         * Constructor.
         */
        public Constructor() {
            this.fragment = EmptyFragment.INSTANCE;
        }

        @Override
        public void setFragment(final Fragment object) {
            this.fragment = object;
        }

        /**
         * Sets child node with 'left' tag.
         * @param object Child node
         */
        public void setLeft(final Expression object) {
            if (object != null) {
                this.left = object;
            }
        }

        /**
         * Sets child node with 'right' tag.
         * @param object Child node
         */
        public void setRight(final Expression object) {
            if (object != null) {
                this.right = object;
            }
        }

        @Override
        public boolean setData(final String value) {
            return value.isEmpty();
        }

        @Override
        public boolean setChildrenList(final List<Node> list) {
            final NodeAllocator allocator = new NodeAllocator(AdditionType.CHILD_TYPES);
            final Node[] nodes = new Node[2];
            final boolean result = allocator.allocate(nodes, list);
            if (result) {
                this.left = (Expression) nodes[0];
                this.right = (Expression) nodes[1];
            }
            return result;
        }

        @Override
        public boolean isValid() {
            return this.left != null && this.right != null;
        }

        @Override
        public Node createNode() {
            if (!this.isValid()) {
                throw new IllegalStateException();
            }
            final Addition node = new Addition();
            node.fragment = this.fragment;
            node.left = this.left;
            node.right = this.right;
            node.children = new ListUtils<Node>().add(this.left, this.right).make();
            return node;
        }
    }
}
```