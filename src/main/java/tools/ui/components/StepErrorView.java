package tools.ui.components;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;

import tools.EverythingService;
import tools.beans.StepErrorCount;

@SuppressWarnings("serial")
public class StepErrorView extends HorizontalLayout implements TabbeableView {

	static final String VIEW_NAME = "Build Error Summary";
	
	Grid grid;
	public StepErrorView() {
		grid = new Grid(VIEW_NAME);
		grid.setSizeFull();
		addComponent(grid);
		setExpandRatio(grid, 1);
		setSizeFull();
	}

	IndexedContainer createContainer(String cat) {
		List<Integer> lastBuilds = EverythingService.getLastNBuild(cat, 5);
		Map<String, StepErrorCount> results = EverythingService.getStepErrorCount(cat, lastBuilds.toArray(new Integer[0]));
		IndexedContainer ic = new IndexedContainer();
		ic.addContainerProperty("category", String.class, null);
		for (int build : lastBuilds) {
			ic.addContainerProperty("build-" + build, Integer.class, null);
		}

		if (results != null) {
			for (Map.Entry<String, StepErrorCount> entry : results.entrySet()) {

				final Item item = ic.addItem(entry.getKey());
				item.getItemProperty("category").setValue(entry.getKey());

				StepErrorCount bean = entry.getValue();
				for (int build : lastBuilds) {
					item.getItemProperty("build-" + build).setValue(bean.getErrorCount(build));
				}
			}
		}
		
		return ic;
	}

	@Override
	public void enter(ViewChangeEvent event) {
	    String params = event.getParameters();
	    if (StringUtils.isNotEmpty(params)) {
	        grid.setContainerDataSource(createContainer(params.split("/")[1]));
	        grid.recalculateColumnWidths();
	    }
		
	}

	@Override
	public String getTabCaption() {
		return VIEW_NAME;
	}

	@Override
	public Component getViewComponent() {
		return this;
	}
}
