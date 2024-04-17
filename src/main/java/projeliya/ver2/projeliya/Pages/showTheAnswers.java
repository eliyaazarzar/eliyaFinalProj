package projeliya.ver2.projeliya.Pages;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import projeliya.ver2.projeliya.AppMainLayout;
import projeliya.ver2.projeliya.Classes.ErrorsReport;
import projeliya.ver2.projeliya.Classes.Point;
import projeliya.ver2.projeliya.Classes.Solution;
import projeliya.ver2.projeliya.Classes.User;
import projeliya.ver2.projeliya.Services.QuestionService;
import projeliya.ver2.projeliya.Services.SolutionService;
import projeliya.ver2.projeliya.Services.UserService;
import projeliya.ver2.projeliya.Services.SolutionService.SoultionsChangeListner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Route(value = "/show", layout = AppMainLayout.class)
public class showTheAnswers extends VerticalLayout {
    private final QuestionService questionService;
    private final String checkString = "✅";
    private final String exString = "❌";
    private SolutionService solutionService;
    private Button deleteSolution;
    private H6 text;
    private Grid<Solution> grid;
    private Button toggleButton;
    private Button toggleButtonForAllSoultions;
    private HorizontalLayout layoutForGrid;
    private UserService userService;
    List<Button> allButtons;

    public showTheAnswers(QuestionService questionService, UserService userService, SolutionService solutionService) {
        String user = (String) VaadinSession.getCurrent().getAttribute("user");
        this.userService = userService;
        solutionService.addSoulitonChangeListener(new SoultionsChangeListner() {
            @Override
            public void onChange() {
                UI ui = getUI().orElseThrow();
                ui.access(() -> refreshSoultions());
                String msg = "\n---> " + user;
                System.out.println(msg);
            }
        });
        this.solutionService = solutionService;
        this.questionService = questionService;
        this.grid = new Grid<>();
        allButtons = new ArrayList<Button>();
        ;
        // Set up grid configuration
        grid.addColumn(Solution::getAnswerDate).setHeader("Date");
        grid.addColumn(Solution::getNameOfWriter).setHeader("Name Of Writer");
        grid.addColumn(Solution::getNameOfQuestion).setHeader("Question Name");
        grid.addColumn(Solution::getFinalGrade).setHeader("Grade");
        grid.asSingleSelect().addValueChangeListener(event -> {
            showAnswerDetails(event.getValue());
        });
        text = new H6();
        if (user == null) {
            System.out.println("-------- User NOT Authorized - can't use chat! --------");
            UI.getCurrent().getPage().setLocation("/"); // Redirect to login page (HomePage).
            return;
        }
        HorizontalLayout buttonLayout = new HorizontalLayout();

        toggleButton = createButtonWithIcon("My Solutions", VaadinIcon.USER, "blue", event -> refreshGridForUser());
        toggleButtonForAllSoultions = createButtonWithIcon("All Solutions", VaadinIcon.GLOBE, "green",
                event -> refreshSoultions());
        deleteSolution = createButtonWithIcon("Delete Solution", VaadinIcon.TRASH, "black", event -> deleteSolution());
        deleteSolution.getStyle().set("background-color", "red");
        deleteSolution.setEnabled(false);
        allButtons.add(deleteSolution);
        allButtons.add(toggleButton);
        allButtons.add(toggleButtonForAllSoultions);
        buttonLayout.add(toggleButton, toggleButtonForAllSoultions, deleteSolution);

        HorizontalLayout layout = new HorizontalLayout();
        grid.setWidth("800px");

        String textContent = "All the Answers of the Question!";
        H1 textOpen = new H1(textContent);
        textOpen.getElement().setProperty("innerHTML", textContent);
        layoutForGrid = new HorizontalLayout();
        layout.add(grid, text);
        add(textOpen, buttonLayout, layout);
        refreshSoultions();
        grid.setVisible(true);
    }

    // Utility method to create button with icon
    private Button createButtonWithIcon(String caption, VaadinIcon icon, String color,
            ComponentEventListener<ClickEvent<Button>> listener) {
        Button button = null;
        Icon vaadinIcon = icon.create();
        vaadinIcon.setColor(color);
        if (color.equals("red")) {
            System.out.println("here red");
            button = new Button(caption, vaadinIcon, listener);
            button.addThemeVariants(ButtonVariant.LUMO_ERROR);
        } else {
            button = new Button(caption, vaadinIcon, listener);
            button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        }

        return button;
    }

    private void deleteSolution() {
        Solution selectedSolution = grid.asSingleSelect().getValue();
        solutionService.deleteSolution(selectedSolution.getId());
        text.setText("");
        deleteSolution.setVisible(false);
        refreshSoultions();

    }

    private void refreshGridForUser() {

        deleteSolution.setVisible(true);
        String username = (String) VaadinSession.getCurrent().getAttribute("user");
        List<Solution> userAnswers = solutionService.showTheAnswersForUser(username);
        grid.setItems(userAnswers);
        grid.deselectAll();
        deleteSolution.setEnabled(false);
        text.setText("");

    }

