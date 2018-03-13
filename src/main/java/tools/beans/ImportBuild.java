package tools.beans;

import java.io.File;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ImportBuild {

	@NotNull
	Integer build;
	
	@NotNull
	@Size(min=1, max=200)
	String regression;

	String source;
	
	boolean purgeTable;
	
	boolean upload;
	
	File uploadeFile;
	
	boolean uploadComplete;
	
	public Integer getBuild() {
		return build;
	}
	public void setBuild(Integer build) {
		this.build = build;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public boolean isPurgeTable() {
		return purgeTable;
	}
	public void setPurgeTable(boolean purgeTable) {
		this.purgeTable = purgeTable;
	}
	public boolean isUpload() {
		return upload;
	}
	public void setUpload(boolean upload) {
		this.upload = upload;
	}
	public File getUploadeFile() {
		return uploadeFile;
	}
	public void setUploadeFile(File uploadeFile) {
		this.uploadeFile = uploadeFile;
	}
	public boolean isUploadComplete() {
		return uploadComplete;
	}
	public void setUploadComplete(boolean uploadComplete) {
		this.uploadComplete = uploadComplete;
	}
	
	public String getRegression() {
        return regression;
    }
    public void setRegression(String regression) {
        this.regression = regression;
    }
    public void reset() {
		build = null;
		source = null;
		this.regression = null;
		this.purgeTable = false;
		this.upload = false;
		this.uploadeFile = null;
		this.uploadComplete = false;
	}
}
