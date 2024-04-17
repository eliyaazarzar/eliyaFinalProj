package projeliya.ver2.projeliya;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("")
public class AppMainLayouGuess extends AppLayout {

    public AppMainLayouGuess() {
        String userName = (String) VaadinSession.getCurrent().getAttribute("user");
        String userDisplayName = ("👤: " + (userName != null ? userName : "Guest"));
        Span userSpan = new Span(userDisplayName);

        DrawerToggle drawerToggle = new DrawerToggle();
        drawerToggle.getElement().setAttribute("aria-label", "Menu toggle");

        addToNavbar(drawerToggle);
        addToDrawer(userSpan);
    }
}
