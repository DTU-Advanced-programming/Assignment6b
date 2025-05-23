package dk.dtu.compute.course02324.mini_java;

import dk.dtu.compute.course02324.mini_java.model.*;
import dk.dtu.compute.course02324.mini_java.semantics.*;

import static dk.dtu.compute.course02324.mini_java.utils.Shortcuts.*;
import static dk.dtu.compute.course02324.mini_java.model.Operator.*;

public class MiniJavaRun {

    public static void printTypeEvaluate(Statement statement) {
        System.out.println("Result by our program:");

        ProgramSerializerVisitor ps = new ProgramSerializerVisitor();
        ps.visit(statement);
        System.out.println(ps.result());

        ProgramTypeVisitor pv = new ProgramTypeVisitor();
        pv.visit(statement);

        if (!pv.problems.isEmpty()) {
            System.out.println("There were some problems in the statement:");
            for (String problem : pv.problems) {
                System.out.println(problem);
            }
        } else {
            System.out.println("Expression type checks correctly. Variable types:");
            for (Var var: pv.variables) {
                System.out.println(pv.typeMapping.get(var).getName() + " " + var.name + ";");
            }

            System.out.println("Program  starting. Output:");

            ProgramExecutorVisitor pev = new ProgramExecutorVisitor(pv);
            pev.visit(statement);

            System.out.println("Program finished!");

            System.out.println("Values of variables in the end");

            for (Var var: pv.variables) {
                System.out.println(var.name + " = " + pev.values.get(var));
            }
        }
        System.out.println("------------------------------");
        System.out.println();
    }

    public static void main(String... args) {

        System.out.println("Result provided by Java");
        int i;
        int j = i = 2 + (i = 3);
        System.out.println("i = " + i);
        System.out.println("j = " + j);
        System.out.println();

        Statement statement0 = Sequence(
                Declaration(INT, Var("i")),
                Declaration(
                        INT,
                        Var("j"),
                        Assignment(
                                Var("i"),
                                OperatorExpression(
                                        PLUS2,
                                        Literal(2),
                                        Assignment(
                                                Var("i"),
                                                Literal(3)
                                        )
                                )
                        )
                )
        );

        printTypeEvaluate(statement0);

        System.out.println("Java evaluates - + -1 + 7 - 1 to " + (- + -1 + 7 - 1));

        Sequence printStatements = Sequence(
            PrintStatement(" - + -1 + 7 - 1: ",
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
            )
        );

        printTypeEvaluate(printStatements);

        Statement whileLoops =  Sequence(
            Declaration(INT, Var("i"), Literal(5)),
            WhileLoop(
                Var("i"),
                Sequence(
                    Declaration(INT, Var("j"), Var("i")),
                    WhileLoop(
                        Var("j"),
                        Sequence(
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

        printTypeEvaluate(whileLoops);


        System.out.println("Result provided by Java");
        int i2;
        int j2 = i2 = 2 + (i2 = 3);
        j2 = 2 * j2;
        System.out.println("i = " + i2);
        System.out.println("j = " + j2);
        System.out.println();

        Statement statement = Sequence(
                Declaration(INT, Var("i")),
                Declaration(
                        INT,
                        Var("j"),
                        Assignment(
                                Var("i"),
                                OperatorExpression(
                                        PLUS2,
                                        Literal(2),
                                        Assignment(
                                                Var("i"),
                                                Literal(3)
                                        )
                                )
                        )
                ),
                Assignment(
                        Var("j"),
                        OperatorExpression(
                                MULT,
                                Literal(2),
                                Var("j")
                        )
                ),
                PrintStatement("result i = ", Var("i")),
                PrintStatement("result j = ", Var("j"))
        );

        printTypeEvaluate(statement);


        System.out.println("Result provided by Java");
        float ii;
        float jj = ii = 2.75f - ( ii = 3.21f );
        System.out.println("i = " + ii);
        System.out.println("j = " + jj);
        System.out.println();

        // Note that from here we do not make (much) use of the Shorthands.
        // See what difference it makes.
        Statement statement2 =
                new Sequence(
                        new Declaration(FLOAT, new Var("i")),
                        new Declaration(
                                FLOAT,
                                new Var("j"),
                                new Assignment(
                                        new Var("i"),
                                        new OperatorExpression(
                                                Operator.MINUS2,
                                                new FloatLiteral(2.75f),
                                                new Assignment(
                                                        new Var("i"),
                                                        Literal(3.21f)
                                                )
                                        )
                                )
                        )
                );

        printTypeEvaluate(statement2);

        System.out.println("Result provided by Java");
        float iii;
        float jjj = iii = 2.75f + ( iii = 3.21f ) - 2.1f * iii;
        System.out.println("i = " + iii);
        System.out.println("j = " + jjj);
        System.out.println();

        Statement statement3 = new Sequence(
                new Declaration(FLOAT, new Var("i")),
                new Declaration(
                        FLOAT,
                        new Var("j"),
                        new Assignment(
                                new Var("i"),
                                new OperatorExpression(
                                        MINUS2,
                                        new OperatorExpression(Operator.PLUS2,
                                                new FloatLiteral(2.75f),
                                                new Assignment(
                                                        new Var("i"),
                                                        new FloatLiteral(3.21f)
                                                )
                                        ),
                                        new OperatorExpression(
                                                MULT,
                                                new FloatLiteral(2.1f),
                                                new Var("i")
                                        )
                                )
                        ))

        );

        printTypeEvaluate(statement3);

        System.out.println("Some incorrect programs");

        Statement statement4 =
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
                        )
                );

        printTypeEvaluate(statement4);

        Statement statement5 = new Sequence(
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
                                                new FloatLiteral(3)
                                        )))
                )
        );

        printTypeEvaluate(statement5);

        Statement ifStatement1 =  Sequence(
                Declaration(INT, Var("i"), Literal(5)),
                new ConditionalStatement(
                        Var("i"),
                        new Sequence (
                            PrintStatement("in an if statement, i value is: ", Var("i"))
                        ),
                        new Sequence (
                                PrintStatement("in an else statement, i value is: ", Var("i"))
                        )
                )
        );

        printTypeEvaluate(ifStatement1);

        Statement elseStatement =  Sequence(
                Declaration(INT, Var("i"), Literal(-2)),
                new ConditionalStatement(
                        Var("i"),
                        new Sequence (
                                PrintStatement("in an if statement, i value is: ", Var("i"))
                        ),
                        new Sequence (
                                PrintStatement("in an else statement, i value is: ", Var("i"))
                        )
                )
        );


        printTypeEvaluate(elseStatement);

        System.out.println("And now some syntactically wrong examples (crashing) when building statement!");

        Statement statement6 = new Sequence(
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
                                                new FloatLiteral(3)),
                                        new FloatLiteral(3.5f)
                                        ))
                )
        );
    }

}
