package com.oracle.smartDB;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyCostRepo extends CrudRepository<DailyCostDo, String> {

}