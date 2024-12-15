package vn.edu.iuh.fit.frontend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.iuh.fit.backend.converters.ConvertTime;
import vn.edu.iuh.fit.backend.models.*;
import vn.edu.iuh.fit.backend.repositories.CompanyRepository;
import vn.edu.iuh.fit.backend.repositories.JobRepository;
import vn.edu.iuh.fit.backend.repositories.SkillRepository;
import vn.edu.iuh.fit.backend.services.CompanyService;
import vn.edu.iuh.fit.backend.services.JobService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class CompanyController {
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private JobService jobService;

    @Autowired
    private CompanyService companyService;

    @GetMapping("/company")
    public String showJobListPaging (Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        Page<Company> companies = companyService.findAll(currentPage-1, pageSize,"id","asc");
        model.addAttribute("companies", companies);
        int totalPages = companies.getTotalPages();
        if(totalPages>0){
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "companies/companies-paging";
    }

    @GetMapping("/company/{companyId}")
    public String showListJobsOfCompany(
            Model model,
            @PathVariable("companyId") Long companyId,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size
    ) {

        Company company = companyRepository.findById(companyId).orElse(null);
        model.addAttribute("company", company);
        // Fetching jobs by company
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        Page<Job> jobs = jobService.findByCompanyId(companyId, currentPage - 1, pageSize, "id", "asc");

        model.addAttribute("jobs", jobs);

        // Handling pagination for jobs
        int totalPages = jobs.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        // Formatting job creation and expiration times
        List<String> timeAgoList = jobs.getContent().stream()
                .map(job -> ConvertTime.calculateTimeAgo(job.getJobCreateAt()))
                .collect(Collectors.toList());
        model.addAttribute("timeAgoList", timeAgoList);

        List<String> timeExpired = jobs.getContent().stream()
                .map(job -> ConvertTime.calculateExpire(job.getJobExpire()))
                .collect(Collectors.toList());
        model.addAttribute("timeExpired", timeExpired);

        return "companies/list-jobs-of-company";
    }

    @GetMapping("/dang-tin-tuyen-dung")
    public String showTrangDangTinTuyenDung(Model model){
        model.addAttribute("skills", skillRepository.findAll());
        model.addAttribute("companies", companyRepository.findAll());
        model.addAttribute("job", new Job());
        return "companies/dang-tuyen-dung";
    }

    @PostMapping("/dang-tin-tuyen-dung")
    public String postJob(
            @ModelAttribute("job") Job job,
            @RequestParam("company") Long companyId,
            @RequestParam("skills") List<Long> skillIds,
            @RequestParam("jobExpire") String jobExpire,
            BindingResult result,
            Model model, RedirectAttributes redirectAttributes) {


        // Set company
        Company company = companyRepository.findById(companyId).orElse(null);
        job.setCompany(company);

        // Convert and set job expiration date
        job.setJobExpire(LocalDateTime.parse(jobExpire));

        job.setJobCreateAt(LocalDateTime.now());

        jobRepository.save(job);

        job.setId(jobRepository.findTopByOrderByIdDesc().getId());

        // Add skills to the job
        for (Long skillId : skillIds) {
            Skill skill = skillRepository.findById(skillId).orElse(null);
            if (skill != null) {
                JobSkill jobSkill = new JobSkill();
                JobSkillId jobSkillId = new JobSkillId(job.getId(), skill.getId());
                jobSkill.setId(jobSkillId);
                jobSkill.setSkill(skill);
                jobSkill.setJob(job);
                jobSkill.setSkillLevel(Byte.valueOf("2"));
                jobSkill.setMoreInfos(null);
                job.getJobSkills().add(jobSkill);
            }
        }

        // Save the job
        jobRepository.save(job);

        // Add success message to flash attributes
        redirectAttributes.addFlashAttribute("successMessage", "Đã đăng tin tuyển dụng thành công!");

        return "redirect:/dang-tin-tuyen-dung"; // Redirect to job listing page after submission
    }
}
