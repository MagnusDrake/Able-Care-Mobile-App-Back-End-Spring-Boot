package com.example.demo.Controller;

import com.example.demo.Entity.PatientCase;
import com.example.demo.Entity.TherapyType;
import com.example.demo.Service.PatientCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allows your local Angular app to make requests here
public class PatientCaseController {

    private final PatientCaseService patientCaseService;

    // 1. Admin/Assistant Endpoint: Broadcast a new case
    @PostMapping("/broadcast")
    public ResponseEntity<PatientCase> broadcastCase(@RequestBody PatientCase newCase) {
        PatientCase createdCase = patientCaseService.broadcastNewCase(newCase);
        return new ResponseEntity<>(createdCase, HttpStatus.CREATED);
    }

    // 2. Therapist Endpoint: Claim a case
    // In a real app, the therapistId would come from the security token.
    // We are passing it as a parameter here for prototyping.
    @PostMapping("/{caseId}/claim")
    public ResponseEntity<PatientCase> claimCase(
            @PathVariable Long caseId,
            @RequestParam Long therapistId) {

        PatientCase claimedCase = patientCaseService.claimCase(caseId, therapistId);
        return ResponseEntity.ok(claimedCase);
    }

    // Available Cases in a given zip code
    // Add the @RequestParam for specialty
    @GetMapping("/available")
    public ResponseEntity<List<PatientCase>> getAvailableCases(
            @RequestParam String zipCode,
            @RequestParam TherapyType specialty) {
        return ResponseEntity.ok(patientCaseService.getAvailableCases(zipCode, specialty));
    }

    // Get Cases for a Therapist
    @GetMapping("/therapist/{therapistId}")
    public ResponseEntity<List<PatientCase>> getTherapistCases(@PathVariable Long therapistId) {
        return ResponseEntity.ok(patientCaseService.getCasesForTherapist(therapistId));
    }

    // --- Exception Handling for Double-Booking ---
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<String> handleOptimisticLockingFailure() {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Sorry, this case was just claimed by another therapist!");
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    // Admin/Assistant can view all cases
    @GetMapping("/all")
    public ResponseEntity<List<PatientCase>> getAllCases() {
        return ResponseEntity.ok(patientCaseService.getAllCases());
    }

    // Admin/Assistant can update case details
    @PutMapping("/{id}")
    public ResponseEntity<PatientCase> updateCase(@PathVariable Long id, @RequestBody PatientCase caseDetails) {
        return ResponseEntity.ok(patientCaseService.updateCase(id, caseDetails));
    }
}