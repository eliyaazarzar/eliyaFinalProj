package projeliya.ver2.projeliya.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.springframework.stereotype.Service;

import projeliya.ver2.projeliya.Classes.Question;
import projeliya.ver2.projeliya.Classes.Solution;
import projeliya.ver2.projeliya.Reporitories.QuestionRepo;

@Service

public class QuestionService 
{
    private UserService userService;
    private List<Solution> currentAnswers;
    private final QuestionRepo questionRepository;
  
 
    public QuestionService(QuestionRepo questionRepository, UserService userService) 
    {
        this.userService = userService;
        this.questionRepository = questionRepository;
    }
    public Question findById(String id)
    {
        Optional<Question> questionsList = questionRepository.findById(id);
        Question question =  questionsList.isPresent() ? questionsList.get() : null;
        if(question == null)
        {
            System.out.println("the question Null");
        }
        return question;
    }
      public List<Question> getAllQuestions() 
    {
        return questionRepository.findAll();
    }
}
