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
 */
package net.jumperz.app.MMonjaDB.eclipse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;

import net.jumperz.app.MMonjaDB.eclipse.action.MSshConnectAction;
import net.jumperz.app.MMonjaDB.eclipse.pref.MPrefManager;
import net.jumperz.app.MMonjaDBCore.MAbstractLogAgent;
import net.jumperz.app.MMonjaDBCore.MConstants;
import net.jumperz.app.MMonjaDBCore.MDataManager;
import net.jumperz.app.MMonjaDBCore.MStdoutView;
import net.jumperz.app.MMonjaDBCore.action.MActionManager;
import net.jumperz.app.MMonjaDBCore.event.MEventManager;
import net.jumperz.util.MLogServer;
import net.jumperz.util.MProperties;
import net.jumperz.util.MStreamUtil;
import net.jumperz.util.MSystemUtil;

import org.aw20.util.FileUtil;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements MConstants {
	public static final String PLUGIN_ID = "MongoWorkBench"; //$NON-NLS-1$

	private static Activator plugin;
	private File configFile, commandFile;
	private volatile Shell shell;

	public Activator() {
	}

	public synchronized void setShell(Shell s) {
		if (shell == null) {
			shell = s;
			MMenuManager.getInstance().initMenus();
		}
	}

	public Shell getShell() {
		return shell;
	}

	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		
		// no console found, so create a new ones
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	private void setupConsole() {
		MessageConsole mc = findConsole(CONSOLE_NAME);
		MessageConsoleStream out = mc.newMessageStream();
		PrintStream ps = new PrintStream(out);
		MAbstractLogAgent.enabled = true;
		MLogServer.getInstance().setSimpleOut(ps);
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		setupConsole();
		loadConfig();
		MPrefManager.getInstance().init();

		MLogServer.getInstance().addIgnoredClassName("MAbstractView");
		MEventManager.getInstance().register2(MDataManager.getInstance());
		MEventManager.getInstance().register2(new MStdoutView());
		MEventManager.getInstance().register2(new MAuthManager());
		MActionManager.getInstance().addAction("^mj connect ssh.*", MSshConnectAction.class);
	}

	public void loadConfig() throws IOException {
		Location location = Platform.getConfigurationLocation();
		if (location != null) {
			URL configURL = location.getURL();
			if (configURL != null && configURL.getProtocol().startsWith("file")) {
				File platformDir = new File(configURL.getFile(), Activator.PLUGIN_ID);
				MSystemUtil.createDir(platformDir.getAbsolutePath());
				String configFileName = platformDir.getAbsolutePath() + "/" + DEFAULT_CONFIG_FILE_NAME;
				
				commandFile	= new File( platformDir.getAbsolutePath(), "command.txt" );
				
				loadConfig(configFileName);
			}
		} else {
			loadConfig("_dummy_not_exist_");
		}
	}

	private void loadConfig(String configFileName) throws IOException {
		MProperties prop = new MProperties();
		configFile = new File(configFileName);
		InputStream in = null;
		
		try{
			
			if (configFile.exists() && configFile.isFile()) {
				in = new FileInputStream(configFile);
			} else {
				in = MStreamUtil.getResourceStream("net/jumperz/app/MMonjaDB/eclipse/resources/" + DEFAULT_CONFIG_FILE_NAME);
			}
			prop.load(in);
		
		}finally{
			if ( in != null )
				in.close();
		}

		MDataManager.getInstance().setProp(prop);
	}

	public void saveConfig() throws IOException {
		OutputStream out = new FileOutputStream(configFile);
		try {
			MDataManager.getInstance().getProp().store(out);
		} finally {
			out.close();
		}
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		saveConfig();
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}

	public void saveWorkBench(String text) {
		try {
			FileUtil.writeToFile(commandFile, text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getWorkBench() {
		try {
			return FileUtil.readToString(commandFile);
		} catch (IOException e) {
			return null;
		}
	}

}
