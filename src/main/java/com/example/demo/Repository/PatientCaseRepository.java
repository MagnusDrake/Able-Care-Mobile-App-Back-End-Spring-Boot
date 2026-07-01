package com.example.demo.Repository;

import com.example.demo.Entity.CaseStatus;
import com.example.demo.Entity.PatientCase;
import com.example.demo.Entity.TherapyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientCaseRepository extends JpaRepository<PatientCase, Long> {

    // For the Admin Dashboard: View cases that are still waiting to be claimed in an area
    //List<PatientCase> findByStatusAndZipCode(CaseStatus status, String zipCode);

    // To this:
    List<PatientCase> findByStatusAndZipCodeAndRequiredSpecialty(CaseStatus status, String zipCode, TherapyType specialty);

    // For the Therapist Dashboard: View their currently assigned/completed cases
    List<PatientCase> findByAssignedTherapistId(Long assignedTherapistId);
}