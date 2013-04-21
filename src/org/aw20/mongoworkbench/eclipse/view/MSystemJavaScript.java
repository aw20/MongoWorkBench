package org.aw20.mongoworkbench.eclipse.view;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Cursor;

public class MSystemJavaScript extends Composite {
	private Text textBox;
	private String HELPURL = "http://docs.mongodb.org/manual/tutorial/store-javascript-function-on-server/";

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MSystemJavaScript(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(4, true);
		setLayout(gridLayout);
		
		textBox = new Text(this, SWT.BORDER);
		GridData gd_textBox = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_textBox.widthHint = 1340;
		gd_textBox.heightHint = 236;
		textBox.setLayoutData(gd_textBox);
		new Label(this, SWT.NONE);
		
		Label urlLabel = new Label(this, SWT.WRAP);
		urlLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		urlLabel.setText(HELPURL);
		urlLabel.setToolTipText("click here to visit the java documentation");
		urlLabel.setCursor( new Cursor( this.getDisplay(), SWT.CURSOR_HAND ) );
		urlLabel.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {}

			@Override
			public void mouseDown(MouseEvent e) {}

			@Override
			public void mouseUp(MouseEvent e) {
				try {
					java.awt.Desktop.getDesktop().browse(java.net.URI.create(HELPURL));
				} catch (IOException e1) {}
			}
			
		});
		
		Button btnValidate = new Button(this, SWT.NONE);
		btnValidate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnValidate.setText("validate");
		btnValidate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onValidate();
			}

		});
		
		Button btnSave = new Button(this, SWT.NONE);
		btnSave.setText("save");
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onSave();
			}
		});
		

	}
	private void onValidate() {
		
	}
	
	private void onSave() {
		
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
