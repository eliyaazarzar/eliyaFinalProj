package projeliya.ver2.projeliya.Services;
import org.springframework.stereotype.Service;

import projeliya.ver2.projeliya.Classes.Examination;
import projeliya.ver2.projeliya.Reporitories.ExaminationRepo;

import java.util.Optional;
@Service
public class ExaminationService {

    private ExaminationRepo examinationRepo;

    public ExaminationService(ExaminationRepo examinationRepo) {
        this.examinationRepo = examinationRepo;
    }

    public Examination getById(String id) {
            Optional<Examination> examinationOptional = examinationRepo.findById(id);
            Examination examination =  examinationOptional.isPresent() ? examinationOptional.get() : null;
       return examination;
    }
}
