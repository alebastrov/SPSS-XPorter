package com.nikondsl.spss.record;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 *
 */
final class StringValueProcessor {
    private final SPSSWriter spssWriter;
    private final SPSSBuffer buffer;

    StringValueProcessor(SPSSWriter spssWriter, SPSSBuffer buffer) {
        this.spssWriter = spssWriter;
        this.buffer = buffer;
    }

    static Variable createLastVariable(SPSSWriter spssWriter, Variable variable, String uniqueName, int length) throws UnsupportedEncodingException {
        VariableKey key = new VariableKey(uniqueName, variable.getLabel(), length);
        Variable last = spssWriter.lastVariablesCached.get(key);
        if (last != null) {
            return last;
        }
        last = new Variable(spssWriter.charset, uniqueName, variable.getLabel(), length);
        if (spssWriter.lastVariablesCached.size() < spssWriter.cacheSize) {
            spssWriter.lastVariablesCached.put(key, last);
        }
        return last;
    }

    void writeStringValue(Variable variable, String value) throws IOException {
        if (value == null) value = "";
        int additionalVariables = variable.getRealWidth() / 252;
        if (additionalVariables <= 0) {
//            strings less 256 bytes
            addStringValue(buffer, variable, value.getBytes(spssWriter.charset));
            return;
        }
        //continuos strings >255 bytes
        final byte[] bytes = SPSSUtil.substring(spssWriter.charset,
                variable.getOldNameAsString(),
                6);//beginning of variable's name
        String addVariableName = new String(bytes, spssWriter.charset);
        List<byte[]> dividedLongStringBy255bytes = null;
        dividedLongStringBy255bytes = new SPSSUtil(spssWriter).divideByLength(spssWriter.charset, value, 255, spssWriter.cacheSize);

        for (int i = 0; i < additionalVariables; i++) {
            Variable additionalVariable = spssWriter.createAdditionalVariable(variable, addVariableName, i);
            if (dividedLongStringBy255bytes.size() > i) addStringValue(buffer,
                    additionalVariable,
                    dividedLongStringBy255bytes.get(i));
            else addStringValue(buffer, additionalVariable, EmptyArrays.emptyBytes[ 0 ]);
            if (value.getBytes(spssWriter.charset).length <= 256 * i + 255) {
                value = "";
            }
        }
        //the last variable
        Variable additionalVariable = createLastVariable(spssWriter,
                variable,
                addVariableName,
                variable.getRealWidth() - additionalVariables * 252);
        if (dividedLongStringBy255bytes.size() > additionalVariables) {
            addStringValue(buffer,
                    additionalVariable,
                    dividedLongStringBy255bytes.get(additionalVariables));
        } else addStringValue(buffer,
                additionalVariable,
                value.getBytes(spssWriter.charset));
    }

