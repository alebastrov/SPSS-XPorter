import com.nikondsl.spss.ISPSSWriter;
import com.nikondsl.spss.IVariable;
import com.nikondsl.spss.record.MissingValueOne;
import com.nikondsl.spss.record.SPSSCase;
import com.nikondsl.spss.record.SPSSFacade;
import com.nikondsl.spss.record.VariableType;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ManualModeDemo {

    static {
        System.setProperty("XPorter.logging", "true");
        System.setProperty("XPorter.queue", "10");
    }

    private static List<IVariable> defineVariables(ISPSSWriter writer) throws UnsupportedEncodingException {
        List<IVariable> variables = new ArrayList<IVariable>();
        Map<Double, String> labels = new HashMap<Double, String>();
        labels.put(0d, "None");
        labels.put(1d, "One");
        labels.put(2d, "Few");
        labels.put(3d, "Several");
        labels.put(99d, "Many");

        IVariable variable1 = SPSSFacade.createVariable(writer, VariableType.NUMERIC, "Number", "", labels);
        variable1.setMissingValue(new MissingValueOne(99));
        variables.add(variable1);

        IVariable variable2 = SPSSFacade.createVariable(writer, "Some big text", "Label for variable", 32);
        variables.add(variable2);
        return variables;
    }

    private static List<SPSSCase> defineCases() throws IOException {
        List<SPSSCase> cases = new ArrayList<SPSSCase>();
        cases.add(new SPSSCase(0d, "Zero"));
        cases.add(new SPSSCase(1d, "One"));
        cases.add(new SPSSCase(1d, "One Two"));
        cases.add(new SPSSCase(3d, "Tree"));
        cases.add(new SPSSCase(2d, "Two Two"));
        cases.add(new SPSSCase(99d, "Ninty nine"));
        return cases;
    }

    static void main(String[] args) throws Exception {
        FileOutputStream os = new FileOutputStream(new File("c:/tmp/SPSS3.sav"));
        ISPSSWriter writer = SPSSFacade.createWriter("UTF-8", " Demo", os);
        writer.setFileLabel("Automatic mode generator");
        List<IVariable> variables = defineVariables(writer);
        writer.addVariables(variables);
        List<SPSSCase> cases = defineCases();
        writer.generate(cases);
        writer.generateFinishSection();
        writer.close();
    }
}
