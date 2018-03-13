package tools.ui.components;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import tools.EverythingService;
import tools.ui.MainUI;

/**
 * Content of the UI when the user is logged in.
 * 
 * 
 */
public class MainScreen extends VerticalLayout {
    private Menu menu;

    MainUI ui;

    public MainScreen(MainUI ui) {
        this.ui = ui;
        setStyleName("main-screen");
        HorizontalLayout layout = new HorizontalLayout();

        MenuBar.Command newBuildCommand = new MenuBar.Command() {

            @Override
            public void menuSelected(MenuItem selectedItem) {
                new ImportBuildDialog(ui);
            }
        };

        // CssLayout viewContainer = new CssLayout();
        TabSheet viewContainer = new TabSheet();
        viewContainer.addStyleName("valo-content");
        viewContainer.setSizeFull();

        final Navigator navigator = new Navigator(ui, view -> {
            TabbeableView tabView = (TabbeableView) view;
            TabSheet.Tab tab = viewContainer.addTab(tabView.getViewComponent(), tabView.getTabCaption());
            tab.setClosable(true);
            viewContainer.setSelectedTab(tab);
        });
        navigator.setErrorView(ErrorView.class);
        menu = new Menu(navigator);
        // menu.addView(new AboutView(), "", AboutView.VIEW_NAME, FontAwesome.INFO_CIRCLE);
        for (String cat : EverythingService.getRegressions()) {
            menu.addView(StepErrorView.class, cat, _url(StepErrorView.class, cat, null), "Steps Error View", FontAwesome.INFO_CIRCLE);
            addBuildMenus(cat, menu);
        }

        menu.addCommand("Import new build", newBuildCommand);
        navigator.addViewChangeListener(viewChangeListener);

        layout.addComponent(menu);
        layout.addComponent(viewContainer);
        layout.setExpandRatio(viewContainer, 1);

        addComponent(layout);
        setExpandRatio(layout, 1);

        layout.setSizeFull();
        setSizeFull();
    }

    String _url(Class<?> view, String category, String build) {
        String href = view.getSimpleName() + "/" + category;
        if (!StringUtils.isEmpty(build)) {
            href += "/" + build;
        }

        return href;
    }

    void addBuildMenus(String category, Menu menu) {
        List<Integer> builds = EverythingService.getLastNBuild(category, 10);
        for (int build : builds) {
            menu.addView(BuildErrorView.class, category, _url(BuildErrorView.class, category, String.valueOf(build)), 
                    "Build " + build, FontAwesome.INFO_CIRCLE);
        }
    }

    // notify the view menu about view changes so that it can display which view
    // is currently active
    ViewChangeListener viewChangeListener = new ViewChangeListener() {

        @Override
        public boolean beforeViewChange(ViewChangeEvent event) {
            return true;
        }

        @Override
        public void afterViewChange(ViewChangeEvent event) {
            menu.setActiveView(event.getViewName());
        }

    };

}
