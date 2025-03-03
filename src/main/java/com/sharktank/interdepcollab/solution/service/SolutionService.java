package com.sharktank.interdepcollab.solution.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.sharktank.interdepcollab.ai.Constants.SourceType;
import com.sharktank.interdepcollab.ai.Model.*;
import com.sharktank.interdepcollab.ai.Service.Parallel;
import com.sharktank.interdepcollab.exception.InvalidUserException;
import com.sharktank.interdepcollab.file.model.FileMetadata;
import com.sharktank.interdepcollab.file.service.BlobManagementService;
import com.sharktank.interdepcollab.solution.model.*;
import com.sharktank.interdepcollab.solution.repository.SolutionRepository;
import com.sharktank.interdepcollab.user.model.*;
import com.sharktank.interdepcollab.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SolutionService {

    private final UserService userService;
    private final SolutionRepository solutionRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final BlobManagementService fileService;
    private final Parallel parallelService;

    @Transactional
    public SolutionDetailedDTO createSolution(SolutionInput solution) throws InvalidUserException, Exception {
        AppUser user = userService.getLoggedInUser();

        if (user == null) {
            throw new InvalidUserException("Invalid Creator user");
        }

        log.info("Solution input: {}", solution.toString());
        Solution finalSolution = modelMapper.map(solution, Solution.class);
        finalSolution.setCreatedBy(user);

        AppUser deliveryManager = userService.getUserByEmail(solution.getDeliveryManager())
                .orElseThrow(() -> new InvalidUserException("Invalid Delivery Manager"));
        AppUser pmo = userService.getUserByEmail(solution.getPmo())
                .orElseThrow(() -> new InvalidUserException("Invalid PMO User"));

        finalSolution.setDeliveryManager(deliveryManager);
        finalSolution.setPmo(pmo);

        log.info("Final solution: {}",finalSolution.toString());
        finalSolution = solutionRepository.save(finalSolution);
                
        List<InputStream> fileInputs = new ArrayList<InputStream>();
        log.info("Files in solution: {}", solution.getFiles().toString());
        if (solution.getFiles() != null) {
            for (Integer fileId : solution.getFiles()) {
                FileMetadata file = fileService.tagFileToParent(fileId,
                        finalSolution.getClass().getSimpleName().toUpperCase(), finalSolution.getId());
                log.info("Adding to solution {}: {}", finalSolution.getId(), file.toString());
                finalSolution.getFiles().add(file);
                fileInputs.add(fileService.getFile(fileId));
            }
        }
        SourceDocumentBase fileVectoriseBase = new SourceDocumentBase(SourceType.SOLUTION_DOCUMENT.toString(), finalSolution.getId(), fileInputs);
        parallelService.parallelVectorizeFile(fileVectoriseBase, SourceType.SOLUTION_DOCUMENT, finalSolution.getId().toString());

        log.info("Infra in solution: {}", solution.getInfraResources().toString());
        if (solution.getInfraResources() != null) {
            final Solution currSolution = finalSolution;
            solution.getInfraResources().forEach(x -> x.setSolution(currSolution));
            finalSolution.setInfraResources(solution.getInfraResources());
        }

        solutionRepository.save(finalSolution);
        
        // Send to AI Background vectorizing service
        SourceBase<Solution> solutionVectorize = new SourceBase<Solution>(SourceType.SOLUTION.toString(), finalSolution.getId(), finalSolution);
        JsonNode node = objectMapper.convertValue(solutionVectorize, JsonNode.class);
        parallelService.parallelVectorizeObject(node, SourceType.SOLUTION, finalSolution.getId().toString());

        SolutionDetailedDTO solutionOutput = modelMapper.map(finalSolution, SolutionDetailedDTO.class);

        return solutionOutput;
    }

    public SolutionDetailedDTO patchSolution(Integer id, JsonPatch jsonPatch)
            throws JsonPatchException, JsonProcessingException, NoSuchElementException {
        Solution existingSolution = solutionRepository.findById(id).orElseThrow();
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(existingSolution, JsonNode.class));
        existingSolution = objectMapper.treeToValue(patched, Solution.class);

        return this.updateSolution(id, modelMapper.map(existingSolution, SolutionInput.class));
    }

    public SolutionDetailedDTO updateSolution(Integer id, SolutionInput solution) {
        Solution existingSolution = solutionRepository.findById(id).orElseThrow();
        if (solution.getTitle() != null && !solution.getTitle().isEmpty()) {
            existingSolution.setTitle(solution.getTitle());
        }
        if (solution.getDescription() != null && !solution.getDescription().isEmpty()) {
            existingSolution.setDescription(solution.getDescription());
        }
        if (solution.getCategory() != null && !solution.getCategory().isEmpty()) {
            existingSolution.setCategory(solution.getCategory());
        }
        if (solution.getDepartment() != null && !solution.getDepartment().isEmpty()) {
            existingSolution.setDepartment(solution.getDepartment());
        }
        if (solution.getImpact() != null && !solution.getImpact().isEmpty()) {
            existingSolution.setImpact(solution.getImpact());
        }
        if (solution.getTags() != null && !solution.getTags().isEmpty()) {
            existingSolution.setTags(solution.getTags());
        }
        if (solution.getProblemStatement() != null && !solution.getProblemStatement().isEmpty()) {
            existingSolution.setProblemStatement(solution.getProblemStatement());
        }
        if(solution.getInfraResources() != null  && !solution.getInfraResources().isEmpty()){
            solution.getInfraResources().forEach(x -> x.setSolution(existingSolution));
            existingSolution.getInfraResources().addAll(solution.getInfraResources());
        }
        // if (solution.getCreatedBy() != null
        //         && !solution.getCreatedBy().equals(existingSolution.getCreatedBy().getEmail())) {
        //     throw new InvalidUserException("Cannot change the creator of the solution");
        // }

        if (solution.getDeliveryManager() != null
                && !solution.getDeliveryManager().equals(existingSolution.getDeliveryManager().getEmail())) {
            AppUser deliveryManager = userService.getUserByEmail(solution.getDeliveryManager())
                    .orElseThrow(() -> new InvalidUserException("Invalid Delivery Manager"));
            existingSolution.setDeliveryManager(deliveryManager);
        }

        if (solution.getPmo() != null && !solution.getPmo().equals(existingSolution.getPmo().getEmail())) {
            AppUser pmo = userService.getUserByEmail(solution.getPmo())
                    .orElseThrow(() -> new InvalidUserException("Invalid PMO User"));
            existingSolution.setPmo(pmo);
        }

        solutionRepository.save(existingSolution);
        return modelMapper.map(existingSolution, SolutionDetailedDTO.class);
    }

    public Page<SolutionBaseDTO> getAllSolutions(Pageable pageable) {
        Page<Solution> solutions = solutionRepository.findAll(pageable);
        return solutions.map(solution -> modelMapper.map(solution, SolutionBaseDTO.class));
    }

    @Transactional
    public SolutionDetailedDTO getSolution(Integer id){
        AppUser user = userService.getLoggedInUser();
        Solution solution = solutionRepository.findById(id).orElseThrow();
        
        // Send to AI Background vectorizing service
        try {
            SourceBase<Solution> solutionVectorize = new SourceBase<Solution>(SourceType.SOLUTION.toString(),
                    solution.getId(), solution);
            JsonNode node = objectMapper.convertValue(solutionVectorize, JsonNode.class);
            log.info("Vectorising solution -> {}", solution.getId());
            parallelService.parallelVectorizeObject(node, SourceType.SOLUTION, solution.getId().toString());
        } catch (Exception ex) {
            log.error("Exception while vectorising solution", ex);
        }

        SolutionDetailedDTO dto = modelMapper.map(solution, SolutionDetailedDTO.class);

        Action userAction = getOrInitUserAction(user, solution);
        
        if(!userAction.getIsViewed()) {
            solution.addView();
            userAction.setIsViewed(true);
        }

        solutionRepository.save(solution);
        userService.saveUser(user);

        dto.setIsLiked(userAction.getIsLiked());
        return dto;
    }

    public Set<FAQ> getFAQs(Integer id) {
        Solution solution = solutionRepository.findById(id).orElseThrow();
        return solution.getFaqs();
    }

    public Set<FAQ> addFAQs(Integer id, Set<FAQ> faqs) {
        final Solution solution = solutionRepository.findById(id).orElseThrow();
        faqs.forEach(faq -> faq.setSolution(solution));
        solution.getFaqs().addAll(faqs);
        return solutionRepository.save(solution).getFaqs();
    }

    @Transactional
    public Boolean toggleLike(Integer id) {
        AppUser user = userService.getLoggedInUser();
        Solution solution = solutionRepository.findById(id).orElseThrow();
        SolutionAction action = getOrInitUserAction(user, solution);
        action.setIsLiked(!action.getIsLiked());

        if (action.getIsLiked()) {
            solution.addLike();
        } else {
            solution.removeLike();
        }

        solutionRepository.save(solution);
        userService.saveUser(user);

        return action.getIsLiked();
    }

    private SolutionAction getOrInitUserAction(AppUser user, Solution solution) {

        SolutionAction userAction = user.getActions().stream()
                .filter(action -> action instanceof SolutionAction)
                .map(action -> (SolutionAction) action)
                .filter(action -> action.getSolution().getId().equals(solution.getId())).findFirst().orElse(
                        null);

        if (userAction == null) {
            userAction = new SolutionAction();
            userAction.setSolution(solution);
            userAction.setUser(user);
            userAction.setIsLiked(false);
            userAction.setIsViewed(false);
            user.getActions().add(userAction);
        }

        return userAction;
    }

    @Transactional
    public FileMetadata addFile(MultipartFile file, Integer solutionId) throws IOException {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow();
        FileMetadata fileMetadata = fileService.uploadFile(file, solution.getClass().getSimpleName().toUpperCase(), solution.getId());
        solution.getFiles().add(fileMetadata);
        solutionRepository.save(solution);
        return fileMetadata;
    }
    
    @Transactional
    public void removeFile(Integer fileId, Integer solutionId) throws IOException {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow();
        FileMetadata fileMetadata = fileService.deleteFile(fileId);
        solution.getFiles().remove(fileMetadata);
        solutionRepository.save(solution);
    }

    public Map<String, Integer> getAllFiles(Integer solutionId) {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow();
        return solution.getFiles().stream().collect(Collectors.toMap(file -> file.getOriginalName(), file -> file.getId()));
    }

}
