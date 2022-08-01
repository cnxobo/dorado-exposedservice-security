package org.xobo.dorado.bdf2.security.service;

import java.util.Collection;
import org.springframework.security.access.AccessDeniedException;
import org.xobo.dorado.bdf2.security.dao.ExposedserviceSecurityDao;
import org.xobo.dorado.exposedservice.security.api.DoradoUrlProvider;
import org.xobo.dorado.exposedservice.security.api.UrlAuthorizationProvider;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.bdf2.core.security.SecurityUtils;
import com.bstek.bdf2.core.security.UserAuthentication;

public class BDF2UrlAuthorizationProvider implements UrlAuthorizationProvider, DoradoUrlProvider {

  public boolean authorize(String url) {
    boolean hasAuthorize = false;
    UserAuthentication authentication = new UserAuthentication(ContextHolder.getLoginUser());
    try {
      SecurityUtils.checkUrl(authentication, url);
      hasAuthorize = true;
    } catch (AccessDeniedException e) {

    }
    return hasAuthorize;
  }

  public Collection<String> getUrls() {
    return exposedserviceSecurityDao.getUrls();
  }

  private ExposedserviceSecurityDao exposedserviceSecurityDao;

  public BDF2UrlAuthorizationProvider(ExposedserviceSecurityDao exposedserviceSecurityDao) {
    this.exposedserviceSecurityDao = exposedserviceSecurityDao;
  }


}
