package com.example.demo.Entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_cases")
@Data
@NoArgsConstructor
public class PatientCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String patientIdentifier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TherapyType requiredSpecialty;

    @Column(nullable = false)
    private String zipCode;

    @Enumerated(EnumType.STRING)
    private CaseStatus status = CaseStatus.BROADCASTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_therapist_id")
    private User assignedTherapist;

    // This single annotation handles the concurrency control!
    @Version
    private Integer version;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime claimedAt;
}