package org.example.task2restapi.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.example.task2restapi.dto.ExecutionFactFilterOptionsDto;
import org.example.task2restapi.entity.ExecutionFact;
import org.example.task2restapi.entity.ExecutionFact_;
import org.example.task2restapi.entity.Participant;
import org.example.task2restapi.entity.Participant_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ExecutionFactSpecs {

    public Specification<ExecutionFact> byFilterDto(ExecutionFactFilterOptionsDto optionsDto) {
        return (root, query, builder) -> {
            Predicate resultQuery = builder.conjunction();
            resultQuery = addEmailCriteria(optionsDto, root, builder, resultQuery);
            resultQuery = addDescriptionCriteria(optionsDto, root, builder, resultQuery);
            resultQuery = addFinishTimeCriteria(optionsDto, root, builder, resultQuery);
            return resultQuery;
        };
    }

    private Predicate addFinishTimeCriteria(ExecutionFactFilterOptionsDto optionsDto,
                                                   Root<ExecutionFact> root,
                                                   CriteriaBuilder builder,
                                                   Predicate resultQuery) {
        if(optionsDto.getFromFinishTime() != null && optionsDto.getToFinishTime() != null) {
            resultQuery = builder.and(
                    resultQuery,
                    builder.and(
                            builder.greaterThanOrEqualTo(root.get(ExecutionFact_.finishTime), optionsDto.getFromFinishTime()),
                            builder.lessThanOrEqualTo(root.get(ExecutionFact_.finishTime), optionsDto.getToFinishTime())
                    )
            );
        }
        return resultQuery;
    }

    private Predicate addDescriptionCriteria(ExecutionFactFilterOptionsDto optionsDto,
                                                    Root<ExecutionFact> root,
                                                    CriteriaBuilder builder,
                                                    Predicate resultQuery) {
        if(optionsDto.getDescription() != null) {
            resultQuery = builder.and(
                    resultQuery,
                    builder.equal(root.get(ExecutionFact_.description), optionsDto.getDescription())
            );
        }
        return resultQuery;
    }

    private Predicate addEmailCriteria(ExecutionFactFilterOptionsDto optionsDto,
                                              Root<ExecutionFact> root,
                                              CriteriaBuilder builder,
                                              Predicate resultQuery) {
        if(optionsDto.getExecutorEmail() != null) {
            Join<ExecutionFact, Participant> join = root.join(ExecutionFact_.executor);
            resultQuery = builder.and(
                    resultQuery,
                    builder.equal(join.get(Participant_.email), optionsDto.getExecutorEmail())
            );
        }
        return resultQuery;
    }

}
