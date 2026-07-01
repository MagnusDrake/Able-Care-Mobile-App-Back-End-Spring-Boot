package com.example.demo.Service;

import com.example.demo.Entity.CaseStatus;
import com.example.demo.Entity.PatientCase;
import com.example.demo.Entity.TherapyType;
import com.example.demo.Entity.User;
import com.example.demo.Repository.PatientCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientCaseService {

    private final PatientCaseRepository patientCaseRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate; // <-- Inject the messenger

    // 1. The Admin/Assistant broadcasts a new case
    public PatientCase broadcastNewCase(PatientCase newCase) {
        newCase.setStatus(CaseStatus.BROADCASTED);

        // Save the case to the database
        PatientCase savedCase = patientCaseRepository.save(newCase);

        // TODO: (DONE)Trigger WebSocket notification to therapists in the zip code here
        // Push the new case to a specific topic for that zip code
        // e.g., "/topic/cases/80918"
        messagingTemplate.convertAndSend("/topic/cases/" + savedCase.getZipCode(), savedCase);

        return savedCase;
    }

    // 2. A Therapist clicks "Accept"
    @Transactional
    public PatientCase claimCase(Long caseId, Long therapistId) {
        PatientCase patientCase = patientCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        // Check if it's already claimed
        if (patientCase.getStatus() != CaseStatus.BROADCASTED) {
            throw new IllegalStateException("This case has already been claimed or cancelled.");
        }

        User therapist = userService.getUserById(therapistId);

        // Update the case details
        patientCase.setAssignedTherapist(therapist);
        patientCase.setStatus(CaseStatus.CLAIMED);
        patientCase.setClaimedAt(LocalDateTime.now());

        // Save it. If another therapist claimed it a millisecond earlier,
        // Spring Data JPA will throw an ObjectOptimisticLockingFailureException here.
        return patientCaseRepository.save(patientCase);
    }
    // Get Available Cases
    // Update this method to require the specialty
    public List<PatientCase> getAvailableCases(String zipCode, TherapyType specialty) {
        return patientCaseRepository.findByStatusAndZipCodeAndRequiredSpecialty(CaseStatus.BROADCASTED, zipCode, specialty);
    }


    // A therapist absolutely needs a "My Caseload" view
    public List<PatientCase> getCasesForTherapist(Long therapistId) {
        return patientCaseRepository.findByAssignedTherapistId(therapistId);
    }

    // Admin/Assistant can view all cases
    public List<PatientCase> getAllCases() {
        return patientCaseRepository.findAll();
    }

    // Admin/Assistant can update case details
    public PatientCase updateCase(Long id, PatientCase updatedDetails) {
        PatientCase existingCase = patientCaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        existingCase.setPatientIdentifier(updatedDetails.getPatientIdentifier());
        existingCase.setRequiredSpecialty(updatedDetails.getRequiredSpecialty());
        existingCase.setZipCode(updatedDetails.getZipCode());
        existingCase.setStatus(updatedDetails.getStatus());

        return patientCaseRepository.save(existingCase);
    }
}