package com.sharktank.interdepcollab.solution.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.ws.rs.core.StreamingOutput;

import org.modelmapper.ModelMapper;
import org.slf4j.event.KeyValuePair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.sharktank.interdepcollab.exception.InvalidUserException;
import com.sharktank.interdepcollab.file.model.FileMetadata;
import com.sharktank.interdepcollab.file.service.BlobManagementService;
import com.sharktank.interdepcollab.solution.model.*;
import com.sharktank.interdepcollab.solution.repository.SolutionRepository;
import com.sharktank.interdepcollab.user.model.*;
import com.sharktank.interdepcollab.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SolutionService {

    private final UserService userService;
    private final SolutionRepository solutionRepository;
    private final ModelMapper solutionMapper;
    private final ObjectMapper objectMapper;
    private final BlobManagementService fileService;

    public SolutionDTO createSolution(SolutionDTO solution) throws InvalidUserException {
        AppUser user = userService.getLoggedInUser();

        if (user == null || solution.getCreatedBy() == user.getEmail()) {
            throw new InvalidUserException("Invalid Creator user");
        }

        Solution finalSolution = solutionMapper.map(solution, Solution.class);
        finalSolution.setCreatedBy(user);

        AppUser deliveryManager = userService.getUserByEmail(solution.getDeliveryManager())
                .orElseThrow(() -> new InvalidUserException("Invalid Delivery Manager"));
        AppUser pmo = userService.getUserByEmail(solution.getPmo())
                .orElseThrow(() -> new InvalidUserException("Invalid PMO User"));

        finalSolution.setDeliveryManager(deliveryManager);
        finalSolution.setPmo(pmo);

        finalSolution = solutionRepository.save(finalSolution);
        solution = solutionMapper.map(finalSolution, SolutionDTO.class);

        return solution;
    }

    public SolutionDTO patchSolution(Integer id, JsonPatch jsonPatch)
            throws JsonPatchException, JsonProcessingException, NoSuchElementException {
        Solution existingSolution = solutionRepository.findById(id).orElseThrow();
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(existingSolution, JsonNode.class));
        existingSolution = objectMapper.treeToValue(patched, Solution.class);

        return this.updateSolution(id, solutionMapper.map(existingSolution, SolutionDTO.class));
    }

    public SolutionDTO updateSolution(Integer id, SolutionDTO solution) {
        Solution existingSolution = solutionRepository.findById(id).orElseThrow();
        existingSolution.setName(solution.getName());
        existingSolution.setDepartment(solution.getDepartment());

        if (solution.getCreatedBy() != null
                && !solution.getCreatedBy().equals(existingSolution.getCreatedBy().getEmail())) {
            throw new InvalidUserException("Cannot change the creator of the solution");
        }

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

        existingSolution = solutionRepository.save(existingSolution);
        return solutionMapper.map(existingSolution, SolutionDTO.class);
    }

    //TODO: Remove unecessary fields
    public Page<SolutionDTO> getAllSolutions(Pageable pageable) {
        Page<Solution> solutions = solutionRepository.findAll(pageable);
        return solutions.map(solution -> solutionMapper.map(solution, SolutionDTO.class));
    }

    // BUG: Is viewed is not being set
    // BUG: User details are being sent
    @Transactional
    public SolutionDTO getSolution(Integer id) {
        AppUser user = userService.getLoggedInUser();
        Solution solution = solutionRepository.findById(id).orElseThrow();
        SolutionDTO dto = solutionMapper.map(solution, SolutionDTO.class);

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
    public void addFile(MultipartFile file, Integer solutionId) throws IOException {
        Solution solution = solutionRepository.findById(solutionId).orElseThrow();
        FileMetadata fileMetadata = fileService.uploadFile(file, "SOLUTION", solution.getId());
        solution.getFiles().add(fileMetadata);
        solutionRepository.save(solution);
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
        return solution.getFiles().stream().collect(Collectors.toMap(file -> file.getName(), file -> file.getId()));
    }

    public static class FileStreamingOutput implements StreamingOutput {
        private final InputStream blobStream;

        public FileStreamingOutput(InputStream blobStream) {
            this.blobStream = blobStream;
        }

        @Override
        public void write(OutputStream output) throws IOException {
            byte[] buffer = new byte[4096];
            int bytesRead;
            try {
                while ((bytesRead = blobStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            } finally {
                blobStream.close();
            }
        }
    }

    public StreamingOutput getFileStream(Integer fileId) throws Exception {
        InputStream blobStream = fileService.getFile(fileId);
        return new FileStreamingOutput(blobStream);
    }

}
