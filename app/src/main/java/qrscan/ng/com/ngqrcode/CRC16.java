package qrscan.ng.com.ngqrcode;
public class CRC16 {
	public String generateCRC16(String tlv){
        int crc = 0xFFFF;          // initial value
        int polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12) 
        byte[] bytes = tlv.getBytes();

        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b   >> (7-i) & 1) == 1);
                boolean c15 = ((crc >> 15    & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }

        crc &= 0xffff;
        String crcVal= Integer.toHexString(crc);
        if(crcVal.length()<4){
        	crcVal=0+crcVal;
        }
        return crcVal;

	    }	
}
