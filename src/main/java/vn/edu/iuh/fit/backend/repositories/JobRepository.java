package vn.edu.iuh.fit.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.backend.models.Job;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    Page<Job> findByCompanyId(Long companyId, Pageable pageable);
    Job findTopByOrderByIdDesc();
}