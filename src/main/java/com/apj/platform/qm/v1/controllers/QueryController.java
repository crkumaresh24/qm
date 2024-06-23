package com.apj.platform.qm.v1.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apj.platform.qm.v1.controllers.vo.CreateQuery;
import com.apj.platform.qm.v1.entities.QueryMetadata;
import com.apj.platform.qm.v1.services.QueryService;
import com.apj.platform.qm.v1.services.exceptions.QueryNotFoundException;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/queries")
public class QueryController {

    private final QueryService queryService;

    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    @PostMapping
    public QueryMetadata createQuery(@Valid @RequestBody CreateQuery query) {
        return this.queryService.create(query.getName(), query.getQuery());
    }

    @GetMapping("/list")
    public List<QueryMetadata> listAllQueries() throws Exception {
        return this.queryService.listAll();
    }

    @GetMapping("/{id}")
    public QueryMetadata getQuery(@PathVariable("id") Long id) throws QueryNotFoundException {
        return this.queryService.read(id);
    }

    @PutMapping("/{id}")
    public QueryMetadata updateQuery(@PathVariable("id") Long id, @RequestBody CreateQuery query)
            throws QueryNotFoundException {
        return this.queryService.update(id, query.getName(), query.getQuery());
    }

    @DeleteMapping("/{id}")
    public void deleteQuery(@PathVariable("id") Long id) throws QueryNotFoundException {
        this.queryService.delete(id);
    }

    @PostMapping("/execute/{id}")
    public List<Map<String, Object>> postMethodName(@PathVariable("id") Long id,
            @RequestBody Map<String, Object> params) throws QueryNotFoundException {
        return this.queryService.execute(id, params);
    }

}
