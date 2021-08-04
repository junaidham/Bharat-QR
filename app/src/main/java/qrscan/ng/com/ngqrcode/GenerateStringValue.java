package qrscan.ng.com.ngqrcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import qrscan.ng.com.ngqrcode.CRC16;
import qrscan.ng.com.ngqrcode.SystemConstants;
import qrscan.ng.com.ngqrcode.TagsAndValuesMap;


public class GenerateStringValue {
	String tag55Value = "";

	public String getStringValue(Map<String, Object> qrData) {
		String qrString = "";
		String finalTlvString = "";
		String transactionType = "";
		CRC16 crc = new CRC16();
		List<String> tlv = new ArrayList<String>();
		Set<String> keySet = qrData.keySet();
		if (keySet.contains(SystemConstants.Tag55)) {
			if (qrData.get(SystemConstants.Tag55).equals(SystemConstants.Tag5502)
					&& !keySet.contains(SystemConstants.Tag56)) {
				return SystemConstants.errorTag5502;
			} else if (qrData.get(SystemConstants.Tag55).equals(SystemConstants.Tag5503)
					&& !keySet.contains(SystemConstants.Tag57)) {
				return SystemConstants.errorTag5503;
			}
		}
		for (Entry<String, Object> entry : qrData.entrySet()) {
			if (entry.getKey().equals(SystemConstants.Tag54)) {
				transactionType = SystemConstants.Tag01Dynamic;
			}
		}
		if (null == transactionType || transactionType == "") {
			transactionType = SystemConstants.Tag01Static;
		}
		Iterator<Entry<String, Object>> it = qrData.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object> pair = (Entry<String, Object>) it.next();
			String generatedTlv = generateTlv(pair, transactionType);
			tlv.add(generatedTlv);
			it.remove(); // avoids a ConcurrentModificationException
		}
		StringBuilder sb = new StringBuilder(SystemConstants.Tag00);
		sb.append(SystemConstants.Tag00length).append(SystemConstants.Tag00Value);
		for (String tlvString : tlv) {
			sb.append(tlvString);
			finalTlvString = sb.toString();
		}
		sb = new StringBuilder(finalTlvString);
		sb.append(SystemConstants.CRCTagValue).append(SystemConstants.CRCLength);
		String crc16 = crc.generateCRC16(sb.toString());
		qrString = sb.append(crc16.toUpperCase()).toString();
		return qrString;
	}

	public String generateTlv(Entry<String, Object> pair, String transactionType) {
		Map<String, String> tagValueMap = new HashMap<String, String>();
		Map<String, String> tag01ValueMap = new HashMap<String, String>();
		Map<String, String> tag62ValueMap = new HashMap<String, String>();

		String tag = "";
		String tlv = "";
		tagValueMap = TagsAndValuesMap.getTagValues();
		tag01ValueMap = TagsAndValuesMap.getTag01Values();
		tag62ValueMap = TagsAndValuesMap.getTag62Values();
		for (Entry<String, String> entry : tagValueMap.entrySet()) {
			if (pair.getKey().equals(entry.getKey())) {
				tag = entry.getValue();
				break;
			} else {
				continue;
			}
		}
		if (tag.equals(SystemConstants.Tag01Values)) {
			String tagDetailChar1 = "";
			String tagDetailChar2 = "";
			for (Entry<String, String> entry : tag01ValueMap.entrySet()) {
				if (entry.getKey().equals(transactionType)) {
					tagDetailChar2 = entry.getValue();

				}
				if (entry.getKey().equals(pair.getValue())) {
					tagDetailChar1 = entry.getValue();

				}

			}
			StringBuilder sb = new StringBuilder(tag);
			sb.append(SystemConstants.Tag01Length).append(tagDetailChar1).append(tagDetailChar2);
			tlv = sb.toString();

		} else if (tag.equals(SystemConstants.Tag62Values)) {
			String tagDetail = "";
			StringBuilder sb = new StringBuilder(tag);
			StringBuilder additionalSb = new StringBuilder();
			Map<String, String> additionalData = (Map<String, String>) pair.getValue();
			for (Entry<String, String> additionalEntry : additionalData.entrySet()) {
				for (Entry<String, String> entry : tag62ValueMap.entrySet()) {
					if (entry.getKey().equals(additionalEntry.getKey())) {
						tagDetail = entry.getValue();

					}
				}
				Integer length = additionalEntry.getValue().length();
				String finalLength = null;
				if (length < 10) {
					finalLength = 0 + (length.toString());
				} else {
					finalLength = length.toString();
				}
				additionalSb.append(tagDetail).append(finalLength).append(additionalEntry.getValue());
			}
			sb.append(additionalSb.length()).append(additionalSb);
			tlv = sb.toString();
		} else {
			Integer length = (pair.getValue().toString()).length();
			String finalLength = null;
			StringBuilder sb = new StringBuilder(tag);
			if (length < 10) {
				finalLength = 0 + (length.toString());
			} else {
				finalLength = length.toString();
			}
			sb.append(finalLength).append(pair.getValue());
			tlv = sb.toString();
		}
		return tlv;
	}
}
