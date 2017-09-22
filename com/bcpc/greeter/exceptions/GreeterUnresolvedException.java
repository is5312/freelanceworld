package com.bcpc.greeter.exceptions;

public class GreeterUnresolvedException extends Exception{
	
	private static final long serialVersionUID = 7864142855117723499L;

	GreeterUnresolvedException(String message)
	{
		super(message);	
	}

}
