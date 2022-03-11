package sn.free.selfcare.helper.header;

public enum LigneHeaderEnum {
    NUMERO("numero"),
    IMSI("imsi");

    LigneHeaderEnum(String headerName) {
        this.headerName = headerName;
    }

    private String headerName;
}
