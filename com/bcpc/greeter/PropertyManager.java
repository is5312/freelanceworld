package com.bcpc.greeter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

@Singleton
public class PropertyManager {

	private final Logger log = LoggerFactory.getLogger(PropertyManager.class);

	private static final String propertyFilePath = "/var/matrix/config.properties";
	private static final String propertyFile = "/WEB-INF/cfg/config.properties";
	private static final String logback_prop_file = "logback.xml";
	private static final String logback_prop_file_path = "/WEB-INF/cfg/" + logback_prop_file;
	private static final String LOG_ROOT = "LOG_ROOT";


	private final Properties props;
	private static boolean isInitialized = false;

	@Inject
	PropertyManager(Properties props) {
		this.props = props;
	}

	public static boolean isInitialized() {
		return isInitialized;
	}
	
	

	public static void setInitialized(boolean isInitialized) {
		PropertyManager.isInitialized = isInitialized;
	}

	public void load(Path path) {
		InputStream iStream = null;
		try {
			iStream = Files.newInputStream(path);
			props.load(iStream);
			Path logFlPath = path.resolveSibling(logback_prop_file);
			Map<String, String> logProps = new HashMap<String, String>();
			logProps.put(LOG_ROOT, Paths.get((logFlPath.getParent() + "/..")).normalize().toString());
			configureLogging(logFlPath.toString(), logProps);
			setInitialized(true);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			FileUtil.close(iStream);
		}
		log.info(props.toString());
	}
	
	public void load(ServletContext ctx) {
		InputStream iStream = null;

		if (isInitialized) {
			return;
		}
		try {
			iStream = ctx.getResourceAsStream(propertyFile);
			//Path propPath = Paths.get(propertyFilePath);
			//iStream = Files.newInputStream(propPath);
			props.load(iStream);
			String realPath = ctx.getRealPath(logback_prop_file_path);
			Map<String, String> logProps = new HashMap<String, String>();
			logProps.put(LOG_ROOT, props.getProperty("LOG_ROOT", ""));
			configureLogging(realPath, logProps);
			setInitialized(true);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			FileUtil.close(iStream);
		}

		log.info(props.toString());
	}

	private void configureLogging(String realPath, Map<String, String> props) {
		LoggerContext logCtx = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator config = new JoranConfigurator();
		config.setContext(logCtx);
		logCtx.reset();

		if (props != null) {
			props.forEach((k, v) -> logCtx.putProperty(k, v));
		}
		try {
			config.doConfigure(realPath);
		} catch (JoranException e) {
			log.error(e.getMessage(), e);
		}
	}

	public String getStringProp(String key) {
		return props.getProperty(key);
	}
}
