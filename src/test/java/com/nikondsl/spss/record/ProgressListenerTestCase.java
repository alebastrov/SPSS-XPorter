package com.nikondsl.spss.record;

import com.nikondsl.spss.IBufferProgressListener;
import com.nikondsl.spss.IProgressListener;
import com.nikondsl.spss.IVariable;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: igor
 * Date: 3/5/12
 * Time: 10:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProgressListenerTestCase {

    @Test
    public void testCreateVariant1() throws IOException {
        //работает синхронный код, поэтому буфера нет
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        SPSSWriter spssWriter = new SPSSWriter("UTF-8", "", os) {
            @Override
            Record1 createRecord1(String charset, String header, FileLayoutCode layoutCode) throws UnsupportedEncodingException {
                return new Record1(charset, header, layoutCode, this) {
                    @Override
                    Date getCreationDate() {
                        return new Date(1325368800000L);//2012-01-01
                    }
                };
            }

            @Override
            String getBuildNumber() {
                return "1.7 build 128";
            }
        };
        IVariable var = spssWriter.createVariable("Text", "", 266);
        spssWriter.addVariable(var);

        List<SPSSCase> cases = new ArrayList<SPSSCase>();
        for (int i = 0; i < 1000; i++) {
            SPSSCase spssCase = new SPSSCase("Some long " + Long.toHexString(i * i) + " english text");
            cases.add(spssCase);
        }
        final AtomicInteger step = new AtomicInteger(0);
        final AtomicBoolean stop = new AtomicBoolean(false);
        IProgressListener progressListener = new IProgressListener() {
            @Override
            public void nextStep() {
                step.incrementAndGet();
            }

            @Override
            public void finish() {
                stop.set(true);
            }
        };
        spssWriter.setProgressListener(progressListener);//buffer is ignored
        spssWriter.setNumberOfCases(1000);
        spssWriter.generateDictionary();
        spssWriter.generate(cases);
        spssWriter.generateFinishSection();
        spssWriter.close();

        assertEquals(1000, step.get());
        assertTrue(stop.get());
    }

    @Test
    public void testCreateVariant2() throws IOException {
        //работает синхронный код, поэтому буфера нет
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        SPSSWriter spssWriter = new SPSSWriter("UTF-8", "", os) {
            @Override
            Record1 createRecord1(String charset, String header, FileLayoutCode layoutCode) throws UnsupportedEncodingException {
                return new Record1(charset, header, layoutCode, this) {
                    @Override
                    Date getCreationDate() {
                        return new Date(1325368800000L);//2012-01-01
                    }
                };
            }

            @Override
            String getBuildNumber() {
                return "1.7 build 128";
            }
        };
        IVariable var = spssWriter.createVariable("Text", "", 266);
        spssWriter.addVariable(var);

        List<SPSSCase> cases = new ArrayList<SPSSCase>();
        for (int i = 0; i < 500; i++) {
            SPSSCase spssCase = new SPSSCase("Some long " + Long.toHexString(i * i) + " english text");
            cases.add(spssCase);
        }
        final AtomicInteger step = new AtomicInteger(0);
        final AtomicBoolean stop1 = new AtomicBoolean(false);
        final AtomicBoolean stop2 = new AtomicBoolean(false);
        IProgressListener progressListener = new IProgressListener() {
            @Override
            public void nextStep() {
                step.incrementAndGet();
            }

            @Override
            public void finish() {
                stop1.set(true);
            }
        };
        IBufferProgressListener bufferProgressListener = new IBufferProgressListener() {
            @Override
            public void status(int vacated, int free) {
                step.incrementAndGet();
            }

            @Override
            public void finish() {
                stop2.set(true);
            }
        };

        spssWriter.setBufferProgressListener(bufferProgressListener);
        spssWriter.setProgressListener(progressListener);//buffer is ignored
        spssWriter.setNumberOfCases(500);
        spssWriter.generateDictionary();
        spssWriter.generate(cases);
        spssWriter.generateFinishSection();
        spssWriter.close();

        assertEquals(500, step.get());
        assertTrue(stop1.get());
        assertTrue(stop2.get());
    }

    @Test
    public void testCreateVariant3() throws IOException {
        //работает синхронный код, поэтому буфера нет
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        SPSSWriter spssWriter = new SPSSWriter("UTF-8", "", os) {
            @Override
            Record1 createRecord1(String charset, String header, FileLayoutCode layoutCode) throws UnsupportedEncodingException {
                return new Record1(charset, header, layoutCode, this) {
                    @Override
                    Date getCreationDate() {
                        return new Date(1325368800000L);//2012-01-01
                    }
                };
            }

            @Override
            String getBuildNumber() {
                return "1.7 build 128";
            }
        };
        IVariable var = spssWriter.createVariable("Text", "", 266);
        spssWriter.addVariable(var);

        List<SPSSCase> cases = new ArrayList<SPSSCase>();
        for (int i = 0; i < 500; i++) {
            SPSSCase spssCase = new SPSSCase("Some long " + Long.toHexString(i * i) + " english text");
            cases.add(spssCase);
        }
        final AtomicInteger step = new AtomicInteger(0);
        final AtomicBoolean stop1 = new AtomicBoolean(false);
        final AtomicBoolean stop2 = new AtomicBoolean(false);
        IProgressListener progressListener = new IProgressListener() {
            @Override
            public void nextStep() {
                step.incrementAndGet();
            }

            @Override
            public void finish() {
                stop1.set(true);
            }
        };
        IBufferProgressListener bufferProgressListener = new IBufferProgressListener() {
            @Override
            public void status(int vacated, int free) {
                step.incrementAndGet();
            }

            @Override
            public void finish() {
                stop2.set(true);
            }
        };

        spssWriter.setBufferProgressListener(bufferProgressListener);
        spssWriter.setProgressListener(progressListener);//buffer is ignored
        spssWriter.setNumberOfCases(500);
        spssWriter.generateDictionary();
        spssWriter.generate(cases);
        spssWriter.generateFinishSection();
        spssWriter.close();

        assertEquals(500, step.get());
        assertTrue(stop1.get());
        assertTrue(stop2.get());
    }

    @Test
    public void testCreateVariant4() throws IOException {
        //работает асинхронный код, поэтому буфер есть
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        SPSSWriter spssWriter = new SPSSWriter("UTF-8", "", os) {
            @Override
            Record1 createRecord1(String charset, String header, FileLayoutCode layoutCode) throws UnsupportedEncodingException {
                return new Record1(charset, header, layoutCode, this) {
                    @Override
                    Date getCreationDate() {
                        return new Date(1325368800000L);//2012-01-01
                    }
                };
            }

            @Override
            String getBuildNumber() {
                return "1.7 build 128";
            }
        };
        IVariable var = spssWriter.createVariable("Text", "", 266);
        spssWriter.addVariable(var);

        List<SPSSCase> cases = new ArrayList<SPSSCase>();
        for (int i = 0; i < 500; i++) {
            SPSSCase spssCase = new SPSSCase("Some long " + Long.toHexString(i * i) + " english text");
            cases.add(spssCase);
        }
        final AtomicInteger step = new AtomicInteger(0);
        final AtomicInteger status = new AtomicInteger(0);
        final AtomicBoolean stop1 = new AtomicBoolean(false);
        final AtomicBoolean stop2 = new AtomicBoolean(false);
        IProgressListener progressListener = new IProgressListener() {
            @Override
            public void nextStep() {
                step.incrementAndGet();
            }

            @Override
            public void finish() {
                stop1.set(true);
            }
        };

        IBufferProgressListener bufferProgressListener = new IBufferProgressListener() {
            @Override
            public void status(int vacated, int free) {
                status.incrementAndGet();
            }

            @Override
            public void finish() {
                stop2.set(true);
            }
        };
        spssWriter.setLineByLineMode(true);
        spssWriter.setBufferProgressListener(bufferProgressListener);
        spssWriter.setProgressListener(progressListener);//buffer is ignored
        spssWriter.setNumberOfCases(500);
        spssWriter.generateDictionary();
        spssWriter.generate(cases);
        spssWriter.generateFinishSection();
        spssWriter.close();

        assertEquals(500, step.get());
        assertEquals(500, status.get());
        assertTrue(stop1.get());
        assertTrue(stop2.get());
    }
}
