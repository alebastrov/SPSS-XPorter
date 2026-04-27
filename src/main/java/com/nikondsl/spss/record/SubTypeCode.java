package com.nikondsl.spss.record;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 14/1/2008
 * Time: 1:11:35
 * To change this template use File | Settings | File Templates.
 */
public enum SubTypeCode {
    DataEntryInformation(1),//VAX Only
    SPSSReleaseAndMachineSpecificIntegerInformation(3),//release and machine-specific  integer information
    SPSSReleaseAndMachineSpecificFloatInformation(4),
    VariableSetsInformation(5),
    TrendsDateVariableInformation(6),
    MultipleResponseGroupsInformation(7),
    DataEntryForWindowsInformation(8),
    ReservedForExpansion(9),
    TextProductInformation(10),
    MeasurementLevelAndColumnWidthAndAlignment(11),
    AdditionalDataEntryForWindowsInformation(12),
    ExtendedVariableNames(13),
    ExtendedStrings(14),
    ClementineData(15),
    NumberOfCasesIn64bit(16),
    DatasetAttributes(17),
    VariableAttributes(18),
    EnhancedVariableSets(19),
    CharacterEncodingOrCodePage(20);

    private final int subCode;

    SubTypeCode(final int subCode) {
        this.subCode = subCode;
    }

    int getSubCode() {
        return subCode;
    }
}
