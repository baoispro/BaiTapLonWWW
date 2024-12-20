package vn.edu.iuh.fit.frontend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.iuh.fit.backend.models.Address;
import vn.edu.iuh.fit.backend.models.Candidate;
import vn.edu.iuh.fit.backend.repositories.AddressRepository;
import vn.edu.iuh.fit.backend.repositories.CandidateRepository;
import vn.edu.iuh.fit.backend.repositories.SkillRepository;
import vn.edu.iuh.fit.backend.services.CandidateService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class CandidateController {
    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private CandidateService candidateService;

    //Load dữ liệu không phân trang
    @GetMapping("/list")
    public String showCandidateList (Model model) {
        model.addAttribute("candidates", candidateRepository.findAll());
        return "candidates/candidates";
    }

    //Load dữ liệu có phân trang
    @GetMapping("/candidate")
    public String showCandidatesListPaging (Model model, @RequestParam("page") Optional<Integer> page,@RequestParam("size") Optional<Integer> size, @RequestParam(name = "skill", required = false) String skill) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        Page<Candidate> candidates;
        if (skill != null && !skill.isEmpty()) {
            candidates = candidateService.findCandidateBySkillName(skill, currentPage-1, pageSize,"id","asc");
        } else {
            candidates = candidateService.findAll(currentPage-1, pageSize,"id","asc");
        }
        model.addAttribute("candidates", candidates);
        int totalPages = candidates.getTotalPages();
        if(totalPages>0){
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }


        return "candidates/candidates-paging";
    }

    // Thêm ứng viên
    @PostMapping("/candidates/add")
    public String addCandidate (@ModelAttribute("candidate") Candidate candidate,
                                @ModelAttribute("address")Address address,
                                BindingResult bindingResult, Model model) {
        addressRepository.save(address);
        candidate.setAddress(address);
        candidateRepository.save(candidate);
        return "redirect:/candidates";
    }
}
