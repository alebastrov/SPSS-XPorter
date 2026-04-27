package com.nikondsl.spss.record;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 14/1/2008
 * Time: 1:20:02
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class ConsumerThread extends Thread {
    static final SPSSCase MARKER_EOF = new SPSSCase("marker EOF");
    private final SPSSWriter spssWriter;

    ConsumerThread(SPSSWriter spssWriter) {
        super("XPorter consumer thread (Ready to consume SPSSCases)");
        this.spssWriter = spssWriter;
    }

    public void run() {
        try {
            boolean stopFound = false;
            while (true) {
                SPSSCase spssCase = spssWriter.getQueue().poll(100L, TimeUnit.MILLISECONDS);
                if (spssCase == null && stopFound) {
                    spssWriter.countDownLatch.countDown();
                    return;
                }
                if (spssCase == null) {
                    continue;
                }
                if (spssCase == MARKER_EOF) {
                    //we have to empty queue and only then return
                    stopFound = true;
                    continue;
                }
                if (spssWriter.getBufferListener() != null) {
                    try {
                        int size = spssWriter.getQueue().size();
                        spssWriter.getBufferListener().status(size, spssWriter.getQueue().remainingCapacity());
                    } catch (Exception ex) {
                        log.error("Unexpected exception happened while sending STATE signal to Queue State Listener", ex);
                    }
                }
                spssWriter.addCase(spssCase);
            }
        } catch (InterruptedException ex) {
            spssWriter.getQueue().clear();
            Thread.currentThread().interrupt();
            log.error("Writing was interrupted:", ex);
        } catch (Exception ex) {
            log.error("An unexpected exception has occurred while writing:", ex);
        }
        try {
            if (spssWriter.getBufferListener() != null) spssWriter.getBufferListener().finish();
        } catch (Exception ex) {
            log.error("An unexpected exception has occurred while sending FINISH signal to Progress Listener", ex);
        }
    }
}
