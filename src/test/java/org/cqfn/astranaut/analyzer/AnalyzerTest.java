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
package org.cqfn.astranaut.analyzer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.cqfn.astranaut.exceptions.BaseException;
import org.cqfn.astranaut.exceptions.DuplicateRule;
import org.cqfn.astranaut.parser.ProgramParser;
import org.cqfn.astranaut.rules.Instruction;
import org.cqfn.astranaut.rules.Program;
import org.cqfn.astranaut.rules.Vertex;
import org.cqfn.astranaut.utils.FilesReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link Analyzer} of parsed rules.
 *
 * @since 0.1.5
 */
@SuppressWarnings("PMD.TooManyMethods")
public class AnalyzerTest {
    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/analyzer/";

    /**
     * The name of Java language.
     */
    private static final String JAVA_LANGUAGE = "java";

    /**
     * The type A.
     */
    private static final String A_TYPE = "A";

    /**
     * The type B.
     */
    private static final String B_TYPE = "B";

    /**
     * The type C.
     */
    private static final String C_TYPE = "C";

    /**
     * The type D.
     */
    private static final String D_TYPE = "D";

    /**
     * The type E.
     */
    private static final String E_TYPE = "E";

    /**
     * The type F.
     */
    private static final String F_TYPE = "F";

    /**
     * The type G.
     */
    private static final String G_TYPE = "G";

    /**
     * The type H.
     */
    private static final String H_TYPE = "H";

    /**
     * The type Addition.
     */
    private static final String ADD_TYPE = "Addition";

