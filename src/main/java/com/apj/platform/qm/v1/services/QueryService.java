package com.apj.platform.qm.v1.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.apj.platform.commons.beans.UsernameAuditorAware;
import com.apj.platform.qm.v1.entities.QueryMetadata;
import com.apj.platform.qm.v1.repos.QueryRepo;
import com.apj.platform.qm.v1.services.exceptions.QueryNotFoundException;

@Service
public class QueryService {

    private final QueryRepo queryRepo;
    private final JdbcTemplate jsJdbcTemplate;
    private final UsernameAuditorAware usernameAuditorAware;

    QueryService(QueryRepo queryRepo, JdbcTemplate jdbcTemplate, UsernameAuditorAware usernameAuditorAware) {
        this.queryRepo = queryRepo;
        this.jsJdbcTemplate = jdbcTemplate;
        this.usernameAuditorAware = usernameAuditorAware;
    }

    public QueryMetadata create(String name, String query) {
        String queryInLowerCase = query.toLowerCase();
        QueryMetadata queryMetadata = new QueryMetadata();
        queryMetadata.setName(name);
        queryMetadata.setQuery(query);
        queryMetadata.setSelectQuery(queryInLowerCase.startsWith("select"));
        queryMetadata.setContainsReqParams(queryInLowerCase.contains("${req."));
        queryMetadata.setContainsSysParams(queryInLowerCase.contains("${sys."));
        return this.queryRepo.save(queryMetadata);
    }

    public QueryMetadata read(Long id) throws QueryNotFoundException {
        return this.queryRepo.findById(id).orElseThrow(() -> new QueryNotFoundException(id));
    }

    public List<QueryMetadata> listAll() {
        return this.queryRepo.findAll();
    }

    public QueryMetadata update(Long id, String name, String query) throws QueryNotFoundException {
        QueryMetadata queryMetadata = read(id);
        if (StringUtils.hasText(name)) {
            queryMetadata.setName(name);
        }
        if (StringUtils.hasText(query)) {
            queryMetadata.setQuery(query);
        }
        String queryInLowerCase = queryMetadata.getQuery().toLowerCase();
        queryMetadata.setSelectQuery(queryInLowerCase.startsWith("select"));
        queryMetadata.setContainsReqParams(queryInLowerCase.contains("${req."));
        queryMetadata.setContainsSysParams(queryInLowerCase.contains("${sys."));
        return this.queryRepo.save(queryMetadata);
    }

    public void delete(Long id) throws QueryNotFoundException {
        QueryMetadata queryMetadata = read(id);
        this.queryRepo.delete(queryMetadata);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> execute(Long id, Map<String, Object> params) throws QueryNotFoundException {
        QueryMetadata queryMetadata = read(id);
        String query = queryMetadata.getQuery();
        if (queryMetadata.isContainsReqParams()) {
            query = applyReqParams(query, params);
        }
        if (queryMetadata.isContainsSysParams()) {
            query = applySysParams(query);
        }
        if (queryMetadata.isSelectQuery()) {
            return this.jsJdbcTemplate.queryForList(query);
        } else {
            this.jsJdbcTemplate.execute(query);
            return Collections.EMPTY_LIST;
        }
    }

    private String applyReqParams(String query, Map<String, Object> params) {
        for (Entry<String, Object> entrySet : params.entrySet()) {
            String key = entrySet.getKey();
            String formattedKey = "\\$\\{req." + String.valueOf(key) + "\\}";
            query = query.replaceAll(formattedKey, String.valueOf(entrySet.getValue()));
        }
        return query;
    }

    private String applySysParams(String query) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", this.usernameAuditorAware.getCurrentAuditor().orElse(""));
        params.put("timestamp", System.currentTimeMillis());
        for (Entry<String, Object> entrySet : params.entrySet()) {
            String key = entrySet.getKey();
            String formattedKey = "\\$\\{sys." + String.valueOf(key) + "\\}";
            query = query.replaceAll(formattedKey, String.valueOf(entrySet.getValue()));
        }
        return query;
    }
}
