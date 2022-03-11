package sn.free.selfcare.helper;

import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;
import sn.free.selfcare.helper.header.EmployeHeaderEnum;
import sn.free.selfcare.helper.header.LigneHeaderEnum;

public final class CSVImportFileHelper {
    private final static String TYPE = "application/vnd.ms-excel";
    public final static char SEPARATOR = ';';
    public final static String CHARSET_NAME = "UTF-8";

    public static boolean hasCSVFormat(MultipartFile file) {
        if (!TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }

    public static boolean checkRecordForLigne(CSVRecord csvRecord) {
        return (csvRecord.isSet(LigneHeaderEnum.NUMERO.name()) && csvRecord.isSet(LigneHeaderEnum.IMSI.name()));
    }

    public static boolean checkRecordForEmploye(CSVRecord csvRecord) {
        return (csvRecord.isSet(EmployeHeaderEnum.PRENOM.name())
            && csvRecord.isSet(EmployeHeaderEnum.NOM.name())
            && csvRecord.isSet(EmployeHeaderEnum.LIGNE.name())
            && csvRecord.isSet(EmployeHeaderEnum.GROUPE.name())
        );
    }
}
