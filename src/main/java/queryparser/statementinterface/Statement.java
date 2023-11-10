package queryparser.statementinterface;

import queryparser.visitorInterface.StatementVisitor;

public interface Statement {
    void accept(StatementVisitor statementVisitor);
}