    private void refreshSoultions() {

        List<Solution> allSoltions = new ArrayList<>();
        List<Solution> Solutions = solutionService.getAllSolutions();
        Map<String, List<Solution>> groupedAnswers = new LinkedHashMap<>();
        int answerCounter = 0; // Initialize answerCounter

        // Group answers by question topics
        for (Solution Solution : Solutions) {
            allSoltions.add(Solution);
        }

        // Prepare data for grid
        refreshGridForUser();
        grid.setItems(allSoltions);
    }

    private void showAnswerDetails(Solution solution) {

        if (solution != null) {
            String user = (String) VaadinSession.getCurrent().getAttribute("user");
            User userAdmin = userService.getUserByID(user);
            if (userAdmin.isFlag() == true) {
                deleteSolution.setEnabled(true);
            } else if (solution.getNameOfWriter().equals(user)) {
                deleteSolution.setEnabled(true);
            } else {
                deleteSolution.setEnabled(false);
            }
        }
        if (solution != null) {
            StringBuilder details = new StringBuilder();
            ErrorsReport report = solution.getReport();
            details.append("<div style='text-align: left;'>");
            details.append(solution.getAnswer() + "\n");
            details.append("</div>");

            details.append("<hr>");
            details.append("<strong>בדיקת הידור:</strong>\n")
                    .append("בדיקה שקובעת האם הקוד עובר הידור \n")
                    .append("הבדיקה עברה בהצלחה" + checkString);
            details.append("<hr>");
            details.append("<strong>בדיקת תקינות של הפעולה:</strong>(30%):\n");
            details.append(
                    "\n.הבדיקה נועדה לבדוק האם אתה מבצע את הפעולה הנדרשת בכל מצב.הבדיקה מורכבת מ 10 בדיקות של מערכים כל מערך בגודל 30 ומאוכסנים בו איברים בסדרים שיאתגרו את בניית הפונקציה שלך,אם חצי מהבדיקות נכשלו אז מבחינתי הפונקציה לא תקינה -אבל אם יותר ממחצית מהבדיקות נכונות אז בכל שגיאה יורד 10 נקודות מהציון הסופי\n");
            if (report.getErrorsArraysExplain().size() != 0) {
                details.append("<strong>תוצאות הבדיקה:\n </strong>\n");
                for (int i = 0; i < report.getErrorsArraysExplain().size(); i++) {
                    details.append("לא עמדת בבדיקה מספר" + ": " + i + " אשר הייתה מורכבת מ "
                            + report.getErrorsArraysExplain().get(i));
                    details.append(exString);
                    details.append("\n");

                }
            } else {
                details.append("<strong>עמדת בכל הבדיקות שהיו" + checkString + "\n</strong>");
            }
            details.append("הסבר חישוב הציון:\n");
            details.append("100 - " + ("(כמות הטעויות*10)") + "= הציון של הבדיקה(עדיין לא הסופי)\n");
            details.append(" (המשקל בציון שקיבלנו מקודם * 0.3)=הציון הסופי לבדיקה הזאת\n");
            details.append("<hr>");
            details.append("להלן החישוב בבדיקה שלך:\n");
            details.append("שלב ראשון בחישוב:");
            details.append("100 - " + "(" + (100 - +solution.getGrade().getScoreValidtion()) + ") = "
                    + solution.getGrade().getScoreValidtion() + "\n");
            details.append("שלב שני וסופי בחישוב:");
            details.append("(" + solution.getGrade().getScoreValidtion() + " * 0.3)="
                    + solution.getGrade().getScoreValidtion() * 0.3);
            details.append("<hr>");
            details.append("<strong> סדר גודל של הפונקציה +דרך :</strong> (40%):\n")
                    .append("מציאת סדר גודל (זמן ריצה) של פונקציה ובדיקה האם הסדר גודל שנמצא הוא יעיל.")
                    .append("אם אכן הוא יעיל מספיק אתה תקבל 100% מהציון ואם לא אז 0% \n").append(".")
                    .append("את הסדר גודל אנחנו מוצאים על ידי מספר פעולות\n").append(".")
                    .append(".ניצור את הנקודות דרך יצירת מערכים בלולאה מ10 עד 50 (בקפיצות של 10),בכל הפעלה של הפונקציה על מערך מוציאים את מספר הצעדים הגבוה ביותר שנעשתה לאחר שיש לנו את כמות הצעדים אז יוצרים נקודות.\n")
                    .append("לאחר שמצאנו את הנקודות אפשר למצוא שיפועים ואז דרך השיפועים האלו אנחנו מוצאים את המרחקים בין שיפוע לשיפוע. ")
                    .append("(x = גודל המערך,y= מספר הצעדים שנעשו).\n")
                    .append("ולפי שלבים אלו ניתן למצוא את זמן הריצה של הפונקציה.");
            Point[] arrPoints = report.getArrayOfPoints();
            long[] arrSlopes = report.getArrayOfSlope();
            long[] arrDistanceOfSlopes = report.getArrayOfDistance();
            details.append("\n<hr>");
            if (arrPoints != null) {
                int counter2 = 10;
                details.append("The all Points\n");
                for (int i = 0; i < arrPoints.length; i++) {
                    if (arrPoints[i] != null) {
                        details.append("(" + (i + 1) + ")\n");
                        details.append("n = " + counter2 + "\n");
                        details.append(
                                "the Point is: " + "(" + arrPoints[i].getX() + "= X, " + arrPoints[i].getY() + "= Y)"
                                        + "\n");
                        counter2 += 10;
                    }
                }
                details.append("\nThe all slpoes\n");
                int counterForSlpoes = 0;
                for (int j = 0; j < arrSlopes.length; j++) {
                    if (arrSlopes[j] != 0) {
                        details.append("(" + (counterForSlpoes + 1) + ")");
                        details.append("the Slope is: " + arrSlopes[j] + "\t\t");
                        counterForSlpoes++;
                        details.append("\n");
                    }
                }
                details.append("The all the distances:\n");
                int counterDistance = 0;
                for (int j = 0; j < arrDistanceOfSlopes.length; j++) {
                    if (arrDistanceOfSlopes[j] != 0) {
                        details.append("(" + (counterDistance + 1) + ")\t");
                        details.append("the Distance of Slope is: " + arrDistanceOfSlopes[j] + "\n");
                        counterDistance++;
                    }
                }
                details.append("<hr>");
                if (report.getRunTimeOfFunc().equals("O(1)")) {
                    details.append("רואים שהשיפועים והמרחקים שווים לאפס לכן מסיקים שזה O(1)");

                } else if (report.getRunTimeOfFunc().equals("N")) {
                    details.append("רואים שהשיפועים שווים לכן זה O(N)");

                } else if (report.getRunTimeOfFunc().equals("N^2")) {
                    details.append("לפי המרחקים שאנחנו רואים שהם שווים ושונים מאפס אז - זמן הריצה הוא N²");

                } else if (report.getRunTimeOfFunc().equals("N^3")) {
                    details.append("לפי המרחקים ניתן לזהות שזמן הריצה של הפונקציה הוא :\n N³");
                }
            } else {
                System.out.println("בגלל שהפונקציה שלך לא תקינה -אין סיבה שנעשה את שאר הבדיקות.");
            }

            details.append("<hr>");
            details.append("<strong>בדיקת יעילות בזיכרון:</strong> (10%):\n").append(
                    "בדיקה שמודדת את היעילות של הפונקציה   -פעולה לפונקציה לעשות מיון על מערך ענק(בגודל 50 אלף)  אם כמות הזיכרון שלקחה הפעולה עומד בכמות הזיכרון היעיל שהגדרנו (שאמור לקחת לפעולה הזאת). הבדיקה מקבלת 100% מהציון. ואם לא אז 0% \n");
            details.append("חישוב הציון:\n");
            details.append("(0.1*ציון הזיכרון)= הציון הסופי של");
            details.append("<hr>");
            details.append("<strong>בדיקת יעילות בפעולה:</strong>(10%):\n")
                    .append("בדיקה שמודדת את היעילות של הפונקציה   - מדידה של הזמן שלוקח לפונקציה לעשות פעולה על מערך גדול(בגודל 50 אלף).\n  .אם הזמן שלקח עד לסיום הפעולה עומד בזמן שהגדרנו לפעולה. הבדיקה מקבלת 100% מהציון,ואם לא אז 0% \n");
            details.append("חישוב הציון:\n");
            details.append("ציון הזמן *0.1= הציון הסופי של ");
            // .append(report.getTimeRunTest());
            details.append("<hr>");
            details.append("<strong>שליחת הפונקציה אל ChatGpt: </strong>(10%):\n").append(
                    "שליחת הפונקציה שלך לצאט ");
            details.append("לדירוג מ10 עד 100 על פי הפרמטרים לעיל.");
            details.append("חישוב הציון:\n");
            details.append("קבלת הציון מהצאט*0.1= הציון הסופי של ");
            details.append("<hr>");
            details.append("<strong>ציון סופי</strong>:\n").append("<hr>").append(
                    "חישוב הציון הסופי הוא ((" + solution.getGrade().getScoreOfTime() + " * 0.1)   +("
                            + solution.getGrade().getScoreOfSteps() + "* 0.4) +   ("
                            + solution.getGrade().getScoreOfMemory() + "* 0.1) + ("
                            + solution.getGrade().getScoreOfChatGpt() + "* 0.1) + ("
                            + solution.getGrade().getScoreValidtion() + "* 0.3)) / 10\n ="
                            + solution.getGrade().getGrade());

            text.getElement().setProperty("innerHTML", details.toString());
            text.getStyle().set("white-space", "pre-line");
            text.getElement().getStyle().set("text-align", "right");
            text.setText(details.toString());
            text.getElement().getStyle().set("font-size", "20px"); // Adjust the size as needed
            text.setWidth("650px");
            text.getElement().getStyle().set("margin-left", "auto");
            text.getElement().getStyle().set("margin-right", "0");
            text.getElement().getStyle().set("padding-right", "0");
        }

    }

}