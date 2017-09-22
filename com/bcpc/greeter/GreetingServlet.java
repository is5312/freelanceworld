package com.bcpc.greeter;

import static com.bcpc.greeter.Constants.ACCOUNT_SID;
import static com.bcpc.greeter.Constants.ACCOUNT_TOKEN;
import static com.bcpc.greeter.Constants.SUCCESS_MSG_REPLY;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bcpc.greeter.exceptions.GreeterException;
import com.bcpc.greeter.exceptions.GreeterUnresolvedException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.twilio.Twilio;
import com.twilio.twiml.Body;
import com.twilio.twiml.Message;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.TwiMLException;

@Singleton
public class GreetingServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(GreetingServlet.class);

	private static final long serialVersionUID = 1L;

	private final MessageProcessor processor;
	private final PropertyManager props;

	@Inject
	GreetingServlet(MessageProcessor processor, PropertyManager props) {
		this.processor = processor;
		this.props = props;
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {

		props.load(request.getServletContext());
		
		String accId = props.getStringProp(ACCOUNT_SID);
		String accToken = props.getStringProp(ACCOUNT_TOKEN);

		MessagingResponse mResponse = null;

		log.info(String.format("Initializing twilio using accId : %s, accToken: %s", accId, accToken));

		try {

			Twilio.init(accId, accToken);

			String fromNumber = request.getParameter("From");
			String body = request.getParameter("Body");

			processor.processMessage(body, fromNumber);

			mResponse = new MessagingResponse.Builder().message(buildMessage(SUCCESS_MSG_REPLY)).build();

		} catch (GreeterException e) {
			log.error(e.getMessage(), e);
			mResponse = new MessagingResponse.Builder().message(buildMessage(e.getMessage())).build();
		} catch (GreeterUnresolvedException e) {
			log.error(e.getMessage(), e);
			mResponse = new MessagingResponse.Builder().message(buildMessage(SUCCESS_MSG_REPLY)).build();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			mResponse = new MessagingResponse.Builder().message(buildMessage(SUCCESS_MSG_REPLY)).build();
		}
		response.setContentType("application/xml");

		try {
			response.getWriter().write(mResponse.toXml());
		} catch (IOException | TwiMLException e) {
			log.error(e.getMessage(), e);
		}
	}

	private Message buildMessage(String message) {
		Body body = new Body(message);
		return new Message.Builder().body(body).build();
	}
}
