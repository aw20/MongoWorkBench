/* 
 *  MongoWorkBench is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  Free Software Foundation,version 3.
 *  
 *  MongoWorkBench is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  If not, see http://www.gnu.org/licenses/
 *  
 *  Additional permission under GNU GPL version 3 section 7
 *  
 *  If you modify this Program, or any covered work, by linking or combining 
 *  it with any of the JARS listed in the README.txt (or a modified version of 
 *  (that library), containing parts covered by the terms of that JAR, the 
 *  licensors of this Program grant you additional permission to convey the 
 *  resulting work. 
 *  
 *  https://github.com/aw20/MongoWorkBench
 *  Original fork: https://github.com/Kanatoko/MonjaDB
 *  
 *  March 2013
 */
package org.aw20.mongoworkbench.eclipse.dialog;

import java.util.Map;

import org.aw20.util.StringUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import swing2swt.layout.BorderLayout;

public class ServerDialog extends Dialog {

	protected Object result;

	protected Shell shlConnectionProperties;
	private Text textHost;
	private Text textPort;
	private Text textDatabase;
	private Text textName;
	private Text textSSH;
	private Text textPrivateKeyFile;
	private Text textUsername;
	private Text textPassword;
	private Button btnDirectConnection;
	private Button btnSshConnection;
	private Label lblStatus;
	
	private FileDialog 		loadDialog;
	private Map	attributes;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ServerDialog(Shell parent) {
		this( parent, SWT.DIALOG_TRIM );
	}
	
