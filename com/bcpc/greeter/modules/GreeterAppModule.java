package com.bcpc.greeter.modules;

import com.bcpc.greeter.PropertyManager;
import com.bcpc.greeter.db.MessagingDao;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class GreeterAppModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(PropertyManager.class).in(Scopes.SINGLETON);	
		bind(MessagingDao.class).in(Scopes.SINGLETON);
	}

}
