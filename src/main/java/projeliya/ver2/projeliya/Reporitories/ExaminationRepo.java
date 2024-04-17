package projeliya.ver2.projeliya.Reporitories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import projeliya.ver2.projeliya.Classes.Examination;
@Repository
public interface ExaminationRepo extends MongoRepository<Examination,String>
{
    
}
