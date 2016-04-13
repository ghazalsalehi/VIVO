/*
Copyright (c) 2012, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package edu.cornell.mannlib.vitro.webapp.controller.authenticate;

import static edu.cornell.mannlib.vitro.webapp.controller.authenticate.ProgramLogin.ProgramLoginCore.PARAM_EMAIL_ADDRESS;
import static edu.cornell.mannlib.vitro.webapp.controller.authenticate.ProgramLogin.ProgramLoginCore.PARAM_NEW_PASSWORD;
import static edu.cornell.mannlib.vitro.webapp.controller.authenticate.ProgramLogin.ProgramLoginCore.PARAM_PASSWORD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import stubs.javax.servlet.ServletConfigStub;
import stubs.javax.servlet.ServletContextStub;
import stubs.javax.servlet.http.HttpServletRequestStub;
import stubs.javax.servlet.http.HttpServletResponseStub;
import stubs.javax.servlet.http.HttpSessionStub;
import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.testing.AbstractTestClass;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.PermissionSetsLoader;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;

/**
 * Test the basic features of ProgramTest.
 */
public class ProgramLoginTest extends AbstractTestClass {
	private static final Log log = LogFactory.getLog(ProgramLoginTest.class);

	private static final String NEW_USER_URI = "new_user_uri";
	private static final String NEW_USER_NAME = "new_user";
	private static final String NEW_USER_PASSWORD = "new_user_pw";
	private static final UserAccount NEW_USER = createUserAccount(NEW_USER_URI,
			NEW_USER_NAME, NEW_USER_PASSWORD, 0);

	private static final String OLD_USER_URI = "old_user_uri";
	private static final String OLD_USER_NAME = "old_user";
	private static final String OLD_USER_PASSWORD = "old_user_pw";
	private static final UserAccount OLD_USER = createUserAccount(OLD_USER_URI,
			OLD_USER_NAME, OLD_USER_PASSWORD, 10);

	private AuthenticatorStub.Factory authenticatorFactory;
	private AuthenticatorStub authenticator;
	private ServletContextStub servletContext;
	private ServletConfigStub servletConfig;
	private HttpSessionStub session;
	private HttpServletRequestStub request;
	private HttpServletResponseStub response;
	private ProgramLogin servlet;

	@Before
	public void setLogging() {
		// setLoggerLevel(this.getClass(), Level.DEBUG);
		// setLoggerLevel(ProgramLogin.class, Level.DEBUG);
	}

	@Before
	public void setup() throws Exception {
		authenticatorFactory = new AuthenticatorStub.Factory();
		authenticator = authenticatorFactory.getInstance(request);
		authenticator.addUser(NEW_USER);
		authenticator.addUser(OLD_USER);

		servletContext = new ServletContextStub();
		servletContext.setAttribute(AuthenticatorStub.FACTORY_ATTRIBUTE_NAME,
				authenticatorFactory);

		servletConfig = new ServletConfigStub();
		servletConfig.setServletContext(servletContext);

		servlet = new ProgramLogin();
		servlet.init(servletConfig);

		session = new HttpSessionStub();
		session.setServletContext(servletContext);

		request = new HttpServletRequestStub();
		request.setSession(session);
		request.setRequestUrl(new URL("http://this.that/vivo/programLogin"));
		request.setMethod("GET");

		response = new HttpServletResponseStub();
	}

	private static UserAccount createUserAccount(String uri, String name,
			String password, int loginCount) {
		UserAccount user = new UserAccount();
		user.setEmailAddress(name);
		user.setUri(uri);
		user.setPermissionSetUris(Collections
				.singleton(PermissionSetsLoader.URI_DBA));
		user.setMd5Password(Authenticator.applyMd5Encoding(password));
		user.setLoginCount(loginCount);
		user.setPasswordChangeRequired(loginCount == 0);
		return user;
	}

	@After
	public void cleanup() {
		if (servlet != null) {
			servlet.destroy();
		}
	}

	@Test
	public void noUsername() {
		executeRequest(null, null, null);
		assert403();
	}

	@Test
	public void noPassword() {
		executeRequest(OLD_USER_NAME, null, null);
		assert403();
	}

	@Test
	public void unrecognizedUser() {
		executeRequest("bogusUsername", "bogusPassword", null);
		assert403();
	}

	@Test
	public void wrongPassword() {
		executeRequest(OLD_USER_NAME, "bogusPassword", null);
		assert403();
	}

	@Test
	public void success() {
		executeRequest(OLD_USER_NAME, OLD_USER_PASSWORD, null);
		assertSuccess();
	}

	@Test
	public void newPasswordNotNeeded() {
		executeRequest(OLD_USER_NAME, OLD_USER_PASSWORD, "unneededPW");
		assert403();
	}

	@Test
	public void newPasswordMissing() {
		executeRequest(NEW_USER_NAME, NEW_USER_PASSWORD, null);
		assert403();
	}

	@Test
	public void newPasswordTooLong() {
		executeRequest(NEW_USER_NAME, NEW_USER_PASSWORD, "reallyLongPassword");
		assert403();
	}

	@Test
	public void newPasswordEqualsOldPassword() {
		executeRequest(NEW_USER_NAME, NEW_USER_PASSWORD, NEW_USER_PASSWORD);
		assert403();
	}

	@Test
	public void successWithNewPassword() {
		executeRequest(NEW_USER_NAME, NEW_USER_PASSWORD, "newerBetter");
		assertSuccess();
	}

	// ----------------------------------------------------------------------
	// Helper methods
	// ----------------------------------------------------------------------

	private void executeRequest(String email, String password,
			String newPassword) {
		if (email != null) {
			request.addParameter(PARAM_EMAIL_ADDRESS, email);
		}
		if (password != null) {
			request.addParameter(PARAM_PASSWORD, password);
		}
		if (newPassword != null) {
			request.addParameter(PARAM_NEW_PASSWORD, newPassword);
		}

		try {
			servlet.doGet(request, response);
		} catch (ServletException e) {
			log.error(e, e);
			fail(e.toString());
		} catch (IOException e) {
			log.error(e, e);
			fail(e.toString());
		}
	}

	private void assert403() {
		assertEquals("status", 403, response.getStatus());
		log.debug("Message was '" + response.getErrorMessage() + "'");
		assertEquals("logged in", false, LoginStatusBean.getBean(session)
				.isLoggedIn());
	}

	private void assertSuccess() {
		assertEquals("status", 200, response.getStatus());
		assertEquals("logged in", true, LoginStatusBean.getBean(session)
				.isLoggedIn());
	}

}
