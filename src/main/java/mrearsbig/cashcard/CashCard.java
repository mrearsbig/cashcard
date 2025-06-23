package mrearsbig.cashcard;

import org.springframework.data.annotation.Id;

public record CashCard(@Id Long id, Double amount, String owner) {

    // This is a record class that represents a CashCard with an ID and an amount.
    // It can be used to serialize and deserialize JSON representations of cash cards.
    // The record automatically provides equals, hashCode, and toString methods.
    
}
