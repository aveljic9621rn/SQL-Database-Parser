package queryparser.visitorInterface;

import queryparser.statementimplementation.NullValue;
import queryparser.statementimplementation.NumberValue;
import queryparser.statementimplementation.StringValue;

public interface ExpressionVisitor {
    void visit(NullValue var1);

    void visit(StringValue var1);

    void visit(NumberValue var1);
    /*

    void visit(Function var1);

    void visit(SignedExpression var1);

    void visit(JdbcParameter var1);

    void visit(JdbcNamedParameter var1);

    void visit(DoubleValue var1);

    void visit(LongValue var1);

    void visit(HexValue var1);

    void visit(DateValue var1);

    void visit(TimeValue var1);

    void visit(TimestampValue var1);

    void visit(Parenthesis var1);


    void visit(Addition var1);

    void visit(Division var1);

    void visit(Multiplication var1);

    void visit(Subtraction var1);

    void visit(AndExpression var1);

    void visit(OrExpression var1);

    void visit(Between var1);

    void visit(EqualsTo var1);

    void visit(GreaterThan var1);

    void visit(GreaterThanEquals var1);

    void visit(InExpression var1);

    void visit(IsNullExpression var1);

    void visit(LikeExpression var1);

    void visit(MinorThan var1);

    void visit(MinorThanEquals var1);

    void visit(NotEqualsTo var1);

    void visit(Column var1);

    void visit(SubSelect var1);

    void visit(CaseExpression var1);

    void visit(WhenClause var1);

    void visit(ExistsExpression var1);

    void visit(AllComparisonExpression var1);

    void visit(AnyComparisonExpression var1);

    void visit(Concat var1);

    void visit(Matches var1);

    void visit(BitwiseAnd var1);

    void visit(BitwiseOr var1);

    void visit(BitwiseXor var1);

    void visit(CastExpression var1);

    void visit(Modulo var1);

    void visit(AnalyticExpression var1);

    void visit(WithinGroupExpression var1);

    void visit(ExtractExpression var1);

    void visit(IntervalExpression var1);

    void visit(OracleHierarchicalExpression var1);

    void visit(RegExpMatchOperator var1);

    void visit(JsonExpression var1);

    void visit(JsonOperator var1);

    void visit(RegExpMySQLOperator var1);

    void visit(UserVariable var1);

    void visit(NumericBind var1);

    void visit(KeepExpression var1);

    void visit(MySQLGroupConcat var1);

    void visit(RowConstructor var1);

    void visit(OracleHint var1);

    void visit(TimeKeyExpression var1);

    void visit(DateTimeLiteralExpression var1);

    void visit(NotExpression var1);*/
}
