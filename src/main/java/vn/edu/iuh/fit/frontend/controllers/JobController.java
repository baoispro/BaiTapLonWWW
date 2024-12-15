package vn.edu.iuh.fit.frontend.controllers;

import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.iuh.fit.backend.converters.ConvertTime;
import vn.edu.iuh.fit.backend.models.Candidate;
import vn.edu.iuh.fit.backend.models.CandidateSkill;
import vn.edu.iuh.fit.backend.models.Job;
import vn.edu.iuh.fit.backend.models.Skill;
import vn.edu.iuh.fit.backend.repositories.CandidateRepository;
import vn.edu.iuh.fit.backend.repositories.JobRepository;
import vn.edu.iuh.fit.backend.services.JobService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class JobController {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private JobService jobService;

//    @GetMapping("/listJob")
//    public String showJobList (Model model) {
//        model.addAttribute("candidates", candidateRepository.findAll());
//        return "candidates/candidates";
//    }

    //Load dữ liệu có phân trang
    @GetMapping("/")
    public String showJobListPaging (Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        Page<Job> jobs = jobService.findAll(currentPage-1, pageSize,"id","asc");
        model.addAttribute("jobs", jobs);
        int totalPages = jobs.getTotalPages();
        if(totalPages>0){
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        List<String> timeAgoList = jobs.getContent().stream()
                .map(job -> ConvertTime.calculateTimeAgo(job.getJobCreateAt()))  // Sử dụng phương thức từ TimeUtils
                .collect(Collectors.toList());
        model.addAttribute("timeAgoList", timeAgoList);
        List<String> timeExpired = jobs.getContent().stream()
                .map(job -> ConvertTime.calculateExpire(job.getJobExpire()))  // Sử dụng phương thức từ TimeUtils
                .collect(Collectors.toList());
        model.addAttribute("timeExpired", timeExpired);
        return "index";
    }

    @GetMapping("/cong-viec-de-xuat")
    public String congViecDeXuat(Model model,
                                 @RequestParam("page") Optional<Integer> page,
                                 @RequestParam("size") Optional<Integer> size,
                                 @AuthenticationPrincipal UserDetails userDetails) {

        // Get current page and page size
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        // Fetch the Candidate entity based on the logged-in user
        Candidate candidate = candidateRepository.findByUserUsername(userDetails.getUsername());

        // Get the skills of the candidate
        Set<Skill> candidateSkills = candidate.getCandidateSkills().stream()
                .map(CandidateSkill::getSkill)
                .collect(Collectors.toSet());

        // Fetch jobs from jobService
        Page<Job> jobs = jobService.findAll(currentPage - 1, pageSize, "id", "asc");

        // Filter the jobs based on the skills the candidate has
        List<Job> filteredJobs = jobs.getContent().stream()
                .filter(job -> job.getJobSkills().stream()
                        .anyMatch(jobSkill -> candidateSkills.contains(jobSkill.getSkill())))
                .collect(Collectors.toList());

        // Add filtered jobs to the model
        model.addAttribute("jobs", new PageImpl<>(filteredJobs, PageRequest.of(currentPage - 1, pageSize), filteredJobs.size()));

        // Handle pagination
        int totalPages = jobs.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        // Time ago and expiration logic
        List<String> timeAgoList = jobs.getContent().stream()
                .map(job -> ConvertTime.calculateTimeAgo(job.getJobCreateAt()))
                .collect(Collectors.toList());
        model.addAttribute("timeAgoList", timeAgoList);

        List<String> timeExpired = jobs.getContent().stream()
                .map(job -> ConvertTime.calculateExpire(job.getJobExpire()))
                .collect(Collectors.toList());
        model.addAttribute("timeExpired", timeExpired);

        return "jobs/cong-viec-de-xuat";
    }

}
