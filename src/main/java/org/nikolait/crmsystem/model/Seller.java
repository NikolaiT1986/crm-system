package org.nikolait.crmsystem.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sellers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "contact_info", nullable = false, length = 500)
    private String contactInfo;

    @CreationTimestamp
    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @Column(nullable = false)
    private boolean deleted;

}
