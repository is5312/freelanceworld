package com.bcpc.greeter.exceptions;

import com.bcpc.greeter.exceptions.GreeterException.ErrorCode;

public class GreeterUnresolvedException extends Exception{
	
	private static final long serialVersionUID = 7864142855117723499L;

	GreeterUnresolvedException(String message, ErrorCode code)
	{
		super(String.format("%s : %s : %s",message,code.toString(),code.getId()));	
	}

}
