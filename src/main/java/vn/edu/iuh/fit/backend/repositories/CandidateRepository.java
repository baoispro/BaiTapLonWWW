package vn.edu.iuh.fit.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.iuh.fit.backend.models.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    @Query("SELECT c FROM Candidate c " +
            "JOIN CandidateSkill cs ON cs.can = c " +
            "JOIN Skill s ON s = cs.skill " +
            "WHERE s.skillName LIKE %:skillName%")
    Page<Candidate> findCandidatesBySkillName(@Param("skillName") String skillName, Pageable pageable);
    Candidate findByUserUsername(String username);
}