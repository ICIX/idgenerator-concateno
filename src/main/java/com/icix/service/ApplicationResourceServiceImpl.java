package com.icix.service;


import com.icix.model.ApplicationResource;
import com.icix.repository.ApplicationResourceRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
        return applicationResourceRepository.findByKey(key).get(0).getValue();
    }
}
