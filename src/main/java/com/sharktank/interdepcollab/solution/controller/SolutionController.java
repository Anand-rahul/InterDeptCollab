// package com.sharktank.interdepcollab.solution.controller;

// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import com.sharktank.interdepcollab.solution.model.Solution;
// import com.sharktank.interdepcollab.solution.service.SolutionService;

// @RestController
// @RequestMapping("/solution")
// public class SolutionController {

//     @Autowired
//     private SolutionService solutionService;

//     @PostMapping
//     public ResponseEntity<Solution> createSolution(@RequestBody Solution solution) {
//         Solution createdSolution = solutionService.createSolution(solution);
//         return ResponseEntity.ok(createdSolution);
//     }

//     @PutMapping("/{id}")
//     public ResponseEntity<Solution> updateSolution(@PathVariable Long id, @RequestBody Solution solution) {
//         Solution updatedSolution = solutionService.updateSolution(id, solution);
//         return ResponseEntity.ok(updatedSolution);
//     }

//     @GetMapping
//     public ResponseEntity<Page<Solution>> getAllSolutions(Pageable pageable) {
//         Page<Solution> solutions = solutionService.getAllSolutions(pageable);
//         return ResponseEntity.ok(solutions);
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<Solution> getSolution(@PathVariable Long id) {
//         Solution solution = solutionService.getSolution(id);
//         return ResponseEntity.ok(solution);
//     }

//     @PostMapping("/{id}/like")
//     public ResponseEntity<Solution> likeSolution(@PathVariable Long id) {
//         Solution likedSolution = solutionService.likeSolution(id);
//         return ResponseEntity.ok(likedSolution);
//     }

//     @PostMapping("/{id}/undo-like")
//     public ResponseEntity<Solution> undoLikeSolution(@PathVariable Long id) {
//         Solution unlikedSolution = solutionService.undoLikeSolution(id);
//         return ResponseEntity.ok(unlikedSolution);
//     }
// }
