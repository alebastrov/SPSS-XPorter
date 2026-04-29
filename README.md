# SPSS/PSPP XPorter
Java SPSS/PSPP implementation to create *.sav files without using any external API

# Get started (see AutomaticModeTestCase as example)
1. Create an OutputStream
~~~
FileOutputStream os=new FileOutputStream(file);
~~~
2. Create a SPSS/PSPP writer
~~~
ISPSSWriter writer = SPSSFacade.createWriter("UTF-8", "Header with description", os);
~~~
3. Create variables to be written in cases
~~~
 List<IVariable> variables = new ArrayList<>();
 IVariable variable = SPSSFacade.createVariable( writer, VariableType.DATE, "Submit date", "Submit date", null ); 
 variables.add( variable );
 ...
 writer.addVariables( variables );
~~~
4. Create & put cases
~~~
List<SPSSCase> cases = new ArrayList<>();
cases.add( new SPSSCase( 0 ) );
...
writer.setNumberOfCases( cases.size() );
writer.generateDictionary();
writer.generate( cases );
~~~
5. Create a finish section
~~~
writer.generateFinishSection();
~~~
6. Close writer (this also closes an OutputStream)
~~~
writer.close();
~~~