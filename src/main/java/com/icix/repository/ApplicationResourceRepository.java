package com.icix.repository;


import com.icix.model.ApplicationResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationResourceRepository extends JpaRepository<ApplicationResource,Integer> {
    List<ApplicationResource> findByKey(String key);
}
