package tools;

import org.junit.Assert;
import org.junit.Test;

import tools.beans.ErrorCategory;

public class ImporterTest {
	
	@Test
	public void testCategory() {
		String a = "Expected: is \"(156.00)\" got: \"-156.00\"";
		String b = "Expected: a string containing \"MSB838A.txt\" got: \"< files>< /files>\"";
		String c = "Expected: \"AAPJ01\" got: \"\"";
		String d = "Expected: is \"191,570.80\" got: \"\"";
		String files = "Expected: a string containing \"Employee Unmatched Time Labour Report.pdf\" got: \"< files>< /files>\"";
		String files2 = "Expected: a string containing \"MSB777A.txt\" got: \"< files>< /files>\"";
		String date = "Expected: \"23/12/2017\" got: \"12/23/2017\"";
		String textEmpty = "Expected: \"AAPJ01\" got: \"\"";
		String wrongText = "Expected: is \"001\"      got: \"001 - Replace Engine\"";
		Assert.assertSame("Wrong numeric", ErrorCategory.WrongNumeric, ErrorCategory.whichCategory(a));
		//Assert.assertTrue(ErrorCategory.ExpectedFiles.match(b));
		//Assert.assertTrue(ErrorCategory.ExpectedTextNotEmpty.match(c));
		Assert.assertSame("Expected numeric not empty fail", ErrorCategory.ExpectedNumericNotEmpty, ErrorCategory.whichCategory(d));
		Assert.assertSame("Expected Files", ErrorCategory.ExpectedFiles, ErrorCategory.whichCategory(files));
		Assert.assertSame("Expected Files", ErrorCategory.ExpectedFiles, ErrorCategory.whichCategory(files2));
		
		Assert.assertSame("Expected Date", ErrorCategory.WrongDate, ErrorCategory.whichCategory(date));
		Assert.assertSame("Expected Text Not Empty", ErrorCategory.ExpectedTextNotEmpty, ErrorCategory.whichCategory(textEmpty));
		Assert.assertSame("Expected WrongText", ErrorCategory.WrongText, ErrorCategory.whichCategory(wrongText));
		
	}
}
