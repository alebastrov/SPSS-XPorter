import com.nikondsl.spss.IBufferProgressListener;
import com.nikondsl.spss.IProgressListener;
import com.nikondsl.spss.ISPSSWriter;
import com.nikondsl.spss.IVariable;
import com.nikondsl.spss.record.DateTimeFormat;
import com.nikondsl.spss.record.SPSSCase;
import com.nikondsl.spss.record.SPSSFacade;
import com.nikondsl.spss.record.VariableType;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 17/5/2008
 * Time: 7:45:50
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
public class Demo {

    static {
        System.setProperty("XPorter.logging", "true");
        System.setProperty("XPorter.queue", "500");
        System.setProperty("XPorter.cache", "150");
    }

    private final FileOutputStream os = new FileOutputStream(new File("c:/tmp/badSPSS.sav"));
    // os.write(writer.getResult());
    private final ISPSSWriter writer = SPSSFacade.createWriter("UTF-8", " Demo", os);
    private final int TIMES = 30000;

    public Demo() throws Exception {
        writer.setFileLabel("This is a SPSS format file exported from Key Survey (c) server. ");//64 characters maximum

        List<IVariable> variables = defineVariables1();
        writer.addVariables(variables);
        writer.setLineByLineMode(true);
        List<SPSSCase> cases = defineCases1();
        final int numberOfCases = 3 * (TIMES / 3) * cases.size();
        writer.setNumberOfCases(numberOfCases);
        writer.setProgressListener(new IProgressListener() {
            private final long startTime = System.currentTimeMillis();
            private int step = 0;
            private long timeOfPrinting = System.currentTimeMillis();

            public void nextStep() {
                step++;
                if (System.currentTimeMillis() - timeOfPrinting > 5000L) {
                    System.err.printf("written %5.0f (%5.2f %%) in %6.1f sec%n", (double) step, ((double) step * 100.0 / (double) numberOfCases), (double) (System.currentTimeMillis() - startTime) / 1000.0);
                    timeOfPrinting = System.currentTimeMillis();
                }
            }

            public void finish() {
                System.err.println("Finished by IProgressListener");
            }
        });
        writer.setBufferProgressListener(new IBufferProgressListener() {
            private long timeOfPrinting = System.currentTimeMillis();

            @Override
            public void status(int vacated, int free) {
                if (System.currentTimeMillis() - timeOfPrinting > 5000L) {
                    System.err.printf("buffer %5.0f (%5.2f %%)%n", (double) vacated, ((double) vacated / (double) (vacated + free)) * 100.0);
                    timeOfPrinting = System.currentTimeMillis();
                }
            }

            public void finish() {
                System.err.println("Finished by IBufferProgressListener");
            }
        });
        writer.generateDictionary();
        final CountDownLatch cdl = new CountDownLatch(3);
        Thread th1 = new Thread("Generator of cases 1") {
            public void run() {
                try {
                    for (int i = 0; i < TIMES / 3; i++) {
                        List<SPSSCase> cases = defineCases1();
                        writer.generate(cases);
                    }
                } catch (IOException ex) {
                    log.error("Cannot create a case in generator 1", ex);
                }
                cdl.countDown();
            }
        };
        Thread th2 = new Thread("Generator of cases 2") {
            public void run() {
                try {
                    for (int i = 0; i < TIMES / 3; i++) {
                        List<SPSSCase> cases = defineCases1();
                        writer.generate(cases);
                    }
                } catch (IOException ex) {
                    log.error("Cannot create a case in generator 2", ex);
                }
                cdl.countDown();
            }
        };
        Thread th3 = new Thread("Generator of cases 3") {
            public void run() {
                try {
                    for (int i = 0; i < TIMES / 3; i++) {
                        List<SPSSCase> cases = defineCases1();
                        writer.generate(cases);
                    }
                } catch (IOException ex) {
                    log.error("Cannot create a case in generator 3", ex);
                }
                cdl.countDown();
            }
        };
        th1.start();
        th2.start();
        th3.start();
        cdl.await();

        writer.generateFinishSection();
        writer.close();
    }

    static void main(String[] args) throws Exception {
        new Demo();
    }

