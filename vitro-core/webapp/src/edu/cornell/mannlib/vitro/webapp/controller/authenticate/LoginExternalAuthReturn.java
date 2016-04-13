
package edu.cornell.mannlib.vitro.webapp.controller.authenticate;

import static edu.cornell.mannlib.vitro.webapp.controller.authenticate.LoginExternalAuthSetup.ATTRIBUTE_REFERRER;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean.AuthenticationSource;
import edu.cornell.mannlib.vitro.webapp.beans.DisplayMessage;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.controller.accounts.user.UserAccountsFirstTimeExternalPage;
import edu.cornell.mannlib.vitro.webapp.controller.authenticate.Authenticator.LoginNotPermitted;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.login.LoginProcessBean;
import edu.cornell.mannlib.vitro.webapp.controller.login.LoginProcessBean.Message;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.Utils;

import com.qut.middleware.spep.filter.SPEPFilter;

/**
 * Handle the return from the external authorization login server. If we are
 * successful, record the login. Otherwise, display the failure.
 */
public class LoginExternalAuthReturn extends BaseLoginServlet {
	private static final Log log = LogFactory
			.getLog(LoginExternalAuthReturn.class);

	/**
	 * <pre>
	 * Returning from the external authorization server. If we were successful,
	 * the header will contain the name of the user who just logged in.
	 * 
	 * Deal with these possibilities: 
	 * - The header name was not configured in deploy.properties. Complain.
	 * - No username: the login failed. Complain 
	 * - User corresponds to a User acocunt. Record the login. 
	 * - User corresponds to an Individual (self-editor). 
	 * - User is not recognized.
	 * 
	 * On entry, we expect to find:
	 * - A LoginProcessBean, which will give us the afterLoginUrl if the login
	 *      succeeds.
	 * - A referrer URL, to which we will redirect if the login fails.
	 *      TODO: is this equal to LoginProcessBean.getLoginPageUrl()?
	 * These are removed on exit.
	 * </pre>
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		if (log.isDebugEnabled()) {
			@SuppressWarnings("unchecked")
			Enumeration<String> names = req.getHeaderNames();

			log.debug("------------request:" + req.getRequestURL());
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				log.debug(name + "=" + req.getHeader(name));
			}
		}
		
		HttpSession httpSession = req.getSession();	// CR 022 - 19/06/2013
		List<Object> userIdentifiers;
		HashMap<String, List<Object>> attributeMap = (HashMap<String, List<Object>>) httpSession.getAttribute(SPEPFilter.ATTRIBUTES);
		
		if (attributeMap == null){
			log.debug("attributeMap is null");
			complainAndReturnToReferrer(req, resp, ATTRIBUTE_REFERRER,
					MESSAGE_LOGIN_FAILED);
		}
		
		userIdentifiers = attributeMap.get("uid");
		
		if (userIdentifiers == null)
		{
			log.debug("userIdentifiers is null");
			complainAndReturnToReferrer(req, resp, ATTRIBUTE_REFERRER,
					MESSAGE_LOGIN_FAILED);
		}	

		
		/*String externalAuthId = ExternalAuthHelper.getHelper(req) 	
				.getExternalAuthId(req);
		log.debug("externalAuthID='" + externalAuthId + "'");*/

		String externalAuthId = (String) userIdentifiers.get(0);
		if (externalAuthId == null) {
			complainAndReturnToReferrer(req, resp, ATTRIBUTE_REFERRER,
					MESSAGE_LOGIN_FAILED);
			return;
		}
	
		String afterLoginUrl = req.getParameter(Utils.ESOE_REDIRECT_URL_PRAM_NAME);	// LIBRDF-9
		if (afterLoginUrl == null){
			afterLoginUrl = LoginProcessBean.getBean(req).getAfterLoginUrl();	// LIBRDF-87
		}

		removeLoginProcessArtifacts(req);

		UserAccount userAccount = getAuthenticator(req)
				.getAccountForExternalAuth(externalAuthId);

		if (!getAuthenticator(req).isUserPermittedToLogin(userAccount)) {
			log.debug("Logins disabled for " + userAccount);
			complainAndReturnToReferrer(req, resp, ATTRIBUTE_REFERRER,
					MESSAGE_LOGIN_DISABLED);
			return;
		}

		if (userAccount == null) {
			log.debug("Creating new account for " + externalAuthId
					+ ", return to '" + afterLoginUrl + "'");
			//UserAccountsFirstTimeExternalPage.setExternalLoginInfo(req,
					//externalAuthId, afterLoginUrl);
			
			List<Object> lstFirstName = attributeMap.get("givenName");	// CR 022 - 19/06/2013
			List<Object> lstLastName = attributeMap.get("sn");
			List<Object> lstEmailAddresses = attributeMap.get("mail");
			
			String firstName = "";
			String lastName = "";
			String emailAddress = "";
			
			if (lstFirstName != null){
				firstName = (String)lstFirstName.get(0);
			}
			
			if (lstLastName != null){
				lastName = (String)lstLastName.get(0);
			}
			
			if (lstEmailAddresses != null){
				emailAddress = (String)lstEmailAddresses.get(0);
			}
			
			UserAccountsFirstTimeExternalPage.setExternalLoginInfo(req,	
					externalAuthId, afterLoginUrl, firstName, lastName, emailAddress);
			
			resp.sendRedirect(UrlBuilder.getUrl("/accounts/firstTimeExternal"));
			return;
		}

		try {
			log.debug("Logging in as " + userAccount.getUri());
			getAuthenticator(req).recordLoginAgainstUserAccount(userAccount,
					AuthenticationSource.EXTERNAL);
			new LoginRedirector(req, afterLoginUrl).redirectLoggedInUser(resp);
			return;
		} catch (LoginNotPermitted e) {
			// should have been caught by isUserPermittedToLogin()
			log.debug("Logins disabled for " + userAccount);
			complainAndReturnToReferrer(req, resp, ATTRIBUTE_REFERRER,
					MESSAGE_LOGIN_DISABLED);
			return;
		}
	}

	@Override
	protected void complainAndReturnToReferrer(HttpServletRequest req,
			HttpServletResponse resp, String sessionAttributeForReferrer,
			Message message, Object... args) throws IOException {
		DisplayMessage.setMessage(req, message.formatMessage(args));
		super.complainAndReturnToReferrer(req, resp,
				sessionAttributeForReferrer, message, args);
	}

	private void removeLoginProcessArtifacts(HttpServletRequest req) {
		req.getSession().removeAttribute(ATTRIBUTE_REFERRER);
		LoginProcessBean.removeBean(req);
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
