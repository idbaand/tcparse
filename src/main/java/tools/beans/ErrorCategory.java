package tools.beans;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

public enum ErrorCategory {
	TimeOut("[Tt]imed out"),
	NoSuchElement("no such element"),
	WrongDate("Expected: (is )?\"\\d{2}/\\d{2}/\\d{4}\"[\\s]+got: \"\\d{2}/\\d{2}/\\d{4}\""),
	ExpectedDateNotEmpty("Expected: is \"\\d{2}/\\d{2}/\\d{4}\" got: \"\""),
	WrongNumeric("Expected: (is )?\"[\\(< ]?[\\-\\d\\.,]+[\\)>]?\"\\s+got: \"[\\(< ]?[\\-\\d\\.,]+[\\)>]?\""),
	ExpectedNumericNotEmpty("Expected: (is )?\"[\\-\\d\\.,]+\"\\s+got: \"\""),
	ExpectedTextNotEmpty("Expected: (is )?\"[^\"]+\"\\s+got: \"\""),
	ExpectedCollection("Expected: a collection"),
    WrongText("Expected: (is )?\"(\\s+)?[^\"]+\"\\s+got: \"(\\s+)?[^\"]+\""),
    ExpectedEmpty("Expected: (is )?\"[\\s]*\""),
    GridButton("Grid button"),
    GridCell("Grid Cell"),
    GridFlow("Grid flow button"),
    ImplementMe("Implement me"),
    InvalidElementState("invalid element state"),
    StaleElement("stale element reference"),
    ToolbarButton("Toolbar button"),
    UnknownError("Unknown error"),
    WaitedForWidgetValue("Waited for Widget value"),
    WidgetEditableStatus("Widget [\\w']+ editable status"),
    ApplicationNameWrong("Application name wrong"),    
    GridCannotSelectPastEndRows("Cannot select past end of rows"),
	CannotSelectItem("Cannot select item"),
	GridUnableMatchRowCount("Unable to match row count"),
	GridExpectedToHaveResults("Expected grid [\\w]+ to have results"),
	GridExpectedToHaveNoResults("Expected grid [\\w]+ to have no results"),
	GridColumnNotFound("Column named [\\w]+ not found"),
	NoToolbarAction("Could not get toolbar action "),
	ExpectedScreenMessage("Expected the screen to have (no )?messages"),
	ExpectedError("Expected to have errors"),
	ExpectedFiles("Expected: a string containing \"[\\w ]+(\\.txt|\\.pdf|\\.log)?\"[\\s]+got: \"< files>< /files>\"");
		
	
	String pattern;
	Pattern regex;
	ErrorCategory(String pattern) {
		this.pattern = pattern;
		this.regex = Pattern.compile(pattern);
	}
	
	public boolean match(String string) {
		Matcher m = this.regex.matcher(string);
		return m.find();
	}
	
	public static ErrorCategory whichCategory(String string) {
		for (ErrorCategory cat : ErrorCategory.values()) {
			if (cat == UnknownError) continue;
			
			if (cat.match(StringEscapeUtils.unescapeHtml4(string)))
				return cat;
		}
		
		return UnknownError;
	}
}
