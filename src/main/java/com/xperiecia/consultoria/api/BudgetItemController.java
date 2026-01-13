package com.xperiecia.consultoria.api;

import com.xperiecia.consultoria.domain.BudgetItem;
import com.xperiecia.consultoria.domain.BudgetItemRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/budget-items")
@Tag(name = "Budget Items", description = "API para gestión de items de presupuesto")
public class BudgetItemController {

    @Autowired
    private BudgetItemRepository budgetItemRepository;

    @GetMapping
    @Operation(summary = "Obtener todos los items de presupuesto")
    public List<BudgetItem> getAllBudgetItems() {
        return budgetItemRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un item de presupuesto por ID")
    public ResponseEntity<BudgetItem> getBudgetItemById(@PathVariable Long id) {
        Optional<BudgetItem> budgetItem = budgetItemRepository.findById(id);
        return budgetItem.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/budget/{budgetId}")
    @Operation(summary = "Obtener items por presupuesto")
    public List<BudgetItem> getBudgetItemsByBudget(@PathVariable Long budgetId) {
        return budgetItemRepository.findByBudgetId(budgetId);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo item de presupuesto")
    public BudgetItem createBudgetItem(@RequestBody BudgetItem budgetItem) {
        return budgetItemRepository.save(budgetItem);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un item de presupuesto")
    public ResponseEntity<BudgetItem> updateBudgetItem(@PathVariable Long id, @RequestBody BudgetItem itemDetails) {
        Optional<BudgetItem> budgetItem = budgetItemRepository.findById(id);
        if (budgetItem.isPresent()) {
            BudgetItem updatedItem = budgetItem.get();
            updatedItem.setBudget(itemDetails.getBudget());
            updatedItem.setName(itemDetails.getName());
            updatedItem.setAmount(itemDetails.getAmount());

            return ResponseEntity.ok(budgetItemRepository.save(updatedItem));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un item de presupuesto")
    public ResponseEntity<Void> deleteBudgetItem(@PathVariable Long id) {
        Optional<BudgetItem> budgetItem = budgetItemRepository.findById(id);
        if (budgetItem.isPresent()) {
            budgetItemRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
