package dk.dtu.compute.course02324.mini_java;

import dk.dtu.compute.course02324.mini_java.model.*;
import dk.dtu.compute.course02324.mini_java.semantics.*;

import static dk.dtu.compute.course02324.mini_java.utils.Shortcuts.*;
import static dk.dtu.compute.course02324.mini_java.model.Operator.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * These are some basic tests of the MiniJava for computing the types and
 * evaluating expressions.
 */
public class TestMiniJava{

    private ProgramTypeVisitor ptv;

    private ProgramExecutorVisitor pev;

    /**
     *  Sets up the visitors for type checking and execution.
     */
    @BeforeEach
    public void setUp() {
        ptv = new ProgramTypeVisitor();
        pev = new ProgramExecutorVisitor(ptv);
    }

    @Test
    public void testCorrectProgramWithInts() {
        int i;
        int j = i = 2 + (i = 3) ;

        Statement statement = new Sequence(
                new Declaration(INT, new Var("i")),
                new Declaration(
                        INT,
                        new Var("j"),
                        new Assignment(
                                new Var("i"),
                                new OperatorExpression(
                                        PLUS2,
                                        new IntLiteral(2),
                                        new Assignment(
                                                new Var("i"),
                                                new IntLiteral(3)
                                        )))
                )
        );

        ptv.visit(statement);
        if (!ptv.problems.isEmpty()) {
            fail("The type visitor did detect typing problems, which should not be there!");
        }

        pev.visit(statement);

        Set<String> variables = new HashSet<>(List.of("i", "j"));
        for (Var var: ptv.variables) {
            variables.remove(var.name);

            switch (var.name) {
                case "i" -> assertEquals(i, pev.values.get(var), "Value of variable i should be " + i + ".");
                case "j" -> assertEquals(j, pev.values.get(var), "Value of variable j should be " + j + ".");
                default -> fail("A non-existing variable " + var.name + " occurred in evaluation of program.");
            }
        }
        assertEquals(0, variables.size(), "Some variables have not been evaluated");
    }

    @Test
    public void testCorrectlyTypedProgramWithFloats() {
        float i;
        float j = i = 2.75f - ( i = 3.21f );

        Statement statement =
                new Sequence(
                        new Declaration(FLOAT, new Var("i")),
                        new Declaration(
                                FLOAT,
                                new Var("j"),
                                new Assignment(
                                        new Var("i"),
                                        new OperatorExpression(
                                                MINUS2,
                                                new FloatLiteral(2.75f),
                                                new Assignment(
                                                        new Var("i"),
                                                        new FloatLiteral(3.21f)
                                                )
                                        )
                                )
                        )
                );

        ptv.visit(statement);
        if (!ptv.problems.isEmpty()) {
            fail("The type visitor did detect typing problems, which should not be there!");
        }
        pev.visit(statement);

        Set<String> variables = new HashSet<>(List.of("i", "j"));
        for (Var var: ptv.variables) {
            variables.remove(var.name);

            switch (var.name) {
                case "i" -> assertEquals(i, pev.values.get(var), "Value of variable i should be " + i + ".");
                case "j" -> assertEquals(j, pev.values.get(var), "Value of variable j should be " + j + ".");
                default -> fail("A non-existing variable " + var.name + " occurred in evaluation of program.");
            }
        }
        assertEquals(0, variables.size(), "Some variables have not been evaluated");

    }

    @Test
    public void testWronglyTypedProgram() {
        int i;
        int j = i = 2 + (i = 3) ;

        Statement statement =
                new Sequence(
                        new Declaration(FLOAT, new Var("i")),
                        new Declaration(INT, new Var("j")),
                        new Declaration(
                                FLOAT,
                                new Var("j"),
                                new Assignment(
                                        new Var("i"),
                                        new OperatorExpression(
                                                MINUS2,
                                                new FloatLiteral(2.75f),
                                                new Assignment(
                                                        new Var("i"),
                                                        new FloatLiteral(3.21f)
                                                )
                                        )
                                )
                        ),
                        Assignment(Var("i"), Var("k")),
                        Assignment(Var("k"), Literal(3))
                );

        ptv.visit(statement);

        if (ptv.problems.isEmpty()) {
            fail("No type problems detected in a mistyped statement!");
        }
    }