    /**
     * Test for analysis of depth 3 nodes inheritance.
     * Case with Java nodes request.
     */
    @Test
    public void testThreeDepthHierarchyJava() {
        boolean oops = false;
        final String source = this.readTest("depth3_java_set.txt");
        final ProgramParser parser = new ProgramParser(source);
        try {
            final Program program = parser.parse();
            final List<Instruction<Vertex>> vertices = program.getVertices();
            final Analyzer analyzer = new Analyzer(vertices, AnalyzerTest.JAVA_LANGUAGE);
            analyzer.analyze();
            Assertions.assertEquals(AnalyzerTest.JAVA_LANGUAGE, analyzer.getLanguage());
            final List<String> etype = Collections.singletonList(AnalyzerTest.E_TYPE);
            Assertions.assertEquals(etype, analyzer.getHierarchy(AnalyzerTest.E_TYPE));
            final List<String> btype = Arrays.asList(
                AnalyzerTest.B_TYPE,
                AnalyzerTest.E_TYPE
            );
            Assertions.assertEquals(btype, analyzer.getHierarchy(AnalyzerTest.B_TYPE));
            final List<String> atype = Arrays.asList(
                AnalyzerTest.A_TYPE,
                AnalyzerTest.B_TYPE,
                AnalyzerTest.E_TYPE
            );
            Assertions.assertEquals(atype, analyzer.getHierarchy(AnalyzerTest.A_TYPE));
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test for analysis of depth 3 nodes inheritance.
     * Case with green nodes request.
     */
    @Test
    public void testThreeDepthHierarchyGreen() {
        boolean oops = false;
        final ProgramParser parser = this.getSource();
        try {
            final Program program = parser.parse();
            final List<Instruction<Vertex>> vertices = program.getVertices();
            final Analyzer analyzer = new Analyzer(vertices, "");
            analyzer.analyze();
            Assertions.assertEquals("", analyzer.getLanguage());
            final List<String> etype = Collections.singletonList(AnalyzerTest.E_TYPE);
            Assertions.assertEquals(etype, analyzer.getHierarchy(AnalyzerTest.E_TYPE));
            final List<String> btype = Arrays.asList(
                AnalyzerTest.B_TYPE,
                AnalyzerTest.E_TYPE
            );
            Assertions.assertEquals(btype, analyzer.getHierarchy(AnalyzerTest.B_TYPE));
            final List<String> atype = Arrays.asList(
                AnalyzerTest.A_TYPE,
                AnalyzerTest.B_TYPE,
                AnalyzerTest.E_TYPE
            );
            Assertions.assertEquals(atype, analyzer.getHierarchy(AnalyzerTest.A_TYPE));
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test for analysis of depth 3 nodes inheritance.
     */
    @Test
    public void testThreeDepthTags() {
        boolean oops = false;
        final ProgramParser parser = this.getSource();
        try {
            final Program program = parser.parse();
            final List<Instruction<Vertex>> vertices = program.getVertices();
            final Analyzer analyzer = new Analyzer(vertices, "");
            analyzer.analyze();
            String list = "[{left, E, true}, {right, E, true}]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.A_TYPE).toString()
            );
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.F_TYPE).toString()
            );
            list = "[{right, E, false}, {left, E, false}]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.B_TYPE).toString()
            );
            list = "[]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.E_TYPE).toString()
            );
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test hierarchy of nodes in depth4_set.txt.
     */
    @Test
    public void testFourDepthHierarchy() throws BaseException {
        final Analyzer analyzer = this.getAnalyzer();
        final List<String> atype = Collections.singletonList(AnalyzerTest.A_TYPE);
        Assertions.assertEquals(atype, analyzer.getHierarchy(AnalyzerTest.A_TYPE));
        final List<String> btype = Arrays.asList(
            AnalyzerTest.B_TYPE,
            AnalyzerTest.A_TYPE
        );
        Assertions.assertEquals(btype, analyzer.getHierarchy(AnalyzerTest.B_TYPE));
        final List<String> ctype = Arrays.asList(
            AnalyzerTest.C_TYPE,
            AnalyzerTest.A_TYPE
        );
        Assertions.assertEquals(ctype, analyzer.getHierarchy(AnalyzerTest.C_TYPE));
        final List<String> dtype = Arrays.asList(
            AnalyzerTest.D_TYPE,
            AnalyzerTest.E_TYPE,
            AnalyzerTest.B_TYPE,
            AnalyzerTest.A_TYPE
        );
        Assertions.assertEquals(dtype, analyzer.getHierarchy(AnalyzerTest.D_TYPE));
        final List<String> etype = Arrays.asList(
            AnalyzerTest.E_TYPE,
            AnalyzerTest.B_TYPE,
            AnalyzerTest.A_TYPE
        );
        Assertions.assertEquals(etype, analyzer.getHierarchy(AnalyzerTest.E_TYPE));
        final List<String> ftype = Arrays.asList(
            AnalyzerTest.F_TYPE,
            AnalyzerTest.B_TYPE,
            AnalyzerTest.A_TYPE
        );
        Assertions.assertEquals(ftype, analyzer.getHierarchy(AnalyzerTest.F_TYPE));
        final List<String> gtype = Arrays.asList(
            AnalyzerTest.G_TYPE,
            AnalyzerTest.C_TYPE,
            AnalyzerTest.A_TYPE
        );
        Assertions.assertEquals(gtype, analyzer.getHierarchy(AnalyzerTest.G_TYPE));
        final List<String> htype = Arrays.asList(
            AnalyzerTest.H_TYPE,
            AnalyzerTest.C_TYPE,
            AnalyzerTest.A_TYPE
        );
        Assertions.assertEquals(htype, analyzer.getHierarchy(AnalyzerTest.H_TYPE));
    }

    /**
     * Test hierarchy of nodes in depth4_set.txt.
     */
    @Test
    public void testFourDepthTags() throws BaseException {
        final Analyzer analyzer = this.getAnalyzer();
        String list = "[{a, A, false}]";
        Assertions.assertEquals(
            list,
            analyzer.getTags(AnalyzerTest.A_TYPE).toString()
        );
        list = "[{a, A, true}]";
        Assertions.assertEquals(
            list,
            analyzer.getTags(AnalyzerTest.C_TYPE).toString()
        );
        Assertions.assertEquals(
            list,
            analyzer.getTags(AnalyzerTest.H_TYPE).toString()
        );
        list = "[{a, A, true}, {e, E, true}]";
        Assertions.assertEquals(
            list,
            analyzer.getTags(AnalyzerTest.D_TYPE).toString()
        );
        Assertions.assertEquals(
            list,
            analyzer.getTags(AnalyzerTest.E_TYPE).toString()
        );
        Assertions.assertEquals(
            list,
            analyzer.getTags(AnalyzerTest.F_TYPE).toString()
        );
        list = "[{a, A, true}, {e, E, false}]";
        Assertions.assertEquals(
            list,
            analyzer.getTags(AnalyzerTest.B_TYPE).toString()
        );
        Assertions.assertEquals(
            list,
            analyzer.getTags(AnalyzerTest.G_TYPE).toString()
        );
    }

    /**
     * Test analysis of DSL rules which contain duplicated rules for one node.
     */
    @Test
    public void testDslWithDuplicatedRulesForNode() {
        boolean oops = false;
        try {
            final String source = this.readTest("duplicated_rule_set.txt");
            final ProgramParser parser = new ProgramParser(source);
            final Program program = parser.parse();
            final List<Instruction<Vertex>> vertices = program.getVertices();
            try {
                final Analyzer analyzer = new Analyzer(vertices, "");
                analyzer.analyze();
            } catch (final DuplicateRule exception) {
                oops = true;
            }
        } catch (final BaseException ignored) {
        }
        Assertions.assertTrue(oops);
    }

    /**
     * Test analysis of DSL rules which contain a node that inherits several abstract
     * nodes.
     */
    @Test
    public void testDslWithDuplicatedInheritance() {
        boolean oops = false;
        try {
            final String source = this.readTest("duplicated_inheritance_set.txt");
            final ProgramParser parser = new ProgramParser(source);
            final Program program = parser.parse();
            final List<Instruction<Vertex>> vertices = program.getVertices();
            try {
                final Analyzer analyzer = new Analyzer(vertices, "");
                analyzer.analyze();
            } catch (final DuplicateRule exception) {
                oops = true;
            }
        } catch (final BaseException ignored) {
        }
        Assertions.assertTrue(oops);
    }

    /**
     * Test finding one node that should be imported to the generated class
     * of the specified node.
     */
    @Test
    public void testFindingOneNodeToBeImported() {
        boolean oops = false;
        try {
            final String source = this.readTest("one_import_set.txt");
            final ProgramParser parser = new ProgramParser(source);
            final Program program = parser.parse();
            final List<Instruction<Vertex>> vertices = program.getVertices();
            final Analyzer analyzer = new Analyzer(vertices, AnalyzerTest.JAVA_LANGUAGE);
            analyzer.analyze();
            final Set<String> imports = analyzer.getImports(AnalyzerTest.D_TYPE);
            Assertions.assertEquals("[E]", imports.toString());
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test finding several nodes that should be imported to the generated class
     * of the specified node.
     */
    @Test
    public void testFindingSeveralNodesToBeImported() {
        boolean oops = false;
        try {
            final String source = this.readTest("several_imports_set.txt");
            final ProgramParser parser = new ProgramParser(source);
            final Program program = parser.parse();
            final List<Instruction<Vertex>> vertices = program.getVertices();
            final Analyzer analyzer = new Analyzer(vertices, AnalyzerTest.JAVA_LANGUAGE);
            analyzer.analyze();
            final Set<String> imports = analyzer.getImports(AnalyzerTest.D_TYPE);
            Assertions.assertEquals("[E, B]", imports.toString());
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test finding no nodes that should be imported to the generated class
     * of the specified node.
     */
    @Test
    public void testFindingNoNodesToBeImported() {
        boolean oops = false;
        try {
            final String source = this.readTest("no_imports_set.txt");
            final ProgramParser parser = new ProgramParser(source);
            final Program program = parser.parse();
            final List<Instruction<Vertex>> vertices = program.getVertices();
            final Analyzer analyzer = new Analyzer(vertices, AnalyzerTest.JAVA_LANGUAGE);
            analyzer.analyze();
            final Set<String> imports = analyzer.getImports(AnalyzerTest.D_TYPE);
            Assertions.assertEquals("[]", imports.toString());
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test analysis of DSL rules which contain a language-specific node that extends
     * a green abstract node.
     */
    @Test
    public void testSetWithAbstractNodeExtension() {
        boolean oops = false;
        try {
            final String source = this.readTest("green_java_set.txt");
            final ProgramParser parser = new ProgramParser(source);
            final Program program = parser.parse();
            final List<Instruction<Vertex>> vertices = program.getVertices();
            try {
                final Analyzer janalyzer = new Analyzer(vertices, AnalyzerTest.JAVA_LANGUAGE);
                janalyzer.analyze();
                final List<String> btype = Arrays.asList(
                    AnalyzerTest.B_TYPE,
                    AnalyzerTest.B_TYPE,
                    AnalyzerTest.E_TYPE
                );
                Assertions.assertEquals(btype, janalyzer.getHierarchy(AnalyzerTest.B_TYPE));
                final List<String> dtype = Arrays.asList(
                    AnalyzerTest.D_TYPE,
                    AnalyzerTest.B_TYPE,
                    AnalyzerTest.B_TYPE,
                    AnalyzerTest.E_TYPE
                );
                Assertions.assertEquals(dtype, janalyzer.getHierarchy(AnalyzerTest.D_TYPE));
                final Analyzer granalyzer = new Analyzer(vertices, "");
                granalyzer.analyze();
                final List<String> bgrtype = Arrays.asList(
                    AnalyzerTest.B_TYPE,
                    AnalyzerTest.E_TYPE
                );
                Assertions.assertEquals(bgrtype, granalyzer.getHierarchy(AnalyzerTest.B_TYPE));
            } catch (final DuplicateRule exception) {
                oops = true;
            }
        } catch (final BaseException ignored) {
            oops = false;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test analysis of DSL rules which contain different types of vertices.
     */
    @Test
    public void testSetWithVariousVertices() {
        boolean oops = false;
        try {
            final String source = this.readTest("complex_set.txt");
            final ProgramParser parser = new ProgramParser(source);
            final Program program = parser.parse();
            final List<Instruction<Vertex>> vertices = program.getVertices();
            try {
                final Analyzer analyzer = new Analyzer(vertices, "");
                analyzer.analyze();
                final List<String> hierarchy = Arrays.asList(
                    AnalyzerTest.ADD_TYPE,
                    "BinaryExpression",
                    "Expression"
                );
                Assertions.assertEquals(hierarchy, analyzer.getHierarchy(AnalyzerTest.ADD_TYPE));
                final String identifier = "Identifier";
                Assertions.assertEquals(
                    Collections.singletonList(identifier), analyzer.getHierarchy(identifier)
                );
                final String list = "ExpressionList";
                Assertions.assertEquals(
                    Collections.singletonList(list), analyzer.getHierarchy(list)
                );
            } catch (final DuplicateRule exception) {
                oops = true;
            }
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Reads test source from the file.
     * @param name The file name
     * @return Test source
     */
    private String readTest(final String name) {
        String result = "";
        boolean oops = false;
        try {
            result = new FilesReader(AnalyzerTest.TESTS_PATH.concat(name))
                .readAsString();
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return result;
    }

    /**
     * Get analyzer of depth 4 nodes inheritance.
     */
    private Analyzer getAnalyzer() throws BaseException {
        final String source = this.readTest("depth4_set.txt");
        final ProgramParser parser = new ProgramParser(source);
        final Program program = parser.parse();
        final List<Instruction<Vertex>> vertices = program.getVertices();
        final Analyzer analyzer = new Analyzer(vertices, "");
        analyzer.analyze();
        return analyzer;
    }

    /**
     * Get program parser of depth 3 nodes inheritance.
     */
    private ProgramParser getSource() {
        final String source = this.readTest("depth3_set.txt");
        return new ProgramParser(source);
    }
}
