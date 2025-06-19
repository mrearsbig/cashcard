package mrearsbig.cashcard;

import org.springframework.data.repository.CrudRepository;

public interface CashCardRepository extends CrudRepository<CashCard, Long> {
    // This interface extends CrudRepository, which provides basic CRUD operations.
    // Additional query methods can be defined here if needed.
}
