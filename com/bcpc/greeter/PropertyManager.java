package com.bcpc.greeter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PropertyManager {

	private static final Logger log = LoggerFactory.getLogger(PropertyManager.class);
	private static final String propertyFile = "/WEB-INF/cfg/config.properties";
	private final Properties props;
	private static boolean isInitialized = false;

	@Inject
	PropertyManager(Properties props) {
		this.props = new Properties();
	}
		
	public static boolean isInitialized() {
		return isInitialized;
	}

	public void load(Path path) {
		InputStream iStream = null;
		try {
			iStream = Files.newInputStream(path);
			props.load(iStream);
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
			props.load(iStream);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			FileUtil.close(iStream);
		}

		log.info(props.toString());
	}

	public String getStringProp(String key) {
		return props.getProperty(key);
	}
}