	public ServerDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(Map attributes) {
		this.attributes	= attributes;
		
		createContents();
		setAttributes();
		
		shlConnectionProperties.open();
		shlConnectionProperties.layout();
		Display display = getParent().getDisplay();
		while (!shlConnectionProperties.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	
	private boolean validateAttributes(){
		lblStatus.setText("");
		
		if ( textName.getText().trim().length() == 0 ){
			lblStatus.setText("no name given");
			return false;
		}
		
		if ( textHost.getText().trim().length() == 0 ){
			lblStatus.setText("no host given");
			return false;
		}
		
		if ( textPort.getText().trim().length() == 0 ){
			lblStatus.setText("no port given");
			return false;
		}
		
		if ( StringUtil.toInteger(textPort.getText().trim(), -1) == -1 ){
			lblStatus.setText("invalid port");
			return false;
		}

		return true;
	}
	
	public void retriveAttributes(){
		attributes.put("name", textName.getText().trim() );
		attributes.put("host", textHost.getText().trim() );
		attributes.put("port", textPort.getText().trim() );

		attributes.put("username", textUsername.getText().trim() );
		attributes.put("password", textPassword.getText().trim() );

		attributes.put("sshprivatekey", textPrivateKeyFile.getText().trim() );
		attributes.put("textSSH", textUsername.getText().trim() );
		
		if ( btnDirectConnection.getSelection() ){
			attributes.put("direct", true);
		}else{
			attributes.remove("direct");
		}
	}
	
	public Map getAttributes(){
		return attributes;
	}
	
	
	private void setAttributes(){
		if ( attributes.containsKey("name") )
			textName.setText( (String)attributes.get("name") );

		if ( attributes.containsKey("host") )
			textHost.setText( (String)attributes.get("host") );
		else
			textHost.setText( "127.0.0.1" );

		if ( attributes.containsKey("port") )
			textPort.setText( (String)attributes.get("port") );
		else
			textPort.setText( "27017" );
			
		if ( attributes.containsKey("database") )
			textDatabase.setText( (String)attributes.get("database") );
		
		if ( attributes.containsKey("username") )
			textUsername.setText( (String)attributes.get("username") );
		
		if ( attributes.containsKey("password") )
			textPassword.setText( (String)attributes.get("password") );
		
		if ( attributes.containsKey("sshprivatekey") )
			textPrivateKeyFile.setText( (String)attributes.get("sshprivatekey") );
		
		if ( attributes.containsKey("sshhost") )
			textSSH.setText( (String)attributes.get("sshhost") );
		
		if ( attributes.containsKey("direct") ){
			btnDirectConnection.setSelection(true);
			btnSshConnection.setSelection(false);
			textSSH.setEnabled(false);
			textPrivateKeyFile.setEnabled(false);
		}else{
			btnDirectConnection.setSelection(false);
			btnSshConnection.setSelection(true);
			textSSH.setEnabled(true);
			textPrivateKeyFile.setEnabled(true);
		}
	}
	
	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlConnectionProperties = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shlConnectionProperties.setSize(450, 394);
		shlConnectionProperties.setText("Connection Properties");
		shlConnectionProperties.setLayout(new BorderLayout(10, 10));
		
		Composite composite = new Composite(shlConnectionProperties, SWT.NONE);
		composite.setLayoutData(BorderLayout.SOUTH);
		composite.setLayout(new GridLayout(4, false));
		
		lblStatus = new Label(composite, SWT.NONE);
		GridData gd_lblStatus = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblStatus.widthHint = 236;
		lblStatus.setLayoutData(gd_lblStatus);
		new Label(composite, SWT.NONE);
		
		Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnNewButton.setText("Ok");
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ( validateAttributes() ){
					result	= true;
					retriveAttributes();
					shlConnectionProperties.close();				
				}
			}
		});
		
		Button btnCancelButton = new Button(composite, SWT.NONE);
		btnCancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result	= false;
				shlConnectionProperties.close();
			}
		});
		btnCancelButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnCancelButton.setText("Cancel");
		
		Composite composite_1 = new Composite(shlConnectionProperties, SWT.NONE);
		composite_1.setLayoutData(BorderLayout.CENTER);
		composite_1.setLayout(new FormLayout());
		
		Group grpServer = new Group(composite_1, SWT.NONE);
		FormData fd_grpServer = new FormData();
		fd_grpServer.top = new FormAttachment(0, 10);
		fd_grpServer.left = new FormAttachment(0, 10);
		fd_grpServer.bottom = new FormAttachment(0, 143);
		fd_grpServer.right = new FormAttachment(0, 434);
		grpServer.setLayoutData(fd_grpServer);
		grpServer.setText("Server");
		grpServer.setLayout(new GridLayout(3, false));
		
		Label lblName = new Label(grpServer, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("Name:");
		
		textName = new Text(grpServer, SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(grpServer, SWT.NONE);
		
		Label lblHost = new Label(grpServer, SWT.NONE);
		lblHost.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHost.setText("Host:");
		
		textHost = new Text(grpServer, SWT.BORDER);
		textHost.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(grpServer, SWT.NONE);
		
		Label lblPort = new Label(grpServer, SWT.NONE);
		lblPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPort.setText("Port:");
		
		textPort = new Text(grpServer, SWT.BORDER);
		textPort.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		new Label(grpServer, SWT.NONE);
		
		Label lblDatabase = new Label(grpServer, SWT.NONE);
		lblDatabase.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDatabase.setText("Database:");
		
		textDatabase = new Text(grpServer, SWT.BORDER);
		GridData gd_textDatabase = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_textDatabase.widthHint = 151;
		textDatabase.setLayoutData(gd_textDatabase);
		
		Label lblCanBeBlank = new Label(grpServer, SWT.NONE);
		lblCanBeBlank.setText("may be blank");
		
		Group grpConnection = new Group(composite_1, SWT.NONE);
		grpConnection.setLayout(new GridLayout(5, false));
		FormData fd_grpConnection = new FormData();
		fd_grpConnection.top = new FormAttachment(grpServer, 6);
		fd_grpConnection.left = new FormAttachment(0, 10);
		fd_grpConnection.right = new FormAttachment(0, 434);
		grpConnection.setLayoutData(fd_grpConnection);
		grpConnection.setText("Connection");
		
		Group grpAuthentication = new Group(composite_1, SWT.NONE);
		fd_grpConnection.bottom = new FormAttachment(grpAuthentication, -6);
		
		btnDirectConnection = new Button(grpConnection, SWT.RADIO);
		btnDirectConnection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnDirectConnection.setSelection(true);
				btnSshConnection.setSelection(false);
				textSSH.setEnabled(false);
				textPrivateKeyFile.setEnabled(false);
			}
		});
		btnDirectConnection.setSelection(true);
		btnDirectConnection.setText("Direct Connection");
		new Label(grpConnection, SWT.NONE);
		
		Label lblSsh = new Label(grpConnection, SWT.NONE);
		lblSsh.setToolTipText("user@host(:port)");
		lblSsh.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSsh.setText("SSH:");
		
		textSSH = new Text(grpConnection, SWT.BORDER);
		textSSH.setToolTipText("user@host(:port)");
		textSSH.setEnabled(false);
		textSSH.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(grpConnection, SWT.NONE);
		
		btnSshConnection = new Button(grpConnection, SWT.RADIO);
		btnSshConnection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnDirectConnection.setSelection(false);
				btnSshConnection.setSelection(true);
				textSSH.setEnabled(true);
				textPrivateKeyFile.setEnabled(true);
			}
		});
		btnSshConnection.setText("SSH Connection");
		new Label(grpConnection, SWT.NONE);
		
		Label lblPrivateKeyFile = new Label(grpConnection, SWT.NONE);
		lblPrivateKeyFile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPrivateKeyFile.setText("Private Key File:");
		
		textPrivateKeyFile = new Text(grpConnection, SWT.BORDER);
		textPrivateKeyFile.setToolTipText("Full pathname to the private key file");
		textPrivateKeyFile.setEnabled(false);
		textPrivateKeyFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnPrivateKeyFileBrowse = new Button(grpConnection, SWT.NONE);
		btnPrivateKeyFileBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String keyFileName = loadDialog.open();
				if (keyFileName != null) {
					textPrivateKeyFile.setText(keyFileName);
				}
			}
		});
		
		btnPrivateKeyFileBrowse.setEnabled(false);
		btnPrivateKeyFileBrowse.setText("Browse");
		grpAuthentication.setLayout(new GridLayout(2, false));
		FormData fd_grpAuthentication = new FormData();
		fd_grpAuthentication.top = new FormAttachment(0, 240);
		fd_grpAuthentication.bottom = new FormAttachment(100);
		fd_grpAuthentication.left = new FormAttachment(0, 10);
		fd_grpAuthentication.right = new FormAttachment(0, 434);
		grpAuthentication.setLayoutData(fd_grpAuthentication);
		grpAuthentication.setText("Authentication");
		
		Label lblUsername = new Label(grpAuthentication, SWT.NONE);
		lblUsername.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUsername.setText("Username:");
		
		textUsername = new Text(grpAuthentication, SWT.BORDER);
		textUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblPassword = new Label(grpAuthentication, SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPassword.setText("Password:");
		
		textPassword = new Text(grpAuthentication, SWT.BORDER);
		textPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		loadDialog = new FileDialog(getParent(), SWT.OPEN);
	}
}
