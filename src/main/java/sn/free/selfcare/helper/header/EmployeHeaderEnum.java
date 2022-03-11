package sn.free.selfcare.helper.header;

public enum EmployeHeaderEnum {
    PRENOM("prenom"),
    NOM("nom"),
    LIGNE("ligne"),
    GROUPE("groupe"),
    EMAIL("email"),
    POSTE("poste");

    EmployeHeaderEnum(String headerName) {
        this.headerName = headerName;
    }

    private String headerName;
}
