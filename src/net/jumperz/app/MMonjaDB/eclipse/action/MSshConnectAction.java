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
 */
package net.jumperz.app.MMonjaDB.eclipse.action;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.jumperz.app.MMonjaDB.eclipse.Activator;
import net.jumperz.app.MMonjaDB.eclipse.dialog.MPasswordDialog;
import net.jumperz.app.MMonjaDBCore.action.MConnectAction;
import net.jumperz.util.MRegEx;
import net.jumperz.util.MStringUtil;

import org.eclipse.swt.widgets.Shell;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import com.mongodb.Mongo;

public class MSshConnectAction extends MConnectAction {
	// mj connect ssh joe@www.example.jp:22 ~/.ssh/id_dsa 127.0.0.1:27017/dbname
	private String actionStr;

	private int lPort;

	private String lHost = "127.0.0.1";

	private int sshPort;

	private String sshHost;

	private int mongoPort;

	private String mongoHost;

	private String sshUser;

	private String identityFileName;

	private String dbName;

	private Session session;

	public MSshConnectAction() {
	}

	public String getName() {
		return "SSH " + mongoHost + ":" + mongoPort + "/" + sshHost + ":" + sshPort;
	}

	public boolean parse(String action) {
		actionStr = action;
		if (!action.matches("^mj connect ssh.*")) {
			return false;
		}
		String[] array = action.split(" ");

		/*
		 * if( array.length != 6 ) { return false; }
		 */

		try {
			String sshStr = array[3];

			// get sshUser
			if (sshStr.indexOf('@') > -1) {
				sshUser = MRegEx.getMatch("([^@]+)@", sshStr);
				sshStr = sshStr.substring(sshStr.indexOf('@') + 1);
			} else {
				sshUser = System.getProperty("user.name");
			}

			// get sshHost
			if (sshStr.indexOf(':') > 0) {
				sshPort = MStringUtil.parseInt(MRegEx.getMatch(":([0-9]+)", sshStr));
				sshHost = sshStr.substring(0, sshStr.indexOf(':'));
			} else {
				sshPort = default_ssh_port;
				sshHost = sshStr;
			}
			debug("sshUser:" + sshUser);
			debug("sshHost:" + sshHost);
			debug("sshPort:" + sshPort);

			// dbName
			String mongoStr = array[4];
			if (mongoStr.indexOf('/') == -1) {
				dbName = "test";
			} else {
				dbName = MRegEx.getMatch("/(.*)$", mongoStr);
				mongoStr = mongoStr.substring(0, mongoStr.indexOf('/'));
			}

			// port and host
			if (mongoStr.indexOf(':') > -1) {
				mongoPort = MStringUtil.parseInt(MRegEx.getMatch(":([0-9]+)", mongoStr));
				mongoHost = mongoStr.substring(0, mongoStr.indexOf(':'));
			} else {
				mongoPort = default_mongo_port;
				mongoHost = mongoStr;
			}
			debug("dbName:" + dbName);
			debug("mongoPort:" + mongoPort);
			debug("mongoHost:" + mongoHost);

			// key file
			identityFileName = MRegEx.getMatch("\"([^\"]+)\"$", action);
			debug("--" + identityFileName + "--");
			if (!(new File(identityFileName)).exists()) {
				debug("identify file not file:" + identityFileName);
			}
		} catch (Exception e) {
			debug(e);
			return false;
		}
		return true;
	}

	public boolean equals(Object o) {
		if (o.getClass().equals(this.getClass())) {
			MSshConnectAction c = (MSshConnectAction) o;
			if (this.actionStr.equals(c.actionStr)) {
				return true;
			}
		}
		return false;
	}

	public void close() {
		mongo.close();
		session.disconnect();
	}

	public void executeFunction() throws Exception {
		checkExistingConnection();

		JSch jsch = new JSch();
		if ((new File(identityFileName)).exists()) {
			String identityContent = MStringUtil.loadStrFromFile(identityFileName);
			if (identityContent.indexOf("ENCRYPTED") > -1) {
				jsch.addIdentity(identityFileName, promptPassword("SSH Key Password").getBytes());
			} else {
				jsch.addIdentity(identityFileName);
			}
		}
		session = jsch.getSession(sshUser, sshHost, sshPort);
		UserInfo ui = new MyUserInfo();
		session.setUserInfo(ui);
		session.connect();
		lPort = session.setPortForwardingL(lHost, 0, mongoHost, mongoPort);
		mongo = new Mongo(lHost, lPort);
		db = mongo.getDB(dbName);

		/*
		 * (new Thread(){ public void run() { try { Thread.sleep( 30000 ); session.delPortForwardingL( lHost, lPort ); } catch( Exception e ) { e.printStackTrace(); } } }).start();
		 */
	}

	public static String promptPassword(final String prompt) {
		final Set set = Collections.synchronizedSet(new HashSet());
		final Object mutex = new Object();
		String passwd;

		final Shell shell = Activator.getDefault().getShell();
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {// *****

				MPasswordDialog dialog = new MPasswordDialog(shell, prompt, set);
				dialog.open();
				synchronized (mutex) {
					mutex.notify();
				}

			}
		});

		synchronized (mutex) {
			try {
				mutex.wait();
			} catch (Exception e) {
			}
		}

		if (set.size() == 1) {
			passwd = set.iterator().next() + "";
			return passwd;
		} else {
			return null;
		}

	}

	public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {
		private String passwd;

		public String getPassword() {
			return passwd;
		}

		public boolean promptYesNo(String str) {
			return true;
		}

		// JTextField passwordField=(JTextField)new JPasswordField(20);

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {

			/* --> eclipse */
			passwd = MSshConnectAction.promptPassword(message);
			if (passwd == null) {
				return false;
			} else {
				return true;
			}
			/* <-- eclipse */

			/*
			 * Object[] ob={passwordField}; int result= JOptionPane.showConfirmDialog(null, ob, message, JOptionPane.OK_CANCEL_OPTION); if(result==JOptionPane.OK_OPTION){ passwd=passwordField.getText(); return true; } else{ return false; }
			 */
		}

		public void showMessage(String message) {
			JOptionPane.showMessageDialog(null, message);
		}

		final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

		private Container panel;

		public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());

			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.gridx = 0;
			panel.add(new JLabel(instruction), gbc);
			gbc.gridy++;

			gbc.gridwidth = GridBagConstraints.RELATIVE;

			JTextField[] texts = new JTextField[prompt.length];
			for (int i = 0; i < prompt.length; i++) {
				gbc.fill = GridBagConstraints.NONE;
				gbc.gridx = 0;
				gbc.weightx = 1;
				panel.add(new JLabel(prompt[i]), gbc);

				gbc.gridx = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.weighty = 1;
				if (echo[i]) {
					texts[i] = new JTextField(20);
				} else {
					texts[i] = new JPasswordField(20);
				}
				panel.add(texts[i], gbc);
				gbc.gridy++;
			}

			if (JOptionPane.showConfirmDialog(null, panel, destination + ": " + name, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
				String[] response = new String[prompt.length];
				for (int i = 0; i < prompt.length; i++) {
					response[i] = texts[i].getText();
				}
				return response;
			} else {
				return null; // cancel
			}
		}
	}

}