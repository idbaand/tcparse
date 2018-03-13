package tools.ui.components;

import com.vaadin.navigator.View;
import com.vaadin.ui.Component;

public interface TabbeableView extends View {
	String getTabCaption();
	
	Component getViewComponent();
}