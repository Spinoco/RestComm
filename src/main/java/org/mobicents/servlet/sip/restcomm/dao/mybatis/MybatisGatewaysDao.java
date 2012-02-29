/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.mobicents.servlet.sip.restcomm.dao.mybatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import org.mobicents.servlet.sip.restcomm.Gateway;
import org.mobicents.servlet.sip.restcomm.annotations.concurrency.ThreadSafe;
import org.mobicents.servlet.sip.restcomm.dao.GatewaysDao;
import static org.mobicents.servlet.sip.restcomm.dao.DaoUtils.*;

/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
@ThreadSafe public final class MybatisGatewaysDao implements GatewaysDao {
  private static final String namespace = "org.mobicents.servlet.sip.restcomm.dao.GatewaysDao.";
  private final SqlSessionFactory sessions;
  
  public MybatisGatewaysDao(final SqlSessionFactory sessions) {
    super();
    this.sessions = sessions;
  }
  
  @Override public void addGateway(final Gateway gateway) {
    final SqlSession session = sessions.openSession();
    try {
      session.insert(namespace + "addGateway", toMap(gateway));
    } finally {
      session.close();
    }
  }

  @Override public List<Gateway> getGateways() {
    final SqlSession session = sessions.openSession();
    try {
      @SuppressWarnings("unchecked")
      final List<Map<String, Object>> results = (List<Map<String, Object>>)session.selectList(namespace + "getGateways");
      final List<Gateway> gateways = new ArrayList<Gateway>();
      if(results != null && !results.isEmpty()) {
        for(final Map<String, Object> result : results) {
          gateways.add(toGateway(result));
        }
      }
      return gateways;
    } finally {
      session.close();
    }
  }

  @Override public void removeGateway(final String name) {
    final SqlSession session = sessions.openSession();
    try {
      session.delete(namespace + "removeGateway", name);
    } finally {
      session.close();
    }
  }

  @Override public void updateGateway(final Gateway gateway) {
    final SqlSession session = sessions.openSession();
    try {
      session.update(namespace + "updateGateway", toMap(gateway));
    } finally {
      session.close();
    }
  }
  
  private Gateway toGateway(final Map<String, Object> map) {
    final String name = readString(map.get("name"));
    final String password = readString(map.get("password"));
    final String proxy = readString(map.get("proxy"));
    final Boolean register = readBoolean(map.get("register"));
    final String user = readString(map.get("user"));
    return new Gateway(name, password, proxy, register, user);
  }
  
  private Map<String, Object> toMap(final Gateway gateway) {
    final Map<String, Object> map = new HashMap<String, Object>();
    map.put("name", gateway.getName());
    map.put("password", gateway.getPassword());
    map.put("proxy", gateway.getProxy());
    map.put("register", gateway.register());
    map.put("user", gateway.getUser());
    return map;
  }
}