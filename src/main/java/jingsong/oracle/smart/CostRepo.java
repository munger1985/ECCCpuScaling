package jingsong.oracle.smart;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CostRepo    extends CrudRepository<CostDo, String> {

}