package projeliya.ver2.projeliya.Reporitories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import projeliya.ver2.projeliya.Classes.Solution;

@Repository
public interface SolutionRepo  extends MongoRepository<Solution, Integer> 
{
}
