package mrearsbig.cashcard;

public record CashCard(Long id, Double amount) {

    // This is a record class that represents a CashCard with an ID and an amount.
    // It can be used to serialize and deserialize JSON representations of cash cards.
    // The record automatically provides equals, hashCode, and toString methods.
    
}
