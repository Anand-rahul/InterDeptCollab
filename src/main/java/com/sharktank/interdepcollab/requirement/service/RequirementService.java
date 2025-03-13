package com.sharktank.interdepcollab.requirement.service;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sharktank.interdepcollab.devops.model.UserStory;
import com.sharktank.interdepcollab.exception.InvalidUserException;
import com.sharktank.interdepcollab.file.model.FileMetadata;
import com.sharktank.interdepcollab.file.service.BlobManagementService;
import com.sharktank.interdepcollab.requirement.model.*;
import com.sharktank.interdepcollab.requirement.repository.RequirementRepository;
import com.sharktank.interdepcollab.solution.model.Solution;
import com.sharktank.interdepcollab.solution.repository.SolutionRepository;
import com.sharktank.interdepcollab.user.model.AppUser;
import com.sharktank.interdepcollab.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequirementService {

    private final RequirementRepository requirementRepository;
    private final SolutionRepository solutionRepository;
    private final BlobManagementService fileService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Transactional
    public RequirementDetailedDTO createRequirement(RequirementInput requirement) {
        AppUser user = userService.getLoggedInUser();

        if (user == null) {
            throw new InvalidUserException("Invalid Creator user");
        }

        log.info("Requirement input: {}", requirement.toString());
        Requirement finalRequirement = modelMapper.map(requirement, Requirement.class);

        finalRequirement.setCreatedBy(user);

        log.info("Final requirement: {}", finalRequirement.toString());
        finalRequirement = requirementRepository.save(finalRequirement);

        if (requirement.getFiles() != null) {
            log.info("Files in requirement: {}", requirement.getFiles().toString());
            for (Integer fileId : requirement.getFiles()) {
                FileMetadata file = fileService.tagFileToParent(fileId,
                        finalRequirement.getClass().getSimpleName().toUpperCase(), finalRequirement.getId());
                log.info("Adding to requirement {}: {}", finalRequirement.getId(), file.toString());
                finalRequirement.getFiles().add(file);
            }
        }

        if (finalRequirement.getSolution() != null) {
            Solution solution = solutionRepository.findById(finalRequirement.getSolution().getId()).orElseThrow();
            finalRequirement.setStatus(Status.NEW);
            finalRequirement.setAssignedTo(solution.getPmo());
        }
        else{
            finalRequirement.setStatus(Status.NEW);
        }

        finalRequirement = requirementRepository.save(finalRequirement);

        RequirementDetailedDTO requirementOutput = modelMapper.map(finalRequirement, RequirementDetailedDTO.class);

        return requirementOutput;
    }

    public RequirementDetailedDTO updateRequirement(Integer id, RequirementInput requirement) {
        Requirement existingRequirement = requirementRepository.findById(id).orElseThrow();

        if (requirement.getTitle() != null && !requirement.getTitle().isEmpty()) {
            existingRequirement.setTitle(requirement.getTitle());
        }
        if (requirement.getDescription() != null && !requirement.getDescription().isEmpty()) {
            existingRequirement.setDescription(requirement.getDescription());
        }
        if (requirement.getPriority() != null && !requirement.getPriority().isEmpty()) {
            existingRequirement.setPriority(requirement.getPriority());
        }
        if (requirement.getSolution() != null && requirement.getSolution().getId() != null) {
            Solution solution = solutionRepository.findById(requirement.getSolution().getId()).orElseThrow();
            existingRequirement.setSolution(solution);
        }
        if (requirement.getRequestingDepartment() != null && !requirement.getRequestingDepartment().isEmpty()) {
            existingRequirement.setRequestingDepartment(requirement.getRequestingDepartment());
        }
        if (requirement.getSubDepartment() != null && !requirement.getSubDepartment().isEmpty()) {
            existingRequirement.setSubDepartment(requirement.getSubDepartment());
        }
        if (requirement.getLineOfBusiness() != null && !requirement.getLineOfBusiness().isEmpty()) {
            existingRequirement.setLineOfBusiness(requirement.getLineOfBusiness());
        }
        if (requirement.getProductName() != null && !requirement.getProductName().isEmpty()) {
            existingRequirement.setProductName(requirement.getProductName());
        }
        if (requirement.getProblemStatement() != null && !requirement.getProblemStatement().isEmpty()) {
            existingRequirement.setProblemStatement(requirement.getProblemStatement());
        }
        if (requirement.getExpectedImpact() != null && !requirement.getExpectedImpact().isEmpty()) {
            existingRequirement.setExpectedImpact(requirement.getExpectedImpact());
        }

        existingRequirement = requirementRepository.save(existingRequirement);

        return modelMapper.map(existingRequirement, RequirementDetailedDTO.class);
    }

    public Page<RequirementBaseDTO> getAllRequirements(Pageable pageable) {
        Page<Requirement> requirements = requirementRepository.findAll(pageable);
        return requirements.map(requirement -> modelMapper.map(requirement, RequirementBaseDTO.class));
    }
    
    public Page<RequirementBaseDTO> getAllNewRequirements(Pageable pageable) {
        Page<Requirement> requirements = requirementRepository.findBySolutionIsNullAndStatus(Status.NEW, pageable);
        return requirements.map(requirement -> modelMapper.map(requirement, RequirementBaseDTO.class));
    }
    
    public Page<RequirementBaseDTO> getMyRequirements(Pageable pageable) {
        AppUser user = userService.getLoggedInUser();
        Page<Requirement> requirements = requirementRepository.findByCreatedByOrAssignedTo(user, user, pageable);
        return requirements.map(requirement -> modelMapper.map(requirement, RequirementBaseDTO.class));
    }

    @Transactional
    public RequirementDetailedDTO getRequirement(Integer id) {
        Requirement requirement = requirementRepository.findById(id).orElseThrow();
        RequirementDetailedDTO dto = modelMapper.map(requirement, RequirementDetailedDTO.class);

        return dto;
    }

    public RequirementBaseDTO updateStatus(Integer id, Status newStatus, AssignInput input){
        Requirement requirement = requirementRepository.findById(id).orElseThrow();
        AppUser user = userService.getUserByEmail(input.getUser()).orElse(userService.getLoggedInUser());
       
        updateStatusNoSave(requirement, newStatus, user, input.getUserStory());
        requirementRepository.save(requirement);

        return modelMapper.map(requirement, RequirementBaseDTO.class);
    }

    private Requirement updateStatusNoSave(Requirement requirement, Status newStatus, AppUser user,
            UserStory userStory) {
        switch (newStatus) {
            case NEW:
                requirement.setAssignedTo(null);
                requirement.setPickedDate(null);
                requirement.setStatus(newStatus);
                break;
            case DISCUSSION:
                requirement.setStatus(newStatus);
                if (requirement.getAssignedTo() == null) {
                    requirement.setAssignedTo(user);
                }
                if (requirement.getPickedDate() == null) {
                    requirement.setPickedDate(Instant.now());
                }
                break;
            case ACCEPTED:
                requirement.setStatus(newStatus);
                if (requirement.getAssignedTo() == null) {
                    requirement.setAssignedTo(user);
                }
                if (requirement.getPickedDate() == null) {
                    requirement.setPickedDate(Instant.now());
                }
                if (userStory != null) {
                    userStory.setRequirement(requirement);
                    requirement.setUserStory(userStory);
                }
                break;
            case COMPLETED:
                requirement.setStatus(newStatus);
                requirement.setClosedDate(Instant.now());
                break;
            case CANCELLED:
                requirement.setStatus(newStatus);
                requirement.setClosedDate(Instant.now());
                break;
            default:
                break;
        }
        return requirement;
    }

    @Transactional
    public FileMetadata addFile(MultipartFile file, Integer requirementId) throws IOException {
        Requirement requirement = requirementRepository.findById(requirementId).orElseThrow();
        FileMetadata fileMetadata = fileService.uploadFile(file, requirement.getClass().getSimpleName().toUpperCase(),
                requirement.getId());
        requirement.getFiles().add(fileMetadata);
        requirementRepository.save(requirement);
        return fileMetadata;
    }

    @Transactional
    public void removeFile(Integer fileId, Integer requirementId) throws IOException {
        Requirement requirement = requirementRepository.findById(requirementId).orElseThrow();
        FileMetadata fileMetadata = fileService.deleteFile(fileId);
        requirement.getFiles().remove(fileMetadata);
        requirementRepository.save(requirement);
    }

    public Map<String, Integer> getAllFiles(Integer requirementId) {
        Requirement requirement = requirementRepository.findById(requirementId).orElseThrow();
        return requirement.getFiles().stream()
                .collect(Collectors.toMap(file -> file.getOriginalName(), file -> file.getId()));
    }
}