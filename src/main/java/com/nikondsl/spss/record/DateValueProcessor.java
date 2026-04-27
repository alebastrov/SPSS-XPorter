package com.nikondsl.spss.record;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

final class DateValueProcessor {
    private static final long offset;

    static {
        Calendar calendar = new GregorianCalendar();//October 4, 1582-gregorian epoch
        calendar.clear();
        TimeZone timeZone = TimeZone.getDefault();
        calendar.setTimeZone(timeZone);
        calendar.set(1582, 9, 4);
        offset = calendar.getTimeInMillis();
    }

    private final SPSSBuffer buffer;

    public DateValueProcessor(SPSSBuffer buffer) {
        this.buffer = buffer;
    }

    /**
     *
     * 1313024=> code type=20 width=9 decimals=0
     * 1313536=> code type=20 width=11 decimals=0
     * 1377536=> code type=21 width=5 decimals=0
     * 1378304=> code type=21 width=8 decimals=0
     * 1379074=> code type=21 width=11 decimals=2
     * 1446144=> code type=22 width=17 decimals=0
     * 1446912=> code type=22 width=20 decimals=0
     * 1447682=> code type=22 width=23 decimals=2
     * 1509376=> code type=23 width=8 decimals=0
     * 1509888=> code type=23 width=10 decimals=0
     * 1574144=> code type=24 width=5 decimals=0
     * 1574656=> code type=24 width=7 decimals=0
     * 1640704=> code type=25 width=9 decimals=0
     * 1641472=> code type=25 width=12 decimals=0
     * 1642242=> code type=25 width=15 decimals=2
     * 1704704=> code type=26 width=3 decimals=0
     * 1706240=> code type=26 width=9 decimals=0
     * 1770240=> code type=27 width=3 decimals=0
     * 1771776=> code type=27 width=9 decimals=0
     * 1836544=> code type=28 width=6 decimals=0
     * 1837056=> code type=28 width=8 decimals=0
     * 1902080=> code type=29 width=6 decimals=0
     * 1902592=> code type=29 width=8 decimals=0
     * 1968128=> code type=30 width=8 decimals=0
     * 1968640=> code type=30 width=10 decimals=0
     * 2492160=> code type=38 width=7 decimals=0
     * 2492928=> code type=38 width=10 decimals=0
     */
    void addDateValue(Variable variable, UnmodifiableDate value) {
        int formatCode = variable.getFormatCode().getCode();
        switch (formatCode) {
            case 2558464:
            case 1313024:
            case 1313536:
            case 1377536:
            case 1378304:
            case 1379074:
            case 1446144:
            case 1446912:
            case 1447682:
            case 1509376:
            case 1509888:
            case 1574144:
            case 1574656:
            case 1640704:
            case 1641472:
            case 1642242:
            case 1704704:
            case 1706240:
            case 1770240:
            case 1771776:
            case 1836544:
            case 1837056:
            case 1902080:
            case 1902592:
            case 1968128:
            case 1968640:
            case 2492160:
            case 2492928:
                boolean dst = TimeZone.getDefault().inDaylightTime(new Date(System.currentTimeMillis()));
                long num = (value.getTime() - offset) / 1000L + (dst ? 3600L : 0L);
                buffer.addCompressedByte(CompressBufferPredefinedCodes.COMPRESS_NOT_COMPRESSED.getCode());
                buffer.addDataBytes(SPSSUtil.convert(Double.doubleToLongBits(num)));
                break;

            default:
                throw new IllegalArgumentException("Format for Date is not suitable. FormatCode=" + formatCode);
        }
    }
}
