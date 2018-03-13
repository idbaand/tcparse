package tools.ui.components;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.Window;

import tools.EverythingService;
import tools.Importer;
import tools.beans.ImportBuild;

@SuppressWarnings("serial")
public class ImportBuildDialog extends Window implements Receiver, SucceededListener {

	TextField buildField;
	ComboBox regressionField;
	TextField sourceField;
	Button importButton;

	ImportBuild bean;
	BeanFieldGroup<ImportBuild> binder;
	UI ui;

	public ImportBuildDialog(UI ui) {
		setModal(true);
		this.ui = ui;
		FormLayout layout = new FormLayout();
		
		bean = new ImportBuild();
		binder  = new BeanFieldGroup<>(ImportBuild.class);
		
		binder.setItemDataSource(bean);
		importButton = new Button("Import", ev -> submit());
		TextField buildField = binder.buildAndBind("Build No.", "build", TextField.class);
		TextField sourceField = binder.buildAndBind("Source", "source", TextField.class);
		CheckBox purgeTableField = binder.buildAndBind("Purge Table?", "purgeTable", CheckBox.class);
		CheckBox isUploadField = binder.buildAndBind("Upload?", "upload", CheckBox.class);
		regressionField  = binder.buildAndBind("Regression", "regression", ComboBox.class);
		regressionField.setInvalidAllowed(false);
		regressionField.setNullSelectionAllowed(false);
		regressionField.addItems(EverythingService.getRegressions());
		
		Upload uploadField = new Upload("Upload", this);
		uploadField.addSucceededListener(this);
		uploadField.setEnabled(false);
		uploadField.addStartedListener(ev -> {
			importButton.setEnabled(false);
			importButton.setCaption("Uploading...");
		});
		
		isUploadField.addValueChangeListener(event -> {
			Boolean bool = (Boolean)event.getProperty().getValue();
			uploadField.setEnabled(bool);
		});
		
		layout.addComponent(buildField);
		layout.addComponent(sourceField);
		layout.addComponent(uploadField);
		
		HorizontalLayout cboxLayout = new HorizontalLayout();
		cboxLayout.addComponent(purgeTableField);
		cboxLayout.addComponent(isUploadField);
		
		layout.addComponent(cboxLayout);
		
		// Button bar
		HorizontalLayout actions = new HorizontalLayout();
		actions.addComponent(importButton);
		
		layout.addComponent(actions);
		this.setContent(layout);
		setWidth("600px");
        setHeight("300px");
        layout.setMargin(true);
        center();
        
		ui.addWindow(this);
	}

	void submit() {
		try {
			binder.commit();
			if (binder.isValid()) {
				Importer importer = new Importer()
						.withBuild(bean.getBuild());
				if(bean.isPurgeTable())
					importer.dropTable();
				
				importer.deleteBuild();
				Object source = null;
				if (bean.isUpload() && bean.isUploadComplete()) {
					source = bean.getUploadeFile();
				} else {
					if (bean.getSource() != null && bean.getSource().length() > 0) {
						source = bean.getSource();
					}
				}
				
				if (source == null) {
					throw new IllegalStateException("source is not set.");
				}
				
				importer.doImport(source);
				ui.removeWindow(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
		} finally {
			bean.reset();
		}
	}
	
	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		try {
			File file = Files.createTempFile(null, filename).toFile();
			FileOutputStream fos = new FileOutputStream(file);
			bean.setUploadeFile(file);
			return fos;
		} catch (IOException e) {
			Notification.show("Failed upload file:" + e.getMessage());
			return null;
		}
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		Notification.show("File uploaded successfully");
		this.importButton.setEnabled(true);
		this.importButton.setCaption("Import");
		this.bean.setUploadComplete(true);
	}
}
