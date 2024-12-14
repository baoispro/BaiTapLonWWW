package vn.edu.iuh.fit.frontend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.iuh.fit.backend.converters.ConvertTime;
import vn.edu.iuh.fit.backend.models.Company;
import vn.edu.iuh.fit.backend.models.Job;
import vn.edu.iuh.fit.backend.repositories.CompanyRepository;
import vn.edu.iuh.fit.backend.repositories.JobRepository;
import vn.edu.iuh.fit.backend.services.CompanyService;
import vn.edu.iuh.fit.backend.services.JobService;

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
}
