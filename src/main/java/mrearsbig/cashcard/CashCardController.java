package mrearsbig.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cashcard")
public class CashCardController {
    
    @GetMapping("/{id}")
    private ResponseEntity<CashCard> findById(@PathVariable Long id) {
        CashCard cashCard = new CashCard(1L, 123.45);

        if (id != 1L) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cashCard);
    }
}
