package vn.edu.iuh.fit.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.backend.ids.JobSkillId;
import vn.edu.iuh.fit.backend.models.JobSkill;

public interface JobSkillRepository extends JpaRepository<JobSkill, JobSkillId> {
}