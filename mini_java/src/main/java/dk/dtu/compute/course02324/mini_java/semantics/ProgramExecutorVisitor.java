package dk.dtu.compute.course02324.mini_java.semantics;

import dk.dtu.compute.course02324.mini_java.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static dk.dtu.compute.course02324.mini_java.model.Operator.*;
import static dk.dtu.compute.course02324.mini_java.utils.Shortcuts.FLOAT;
import static dk.dtu.compute.course02324.mini_java.utils.Shortcuts.INT;
import static java.util.Map.entry;

public class ProgramExecutorVisitor extends ProgramVisitor {

    final private ProgramTypeVisitor pv;

    final public Map<Expression, Number> values = new HashMap<>();

    private Function<List<Number>, Number> plus1int =
            args -> { int arg = args.get(0).intValue();
                return +arg; };
    private Function<List<Number>,Number> plus1float =
            args -> { float arg = args.get(0).floatValue();
                return +arg; };

    private Function<List<Number>,Number> plus2int =
            args -> { int arg1 = args.get(0).intValue();
                int arg2 = args.get(1).intValue();
                return arg1 + arg2; };
    private Function<List<Number>,Number> plus2float =
            args -> { float arg1 = args.get(0).floatValue();
                float arg2 = args.get(1).floatValue();
                return arg1 + arg2; };

    private Function<List<Number>, Number> minus1int =
            args -> { int arg = args.get(0).intValue();
                return -arg; };
    private Function<List<Number>,Number> minus1float =
            args -> { float arg = args.get(0).floatValue();
                return -arg; };

    private Function<List<Number>,Number> minus2int =
            args -> { int arg1 = args.get(0).intValue();
                int arg2 = args.get(1).intValue();
                return arg1 - arg2; };
    private Function<List<Number>,Number> minus2float =
            args -> { float arg1 = args.get(0).floatValue();
                float arg2 = args.get(1).floatValue();
                return arg1 - arg2; };

    private Function<List<Number>,Number> multint =
            args -> { int arg1 = args.get(0).intValue();
                int arg2 = args.get(1).intValue();
                return arg1 * arg2; };
    private Function<List<Number>,Number> multfloat =
            args -> { float arg1 = args.get(0).floatValue();
                float arg2 = args.get(1).floatValue();
                return arg1 * arg2; };

    private Function<List<Number>,Number> divint =
            args -> { int arg1 = args.get(0).intValue();
                int arg2 = args.get(1).intValue();
                return arg1 / arg2; };
    private Function<List<Number>,Number> divfloat =
            args -> { float arg1 = args.get(0).floatValue();
                float arg2 = args.get(1).floatValue();
                return arg1 / arg2; };

    private Function<List<Number>,Number> modint =
            args -> { int arg1 = args.get(0).intValue();
                int arg2 = args.get(1).intValue();
                return arg1 % arg2; };
    private Function<List<Number>,Number> modfloat =
            args -> { float arg1 = args.get(0).floatValue();
                float arg2 = args.get(1).floatValue();
                return arg1 % arg2; };

    /**
     * The map below associates each operator for each possible type with a function
     * (lambda expression), that represents the semantics of that operation. These
     * define what happens when the operator needs to be executed.<p>
     *
     * TODO Assignment 6a: This map and the functions above need to be extended in Assignment 6a
     *      (all operations with the respective types required in assignment must be defined above
     *      and added to the mapping below).
     */
    final private Map<Operator, Map<Type, Function<List<Number>,Number>>> operatorFunctions = Map.ofEntries(
            entry (PLUS1, Map.ofEntries(
                    entry(INT, plus1int ),
                    entry(FLOAT, plus1float ) )
            ),
            entry(PLUS2, Map.ofEntries(
                    entry(INT, plus2int ),
                    entry(FLOAT, plus2float ) )
            ),
            entry (MINUS1, Map.ofEntries(
                    entry(INT, minus1int ),
                    entry(FLOAT, minus1float ) )
            ),
            entry(MINUS2, Map.ofEntries(
                    entry(INT, minus2int ),
                    entry(FLOAT, minus2float ) )
            ),
            entry(MULT, Map.ofEntries(
                    entry(INT, multint ),
                    entry(FLOAT, multfloat ) )
            ),
            entry(DIV, Map.ofEntries(
                    entry(INT, divint ),
                    entry(FLOAT, divfloat ) )
            ),
            entry(MOD, Map.ofEntries(
                    entry(INT, modint ),
                    entry(FLOAT, modfloat ) )
            ));

    public ProgramExecutorVisitor(ProgramTypeVisitor pv) {
        this.pv = pv;
    }

    public void visit(Statement statement) {
        statement.accept(this);
    }

    @Override
    public void visit(Sequence sequence) {
        for (Statement substatement: sequence.statements) {
            visit(substatement);
        }
    }

    @Override
    public void visit(Declaration declaration) {
        if (declaration.expression != null) {
            declaration.expression.accept(this);
            Number result = values.get(declaration.expression);
            values.put(declaration.variable, result);
        }
    }

    @Override
    public void visit(PrintStatement printStatement) {
        printStatement.expression.accept(this);

        System.out.println(printStatement.prefix+values.get(printStatement.expression));

        /* TODO Assignment 6a: Here some code which actually executes the
                print operation must be added. It should actually print out the
                prefix of the print statement and then the CURRENT value of the
                expression.
         */

    }

    @Override
    public void visit(WhileLoop whileLoop) {
        whileLoop.expression.accept(this);

        while ((int) values.get(whileLoop.expression) >= 0) {
            whileLoop.statement.accept(this);
            whileLoop.expression.accept(this);
        }

    }

    @Override
    public void visit(ConditionalStatement conditionalStatement) {
        conditionalStatement.expression.accept(this);

        if ((int) values.get(conditionalStatement.expression) >= 0) {
            conditionalStatement.if_statement.accept(this);
        } else {
            conditionalStatement.else_statement.accept(this);
        }

    }

    @Override
    public void visit(Assignment assignment) {
        assignment.expression.accept(this);
        Number result = values.get(assignment.expression);
        values.put(assignment, result);
        values.put(assignment.variable, result);
    }

    @Override
    public void visit(Literal literal) {
        if (literal instanceof IntLiteral) {
            values.put(literal, ((IntLiteral) literal).literal);
        }  else if (literal instanceof FloatLiteral) {
            values.put(literal, ((FloatLiteral) literal).literal);
        }
    }

    @Override
    public void visit(Var var) {
        // We do not need to do anything here; if the variable was assigned a
        // value already by an assignment or a declaration, this value will be
        // in the values map already (the respective assignment or declaration
        // should have added this value for variable already).
    }

    @Override
    public void visit(OperatorExpression operatorExpression) {
        Type type = pv.typeMapping.get(operatorExpression);
        Map<Type,Function<List<Number>,Number>> typeMap = operatorFunctions.get(operatorExpression.operator);

        // Function<List<Number>,Number> function = typeMap != null && type!= null ? typeMap.get(type) : null;
        Function<List<Number>,Number> function = null;
        if (typeMap != null && type!= null ) {
            function = typeMap.get(type);
        }

        if (function == null) {
            throw new RuntimeException("No function of this type available");
        }

        List<Number> args = new ArrayList<>();
        for (Expression subexpression: operatorExpression.operands ) {
            subexpression.accept(this);
            Number arg = values.get(subexpression);
            if (arg == null) {
                throw new RuntimeException("Value of subexpression does not exist");
            }
            args.add(arg);
        }

        Number result = function.apply(args);
        values.put(operatorExpression, result);
    }

}
