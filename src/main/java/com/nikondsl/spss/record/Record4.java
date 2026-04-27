package com.nikondsl.spss.record;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: User Date: 13.03.2008 Time: 5:34:40 To change this template use
 * File | Settings | File Templates.
 */
class Record4 extends Record {
    private final List<Variable> variables;
    private final Variable variable;

    Record4(String charset, List<Variable> variables, Variable variable) {
        super(charset);
        this.variables = new ArrayList<Variable>(variables);
        this.variable = variable;
        type = RecordType.VARIABLE_INDICES_FOR_VALUE_LABELS;
    }

    public void write(final ByteArray array) throws IOException {
//      Record type code (=4) (I4)
        super.write(array);
//      Number of variables to involve (I4)
        array.addBytes(1);
//      Index of first variable (I4)
        array.addBytes(fixIndex(variable));
    }

    private int fixIndex(Variable variable) throws IOException {
        //индекс переменной начиная с нуля (считаются только записи Record 2)
        //при этом подзаписи для очень длинных переменных типа continuos string учитываются
        int result = 1;
        for (Variable var : variables) {
            if (var != null &&
                    variable.getType() == var.getType() &&
                    variable.getOldName().equals(var.getOldName())) return result;
            result++;
        }
        throw new IllegalStateException(variable + " not found among " + variables);
    }
}
