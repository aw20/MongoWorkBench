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
package org.aw20.mongoworkbench.eclipse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.aw20.io.StreamUtil;
import org.aw20.mongoworkbench.eclipse.pref.MPrefManager;
import org.aw20.mongoworkbench.eclipse.view.MAbstractView;
import org.aw20.mongoworkbench.eclipse.view.MDocumentView;
import org.aw20.util.FileUtil;
import org.aw20.util.StringUtil;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements MConstants {
	public static final String PLUGIN_ID = "org.aw20.MongoWorkBench"; //$NON-NLS-1$

	private static Activator plugin;
	private File configFile, commandFile, serverListFile;
	private volatile Shell shell;

	public synchronized void setShell(Shell s) {
		if (shell == null) {
			shell = s;
			MMenuManager.getInstance().initMenus();
		}
	}

	public void showView( String viewname ){
		showView(viewname, null);
	}
	
	public Object showView( String viewname, String id ){
		try {
			IWorkbench wb = PlatformUI.getWorkbench();
			
			IWorkbenchWindow wbw = null;
			if ( wb.getActiveWorkbenchWindow() == null ){
				wbw	= wb.getWorkbenchWindows()[0];
			}else{
				wbw	= wb.getActiveWorkbenchWindow();
			}
			
			Object v = (MAbstractView)wbw
					.getActivePage()
					.showView( viewname, id, IWorkbenchPage.VIEW_VISIBLE );
			
			if ( id != null && v instanceof MDocumentView )
				((MDocumentView)v).setViewTitle( id );

			return v;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Shell getShell() {
		return shell;
	}

	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		loadConfig();
		MPrefManager.getInstance().init();
	}

	public void loadConfig() throws IOException {
		Location location = Platform.getConfigurationLocation();
		if (location != null) {
			URL configURL = location.getURL();
			if (configURL != null && configURL.getProtocol().startsWith("file")) {
				File platformDir = new File(configURL.getFile(), Activator.PLUGIN_ID);
				createDir(platformDir.getAbsolutePath());
				String configFileName = platformDir.getAbsolutePath() + "/" + DEFAULT_CONFIG_FILE_NAME;
				
				commandFile	= new File( platformDir.getAbsolutePath(), "command.txt" );
				serverListFile	= new File( platformDir.getAbsolutePath(), "serverlist.bin" );
				
				loadConfig(configFileName);
			}
		} else {
			loadConfig("_dummy_not_exist_");
		}
	}

	private void loadConfig(String configFileName) throws IOException {
		configFile = new File(configFileName);
		InputStream in = null;
		
		try{
			
			if (configFile.exists() && configFile.isFile()) {
				in = new FileInputStream(configFile);
			} else {
				in = StreamUtil.getResourceStream("org/aw20/mongoworkbench/eclipse/resources/" + DEFAULT_CONFIG_FILE_NAME);
			}
		
		}finally{
			if ( in != null )
				in.close();
		}
		
	}

	public void saveConfig() throws IOException {
		OutputStream out = new FileOutputStream(configFile);
		try {

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

	public File	getScratchPadsFile(){
		return new File( commandFile.getParent(), "scratchpads.bin" );
	}

	public List<Map> getServerList() {
		List<Map> serverList	= (List<Map>)FileUtil.loadClass(serverListFile);
		if ( serverList == null )
			return new ArrayList<Map>();
		else
			return serverList;
	}

	public void saveServerList(List<Map> serverList){
		FileUtil.saveClass(serverListFile, serverList);
	}

	public Map<String, Object> getServerMap(String sName) {
		Iterator<Map>	it	= getServerList().iterator();
		while ( it.hasNext() ){
			Map	m = it.next();
			if ( m.get("name").equals(sName) )
				return m;
		}
		return null;
	}
	
	public List<Map> removeServerMap(String sName) {
		List<Map> list = getServerList();
		Iterator<Map>	it	= list.iterator();
		while ( it.hasNext() ){
			Map	m = it.next();
			if ( m.get("name").equals(sName) ){
				it.remove();
				break;
			}
		}
		return list;
	}

	public MongoClient getMongoClient(String sName) throws UnknownHostException{
		Map<String, Object>	props	= getServerMap( sName );
		if ( props == null )
			return null;
		
		
		if ( props.containsKey("username") && props.get("username").toString().length() > 0 
				&& props.containsKey("password") && props.get("password").toString().length() > 0){
			StringBuilder sb = new StringBuilder(32);
			sb.append("mongodb://")
				.append(props.get("username") )
				.append(":")
				.append(props.get("password"))
				.append("@")
				.append(props.get("host"))
				.append(":")
				.append( StringUtil.toInteger(props.get("port"), 27017) );
			
			if ( props.containsKey("database") && props.get("database").toString().length() > 0 )
				sb.append("/").append(props.get("database"));
		
			return new MongoClient( new MongoClientURI(sb.toString()) );
		}else
			return new MongoClient( (String)props.get("host"), StringUtil.toInteger(props.get("port"), 27017) );
	}
	
	
	private String createDir(String dirName) throws IOException {
		File dir = new File(dirName);
		if (dir.exists()) {
			if (!dir.isDirectory()) {
				throw new IOException("Couldn't make directory: " + dir.getCanonicalPath());
			}
		} else {
			if (!dir.mkdirs()) {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {}
				if (!dir.isDirectory()) {
					throw new IOException("Couldn't make directory: " + dir.getCanonicalPath());
				}
			}
		}
		return dir.getCanonicalPath();
	}
}
