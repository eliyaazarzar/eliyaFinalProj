package projeliya.ver2.projeliya.Services;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import projeliya.ver2.projeliya.Classes.Solution;
import projeliya.ver2.projeliya.Reporitories.SolutionRepo;

@Service
public class SolutionService 
{
    
    private SolutionRepo SolutionRepo;

    private ArrayList<SoultionsChangeListner> listeners; 
    
    public interface SoultionsChangeListner 
    {
       public void onChange();
    }   
    public SolutionService(SolutionRepo SolutionRepo) {
        listeners = new ArrayList<>();
        this.SolutionRepo = SolutionRepo;
    }

    public List<Solution> getAllSolutions()
    {
        return SolutionRepo.findAll();
    }
    
    public void addSoulitonChangeListener(SoultionsChangeListner listener)
    {
       synchronized (listeners)
       {
        for(int i = 0; i < listeners.size(); i++)
        {
            if(listeners.get(i).equals(listener))
            {
                System.out.println("the Listner is exsits");
                return;
            }
        }
          listeners.add(listener);
       }
    }
    public void addSolution(Solution solution) {
        List<Solution> listOfSolutions = getAllSolutions();
        int num;
        if (listOfSolutions == null || listOfSolutions.isEmpty()) {
            num = 0;
        } else {
            num = listOfSolutions.get(listOfSolutions.size() - 1).getId() + 1;
        }
        solution.setId(num);
    
        SolutionRepo.insert(solution);
        synchronized (listeners) 
        {
            for (SoultionsChangeListner listener : listeners) {
                try{
                listener.onChange();
                }catch (Exception e)
                {
                    System.out.println("the listener not get Change");
                }
            }
        }
    }
    public void removeCanvasChangeListener(SoultionsChangeListner listner) {

        synchronized(listeners) 
        {
            listeners.remove(listner); // Remove all listeners
        }
    }
    
    public void deleteSolution(int id) {
        
    Optional<Solution> solution = SolutionRepo.findById(id);
        if(solution != null)
        {
        SolutionRepo.delete(solution.orElse(null));
        }
    }
    public  List<Solution>  showTheAnswersForUser(String name) 
    {
        List<Solution> listOfSolutions = getAllSolutions();
        List<Solution>listOfSolutionsUser = new ArrayList();
        for(int i=0; i<listOfSolutions.size(); i++) 
        {
            if(listOfSolutions.get(i).getNameOfWriter().equals(name)) 
            {
                listOfSolutionsUser.add(listOfSolutions.get(i));
            }
        } 
        return listOfSolutionsUser;
    }




}
