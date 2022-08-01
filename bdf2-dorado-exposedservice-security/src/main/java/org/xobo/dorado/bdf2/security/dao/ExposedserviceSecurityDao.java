package org.xobo.dorado.bdf2.security.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.bstek.bdf2.core.CoreHibernateDao;
import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.bdf2.core.exception.NoneLoginException;
import com.bstek.bdf2.core.model.Url;

public class ExposedserviceSecurityDao extends CoreHibernateDao {
  @Transactional(readOnly = true)
  public Collection<String> getUrls() {
    IUser user = ContextHolder.getLoginUser();
    if (user == null) {
      throw new NoneLoginException("Please login first");
    }
    String companyId = user.getCompanyId();
    if (StringUtils.isNotEmpty(getFixedCompanyId())) {
      companyId = getFixedCompanyId();
    }
    String hql = "select u.url from " + Url.class.getName() + " u where u.companyId=:companyId";
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("companyId", companyId);
    return this.query(hql, map);
  }
}
