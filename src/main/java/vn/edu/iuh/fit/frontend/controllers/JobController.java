package vn.edu.iuh.fit.frontend.controllers;

import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.iuh.fit.backend.converters.ConvertTime;
import vn.edu.iuh.fit.backend.models.Job;
import vn.edu.iuh.fit.backend.repositories.JobRepository;
import vn.edu.iuh.fit.backend.services.JobService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class JobController {
    @Autowired
    private JobRepository jobRepository;

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
}
