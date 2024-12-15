package vn.edu.iuh.fit.backend.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "skill")
public class Skill {
    @Id
    @Column(name = "skill_id", nullable = false)
    private Long id;

    @Column(name = "skill_description")
    private String skillDescription;

    @Column(name = "skill_name")
    private String skillName;

    @Column(name = "type")
    private Byte type;

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL)
    private Collection<JobSkill> jobSkills = new ArrayList<>();

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL)
    private Collection<CandidateSkill> candidateSkills = new ArrayList<>() ;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill = (Skill) o;
        return Objects.equals(id, skill.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}