    void addStringValue(SPSSBuffer buffer, Variable variable, byte[] valueBytes) throws UnsupportedEncodingException {
        final SPSSBuffer variableBuffer = variable.getEmptyBuffer();
        if (valueBytes.length == 0 && variableBuffer != null) {
            //если данные "пустой стринг" закешированы сразу выводим
            buffer.addCompressedBytes(variableBuffer.getCompressedBytes());
            buffer.addDataBytes(variableBuffer.getDataBytes());
            return;
        }
        HolderKey key = new HolderKey();
        key.valueBytes = valueBytes;
        key.variable = variable;
        Holder holder = spssWriter.stings.get(key);
        if (holder == null) {
            holder = new Holder();
            if (spssWriter.stings.size() < spssWriter.cacheSize) {
                spssWriter.stings.put(key, holder);
            }
        } else {
            buffer.addCompressedBytes(holder.compressedBytesString);
            buffer.addDataBytes(holder.uncompressedBytesString);
            return;
        }
        final int variableWidth = variable.getWidth();
        int width = variableWidth % 8 != 0 ? (variableWidth / 8 + 1) * 8 : variableWidth;
        if (valueBytes.length > variableWidth) {
            //throw new IllegalStateException("cannot be more than "+variable.getWidth()+" but were "+valueBytes.length+" bytes");
            FixedLengthString string = new FixedLengthString(spssWriter.charset,
                    new String(valueBytes, spssWriter.charset),
                    variableWidth);
            valueBytes = string.getTruncated().getBytes(spssWriter.charset);
        }
        int length = valueBytes.length;
        // текстовое значение может быть намного больше 8 байт, его нужно писать блоками по 8 байт
        // предположим, текст составляет 321 байт, тогда блоков для записи должно быть 40 полных и
        // 1 не до конца заполненный
        if (length % 8 != 0) {
            length = (1 + length / 8) * 8;//41*8=328 байт
            ByteArray bytes = ByteArray.createByteArray("temp");
            bytes.addBytes(valueBytes);//копируем сюда целиком наш текст
            int delta = length - valueBytes.length;//вічисляем солько должно быть заполняющих байт
            if (delta > 7)
                throw new Error("Bad position3:" + variable + "{" + new String(valueBytes, spssWriter.charset) + "}[" + bytesToString(valueBytes) + "=>" + new String(valueBytes, spssWriter.charset) + "] delta=" + delta);
            if (delta > 0) {
                bytes.addBytes(EmptyArrays.spacesBytes[ delta ]);//заполняем пробелами
            }
            valueBytes = bytes.getArray();//получаем правильный текст с данными
        }
        byte[] compressed = new byte[ width / 8 ];
        int countForCheck = 0;
        for (int i = 0; i < width / 8; i++) {
            final boolean notCompressed = i < length / 8;
            if (notCompressed) countForCheck++;
            compressed[ i ] = notCompressed
                    ? CompressBufferPredefinedCodes.COMPRESS_NOT_COMPRESSED.getCode()
                    : CompressBufferPredefinedCodes.COMPRESS_ALL_BLANKS.getCode();
        }
        if (countForCheck > 0 && valueBytes.length / 8 / countForCheck != 1) {
            throw new Error("Bad position1:" + variable + "{" + new String(valueBytes, spssWriter.charset) + "}[" + bytesToString(valueBytes) + "=>" + new String(valueBytes, spssWriter.charset) + "]");
        }
        if (countForCheck == 0 && valueBytes.length > 0) {
            throw new Error("Bad position2:" + variable + "{" + new String(valueBytes, spssWriter.charset) + "}[" + bytesToString(valueBytes) + "=>" + new String(valueBytes, spssWriter.charset) + "]");
        }
        if (valueBytes.length == 0 && variableBuffer == null) {
            //кешируем
            SPSSBuffer cached = new SPSSBuffer(width);
            cached.addCompressedBytes(compressed);
//            cached.addDataBytes(valueBytes);
            variable.setEmptyBuffer(cached);
        }
        holder.compressedBytesString = compressed;
        holder.uncompressedBytesString = valueBytes;
        buffer.addCompressedBytes(compressed);
        buffer.addDataBytes(valueBytes);
    }

    private String bytesToString(byte[] bytes) {
        if (bytes == null) return "[null]";
        if (bytes.length == 0) return "[]";
        StringBuilder result = new StringBuilder(128);
        result.append("[\n");
        int count = 0;
        for (byte b : bytes) {
            count++;
            if (count % 8 == 0) {
                result.append("\n");
            }
            result.append(Long.toHexString(b));
            result.append(", ");
        }
        result.setLength(result.length() - 2);
        result.append("\n]");
        return result.toString();
    }

//    public static void main(String[] args) throws Exception {
//        System.err.println("Test bug SUP-3009");
//        Variable var=new Variable("UTF-8", "Text", "");
//        var.setWidth(322);
//      final String str = "Самый длинный текст, что можно только представить. " +
//                         "Немного неправильный и более чем ошибочный. " +
//                         "В предположении, что ничего не работает, " +
//                         "но все оказалось намного проще и сложнее в частности";
//      final byte[] bytes = str.getBytes("utf-8");
//      SPSSWriter spssWriter=new SPSSWriter("utf-8","",new ByteArrayOutputStream());
//      final SPSSBuffer spssBuffer = new SPSSBuffer(322, 3);
//      StringValueProcessor svp=new StringValueProcessor(spssWriter, spssBuffer);
//      svp.writeStringValue(var, str);
//      svp.addStringValue(spssBuffer, var, bytes);
//    }
}
