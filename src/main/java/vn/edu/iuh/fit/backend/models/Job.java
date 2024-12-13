package vn.edu.iuh.fit.backend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "job")
public class Job {
    @Id
    @Column(name = "job_id", nullable = false)
    private Long id;

    @Column(name = "job_desc", nullable = false, length = 2000)
    private String jobDesc;

    @Column(name = "job_name", nullable = false)
    private String jobName;

    @Column(name = "job_salary", nullable = false)
    private String jobSalary;

    @Column(name = "job_createAt", nullable = false)
    private LocalDateTime jobCreateAt;

    @Column(name = "job_quantity", nullable = false)
    private int jobQuantity;

    @Column(name = "job_Expire", nullable = false)
    private LocalDateTime jobExpire;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company")
    private Company company;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private Collection<JobSkill> jobSkills = new ArrayList<>() ;

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", jobDesc='" + jobDesc + '\'' +
                ", jobName='" + jobName + '\'' +
                ", jobSalary='" + jobSalary + '\'' +
                ", jobCreateAt=" + jobCreateAt +
                ", jobQuantity=" + jobQuantity +
                ", jobExpire=" + jobExpire +
                ", company=" + company +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return Objects.equals(id, job.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}