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
import org.cqfn.astranaut.core.exceptions.BaseException;
import org.cqfn.astranaut.core.utils.FilesReader;
import org.cqfn.astranaut.exceptions.DuplicateRule;
import org.cqfn.astranaut.exceptions.ExtendedNodeNotFound;
import org.cqfn.astranaut.exceptions.GeneratorException;
import org.cqfn.astranaut.parser.ProgramParser;
import org.cqfn.astranaut.rules.Instruction;
import org.cqfn.astranaut.rules.Program;
import org.cqfn.astranaut.rules.Vertex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link Analyzer} of parsed rules.
 *
 * @since 0.1.5
 */
@SuppressWarnings("PMD.TooManyMethods")
class AnalyzerTest {
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
     * The type I.
     */
    private static final String I_TYPE = "I";

    /**
     * The type Z.
     */
    private static final String Z_TYPE = "Z";

    /**
     * The type Addition.
     */
    private static final String ADD_TYPE = "Addition";

    /**
     * The file name "depth4_set.txt".
     */
    private static final String FOUR_SET = "depth4_set.txt";

    /**
     * The file name "depth4_set_mixed.txt".
     */
    private static final String FOUR_MIX_SET = "depth4_set_mixed.txt";

    /**
     * Test for analysis of depth 3 nodes inheritance.
     * Case with Java nodes request.
     */
    @Test
    void testThreeDepthHierarchyJava() {
        boolean oops = false;
        final String source = this.readTest("depth3_java_set.txt");
        final ProgramParser parser = new ProgramParser(source);
        try {
            final Program program = parser.parse();
            final Analyzer analyzer = this.createAnalyzer(program);
            final List<String> etype = Collections.singletonList(AnalyzerTest.E_TYPE);
            Assertions.assertEquals(
                etype, analyzer.getHierarchy(AnalyzerTest.E_TYPE, AnalyzerTest.JAVA_LANGUAGE)
            );
            final List<String> btype = Arrays.asList(
                AnalyzerTest.B_TYPE,
                AnalyzerTest.E_TYPE
            );
            Assertions.assertEquals(
                btype, analyzer.getHierarchy(AnalyzerTest.B_TYPE, AnalyzerTest.JAVA_LANGUAGE)
            );
            final List<String> atype = Arrays.asList(
                AnalyzerTest.A_TYPE,
                AnalyzerTest.B_TYPE,
                AnalyzerTest.E_TYPE
            );
            Assertions.assertEquals(
                atype, analyzer.getHierarchy(AnalyzerTest.A_TYPE, AnalyzerTest.JAVA_LANGUAGE)
            );
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
    void testThreeDepthHierarchyGreen() {
        boolean oops = false;
        final ProgramParser parser = this.getSource();
        try {
            final Program program = parser.parse();
            final Analyzer analyzer = this.createAnalyzer(program);
            final List<String> etype = Collections.singletonList(AnalyzerTest.E_TYPE);
            Assertions.assertEquals(etype, analyzer.getHierarchy(AnalyzerTest.E_TYPE, ""));
            final List<String> btype = Arrays.asList(
                AnalyzerTest.B_TYPE,
                AnalyzerTest.E_TYPE
            );
            Assertions.assertEquals(btype, analyzer.getHierarchy(AnalyzerTest.B_TYPE, ""));
            final List<String> atype = Arrays.asList(
                AnalyzerTest.A_TYPE,
                AnalyzerTest.B_TYPE,
                AnalyzerTest.E_TYPE
            );
            Assertions.assertEquals(atype, analyzer.getHierarchy(AnalyzerTest.A_TYPE, ""));
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test for analysis of depth 3 nodes tagged names.
     */
    @Test
    void testThreeDepthTags() {
        boolean oops = false;
        final ProgramParser parser = this.getSource();
        try {
            final Program program = parser.parse();
            final Analyzer analyzer = this.createAnalyzer(program);
            String list = "[{left, E, true}, {right, E, true}]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.A_TYPE, "").toString()
            );
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.F_TYPE, "").toString()
            );
            list = "[{right, E, false}, {left, E, false}]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.B_TYPE, "").toString()
            );
            list = "[]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.E_TYPE, "").toString()
            );
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test for analysis of complex tags, 1 set.
     */
    @Test
    void testComplexTagsOne() {
        boolean oops = false;
        try {
            final String source = this.readTest("complex_tags_1.txt");
            final ProgramParser parser = new ProgramParser(source);
            final Program program = parser.parse();
            final Analyzer analyzer = this.createAnalyzer(program);
            String list = "[]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.A_TYPE, "").toString()
            );
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.A_TYPE, AnalyzerTest.JAVA_LANGUAGE).toString()
            );
            list = "[{d, D, false}, {e, E, false}, {f, F, false}]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.B_TYPE, "").toString()
            );
            list = "[{d, D, false}, {e, E, false}, {g, G, false}, {h, H, false}]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.C_TYPE, "").toString()
            );
            list = "[{l, N, false}, {r, N, false}]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.I_TYPE, AnalyzerTest.JAVA_LANGUAGE).toString()
            );
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test for analysis of complex tags, 2 set.
     */
    @Test
    void testComplexTagsTwo() {
        boolean oops = false;
        try {
            final String source = this.readTest("complex_tags_2.txt");
            final ProgramParser parser = new ProgramParser(source);
            final Program program = parser.parse();
            final Analyzer analyzer = this.createAnalyzer(program);
            String list = "[]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.A_TYPE, "").toString()
            );
            list = "[{d, D, false}, {e, E, false}]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.Z_TYPE, "").toString()
            );
            list = "[{d, D, true}, {e, E, true}, {f, F, false}]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.B_TYPE, "").toString()
            );
            list = "[{d, D, true}, {e, E, true}, {g, G, false}, {h, H, false}]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.C_TYPE, "").toString()
            );
            list = "[{left, N, false}, {right, N, false}]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.I_TYPE, AnalyzerTest.JAVA_LANGUAGE).toString()
            );
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test for analysis of complex tags, 3 set.
     */
    @Test
    void testComplexTagsThree() {
        boolean oops = false;
        try {
            final String source = this.readTest("complex_tags_3.txt");
            final ProgramParser parser = new ProgramParser(source);
            final Program program = parser.parse();
            final Analyzer analyzer = this.createAnalyzer(program);
            String list = "[]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.A_TYPE, "").toString()
            );
            list = "[{d, D, true}, {f, F, false}]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.B_TYPE, "").toString()
            );
            list = "[{le, N, true}, {ri, N, true}]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.E_TYPE, AnalyzerTest.JAVA_LANGUAGE).toString()
            );
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.I_TYPE, AnalyzerTest.JAVA_LANGUAGE).toString()
            );
            list = "[{le, N, false}, {ri, N, false}]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.H_TYPE, AnalyzerTest.JAVA_LANGUAGE).toString()
            );
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test for analysis of complex tags, when there are several languages that extend a
     * green abstract node.
     */
    @Test
    void testComplexTagsOfSeveralLanguages() {
        boolean oops = false;
        try {
            final String source = this.readTest("complex_langs.txt");
            final ProgramParser parser = new ProgramParser(source);
            final Program program = parser.parse();
            final Analyzer analyzer = this.createAnalyzer(program);
            String list = "[]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.A_TYPE, "").toString()
            );
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.A_TYPE, AnalyzerTest.JAVA_LANGUAGE).toString()
            );
            list = "[{d, D, false}, {f, F, false}]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.B_TYPE, "").toString()
            );
            list = "[{d, D, false}, {h, H, false}]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.C_TYPE, "").toString()
            );
            list = "[{one, N, false}, {two, N, false}, {three, N, false}]";
            Assertions.assertEquals(
                list,
                analyzer.getTags(AnalyzerTest.I_TYPE, "py").toString()
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
    void testFourDepthHierarchy() throws BaseException {
        final Analyzer analyzer = this.getAnalyzer(AnalyzerTest.FOUR_SET);
        this.checkFourDepthHierarchy(analyzer);
    }

    /**
     * Test hierarchy of nodes in depth4_set_mixed.txt.
     */
    @Test
    void testFourDepthMixedHierarchy() throws BaseException {
        final Analyzer analyzer = this.getAnalyzer(AnalyzerTest.FOUR_MIX_SET);
        this.checkFourDepthHierarchy(analyzer);
    }

    /**
     * Test tagged names of nodes in depth4_set.txt.
     */
    @Test
    void testFourDepthTags() throws BaseException {
        final Analyzer analyzer = this.getAnalyzer(AnalyzerTest.FOUR_SET);
        this.checkFourDepthTags(analyzer);
    }

    /**
     * Test tagged names of nodes in depth4_set_mixed.txt.
     */
    @Test
    void testFourDepthMixedTags() throws BaseException {
        final Analyzer analyzer = this.getAnalyzer(AnalyzerTest.FOUR_MIX_SET);
        this.checkFourDepthTags(analyzer);
    }

    /**
     * Test analysis of DSL rules which contain duplicated rules for one node.
     */
    @Test
    void testDslWithDuplicatedRulesForNode() {
        boolean oops = false;
        try {
            final String source = this.readTest("duplicated_rule_set.txt");
            final ProgramParser parser = new ProgramParser(source);
            final Program program = parser.parse();
            final List<Instruction<Vertex>> vertices = program.getVertices();
            try {
                final VertexStorage storage = new VertexStorage(
                    vertices, program.getNamesOfAllLanguages()
                );
                storage.collectAndCheck();
                final Analyzer analyzer = new Analyzer(storage);
                analyzer.analyzeGreen();
                for (final String language : program.getNamesOfAllLanguages()) {
                    analyzer.analyze(language);
                }
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
    void testDslWithDuplicatedInheritance() {
        boolean oops = false;
        try {
            final String source = this.readTest("duplicated_inheritance_set.txt");
            final ProgramParser parser = new ProgramParser(source);
            final Program program = parser.parse();
            final List<Instruction<Vertex>> vertices = program.getVertices();
            try {
                final VertexStorage storage = new VertexStorage(
                    vertices, program.getNamesOfAllLanguages()
                );
                storage.collectAndCheck();
                final Analyzer analyzer = new Analyzer(storage);
                analyzer.analyzeGreen();
                for (final String language : program.getNamesOfAllLanguages()) {
                    analyzer.analyze(language);
                }
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
    void testFindingOneNodeToBeImported() {
        boolean oops = false;
        try {
            final String source = this.readTest("one_import_set.txt");
            final ProgramParser parser = new ProgramParser(source);
            final Program program = parser.parse();
            final Analyzer analyzer = this.createAnalyzer(program);
            final Set<String> imports = analyzer.getImports(
                AnalyzerTest.D_TYPE, AnalyzerTest.JAVA_LANGUAGE
            );
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
    void testFindingSeveralNodesToBeImported() {
        boolean oops = false;
        try {
            final String source = this.readTest("several_imports_set.txt");
            final ProgramParser parser = new ProgramParser(source);
            final Program program = parser.parse();
            final Analyzer analyzer = this.createAnalyzer(program);
            final Set<String> imports = analyzer.getImports(
                AnalyzerTest.D_TYPE, AnalyzerTest.JAVA_LANGUAGE
            );
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
    void testFindingNoNodesToBeImported() {
        boolean oops = false;
        try {
            final String source = this.readTest("no_imports_set.txt");
            final ProgramParser parser = new ProgramParser(source);
            final Program program = parser.parse();
            final Analyzer analyzer = this.createAnalyzer(program);
            final String expected = "[]";
            Set<String> imports = analyzer.getImports(
                AnalyzerTest.D_TYPE, AnalyzerTest.JAVA_LANGUAGE
            );
            Assertions.assertEquals(expected, imports.toString());
            imports = analyzer.getImports(
            AnalyzerTest.A_TYPE, ""
            );
            Assertions.assertEquals(expected, imports.toString());
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
    void testSetWithAbstractNodeExtension() {
        boolean oops = false;
        try {
            final String source = this.readTest("green_java_set.txt");
            final ProgramParser parser = new ProgramParser(source);
            final Program program = parser.parse();
            final Analyzer analyzer = this.createAnalyzer(program);
            final List<String> btype = Arrays.asList(
                AnalyzerTest.B_TYPE,
                AnalyzerTest.B_TYPE,
                AnalyzerTest.E_TYPE
            );
            Assertions.assertEquals(
                btype, analyzer.getHierarchy(AnalyzerTest.B_TYPE, AnalyzerTest.JAVA_LANGUAGE)
            );
            final List<String> dtype = Arrays.asList(
                AnalyzerTest.D_TYPE,
                AnalyzerTest.B_TYPE,
                AnalyzerTest.B_TYPE,
                AnalyzerTest.E_TYPE
            );
            Assertions.assertEquals(
                dtype, analyzer.getHierarchy(AnalyzerTest.D_TYPE, AnalyzerTest.JAVA_LANGUAGE)
            );
            final List<String> bgrtype = Arrays.asList(
                AnalyzerTest.B_TYPE,
                AnalyzerTest.E_TYPE
            );
            Assertions.assertEquals(bgrtype, analyzer.getHierarchy(AnalyzerTest.B_TYPE, ""));
        } catch (final BaseException ignored) {
            oops = false;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test analysis of DSL rules which contain different types of vertices.
     */
    @Test
    void testSetWithVariousVertices() {
        boolean oops = false;
        try {
            final String source = this.readTest("complex_set.txt");
            final ProgramParser parser = new ProgramParser(source);
            final Program program = parser.parse();
            final Analyzer analyzer = this.createAnalyzer(program);
            final List<String> hierarchy = Arrays.asList(
                AnalyzerTest.ADD_TYPE,
                "BinaryExpression",
                "Expression"
            );
            Assertions.assertEquals(
                hierarchy, analyzer.getHierarchy(AnalyzerTest.ADD_TYPE, "")
            );
            final String identifier = "Identifier";
            Assertions.assertEquals(
                Collections.singletonList(identifier), analyzer.getHierarchy(identifier, "")
            );
            final String list = "ExpressionList";
            Assertions.assertEquals(
                Collections.singletonList(list), analyzer.getHierarchy(list, "")
            );
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test exception that occurs if a green node that is extended in rules of another language
     * is not found.
     */
    @Test
    void testExtendedNodeNotFoundException() {
        boolean oops = false;
        Program program = null;
        try {
            final String source = this.readTest("extended_not_found.txt");
            final ProgramParser parser = new ProgramParser(source);
            program = parser.parse();
        } catch (final BaseException exception) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        Analyzer analyzer = null;
        Exception thrown = null;
        try {
            final List<Instruction<Vertex>> vertices = program.getVertices();
            final VertexStorage storage = new VertexStorage(
                vertices, program.getNamesOfAllLanguages()
            );
            storage.collectAndCheck();
            analyzer = new Analyzer(storage);
            analyzer.analyzeGreen();
            for (final String language : program.getNamesOfAllLanguages()) {
                analyzer.analyze(language);
            }
        } catch (final GeneratorException exception) {
            thrown = exception;
        }
        Assertions.assertNotNull(thrown);
        Assertions.assertTrue(thrown instanceof ExtendedNodeNotFound);
    }

    /**
     * Test case when there are several nodes in a specific language
     * that extend green abstract nodes.
     */
    @Test
    void testSeveralNodesExtendingGreenAbstract() {
        boolean oops = false;
        Program program = null;
        try {
            final String source = this.readTest("java_set_several_extensions.txt");
            final ProgramParser parser = new ProgramParser(source);
            program = parser.parse();
        } catch (final BaseException exception) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        Analyzer analyzer = null;
        try {
            final List<Instruction<Vertex>> vertices = program.getVertices();
            final VertexStorage storage = new VertexStorage(
                vertices, program.getNamesOfAllLanguages()
            );
            storage.collectAndCheck();
            analyzer = new Analyzer(storage);
            analyzer.analyzeGreen();
            for (final String language : program.getNamesOfAllLanguages()) {
                analyzer.analyze(language);
            }
        } catch (final GeneratorException exception) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test depth four hierarchy of nodes.
     * @param analyzer The analyzer
     */
    private void checkFourDepthHierarchy(final Analyzer analyzer) {
        final List<String> atype = Collections.singletonList(AnalyzerTest.A_TYPE);
        Assertions.assertEquals(atype, analyzer.getHierarchy(AnalyzerTest.A_TYPE, ""));
        final List<String> btype = Arrays.asList(
            AnalyzerTest.B_TYPE,
            AnalyzerTest.A_TYPE
        );
        Assertions.assertEquals(btype, analyzer.getHierarchy(AnalyzerTest.B_TYPE, ""));
        final List<String> ctype = Arrays.asList(
            AnalyzerTest.C_TYPE,
            AnalyzerTest.A_TYPE
        );
        Assertions.assertEquals(ctype, analyzer.getHierarchy(AnalyzerTest.C_TYPE, ""));
        final List<String> dtype = Arrays.asList(
            AnalyzerTest.D_TYPE,
            AnalyzerTest.E_TYPE,
            AnalyzerTest.B_TYPE,
            AnalyzerTest.A_TYPE
        );
        Assertions.assertEquals(dtype, analyzer.getHierarchy(AnalyzerTest.D_TYPE, ""));
        final List<String> etype = Arrays.asList(
            AnalyzerTest.E_TYPE,
            AnalyzerTest.B_TYPE,
            AnalyzerTest.A_TYPE
        );
        Assertions.assertEquals(etype, analyzer.getHierarchy(AnalyzerTest.E_TYPE, ""));
        final List<String> ftype = Arrays.asList(
            AnalyzerTest.F_TYPE,
            AnalyzerTest.B_TYPE,
            AnalyzerTest.A_TYPE
        );
        Assertions.assertEquals(ftype, analyzer.getHierarchy(AnalyzerTest.F_TYPE, ""));
        final List<String> gtype = Arrays.asList(
            AnalyzerTest.G_TYPE,
            AnalyzerTest.C_TYPE,
            AnalyzerTest.A_TYPE
        );
        Assertions.assertEquals(gtype, analyzer.getHierarchy(AnalyzerTest.G_TYPE, ""));
        final List<String> htype = Arrays.asList(
            AnalyzerTest.H_TYPE,
            AnalyzerTest.C_TYPE,
            AnalyzerTest.A_TYPE
        );
        Assertions.assertEquals(htype, analyzer.getHierarchy(AnalyzerTest.H_TYPE, ""));
    }

    /**
     * Test tagged names of nodes with depth four hierarchy.
     * @param analyzer The analyzer
     */
    private void checkFourDepthTags(final Analyzer analyzer) {
        String list = "[{a, A, false}]";
        Assertions.assertEquals(
            list,
            analyzer.getTags(AnalyzerTest.A_TYPE, "").toString()
        );
        list = "[{a, A, true}]";
        Assertions.assertEquals(
            list,
            analyzer.getTags(AnalyzerTest.C_TYPE, "").toString()
        );
        Assertions.assertEquals(
            list,
            analyzer.getTags(AnalyzerTest.H_TYPE, "").toString()
        );
        list = "[{a, A, true}, {e, E, true}]";
        Assertions.assertEquals(
            list,
            analyzer.getTags(AnalyzerTest.D_TYPE, "").toString()
        );
        Assertions.assertEquals(
            list,
            analyzer.getTags(AnalyzerTest.E_TYPE, "").toString()
        );
        Assertions.assertEquals(
            list,
            analyzer.getTags(AnalyzerTest.F_TYPE, "").toString()
        );
        list = "[{a, A, true}, {e, E, false}]";
        Assertions.assertEquals(
            list,
            analyzer.getTags(AnalyzerTest.B_TYPE, "").toString()
        );
        Assertions.assertEquals(
            list,
            analyzer.getTags(AnalyzerTest.G_TYPE, "").toString()
        );
    }

    /**
     * Analyzes the program and returns the result of the analyzer.
     * @param program The DSL program
     * @return The analyzer
     */
    private Analyzer createAnalyzer(final Program program) {
        boolean oops = false;
        Analyzer analyzer = null;
        try {
            final List<Instruction<Vertex>> vertices = program.getVertices();
            final VertexStorage storage = new VertexStorage(
                vertices, program.getNamesOfAllLanguages()
            );
            storage.collectAndCheck();
            analyzer = new Analyzer(storage);
            analyzer.analyzeGreen();
            for (final String language : program.getNamesOfAllLanguages()) {
                analyzer.analyze(language);
            }
        } catch (final GeneratorException exception) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return analyzer;
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
     * @param filename The name of the file with DSL code
     */
    private Analyzer getAnalyzer(final String filename) throws BaseException {
        final String source = this.readTest(filename);
        final ProgramParser parser = new ProgramParser(source);
        final Program program = parser.parse();
        return this.createAnalyzer(program);
    }

    /**
     * Get program parser of depth 3 nodes inheritance.
     */
    private ProgramParser getSource() {
        final String source = this.readTest("depth3_set.txt");
        return new ProgramParser(source);
    }
}
