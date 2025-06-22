package mrearsbig.cashcard;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CashCardRepository extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long> {
    // This interface extends CrudRepository and PagingAndSortingRepository
    // to provide basic CRUD operations and pagination/sorting capabilities.
}
