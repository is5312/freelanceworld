package com.bcpc.greeter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {

	private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

	public static void close(AutoCloseable resource) {

		try {
			if (null != resource) {
				resource.close();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}
}