    @Test
    public void testLoopProgram() {
        int i = 5;
        int j = 0;
        int sum = 0;
        while ( i >= 0 ) {
            j = i;
            while ( j >= 0 ) {
                sum = sum + j;
                j = j - 1;
                // println(" i: ", i);
                // println(" j: ", j);
            };
            i = i - 1;
        };

        Statement statement = Sequence(
                Declaration(INT, Var("i"), Literal(5)),
                Declaration(INT, Var("sum"), Literal(0)),
                WhileLoop(
                        Var("i"),
                        Sequence(
                                Declaration(INT, Var("j"), Var("i")),
                                WhileLoop(
                                        Var("j"),
                                        Sequence(
                                                Assignment(
                                                        Var("sum"),
                                                        OperatorExpression(PLUS2,
                                                                Var("sum"),
                                                                Var("j")
                                                        )
                                                ),
                                                Assignment(
                                                        Var("j"),
                                                        OperatorExpression(MINUS2,
                                                                Var("j"),
                                                                Literal(1)
                                                        )
                                                ),
                                                PrintStatement(" i: ", Var("i")),
                                                PrintStatement(" j: ", Var("j"))
                                        )
                                ),
                                Assignment(
                                        Var("i"),
                                        OperatorExpression(MINUS2,
                                                Var("i"),
                                                Literal(1)
                                        )
                                )
                        )
                )
        );

        ptv.visit(statement);
        if (!ptv.problems.isEmpty()) {
            fail("The type visitor did detect typing problems, which should not be there!");
        }
        pev.visit(statement);

        Set<String> variables = new HashSet<>(List.of("i", "j", "sum"));
        for (Var var: ptv.variables) {
            variables.remove(var.name);

            switch (var.name) {
                case "i" -> assertEquals(i, pev.values.get(var), "Value of variable i should be " + i + ".");
                case "j" -> assertEquals(j, pev.values.get(var), "Value of variable j should be " + j + ".");
                case "sum" -> assertEquals(sum, pev.values.get(var), "Value of variable sum should be " + sum + ".");
                default -> fail("A non-existing variable " + var.name + " occurred in evaluation of program.");
            }
        }
        assertEquals(0, variables.size(), "Some variables have not been evaluated");
    }

    @Test
    public void testPrintAndAdditionalOperators() {
        int i = - + -1 + 7 - 1;
        float x = - + -1.5f + 7.0f - 1.0f;
        int j = 36 % 7;
        int k = 36 / 7;
        float y = 36.0f / 7.0f;

        Sequence printStatements = Sequence(
                Declaration(INT,
                        Var("i"),
                        OperatorExpression(MINUS2,
                                OperatorExpression(PLUS2,
                                        OperatorExpression(MINUS1,
                                                OperatorExpression(PLUS1,
                                                        Literal(-1)
                                                )
                                        ),
                                        Literal(7)
                                ),
                                Literal(1)
                        )
                ),
                PrintStatement(" - + -1 + 7 - 1: ",
                        Var("i")
                ),
                Declaration(FLOAT,
                        Var("x"),
                        OperatorExpression(MINUS2,
                                OperatorExpression(PLUS2,
                                        OperatorExpression(MINUS1,
                                                OperatorExpression(PLUS1,
                                                        Literal(-1.5f)
                                                )
                                        ),
                                        Literal(7.0f)
                                ),
                                Literal(1.0f)
                        )
                ),
                PrintStatement(" - + -1.5f + 7.0f - 1.0f: ",
                        Var("x")
                ),
                Declaration(INT,
                        Var("j"),
                        OperatorExpression(MOD,
                                Literal(36),
                                Literal(7)
                        )
                ),
                PrintStatement("36 % 7: ",
                        Var("j")
                ),
                Declaration(INT,
                        Var("k"),
                        OperatorExpression(DIV,
                                Literal(36),
                                Literal(7)
                        )
                ),
                PrintStatement("36 / 7: ",
                        Var("k")
                ),
                Declaration(FLOAT,
                        Var("y"),
                        OperatorExpression(DIV,
                                Literal(36.0f),
                                Literal(7.0f)
                        )
                ),
                PrintStatement("36.0f / 7.0: ",
                        Var("y")
                )
        );


        ptv.visit(printStatements);
        if (!ptv.problems.isEmpty()) {
            fail("The type visitor did detect typing problems, which should not be there!");
        }

        pev.visit(printStatements);

        Set<String> variables = new HashSet<>(List.of("i", "x", "j", "k", "y"));
        for (Var var: ptv.variables) {
            variables.remove(var.name);

            switch (var.name) {
                case "i" -> assertEquals(i, pev.values.get(var), "Value of variable i should be " + i + ".");
                case "x" -> assertEquals(x, pev.values.get(var), "Value of variable j should be " + x + ".");
                case "j" -> assertEquals(j, pev.values.get(var), "Value of variable j should be " + j + ".");
                case "k" -> assertEquals(k, pev.values.get(var), "Value of variable j should be " + k + ".");
                case "y" -> assertEquals(y, pev.values.get(var), "Value of variable j should be " + y + ".");
                default -> fail("A non-existing variable " + var.name + " occurred in evaluation of program.");
            }
        }
        assertEquals(0, variables.size(), "Some variables have not been evaluated");
    }

