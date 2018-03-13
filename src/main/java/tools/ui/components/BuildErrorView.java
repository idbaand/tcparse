package tools.ui.components;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowCountCallbackHandler;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import tools.EverythingService;
import tools.ui.util.ViewUtil;

@SuppressWarnings("serial")
public class BuildErrorView extends HorizontalLayout implements TabbeableView {

    static final String VIEW_NAME = "Build Error";
    Grid grid;
    HeaderRow headerFilter;
    public BuildErrorView() {
        grid = new Grid("Error summary");
        grid.setSizeFull();
        
        addComponent(grid);
        setExpandRatio(grid, 1);
        setSizeFull();
        
    }

    IndexedContainer createContainer(String category, int build) {
        IndexedContainer ic = new IndexedContainer();
        EverythingService.getBuildStep(this.build, new BuildErrorRowCallback(ic));
        return ic;
    }
    
    void setupFilters() {
        if (headerFilter != null)
            grid.removeHeaderRow(headerFilter);
        
        headerFilter = grid.appendHeaderRow();
        setupFilter(grid, headerFilter, "CATEGORY");
        setupFilter(grid, headerFilter, "ACTUALVALUE");
    }

    void setupFilter(Grid grid, HeaderRow headerFilter, String propertyId) {
        TextField filterText = new TextField();
        filterText.setImmediate(true);

        HeaderCell cell = headerFilter.getCell(propertyId);
        cell.setComponent(filterText);

        filterText.addValueChangeListener(evt -> {
            String filter = (String) evt.getProperty().getValue();
            IndexedContainer container = (IndexedContainer) grid.getContainerDataSource();

            container.removeContainerFilters(propertyId);
            if (filter != null && filter.trim().length() > 0) {
                container.addContainerFilter(propertyId, filter, true, false);
            }

            grid.recalculateColumnWidths();
        });
    }

    @Override
    public void enter(ViewChangeEvent event) {
        String parameters = event.getParameters();
        if (!StringUtils.isEmpty(parameters)) {
            
        }

    }

    class BuildErrorRowCallback extends RowCountCallbackHandler {

        IndexedContainer container;
        String lastValue;
        int lastCount;

        BuildErrorRowCallback(IndexedContainer container) {
            this.container = container;
        }

        @Override
        protected void processRow(ResultSet rs, int rowNum) throws SQLException {
            if (rowNum == 0) {
                for (int i = 0; i < getColumnCount(); i++) {
                    container.addContainerProperty(getColumnNames()[i],
                            SQLTypeMap.toClass(getColumnTypes()[i]), null);
                }
            }

            Item item = container.addItem(rowNum);
            for (int i = 0; i < getColumnCount(); i++) {
                String colName = getColumnNames()[i];
                int colType = getColumnTypes()[i];
                if (colType == Types.CLOB) {
                    Clob clob = rs.getClob(colName);
                    if (clob != null)
                        item.getItemProperty(colName)
                                .setValue(clob.getSubString(1, (int) clob.length()));
                } else {
                    item.getItemProperty(colName).setValue(rs.getObject(colName));
                }
            }
        }

    }

    /**
     * Converts database types to Java class types.
     */
    public static class SQLTypeMap {
        /**
         * Translates a data type from an integer (java.sql.Types value) to a string that represents the corresponding class.
         * 
         * @param type The java.sql.Types value to convert to its corresponding class.
         * @return The class that corresponds to the given java.sql.Types value, or Object.class if the type has no known mapping.
         */
        public static Class<?> toClass(int type) {
            Class<?> result = Object.class;

            switch (type) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.CLOB:
                result = String.class;
                break;

            case Types.NUMERIC:
            case Types.DECIMAL:
                result = java.math.BigDecimal.class;
                break;

            case Types.BIT:
                result = Boolean.class;
                break;

            case Types.TINYINT:
                result = Byte.class;
                break;

            case Types.SMALLINT:
                result = Short.class;
                break;

            case Types.INTEGER:
                result = Integer.class;
                break;

            case Types.BIGINT:
                result = Long.class;
                break;

            case Types.REAL:
            case Types.FLOAT:
                result = Float.class;
                break;

            case Types.DOUBLE:
                result = Double.class;
                break;

            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                result = Byte[].class;
                break;

            case Types.DATE:
                result = java.sql.Date.class;
                break;

            case Types.TIME:
                result = java.sql.Time.class;
                break;

            case Types.TIMESTAMP:
                result = java.sql.Timestamp.class;
                break;
            }

            return result;
        }
    }

    @Override
    public String getTabCaption() {
        return VIEW_NAME + " - " + build;
    }

    @Override
    public Component getViewComponent() {
        return this;
    }
}
