package com.sharktank.interdepcollab.requirement.service;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sharktank.interdepcollab.exception.InvalidUserException;
import com.sharktank.interdepcollab.file.model.FileMetadata;
import com.sharktank.interdepcollab.file.service.BlobManagementService;
import com.sharktank.interdepcollab.requirement.model.*;
import com.sharktank.interdepcollab.requirement.repository.RequirementRepository;
import com.sharktank.interdepcollab.user.model.AppUser;
import com.sharktank.interdepcollab.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequirementService {

    private final RequirementRepository requirementRepository;
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
        finalRequirement.setStatus(Status.NEW);

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

        existingRequirement = requirementRepository.save(existingRequirement);

        return modelMapper.map(existingRequirement, RequirementDetailedDTO.class);
    }

    public Page<RequirementBaseDTO> getAllRequirements(Pageable pageable) {
        Page<Requirement> requirements = requirementRepository.findAll(pageable);
        return requirements.map(requirement -> modelMapper.map(requirement, RequirementBaseDTO.class));
    }

    @Transactional
    public RequirementDetailedDTO getRequirement(Integer id) {
        Requirement requirement = requirementRepository.findById(id).orElseThrow();
        RequirementDetailedDTO dto = modelMapper.map(requirement, RequirementDetailedDTO.class);

        return dto;
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