    @Test
    public void testPrintAndMoreOperators() {
        int a = 5 * 3;
        float b = 5.0f * 3.0f;
        float c = 36.5f % 7.2f;

        Sequence printStatements = Sequence(
                Declaration(INT,
                        Var("a"),
                        OperatorExpression(MULT,
                                Literal(5),
                                Literal(3)
                        )
                ),
                PrintStatement("5 * 3: ", Var("a")),

                Declaration(FLOAT,
                        Var("b"),
                        OperatorExpression(MULT,
                                Literal(5.0f),
                                Literal(3.0f)
                        )
                ),
                PrintStatement("5.0f * 3.0f: ", Var("b")),

                Declaration(FLOAT,
                        Var("c"),
                        OperatorExpression(MOD,
                                Literal(36.5f),
                                Literal(7.2f)
                        )
                ),
                PrintStatement("36.5f % 7.2f: ", Var("c"))
        );

        ptv.visit(printStatements);
        if (!ptv.problems.isEmpty()) {
            fail("The type visitor detected typing problems, which should not be there!");
        }

        pev.visit(printStatements);

        Set<String> variables = new HashSet<>(List.of("a", "b", "c"));
        for (Var var : ptv.variables) {
            variables.remove(var.name);

            switch (var.name) {
                case "a" -> assertEquals(a, pev.values.get(var), "Value of variable a should be " + a + ".");
                case "b" -> assertEquals(b, pev.values.get(var), "Value of variable b should be " + b + ".");
                case "c" -> assertEquals(c, pev.values.get(var), "Value of variable c should be " + c + ".");
                default -> fail("A non-existing variable " + var.name + " occurred in evaluation of program.");
            }
        }
        assertEquals(0, variables.size(), "Some variables have not been evaluated");
    }

    @Test
    public void testPrintAndConditionalStatement() {
        int a = 1;
        int b = -1;
        int c = 0;

        Sequence printStatements = Sequence(
                Declaration(INT,
                        Var("a"),
                        Literal(1)),
                new ConditionalStatement(Literal(1),
                                PrintStatement("This should print: ", Var("a")),
                                PrintStatement("This should not print: ", Var("a"))
                ),

                Declaration(INT,
                        Var("b"),
                        Literal(-1)),
                new ConditionalStatement(Var("b"),
                        PrintStatement("This should not print: ", Var("b")),
                        PrintStatement("This should print: ", Var("b"))
                ),

                Declaration(INT,
                        Var("c"),
                        Literal(0)),
                new ConditionalStatement(Var("c"),
                        PrintStatement("This should print: ", Var("c")),
                        PrintStatement("This should not print: ", Var("c"))
                )
        );

        ptv.visit(printStatements);
        if (!ptv.problems.isEmpty()) {
            fail("The type visitor detected typing problems, which should not be there!");
        }

        pev.visit(printStatements);

        Set<String> variables = new HashSet<>(List.of("a", "b", "c"));
        for (Var var : ptv.variables) {
            variables.remove(var.name);

            switch (var.name) {
                case "a" -> assertEquals(a, pev.values.get(var), "Value of variable a should be " + a + ".");
                case "b" -> assertEquals(b, pev.values.get(var), "Value of variable b should be " + b + ".");
                case "c" -> assertEquals(c, pev.values.get(var), "Value of variable c should be " + c + ".");
                default -> fail("A non-existing variable " + var.name + " occurred in evaluation of program.");
            }
        }
        assertEquals(0, variables.size(), "Some variables have not been evaluated");

    }


}
