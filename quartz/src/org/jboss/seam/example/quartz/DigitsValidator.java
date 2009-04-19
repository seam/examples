package org.jboss.seam.example.quartz;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.hibernate.validator.Validator;

public class DigitsValidator
    implements Validator<Digits>
{
    int integerDigits;
    int fractionalDigits;
    
    public void initialize(Digits configuration) {
        integerDigits      = configuration.integerDigits();
        fractionalDigits = configuration.fractionalDigits();        
    }

    public boolean isValid(Object value) {
        if (value==null) { 
            return true;
        }

        String stringValue = null;

        if (value instanceof String) {
            try {
                stringValue = stringValue(new BigDecimal((String) value));
            } catch (NumberFormatException nfe) {
                return false;
            }
        } else if (value instanceof BigDecimal) {
            stringValue = stringValue((BigDecimal) value);
        } else if (value instanceof BigInteger) {
            stringValue = stringValue((BigInteger) value);
        } else {
            return false;
        }


        int pos = stringValue.indexOf(".");

        int left = (pos == -1) ? stringValue.length() : pos;
        int right = (pos==-1) ? 0 : stringValue.length() - pos -1;

        if (left==1 && stringValue.charAt(0) == '0') {
            left--;
        }
        
        if (left > integerDigits || right > fractionalDigits) {
            return false;
        }

        return true;
    }

    private String stringValue(BigDecimal number) {
        return number.abs().stripTrailingZeros().toPlainString();
    }
    private String stringValue(BigInteger number) {
        return number.abs().toString();
    }
}
