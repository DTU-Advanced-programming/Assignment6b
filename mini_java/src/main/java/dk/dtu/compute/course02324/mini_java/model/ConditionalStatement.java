package dk.dtu.compute.course02324.mini_java.model;

import dk.dtu.compute.course02324.mini_java.semantics.ProgramVisitor;
import org.jetbrains.annotations.NotNull;

public class ConditionalStatement implements Statement {

    final public Expression expression;

    final public Statement if_statement;

    final public Statement else_statement;

    public ConditionalStatement(@NotNull Expression expression,@NotNull Statement if_statement, Statement else_statement) {
        this.expression = expression;
        this.if_statement = if_statement;
        this.else_statement = else_statement;
    }

    @Override
    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

}
