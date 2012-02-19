package org.mobicents.servlet.sip.restcomm.dao.mybatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import org.mobicents.servlet.sip.restcomm.AvailablePhoneNumber;
import org.mobicents.servlet.sip.restcomm.annotations.concurrency.ThreadSafe;
import org.mobicents.servlet.sip.restcomm.dao.AvailablePhoneNumberDao;

@ThreadSafe public final class MybatisAvailablePhoneNumberDao implements AvailablePhoneNumberDao {
  private static final String namespace = "org.mobicents.servlet.sip.restcomm.dao.mybatis.AvailablePhoneNumberDao.";
  private static final char[] lookupTable = new char[] {'2', '2', '2', '3', '3', '3', '4', '4', '4', '5', '5', '5',
      '6', '6', '6', '7', '7', '7', '7', '8', '8', '8', '9', '9', '9', '9'};
  private final SqlSessionFactory sessions;
  
  public MybatisAvailablePhoneNumberDao(final SqlSessionFactory sessions) {
    super();
    this.sessions = sessions;
  }

  @Override public void addAvailablePhoneNumber(final AvailablePhoneNumber availablePhoneNumber) {
    final SqlSession session = sessions.openSession();
    try {
      session.insert(namespace + "addAvailablePhoneNumber", toMap(availablePhoneNumber));
    } finally {
      session.close();
    }
  }
  
  @SuppressWarnings("unchecked")
  private List<AvailablePhoneNumber> getAvailablePhoneNumbers(final String selector, final Object parameter) {
    final SqlSession session = sessions.openSession();
    try {
      List<Map<String, Object>> results = null;
      if(parameter == null) {
        results = (List<Map<String, Object>>)session.selectList(selector);
      } else {
        results = (List<Map<String, Object>>)session.selectList(selector, parameter);
      }
      final List<AvailablePhoneNumber> availablePhoneNumbers = new ArrayList<AvailablePhoneNumber>();
      if(results != null && !results.isEmpty()) {
        for(final Map<String, Object> result : results) {
          availablePhoneNumbers.add(toAvailablePhoneNumber(result));
        }
      }
      return availablePhoneNumbers;
    } finally {
      session.close();
    }
  }

  @Override public List<AvailablePhoneNumber> getAvailablePhoneNumbers() {
    return getAvailablePhoneNumbers(namespace + "getAvailablePhoneNumbers", null);
  }

  @Override public List<AvailablePhoneNumber> getAvailablePhoneNumbersByAreaCode(final String areaCode) {
	final String phoneNumber = new StringBuilder().append("+1").append(areaCode).append("_______").toString();
	return getAvailablePhoneNumbers(namespace + "getAvailablePhoneNumbersByAreaCode", phoneNumber);
  }

  @Override public List<AvailablePhoneNumber> getAvailablePhoneNumbersByPattern(final String pattern) throws IllegalArgumentException {
    return getAvailablePhoneNumbers(namespace + "getAvailablePhoneNumbersByPattern", normalizeAlphaCharacters(pattern.replace("*", "_")));
  }

  @Override public List<AvailablePhoneNumber> getAvailablePhoneNumbersByRegion(final String region) {
    return getAvailablePhoneNumbers(namespace + "getAvailablePhoneNumbersByRegion", region);
  }

  @Override public List<AvailablePhoneNumber> getAvailablePhoneNumbersByPostalCode(final int postalCode) {
    return getAvailablePhoneNumbers(namespace + "getAvailablePhoneNumbersByPostalCode", postalCode);
  }
  
  private String normalizeAlphaCharacters(final String input) throws IllegalArgumentException {
    final char[] tokens = input.toUpperCase().toCharArray();
    final char[] result = new char[tokens.length];
    for(int index = 0; index < tokens.length; index++) {
      final char token = tokens[index];
      if(token == '+' || token == '_' || Character.isDigit(token)) {
        result[index] = token;
        continue;
      } else if(Character.isLetter(token)) {
    	final int delta = 65; // The decimal distance from 0x0000 to 0x0041 which equals to 'A'
        final int position = Character.getNumericValue(token) - delta;
        result[index] = lookupTable[position];
      } else {
        throw new IllegalArgumentException(token + " is not a valid character.");
      }
    }
    return new String(result);
  }

  @Override public void removeAvailablePhoneNumber(final String phoneNumber) {
    final SqlSession session = sessions.openSession();
    try {
      session.delete(namespace + "removeAvailablePhoneNumber", phoneNumber);
    } finally {
      session.close();
    }
  }
  
  private AvailablePhoneNumber toAvailablePhoneNumber(final Map<String, Object> map) {
    final String friendlyName = (String)map.get("friendly_name");
    final String phoneNumber = (String)map.get("phone_number");
    final Integer lata = (Integer)map.get("lata");
    final String rateCenter = (String)map.get("rate_center");
    final Double latitude = (Double)map.get("latitude");
    final Double longitude = (Double)map.get("longitude");
    final String region = (String)map.get("region");
    final Integer postalCode = (Integer)map.get("postal_code");
    final String isoCountry = (String)map.get("iso_country");
    return new AvailablePhoneNumber(friendlyName, phoneNumber, lata, rateCenter, latitude, longitude, region, postalCode, isoCountry);
  }
  
  private Map<String, Object> toMap(final AvailablePhoneNumber availablePhoneNumber) {
    final Map<String, Object> map = new HashMap<String, Object>();
    map.put("friendly_name", availablePhoneNumber.getFriendlyName());
    map.put("phone_number", availablePhoneNumber.getPhoneNumber());
    map.put("lata", availablePhoneNumber.getLata());
    map.put("rate_center", availablePhoneNumber.getRateCenter());
    map.put("latitude", availablePhoneNumber.getLatitude());
    map.put("longitude", availablePhoneNumber.getLongitude());
    map.put("region", availablePhoneNumber.getRegion());
    map.put("postal_code", availablePhoneNumber.getPostalCode());
    map.put("iso_country", availablePhoneNumber.getIsoCountry());
    return map;
  }
}