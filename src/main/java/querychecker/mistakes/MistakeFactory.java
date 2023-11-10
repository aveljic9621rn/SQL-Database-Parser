package querychecker.mistakes;

import observer.MyPublisher;

public class MistakeFactory extends MyPublisher {
    public Mistake createMistake(Mistakes mistakeCode, String name, String description, String recommendation) {
        return switch (mistakeCode) {
            case ALIAS_NO_QUOTES -> new AliasNoQuotesMistake(name, description, recommendation);
            case AGGREGATION_IN_WHERE -> new AggregationInWhereMistake(name, description, recommendation);
            case AGGREGATION_NO_GROUP_BY -> new AggregationNoGroupByMistake(name, description, recommendation);
            case INSERT_WRONG_COLUMNS -> new InsertWrongColumnsMistake(name, description, recommendation);
            case INVALID_ARGUMENT_TYPE -> new InvalidArgumentTypeMistake(name, description, recommendation);
            case NON_FOREIGN_KEY_MERGE -> new NonForeignKeyMergeMistake(name, description, recommendation);
            case PART_OF_QUERY_MISSING -> new PartOfQueryMissingMistake(name, description, recommendation);
            case STATEMENT_ORDER_WRONG -> new StatementOrderWrongMistake(name, description, recommendation);
            case TABLE_OR_COLUMN_DOES_NOT_EXIST -> new TableOrColumnDoesNotExistMistake(name, description, recommendation);
            case UNUSED_VARIABLE -> new UnusedVariableMistake(name, description, recommendation);
            default -> throw new IllegalStateException("Unexpected value: " + mistakeCode);
        };
    }
}
