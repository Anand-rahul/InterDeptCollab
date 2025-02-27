package com.sharktank.interdepcollab.devops.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sharktank.interdepcollab.requirement.model.Requirement;
import com.sharktank.interdepcollab.solution.model.Solution;

import groovy.transform.builder.Builder;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStory {
    @Id
    @NotNull
    @Column(nullable = false)
    private Integer id;
    
    @NotNull
    @Column(nullable = false)
    private String project;
    
    @OneToOne
    @JoinColumn
    @JsonIgnore
    private Requirement requirement;
    
    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private Solution solution;

    @JsonProperty("uri")
    public String getDevopsCompleteURL(){
        return String.format("https://dev.azure.com/BFLDevOpsOrg/%s/_workitems/edit/%d", project, id);
    }
}
