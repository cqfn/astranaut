/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cqfn.astranaut.interpreter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import org.cqfn.astranaut.base.Node;
import org.cqfn.astranaut.base.Type;
import org.cqfn.astranaut.utils.FilesWriter;

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
     * @return The result, {@code true} if the file was successful written
     */
    public boolean serializeToFile(final String filename) {
        final String json = this.serialize();
        boolean success = true;
        try {
            new FilesWriter(filename).writeString(json);
        } catch (final IOException ignored) {
            success = false;
        }
        return success;
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