    private List<IVariable> defineVariables1() throws UnsupportedEncodingException {
        List<IVariable> variables = new ArrayList<IVariable>();
        IVariable respNo = SPSSFacade.createVariable(writer, VariableType.NUMERIC, "Respondent #", "Respondent #", null);
        IVariable date = SPSSFacade.createVariable(writer, VariableType.DATE, "Submit date", "Submit date", null);
        date.setDateFormatStyle(DateTimeFormat.TIRE_ddMMMyyyy);
        IVariable continents = SPSSFacade.createVariable(writer, "Continents_Name", "Continents of the world", 2048);
        continents.setStringValueLabels(Collections.singletonMap("Africa", "Africa continent (include North Africa and islands)"));
        IVariable territorySize = SPSSFacade.createVariable(writer, VariableType.NUMERIC, "Size of territory", "Size of territory (sq km)", null);
        IVariable population = SPSSFacade.createVariable(writer, VariableType.NUMERIC, "Human population", "Human population", null);
        Map<Double, String> labelsForCountries = new HashMap<Double, String>();
        labelsForCountries.put((double) 0L, "No population at all");
        labelsForCountries.put((double) 12L, "Veri small population");
        labelsForCountries.put((double) 23L, "Medium");
        labelsForCountries.put((double) 44L, "Large");
        labelsForCountries.put((double) 53L, "Huge population");
        IVariable countries = SPSSFacade.createVariable(writer, VariableType.NUMERIC, "Total countries", "Number of countries", labelsForCountries);

        Map<Double, String> labelsForOvercrowded = new HashMap<Double, String>();
        labelsForOvercrowded.put((double) 0L, "Empty");
        labelsForOvercrowded.put((double) 1L, "Normal");
        labelsForOvercrowded.put((double) 2L, "Crowded");
        IVariable overcrowded = SPSSFacade.createVariable(writer, VariableType.NUMERIC, "Overcrowded1", "is territory empty or overcrowded", labelsForOvercrowded);

        IVariable dollar = SPSSFacade.createVariable(writer, VariableType.DOLLAR, "Dollars_amount", "Dollar amount", null);
        IVariable dot = SPSSFacade.createVariable(writer, VariableType.DOT, "Dot", "", null);
        dot.setWidth(8);

        variables.add(respNo);
        variables.add(date);
        variables.add(continents);
        variables.add(territorySize);
        variables.add(population);
        variables.add(countries);
        variables.add(overcrowded);
        variables.add(dollar);
        variables.add(dot);
        return variables;
    }

    private List<SPSSCase> defineCases1() throws IOException {
        List<SPSSCase> cases = new ArrayList<SPSSCase>();
        for (int i = 0; i < 1; i++) {
            cases.add(new SPSSCase(1, new Date(), "261a supercrowler AS-Z-200453Asia supercrowler AS-Z-200453Asia supercrowler AS-Z-200453Asia supercrowler AS-Z-200453Asia supercrowler AS-Z-200453Asia supercrowler AS-Z-200453Asia supercrowler AS-Z-200453Asia supercrowler AS-Z-200453Asia supercrowler AS-Z-200453", 44579000L, 3674000000L, 44L, 2L, 22345, 22346));
            cases.add(new SPSSCase(2, new Date(), "Очень длинный русский текст. Хорошо на свете жить, можно денежки копить, если все сразу не выпивать а на утро оставлять. Что-то нужно дописать, кто-то станет дрянь читать, чтобы не было беды, не носи ты бороды", 44579000L, 3674000000L, 44L, 2L, 22345, 22346));
            cases.add(new SPSSCase(3, new Date(), null, Math.random() > 0.8 ? 44579000L : null, 3674000000L, Math.random() > 0.8 ? 44L : null, Math.random() > 0.8 ? 2L : null, 22349, Math.random() > 0.8 ? 22350 : null));
            cases.add(new SPSSCase(4, new Date(), "Africa", 30065000L, Math.random() > 0.8 ? 778000000L : null, Math.random() > 0.8 ? 53L : null, Math.random() > 0.8 ? 1L : null, Math.random() > 0.8 ? 22543 : null, 22348));
            cases.add(new SPSSCase(5, new Date(), "North America", 24256000L, 483000000L, 23L, 1L, 22351, 22352));
            cases.add(new SPSSCase(6, null, "South America", Math.random() > 0.8 ? 17819000L : null, Math.random() > 0.8 ? 342000000L : null, 12L, 1L, 22353, 22354));
            cases.add(new SPSSCase(7, new Date(), "Antarctica", 13209000L, 0L, 0L, 0L, 22355, 22356));
            cases.add(new SPSSCase(8, new Date(), "Europe", 9938000L, 732000000L, 46L, 1L, 22357, 22358));
            cases.add(new SPSSCase(9, new Date(), "Australia/Oceania", 7687000L, 31000000L, 14L, 1L, 22359, 22360));
            cases.add(new SPSSCase(10, new Date(), "Transelvania", 7345560L, 34565456L, 44L, 1L, 22361, 22362));
        }
        return cases;
    }


}
