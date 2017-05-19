package com.icix.service;


import com.icix.model.ApplicationResource;
import com.icix.repository.ApplicationResourceRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationResourceServiceImpl implements ApplicationResourceService {

    private final Logger logger = Logger.getLogger(ApplicationResourceServiceImpl.class);
    private final ApplicationResourceRepository applicationResourceRepository;

    @Autowired
    public ApplicationResourceServiceImpl(ApplicationResourceRepository applicationResourceRepository) {
        this.applicationResourceRepository = applicationResourceRepository;
    }

    @Override
    public byte[] findByKey(String key) {
        List<ApplicationResource> resources = applicationResourceRepository.findByKey(key);
        if(resources != null && resources.isEmpty() == false){
            return resources.get(0).getValue();
        }
        return new byte[0];
    }
}
