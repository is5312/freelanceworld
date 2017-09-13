package com.bcpc.greeter.modules;

import com.google.inject.servlet.ServletModule;
import com.bcpc.greeter.GreetingServlet;

public class GreeterServletModule extends ServletModule {

    @Override
    public void configureServlets() {
        super.configureServlets();
        serve("/sms").with(GreetingServlet.class);
    }

}
