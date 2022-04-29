/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.interpreter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import org.uast.astgen.base.Node;
import org.uast.astgen.base.Type;
import org.uast.astgen.utils.FilesWriter;

/**
 * Converts a syntax tree to a string contains JSON object.
 *
 * @since 1.0
 */
public final class JsonSerializer {
    /**
     * The 'root' string.
     */
    private static final String STR_ROOT = "root";

    /**
     * The 'type' string.
     */
    private static final String STR_TYPE = "type";

    /**
     * The 'data' string.
     */
    private static final String STR_DATA = "data";

    /**
     * The 'children' string.
     */
    private static final String STR_CHILDREN = "children";

    /**
     * The root node.
     */
    private final Node root;

    /**
     * Constructor.
     * @param root The root node.
     */
    public JsonSerializer(final Node root) {
        this.root = root;
    }

    /**
     * Converts the syntax tree to a string contains JSON object.
     * @return The syntax tree represents as a string
     */
    public String serialize() {
        final JsonObject obj = new JsonObject();
        obj.add(JsonSerializer.STR_ROOT, this.convertNode(this.root));
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(obj);
    }

    /**
     * Converts the syntax tree to a string contains JSON object and
     * writes the result to file.
     * @param filename The file name
     * @throws IOException In case if write operation failed
     */
    public void serializeToFile(final String filename) throws IOException {
        final String json = this.serialize();
        new FilesWriter(filename).writeString(json);
    }

    /**
     * Converts node to JSON object.
     * @param node Node
     * @return JSON object
     */
    private JsonObject convertNode(final Node node) {
        final JsonObject result = new JsonObject();
        final Type type = node.getType();
        result.addProperty(JsonSerializer.STR_TYPE, type.getName());
        final String data = node.getData();
        if (!data.isEmpty()) {
            result.addProperty(JsonSerializer.STR_DATA, data);
        }
        final int count = node.getChildCount();
        if (count > 0) {
            final JsonArray children = new JsonArray();
            result.add(JsonSerializer.STR_CHILDREN, children);
            for (int index = 0; index < count; index = index + 1) {
                children.add(this.convertNode(node.getChild(index)));
            }
        }
        return result;
    }
}
