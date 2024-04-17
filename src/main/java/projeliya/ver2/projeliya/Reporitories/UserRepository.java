package projeliya.ver2.projeliya.Reporitories;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import projeliya.ver2.projeliya.Classes.User;

@Repository
public interface UserRepository extends MongoRepository<User,String>
{
    boolean existsById(String name);

}